package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.robot.Constants.ControllerConstants.ControllerConfig1;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.DriverControlsBase;
import frc.taurus.joystick.SteeringMethods;
import frc.taurus.joystick.XboxController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsXboxExample extends DriverControlsBase {

  // define the physical controllers that will be used
  private final XboxController driverController;
  private final SteeringMethods steeringMethods;

  public DriverControlsXboxExample(MessageQueue<ByteBuffer> statusQueue, MessageQueue<ByteBuffer> goalQueue) {

    Joystick joystick = new Joystick(ControllerConfig1.kDriveControllerPort);  
    driverController = new XboxController(joystick, statusQueue, goalQueue);  
    steeringMethods = new SteeringMethods(ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity,
                                          ControllerConfig1.kDriveDeadband, ControllerConfig1.kDriveNonLinearity);
  }

  public ArrayList<Controller> getControllersList() {
    ArrayList<Controller> controllersList = new ArrayList<Controller>();
    controllersList.add(driverController);
    return controllersList;
  }

  public void update() {
    lrMotor = steeringMethods.arcadeDrive(getThrottle(), getSteering());
    writeDrivetrainGoalMessage();
  }
  
  public double getThrottle() { return driverController.getAxis(XboxController.Axis.L_STICK_Y_AXIS); };
  public double getSteering() { return driverController.getAxis(XboxController.Axis.L_STICK_X_AXIS); };
  public boolean getQuickTurn() { return false; };
  public boolean getLowGear() { return false; };
}
