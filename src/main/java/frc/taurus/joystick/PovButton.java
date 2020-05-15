package frc.taurus.joystick;


/**
 * To use the D-Pad (POV) as up to 8 distinct buttons
 */
public class PovButton extends Button
{
    // minimum and maximum values that would result in a button press
    int mMin;
    int mMax;

    public PovButton(final Controller controller, final int id,
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