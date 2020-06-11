package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.joystick.generated.AxisVector;
import frc.taurus.joystick.generated.ButtonVector;
import frc.taurus.joystick.generated.JoystickStatus;

public class JoystickStatusTest {

  double eps = 1e-6; // using floats for joystick axes

  @Test
  public void writeSingleMessageTest() {

    ChannelManager channelManager = ChannelManager.getInstance();
    channelManager.reset();   // reset at the start of every unit test

    var statusQueue = channelManager.fetch(Config.JOYSTICK_PORT_1_STATUS);
    var statusReader = statusQueue.makeReader();

    // values to store in flatbuffer
    double timestamp = 12345678.0;
    int port = 99;

    float[] axes = new float[Controller.maxNumAxes];
    for (int k = 0; k < axes.length; k++) {
      axes[k] = (float) k / 10.0f;
    }

    boolean[] buttons = new boolean[Controller.maxNumButtons];
    for (int k = 0; k < buttons.length; k++) {
      buttons[k] = (k & 1) == 0;
    }
    int pov = 45;

    final int bufferSizeBytes = 128; // slightly larger than required
    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSizeBytes);

    JoystickStatus.startJoystickStatus(builder);
    JoystickStatus.addTimestamp(builder, timestamp);
    JoystickStatus.addPort(builder, port);    
    JoystickStatus.addAxes(builder, AxisVector.createAxisVector(builder, axes));
    JoystickStatus.addButtons(builder, ButtonVector.createButtonVector(builder, buttons));
    JoystickStatus.addPov(builder, pov);
    int offset = JoystickStatus.endJoystickStatus(builder);

    JoystickStatus.finishJoystickStatusBuffer(builder, offset);
    ByteBuffer bb = builder.dataBuffer();

    statusQueue.write(bb);

    // check that we are pre-allocating enough space for the flatbuffer
    assertEquals(bufferSizeBytes, builder.dataBuffer().capacity()); // increase bufferSizeBytes if this fails

    assertFalse(statusReader.isEmpty());
    assertEquals(1, statusReader.size());
    Optional<ByteBuffer> optStatus = statusReader.readLast();
    assertTrue(statusReader.isEmpty());
    assertTrue(optStatus.isPresent());

    if (optStatus.isPresent()) {
      JoystickStatus status = JoystickStatus.getRootAsJoystickStatus(optStatus.get());
      assertEquals(timestamp, status.timestamp(), eps);
      assertEquals(port, status.port());

      AxisVector axesVector = status.axes();
      for (int k = 0; k < axes.length; k++) {
        assertEquals(axes[k], axesVector.axes(k), eps);
      }

      ButtonVector buttonVector = status.buttons();
      for (int k = 0; k < buttons.length; k++) {
        assertEquals(buttons[k], buttonVector.buttons(k));
      }

      assertEquals(pov, status.pov());
    }
  }

}