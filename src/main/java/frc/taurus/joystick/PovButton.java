package frc.taurus.joystick;

/**
 * To use the D-Pad (POV) as up to 8 distinct buttons
 */
public class PovButton extends Button
{
    // minimum and maximum values that would result in a button press
    double mMin;
    double mMax;

    public PovButton(final Joystick joystick, final int id, 
                     final double min, final double max) {
        super(joystick, id);    // store joystick and button ID
        mMin = min;
        mMax = max;
    }

    public void update() {
        double value = mJoystick.wpilibJoystick.getPOV(mId);
        boolean triggered = (value > mMin) && (value < mMax);
        update( triggered );
    }
}