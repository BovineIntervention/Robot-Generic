package frc.taurus.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.messages.MessageQueue;

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

    public ButtonBoardController(Joystick joystick, double deadband, 
                          Optional<MessageQueue<ByteBuffer>> joystickStausQueue,
                          Optional<MessageQueue<ByteBuffer>> joystickGoalQueue) {
        super(joystick, joystickStausQueue, joystickGoalQueue);
        mDeadband = deadband;

        // add all enumerated buttons to button list
        for (Button button : Button.values()) {
            addButton(button.id);
        }
    }

    public ButtonBoardController(Joystick joystick, double deadband, 
                          Optional<MessageQueue<ByteBuffer>> joystickStausQueue) {
        this(joystick, deadband, joystickStausQueue, Optional.empty());
    }

    public ButtonBoardController(Joystick joystick, double deadband) {
        this(joystick, deadband, Optional.empty(), Optional.empty());
    }


    public double getAxis(Axis axis) {
        // invert the y-axis
       boolean invert = (axis == Axis.L_STICK_Y_AXIS) || (axis == Axis.R_STICK_Y_AXIS);
       double value = (invert ? -1 : 1) * getAxis(axis.id);
       return applyDeadband(value, mDeadband);
    }    

 }