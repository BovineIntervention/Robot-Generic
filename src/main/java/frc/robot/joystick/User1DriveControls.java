package frc.robot.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.taurus.joystick.XboxController;
import frc.taurus.messages.MessageQueue;

/**
 * This file defines the user controls / button mappings
 */

public class User1DriveControls extends ControlsBase implements IDriveControls {

    // define the physical controllers that will be used
    private final XboxController mDriveController;

    public User1DriveControls(Optional<MessageQueue<ByteBuffer>> statusQueue, Optional<MessageQueue<ByteBuffer>> goalQueue) {
        // use ControlsBase.addController() to add controllers to this control method
        Joystick joystick = new Joystick(Constants.ControllerConstants.kDriveControllerPort);  
        mDriveController = (XboxController)ControlsBase.addController(new XboxController(joystick, Constants.ControllerConstants.kDriveDeadband, statusQueue, goalQueue));      
    }

    public double getThrottle() { return mDriveController.getAxis(XboxController.Axis.L_STICK_Y_AXIS); };
    public double getSteering() { return mDriveController.getAxis(XboxController.Axis.L_STICK_X_AXIS); };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };

}