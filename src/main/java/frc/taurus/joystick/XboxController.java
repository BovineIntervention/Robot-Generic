package frc.taurus.joystick;

import edu.wpi.first.wpilibj.Joystick;

public class XboxController extends Controller
{
    public final double mDeadband;

    public enum Axis {
        L_STICK_X_AXIS(0), L_STICK_Y_AXIS(1), L_TRIGGER_AXIS(2), 
        R_STICK_X_AXIS(4), R_STICK_Y_AXIS(5), R_TRIGGER_AXIS(3);  

        public final int id;
        Axis(int id) {
            this.id = id;
        }
    }

    public enum Button {
        A(1), B(2), X(3), Y(4), L_BUMPER(5), R_BUMPER(6), BACK(7), START(8), L_STICK(9), R_STICK(10);

        public final int id;
        Button(int id) {
            this.id = id;
        }
    }

    public XboxController(Joystick joystick) {
        this(joystick, 0.0);
    }

    public XboxController(Joystick joystick, double deadband) {
        super(joystick);
        mDeadband = deadband;

        // add all enumerated buttons to button list
        for (Button button : Button.values()) {
            addButton(button.id);
        }
    }

    public double getAxis(Axis axis) {
         // invert the y-axis
        boolean invert = (axis == Axis.L_STICK_Y_AXIS) || (axis == Axis.R_STICK_Y_AXIS);
        double value = (invert ? -1 : 1) * getAxis(axis.id);
        return applyDeadband(value, mDeadband);
    }

    public int getPOV() {
        return super.getPOV(0);
    }

    // setRumble(boolean) available from base class
}