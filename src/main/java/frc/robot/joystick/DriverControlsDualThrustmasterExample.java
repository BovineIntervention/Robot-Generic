package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.ControllerConstants.ControllerConfig2;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.drivetrain.generated.DrivetrainGoal;
import frc.taurus.drivetrain.generated.GoalType;
import frc.taurus.drivetrain.generated.TeleopGoal;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.SteeringMethods;
import frc.taurus.joystick.ThrustmasterController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsDualThrustmasterExample {

  // define the physical controllers that will be used
  final ThrustmasterController leftController;
  final ThrustmasterController rightController;
  final SteeringMethods steeringMethods;
  final MessageQueue<ByteBuffer> goalQueue;
  SteeringMethods.LeftRightMotor lrMotor;


    public DriverControlsDualThrustmasterExample(ChannelManager channelManager) {
        // use Controller.addController() to add controllers to this control method
        Joystick  leftJoystick = new Joystick(ControllerConfig2.kDriverLeftControllerPort);  
        Joystick rightJoystick = new Joystick(ControllerConfig2.kDriverRightControllerPort);  
        leftController  = new ThrustmasterController( leftJoystick, channelManager.fetch(Config.DRIVER_JOYSTICK_STATUS), channelManager.fetch(Config.DRIVER_JOYSTICK_GOAL));  // write both left and right joystick settings to the same queues 
        rightController = new ThrustmasterController(rightJoystick, channelManager.fetch(Config.DRIVER_JOYSTICK_STATUS), channelManager.fetch(Config.DRIVER_JOYSTICK_GOAL));  // (which is only used for logging) 
        steeringMethods = new SteeringMethods(ControllerConfig2.kDriveDeadband, ControllerConfig2.kDriveNonLinearity,
                                              ControllerConfig2.kDriveDeadband, ControllerConfig2.kDriveNonLinearity);   
        goalQueue = channelManager.fetch(Config.DRIVETRAIN_GOAL);                                                   
    }

    public ArrayList<Controller> getControllersList() {
      ArrayList<Controller> controllersList = new ArrayList<Controller>();
      controllersList.add(leftController);
      controllersList.add(rightController);
      return controllersList;
    }  

    public void update() {
      double throttle =  leftController.getAxis(ThrustmasterController.Axis.Y_AXIS);
      double steering = rightController.getAxis(ThrustmasterController.Axis.X_AXIS);
      lrMotor = steeringMethods.arcadeDrive(throttle, steering);
      writeDrivetrainGoalMessage();
    }    

    public double getLeft() { return lrMotor.left; };
    public double getRight() { return lrMotor.right; };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };
    // don't add controls here for anything not related to simply moving the drivetrain around the field
    // most controls (even if they are mapped to the driver's joystick) should be in SuperstructureControls 

    
    int bufferSize = 0;
  
    public void writeDrivetrainGoalMessage() {
      // send a DrivetrainGoal message
      float lMotor = (float)getLeft();
      float rMotor = (float)getRight();
      boolean quickTurn = getQuickTurn();
      boolean lowGear = getLowGear();    
  
      FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);
      int teleopGoalOffset = TeleopGoal.createTeleopGoal(builder, lMotor, rMotor);
      double timestamp = Timer.getFPGATimestamp();
      int offset = DrivetrainGoal.createDrivetrainGoal(builder, timestamp, GoalType.TeleopGoal, teleopGoalOffset, !lowGear, quickTurn);
      builder.finish(offset);
  
      goalQueue.write(builder.dataBuffer()); 
    }    
}