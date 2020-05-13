package frc.taurus.joystick;

/**
 * To use the D-Pad (POV) as up to 8 distinct buttons
 */
public class PovButton
{
    // minimum and maximum values that would result in a button press
    private Controller mController;
    double mMin;
    double mMax;
    boolean mCurrent = false;
    boolean mLast = false;

    public PovButton(final Controller controller, 
                     final double min, final double max) {
        mController = controller;
        mMin = min;
        mMax = max;
    }

    public void update() {
        mLast = mCurrent;
        double value = mController.getPOV();
        mCurrent = (value >= mMin) && (value <= mMax);
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