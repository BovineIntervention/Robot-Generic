package frc.taurus.joystick;

import edu.wpi.first.wpilibj.Joystick;

public class ButtonBoardController extends Controller
{
    double mDeadband;
    
    public enum Axis {
        L_STICK_X_AXIS(0), L_STICK_Y_AXIS(1), L_STICK_TRIGGER_AXIS(2), 
        R_STICK_X_AXIS(4), R_STICK_Y_AXIS(5), R_STICK_TRIGGER_AXIS(3);  

        public final int id;
        Axis(int id) {
            this.id = id;
        }
    }

    public enum Button {
        A(1), B(2), X(3), Y(4), LB(5), RB(6), SHARE(7), OPTIONS(8), SL(9), SR(10);

        public final int id;
        Button(int id) {
            this.id = id;
        }
    }

    public ButtonBoardController(Joystick joystick, double deadband) {
        super(joystick);
        mDeadband = deadband;

        // add all enumerated buttons to button list
        for (Button button : Button.values()) {
            addButton(button.id);
        }        
    }

    // convenience constructor when deadband is not specified
    public ButtonBoardController(Joystick joystick) {
        this(joystick, 0.0);
    }

    public double getAxis(Axis axis) {
        // invert the y-axis
       boolean invert = (axis == Axis.L_STICK_Y_AXIS) || (axis == Axis.R_STICK_Y_AXIS);
       double value = (invert ? -1 : 1) * getAxis(axis.id);
       return applyDeadband(value, mDeadband);
    }    

    public boolean getButton(Button button) {
        return getButton(button.id);
    }

    public boolean getButtonPressed(Button button) {
        return getButtonPressed(button.id);
    }

    public boolean getButtonReleased(Button button) {
        return getButtonReleased(button.id);
    }


 }