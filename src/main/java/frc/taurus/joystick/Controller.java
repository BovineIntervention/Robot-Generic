package frc.taurus.joystick;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A wrapper for WPILib's Joystick class that enables logging and testing via mocking
 */

public class Controller 
{
    public final Joystick wpilibJoystick;
    static ArrayList<Button> buttons;

    public Controller(Joystick joystick) {
        wpilibJoystick = joystick;
        buttons = new ArrayList<>();
    }

    public Button addButton(int buttonId) {
        Button button = new Button(this, buttonId);
        buttons.add(button);
        return button;
    }

    public AxisButton addAxisButton(int axisId, double threshold) {
        AxisButton axisButton = new AxisButton(this, axisId, threshold);
        buttons.add(axisButton);
        return axisButton;
    }

    public PovButton addPovButton(int povId, int minRange, int maxRange) {
        PovButton povButton = new PovButton(this, povId, minRange, maxRange);
        buttons.add(povButton);
        return povButton;
    }

    public void update() {
        for (var button : buttons) {
            button.update();
        }
        log();
        // TODO: check if controller rumble has been requested
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
    public int getPOV(int povId) {
        return wpilibJoystick.getPOV(povId); 
    }

    public void setRumble(boolean on) {
        wpilibJoystick.setRumble(RumbleType.kRightRumble, on ? 1 : 0);
    }

    public static double applyDeadband(double value, double deadband) {
        return (Math.abs(value) > Math.abs(deadband)) ? value : 0;
    }  
    
    public void log()
    {
        // TODO: write a controller status message, including axis and buttons
    }
};
