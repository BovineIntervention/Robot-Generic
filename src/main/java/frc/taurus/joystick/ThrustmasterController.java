package frc.taurus.joystick;

import java.util.Optional;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.messages.MessageQueue;

public class ThrustmasterController extends Controller
{
    public final double mDeadband;

    public enum Axis {
        X_AXIS(0), Y_AXIS(1), Z_ROTATE_AXIS(2), SLIDER_AXIS(3);  

        public final int id;
        Axis(int id) {
            this.id = id;
        }
    }

    public enum Button {
        TRIGGER(1), BOTTOM_THUMB(2), LEFT_THUMB(3), RIGHT_THUMB(4), 
        // counting from the inner (or thumb) side
        TOP1(5), TOP2(6), TOP3(7), BOTTOM3(8), BOTTOM2(9), BOTTOM1(10),
        TOP6(11), TOP5(12), TOP4(13), BOTTOM4(14), BOTTOM5(15), BOTTOM6(16);

        public final int id;
        Button(int id) {
            this.id = id;
        }
    }

    public ThrustmasterController(Joystick joystick, double deadband, 
                                  Optional<MessageQueue<JoystickStatus>> joystickStausQueue,
                                  Optional<MessageQueue<JoystickGoal>> joystickGoalQueue) {
        super(joystick, joystickStausQueue, joystickGoalQueue);
        mDeadband = deadband;

        // add all enumerated buttons to button list
        for (Button button : Button.values()) {
            addButton(button.id);
        }
    }

    public ThrustmasterController(Joystick joystick, double deadband, 
                          Optional<MessageQueue<JoystickStatus>> joystickStausQueue) {
        this(joystick, deadband, joystickStausQueue, Optional.empty());
    }

    public ThrustmasterController(Joystick joystick, double deadband) {
        this(joystick, deadband, Optional.empty(), Optional.empty());
    }


    public double getAxis(Axis axis) {
         // invert the y-axis
        boolean invert = (axis == Axis.X_AXIS);
        double value = (invert ? -1 : 1) * this.wpilibJoystick.getRawAxis(axis.id);
        return applyDeadband(value, mDeadband);
    }

    // setRumble(boolean) available from base class
}