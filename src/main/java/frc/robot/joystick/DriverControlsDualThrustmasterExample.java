package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.taurus.joystick.ThrustmasterController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class DriverControlsDualThrustmasterExample extends ControlsBase implements IDriverControls {

    // define the physical controllers that will be used
    private final ThrustmasterController leftController;
    private final ThrustmasterController rightController;

    public DriverControlsDualThrustmasterExample(Optional<MessageQueue<ByteBuffer>> statusQueue, Optional<MessageQueue<ByteBuffer>> goalQueue) {
        // use ControlsBase.addController() to add controllers to this control method
        Joystick leftJoystick  = new Joystick(Constants.ControllerConstants.ControllerConfig2.kDriverLeftControllerPort);  
        Joystick rightJoystick = new Joystick(Constants.ControllerConstants.ControllerConfig2.kDriverRightControllerPort);  
        leftController  = (ThrustmasterController)ControlsBase.addController(
                            new ThrustmasterController(leftJoystick, Constants.ControllerConstants.kDriveDeadband, statusQueue, goalQueue));      
        rightController = (ThrustmasterController)ControlsBase.addController(
                            new ThrustmasterController(rightJoystick, Constants.ControllerConstants.kDriveDeadband, statusQueue, goalQueue));      
    }

    public double getThrottle() { return leftController.getAxis(ThrustmasterController.Axis.Y_AXIS); };
    public double getSteering() { return rightController.getAxis(ThrustmasterController.Axis.X_AXIS); };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };
    // don't add controls here for anything not related to simply moving the drivetrain around the field
    // most controls (even if they are mapped to the driver's joystick) should be in SuperstructureControls

}