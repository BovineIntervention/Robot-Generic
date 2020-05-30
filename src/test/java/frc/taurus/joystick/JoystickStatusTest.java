package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import frc.taurus.messages.MessageQueueManager;

public class JoystickStatusTest {

    double eps = 1e-6;  // using floats for joystick axes


    @Test 
    public void writeSingleMessageTest() {

        var statusQueue = MessageQueueManager.getInstance().driveJoystickStatusQueue;
        var statusReader = statusQueue.makeMessageReader();

        // values to store in flatbuffer
        double timestamp = 12345678.0;

        float[] axes = new float[6];
        for (int k=0; k<6; k++) {
            axes[k] = (float)k / 10.0f;
        }

        boolean[] buttons = new boolean[16];
        for (int k=0; k<16; k++) {
            buttons[k] = (k & 1) == 0;
        }
        int pov = 45;


        final int bufferSizeBytes = 128;   // slightly larger than required
        FlatBufferBuilder builder = new FlatBufferBuilder(bufferSizeBytes);        

        JoystickStatus.startJoystickStatus(builder);
        JoystickStatus.addTimestamp(builder, timestamp);
        JoystickStatus.addAxes(builder, AxisVector.createAxisVector(builder, axes));
        JoystickStatus.addButtons(builder, ButtonVector.createButtonVector(builder, buttons));
        JoystickStatus.addPov(builder, pov);
        int offset = JoystickStatus.endJoystickStatus(builder);

        statusQueue.writeMessage(builder, offset);



        // check that we are pre-allocating enough space for the flatbuffer
        assertEquals(bufferSizeBytes, builder.dataBuffer().capacity());    // increase bufferSizeBytes if this fails

        assertFalse(statusReader.isEmpty());
        assertEquals(1, statusReader.size());
        Optional<JoystickStatus> optStatus = statusReader.readLastMessage();
        assertTrue(statusReader.isEmpty());
        assertTrue(optStatus.isPresent());

        if (optStatus.isPresent())
        {
            JoystickStatus status = optStatus.get();
            assertEquals(timestamp, status.timestamp(), eps);

            AxisVector axesVector = status.axes();
            for (int k=0; k<6; k++) {
                assertEquals(axes[k], axesVector.axes(k), eps);
            }

            ButtonVector buttonVector = status.buttons();
            for (int k=0; k<16; k++) {
                assertEquals(buttons[k], buttonVector.buttons(k));
            }            
            
            assertEquals(pov, status.pov());
        }
    }    


}