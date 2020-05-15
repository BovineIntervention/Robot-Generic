package frc.taurus.joystick;

/**
 * Use when an axis is used as a button
 */
public class AxisButton extends Button
{
    double mThreshold;              // threshold at which to trigger

    public AxisButton(final Controller controller, final int id, double threshold) {
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