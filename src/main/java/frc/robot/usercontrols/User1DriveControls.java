package frc.robot.usercontrols;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.taurus.joystick.XboxController;

/**
 * This file defines the user controls / button mappings
 */

public class User1DriveControls implements IDriveControls {

    private static User1DriveControls mInstance = null;
    public static User1DriveControls getInstance() {
        if (mInstance == null) {
            mInstance = new User1DriveControls();
        }

        return mInstance;
    }   
    
    private final XboxController mXboxController;
    private final double kDeadband = 0.05;

    private User1DriveControls() {
        Joystick joystick = new Joystick(Constants.ControllerConstants.kDriveControllerPort);
        mXboxController = new XboxController(joystick, kDeadband);
    }
    
    public void update() {
        // call update for all AxisButtons and PovButtons
    }

    public double getThrottle() { return mXboxController.getAxis(XboxController.Axis.L_STICK_Y_AXIS); };
    public double getSteering() { return mXboxController.getAxis(XboxController.Axis.L_STICK_X_AXIS); };
    public boolean getQuickTurn() { return false; };
    public boolean getLowGear() { return false; };


}