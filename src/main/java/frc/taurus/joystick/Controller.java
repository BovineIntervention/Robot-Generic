package frc.taurus.joystick;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A wrapper for WPILib's Joystick class that enables logging and testing via mocking
 */

public class Controller 
{
    public final Joystick wpilibJoystick;

    public Controller(Joystick joystick) {
        wpilibJoystick = joystick;
    }

    public void update() {
            log();
    }

    public double getAxis(int axisId) {
        return wpilibJoystick.getRawAxis(axisId);
    }

    public boolean getButton(int buttonId) {
        return wpilibJoystick.getRawButton(buttonId);
    }

    public boolean getButtonPressed(int buttonId) {
        return wpilibJoystick.getRawButtonPressed(buttonId);
    }

    public boolean getButtonReleased(int buttonId) {
        return wpilibJoystick.getRawButtonReleased(buttonId);
    }

    /**
     * Get the angle in degrees of a POV on the HID.
    *
    * <p>The POV angles start at 0 in the up direction, and increase clockwise (eg right is 90,
    * upper-left is 315).
    *
    * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
    */
    public int getPOV() {
        return wpilibJoystick.getPOV(); 
    }

    public void setRumble(boolean on) {
        wpilibJoystick.setRumble(RumbleType.kRightRumble, on ? 1 : 0);
    }

    public static double applyDeadband(double value, double deadband) {
        return (Math.abs(value) > Math.abs(deadband)) ? value : 0;
    }  
    
    public void log()
    {

    }
};
