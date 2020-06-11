package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.ControllerConstants.ControllerConfig1;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.drivetrain.generated.DrivetrainGoal;
import frc.taurus.drivetrain.generated.GoalType;
import frc.taurus.drivetrain.generated.TeleopGoal;
import frc.taurus.joystick.SteeringMethods;
import frc.taurus.joystick.XboxController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsXboxExample {

  final ArrayList<Integer> portList;
  final XboxController driverController;
  final SteeringMethods steeringMethods;
  final MessageQueue<ByteBuffer> drivetrainGoalQueue;
  SteeringMethods.LeftRightMotor lrMotor;

  public DriverControlsXboxExample(ChannelManager channelManager) {

    portList = new ArrayList<>();
    int port = ControllerConfig1.kDriveControllerPort;
    portList.add(port);   

    driverController = new XboxController(channelManager.fetchJoystickStatusQueue(port),
                                          channelManager.fetchJoystickGoalQueue(port));  
    steeringMethods = new SteeringMethods(ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity,
                                          ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity);
    drivetrainGoalQueue = channelManager.fetch(Config.DRIVETRAIN_GOAL);

  }

  public ArrayList<Integer> getControllerPorts() {
    return portList;
  }

  public void update() {
    driverController.update();  // read in all raw axes & buttons
    double throttle = driverController.getAxis(XboxController.Axis.L_STICK_Y_AXIS);
    double steering = driverController.getAxis(XboxController.Axis.L_STICK_X_AXIS);
    lrMotor = steeringMethods.arcadeDrive(throttle, steering);
    writeDrivetrainGoalMessage();
  }

  public double getLeft()       { return lrMotor.left; }
  public double getRight()      { return lrMotor.right; }
  public boolean getQuickTurn() { return false; }
  public boolean getLowGear()   { return false; }
  // don't add controls here for anything not related to simply moving the drivetrain around the field
  // most controls (even if they are mapped to the driver's joystick) should be in SuperstructureControls 

  public XboxController getDriverController() { return driverController; }

  public void setRumble(RumbleType rumbleType, double rumbleValue) { 
    driverController.setRumble(rumbleType, rumbleValue); 
  }

  
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
    ByteBuffer bb = builder.dataBuffer();
    bufferSize = Math.max(bufferSize, bb.remaining());

    drivetrainGoalQueue.write(bb);
  }
}
