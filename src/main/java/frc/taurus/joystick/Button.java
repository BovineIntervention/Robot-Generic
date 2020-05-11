package frc.taurus.joystick;

public class Button 
{
    public final Joystick mJoystick;
    public final int mId;

    private boolean mLast = false;
    private boolean mCurrent = false;

    public Button(final Joystick joystick, final int id) {
        mJoystick = joystick;
        mId = id;
    }

    public void update() {
        update( mJoystick.wpilibJoystick.getRawButton(mId) );
    }

    public void update(final boolean value) {
        mLast = mCurrent;
        mCurrent = value;
    }

    public boolean onPress() { return mCurrent && !mLast; };
    public boolean onRelease() { return !mCurrent && mLast; };
    public boolean isPressed() { return mCurrent; }

};
