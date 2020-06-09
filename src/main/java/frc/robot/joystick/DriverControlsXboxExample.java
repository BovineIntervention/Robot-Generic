package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.ControllerConstants.ControllerConfig1;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.drivetrain.generated.DrivetrainGoal;
import frc.taurus.drivetrain.generated.GoalType;
import frc.taurus.drivetrain.generated.TeleopGoal;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.SteeringMethods;
import frc.taurus.joystick.XboxController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsXboxExample {

  // define the physical controllers that will be used
  final XboxController driverController;
  final SteeringMethods steeringMethods;
  final MessageQueue<ByteBuffer> goalQueue;
  SteeringMethods.LeftRightMotor lrMotor;

  public DriverControlsXboxExample(ChannelManager channelManager) {

    Joystick joystick = new Joystick(ControllerConfig1.kDriveControllerPort);  
    driverController = new XboxController(joystick, channelManager.fetch(Config.DRIVER_JOYSTICK_STATUS), channelManager.fetch(Config.DRIVER_JOYSTICK_GOAL));  
    steeringMethods = new SteeringMethods(ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity,
                                          ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity);
    goalQueue = channelManager.fetch(Config.DRIVETRAIN_GOAL);
  }

  public ArrayList<Controller> getControllersList() {
    ArrayList<Controller> controllersList = new ArrayList<Controller>();
    controllersList.add(driverController);
    return controllersList;
  }

  public void update() {
    double throttle = driverController.getAxis(XboxController.Axis.L_STICK_Y_AXIS);
    double steering = driverController.getAxis(XboxController.Axis.L_STICK_X_AXIS);
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
