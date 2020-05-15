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
        addButton(button);  // add to button list
        return button;
    }

    public AxisButton addAxisButton(int axisId, double threshold) {
        AxisButton axisButton = new AxisButton(this, axisId, threshold);
        addButton(axisButton);  // add to button list
        return axisButton;
    }

    public PovButton addPovButton(int povId, int minRange, int maxRange) {
        PovButton povButton = new PovButton(this, povId, minRange, maxRange);
        addButton(povButton);  // add to button list
        return povButton;
    }

    public void addButton(Button button) {
        if (!buttons.contains(button)) {
            buttons.add(button);
        }
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

    private boolean getButton(int buttonId) {
        // called only from Button.update();
        return wpilibJoystick.getRawButton(buttonId);
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





    public class Button
    {
        protected Controller mController;         // controller with this button
        protected int mId;                        // id of button on controller
        private boolean mCurrent = false;
        private boolean mLast = false;
    
        private Button(final Controller controller, final int id) {
            mController = controller;
            mId = id;
        }
    
        public void update() {
            update( mController.getButton(mId) );
        }
    
        public void update(boolean val) {
            mLast = mCurrent;
            mCurrent = val;
        }
    
        // WPILib defines these functions, but with this Button class 
        // we can extend this to PovButtons and AxisButtons
    
        public boolean getButton() {
            return mCurrent;
        }
    
        public boolean getButtonPressed() {
            return mCurrent && !mLast;
        }
    
        public boolean getButtonReleased() {
            return !mCurrent && mLast;
        }        
    }  
    



    /**
     * Use when an axis is used as a button
     */
    public class AxisButton extends Button
    {
        double mThreshold;              // threshold at which to trigger

        private AxisButton(final Controller controller, final int id, double threshold) {
            super(controller, id);
            mThreshold = threshold;
        }

        public void update() {
            double value = mController.getAxis(mId);
            boolean pressed = (Math.signum(value) == Math.signum(mThreshold)) &&
                            (value >= mThreshold);
            update(pressed);
        }
    }    




    /**
     * To use the D-Pad (POV) as up to 8 distinct buttons
     */
    public class PovButton extends Button
    {
        // minimum and maximum values that would result in a button press
        int mMin;
        int mMax;

        private PovButton(final Controller controller, final int id,
                        final int min, final int max) {
            super(controller, id);
            mMin = min;
            mMax = max;
        }

        public void update() {
            int value = mController.getPOV(mId);
            boolean pressed = false;
            // if POV is not pressed, it returns -1
            if (value >= 0) {
                // the negative value check lets us specify a 
                // range of -45 to +45 for north, for example
                int negValue = value - 360;
                pressed = ((value >= mMin) && (value <= mMax)) || 
                        ((negValue >= mMin) && (negValue <= mMax));
            }
            update(pressed);
        }
    
    }
};
