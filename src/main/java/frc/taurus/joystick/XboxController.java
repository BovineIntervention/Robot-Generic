package frc.taurus.joystick;

public class XboxController extends Joystick
{
    public final double mDeadband;

    public enum Axis {
        L_STICK_X_AXIS(0), L_STICK_Y_AXIS(1), L_STICK_TRIGGER_AXIS(2), 
        R_STICK_X_AXIS(4), R_STICK_Y_AXIS(5), R_STICK_TRIGGER_AXIS(3);  

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

    public XboxController(int port) {
        this(port, 0.0);
    }

    public XboxController(int port, double deadband) {
        super(port);
        mDeadband = deadband;
    }

    public double getAxis(Axis axis) {
         // invert the y-axis
        boolean invert = (axis == Axis.L_STICK_Y_AXIS) || (axis == Axis.R_STICK_Y_AXIS);
        double value = (invert ? -1 : 1) * this.wpilibJoystick.getRawAxis(axis.id);
        return handleDeadband(value, mDeadband);
    }

    public boolean getButton(Button button) {
        return wpilibJoystick.getRawButton(button.id);
    }

    public int getDPad() {
        return wpilibJoystick.getPOV();    // returns -1 if not pressed, 0, 90, 180, 270 if pressed
    }

    public void setRumble(boolean on) {
        wpilibJoystick.setRumble(edu.wpi.first.wpilibj.GenericHID.RumbleType.kRightRumble, on ? 1 : 0);
    }

    private double handleDeadband(double value, double deadband) {
        return (Math.abs(value) > Math.abs(deadband)) ? value : 0;
    }
}