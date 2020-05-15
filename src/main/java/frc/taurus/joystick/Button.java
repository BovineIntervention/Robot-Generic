package frc.taurus.joystick;

/**
 * Use when an axis is used as a button
 */
public class Button
{
    // minimum and maximum values that would result in a button press
    protected Controller mController;         // controller with this button
    protected int mId;                        // id of button on controller
    private boolean mCurrent = false;
    private boolean mLast = false;

    public Button(final Controller controller, final int id) {
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