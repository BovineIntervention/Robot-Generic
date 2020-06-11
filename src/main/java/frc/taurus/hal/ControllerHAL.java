package frc.taurus.hal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import frc.taurus.config.ChannelManager;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.generated.AxisVector;
import frc.taurus.joystick.generated.ButtonVector;
import frc.taurus.joystick.generated.JoystickGoal;
import frc.taurus.joystick.generated.JoystickStatus;
import frc.taurus.joystick.generated.RumbleSide;
import frc.taurus.messages.MessageQueue;

public class ControllerHAL implements IHAL {

  HashSet<Integer> registeredPortList = new HashSet<>();
  ArrayList<JoystickStatusWriter> joystickList = new ArrayList<>();
  final ChannelManager channelManager;

  /**
   * ControllerHAL generates JoystickStatus messages for all joystick registered with it,
   * and activates joystick rumble based on JoystickGoal messages
   * @param channelManager
   */
  public ControllerHAL(ChannelManager channelManager) {
    this.channelManager = channelManager;
  }

  public int size() { return joystickList.size(); }

  /**
   * Register all physical controllers used by driver and operator control schemes
   * Usage:
   *   controllerHAL.register( driverControls.getControllerPorts() );
   *   controllerHAL.register( superstructureControls.getControllerPorts() );
   * @param controllers ArrayList of all controller ports used by user control scheme
   */
  public void register(ArrayList<Integer> ports) {
    for (var port : ports) {
      register(port.intValue());
    }
  }
  
  public void register(int port) {
    if (!registeredPortList.contains(port)) {
      joystickList.add(new JoystickStatusWriter(channelManager, port));
    }
  }


  public void zeroSensors() {}

  /**
   * Update all Controllers that were registered
   * (Update all axis and button values, then create JoystickStatus message)
   */
  public void readSensors() {
    for (var joystick : joystickList) {
      joystick.readSensors();
    }
  }

  public void writeActuators() {
    for (var joystick : joystickList) {
      joystick.writeActuators();
    }    
  }

  public void stop() {
    for (var joystick : joystickList) {
      joystick.stop();
    }     
  }



  public class JoystickStatusWriter {
    int port;
    Joystick joystick;
    MessageQueue<ByteBuffer> statusQueue;
    MessageQueue<ByteBuffer>.QueueReader rumbleReader;
    float rumbleLast[] = {0.0f, 0.0f};

    JoystickStatusWriter(ChannelManager channelManager, int port) {
      this.port = port;
      this.joystick = new Joystick(port);
      this.statusQueue = channelManager.fetchJoystickStatusQueue(port);
      this.rumbleReader = channelManager.fetchJoystickGoalQueue(port).makeReader();
    }

    int bufferSize = 0;
    public void readSensors() {
  
      float[] axes = new float[Controller.maxNumAxes];
      for (int k = 0; k < axes.length; k++) {
        axes[k] = (float)joystick.getRawAxis(k);   // axis IDs are base 0
      }
  
      boolean[] buttons = new boolean[Controller.maxNumButtons];
      for (int k = 0; k < buttons.length; k++) {
        buttons[k] = joystick.getRawButton(k + 1);  // button IDs are base 1, not base 0
      }
  
      FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);
      JoystickStatus.startJoystickStatus(builder);
      JoystickStatus.addTimestamp(builder, Timer.getFPGATimestamp());
      JoystickStatus.addPort(builder, port);
      JoystickStatus.addAxes(builder, AxisVector.createAxisVector(builder, axes));
      JoystickStatus.addButtons(builder, ButtonVector.createButtonVector(builder, buttons));
      JoystickStatus.addPov(builder, joystick.getPOV(0));
      int offset = JoystickStatus.endJoystickStatus(builder);
  
      JoystickStatus.finishJoystickStatusBuffer(builder, offset);
      ByteBuffer bb = builder.dataBuffer();
      bufferSize = Math.max(bufferSize, bb.remaining()); // correct buffer size for next time
  
      statusQueue.write(bb);
    }   
    
    public void writeActuators() {   
      Optional<ByteBuffer> obb = rumbleReader.readLast();
      if (obb.isPresent()) {
        JoystickGoal joystickGoal = JoystickGoal.getRootAsJoystickGoal(obb.get());
        byte rumbleSide = joystickGoal.rumbleSide();
        RumbleType rumbleType = (rumbleSide==RumbleSide.LEFT_RUMBLE) ? RumbleType.kLeftRumble : RumbleType.kRightRumble;
        float rumbleValue = joystickGoal.rumbleValue();
        if (rumbleValue != rumbleLast[rumbleSide]) {
          rumbleLast[rumbleSide] = rumbleValue;
          joystick.setRumble(rumbleType, rumbleValue);
        }
      }
    }

    public void stop() {  
      rumbleLast[0] = 0.0f; 
      rumbleLast[1] = 0.0f; 
      joystick.setRumble(RumbleType.kLeftRumble, 0.0);
      joystick.setRumble(RumbleType.kRightRumble, 0.0);
    }    
  }
 
}