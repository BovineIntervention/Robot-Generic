package frc.robot.usercontrols;

import frc.taurus.joystick.XboxController;
import frc.robot.Constants;

/**
 * This file defines the user controls / button mappings
 */

public class UserDriveControls implements IUserDriveControls {

    private static UserDriveControls mInstance = null;
    public static UserDriveControls getInstance() {
        if (mInstance == null) {
            mInstance = new UserDriveControls();
        }

        return mInstance;
    }   
    
    private final XboxController mController;
    private final double kDeadband = 0.05;

    private UserDriveControls() {
        mController = new XboxController(Constants.UserControlConstants.kDriveJoystickPort, kDeadband);
    }
    
    
    public double getThrottle() { return mController.getAxis(XboxController.Axis.L_STICK_Y_AXIS); };
    public double getSteering() { return mController.getAxis(XboxController.Axis.L_STICK_X_AXIS); };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };


}