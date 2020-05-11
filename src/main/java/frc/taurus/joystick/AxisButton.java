package frc.taurus.joystick;

/**
 * Use when an axis is used as a button
 */

public class AxisButton extends Button
{
    double mThreshold;

    public AxisButton(final Joystick joystick, final int id, double threshold) {
        super(joystick, id);    // store joystick and button ID
        mThreshold = threshold;
    }

    public void update() {
        double value = mJoystick.wpilibJoystick.getRawAxis(mId);
        boolean triggered = (Math.signum(value) == Math.signum(mThreshold)) &&
                            (value >= mThreshold);
        update( triggered );
    }
}