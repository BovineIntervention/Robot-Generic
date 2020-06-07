package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants.ControllerConstants.ControllerConfig2;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.DriverControlsBase;
import frc.taurus.joystick.SteeringMethods;
import frc.taurus.joystick.ThrustmasterController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsDualThrustmasterExample extends DriverControlsBase {

    // define the physical controllers that will be used
    private final ThrustmasterController leftController;
    private final ThrustmasterController rightController;
    private final SteeringMethods steeringMethods;
    
    public DriverControlsDualThrustmasterExample(MessageQueue<ByteBuffer> joystickStatusQueue, MessageQueue<ByteBuffer> joystickGoalQueue) {
        // use Controller.addController() to add controllers to this control method
        Joystick leftJoystick  = new Joystick(ControllerConfig2.kDriverLeftControllerPort);  
        Joystick rightJoystick = new Joystick(ControllerConfig2.kDriverRightControllerPort);  
        leftController  = new ThrustmasterController( leftJoystick, joystickStatusQueue, joystickGoalQueue);  // write both left and right joystick settings to the same queues 
        rightController = new ThrustmasterController(rightJoystick, joystickStatusQueue, joystickGoalQueue);  // (which is only used for logging) 
        steeringMethods = new SteeringMethods(ControllerConfig2.kDriveDeadband, ControllerConfig2.kDriveNonLinearity,
                                              ControllerConfig2.kDriveDeadband, ControllerConfig2.kDriveNonLinearity);        
    }

    public ArrayList<Controller> getControllersList() {
      ArrayList<Controller> controllersList = new ArrayList<Controller>();
      controllersList.add(leftController);
      controllersList.add(rightController);
      return controllersList;
    }  

    public double getThrottle() { return  leftController.getAxis(ThrustmasterController.Axis.Y_AXIS); };
    public double getSteering() { return rightController.getAxis(ThrustmasterController.Axis.X_AXIS); };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };
    // don't add controls here for anything not related to simply moving the drivetrain around the field
    // most controls (even if they are mapped to the driver's joystick) should be in SuperstructureControls

    public void update() {
      lrMotor = steeringMethods.arcadeDrive(getThrottle(), getSteering());
      writeDrivetrainGoalMessage();
    }    
}