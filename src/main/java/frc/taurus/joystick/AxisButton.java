package frc.taurus.joystick;

/**
 * Use when an axis is used as a button
 */
public class AxisButton
{
    // minimum and maximum values that would result in a button press
    private Controller mController;
    int mId;                        // id of Axis to use as button
    double mThreshold;              // threshold at which to trigger
    boolean mCurrent = false;
    boolean mLast = false;

    public AxisButton(final Controller controller, final int id, double threshold) {
        mController = controller;
        mId = id;
        mThreshold = threshold;
    }

    public void update() {
        mLast = mCurrent;
        double value = mController.getAxis(mId);
        mCurrent = (Math.signum(value) == Math.signum(mThreshold)) &&
                   (value >= mThreshold);
    }

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