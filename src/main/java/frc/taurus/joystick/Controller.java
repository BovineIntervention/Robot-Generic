package frc.taurus.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import frc.taurus.messages.JoystickGoal;
import frc.taurus.messages.JoystickStatus;
import frc.taurus.messages.MessageQueue;

/**
 * A wrapper for WPILib's Joystick class that enables logging and testing via mocking
 */

public class Controller 
{
    public final Joystick wpilibJoystick;
    static ArrayList<Button> buttons;
    public final Optional<MessageQueue<JoystickStatus>> mStatusQueue;   // Optional: if a status queue is not given in the constructor, don't send JoystickStatus
    public final Optional<MessageQueue<JoystickGoal>> mRumbleQueue;   // Optional: if a rumble queue is not given in the constructur, don't check for rumble commands
    public MessageQueue<JoystickGoal>.QueueReader mRumbleReader;

    public Controller(final Joystick joystick) {
        this(joystick, Optional.empty(), Optional.empty());
    }

    public Controller(final Joystick joystick, Optional<MessageQueue<JoystickStatus>> statusQueue) {
        this(joystick, statusQueue, Optional.empty());
    }

    public Controller(final Joystick joystick, Optional<MessageQueue<JoystickStatus>> statusQueue, Optional<MessageQueue<JoystickGoal>> rumbleQueue) {
        wpilibJoystick = joystick;
        buttons = new ArrayList<>();
        mStatusQueue = statusQueue;
        mRumbleQueue = rumbleQueue;
        if (mRumbleQueue.isPresent())
            mRumbleReader = rumbleQueue.get().makeReader();
    }

    public Button addButton(int buttonId) {
        Button button = new Button(this, buttonId);
        addButton(button);  // add to button list
        return button;
    }

    public AxisButton addAxisButton(int axisId, double threshold) {
        AxisButton axisButton = new AxisButton(this, axisId, threshold);
        addButton(axisButton);  // add to button list
        return axisButton;
    }

    public PovButton addPovButton(int povId, int minRange, int maxRange) {
        PovButton povButton = new PovButton(this, povId, minRange, maxRange);
        addButton(povButton);  // add to button list
        return povButton;
    }

    public void addButton(Button button) {
        /**
         * All buttons should be added to the buttons list as they are constructed
         */
        if (!buttons.contains(button)) {
            buttons.add(button);
        }
    }

    public void update() {
        for (var button : buttons) {
            button.update();
        }

        log();

        if (mRumbleQueue.isPresent()) {
            Optional<ByteBuffer> obb = mRumbleReader.readLast();
            if (obb.isPresent()) {
                JoystickGoal joystickGoal = JoystickGoal.getRootAsJoystickGoal(obb.get());
                setRumble(joystickGoal.rumble());
            }
        }
    }

    public double getAxis(int axisId) {
        return wpilibJoystick.getRawAxis(axisId);
    }

    private boolean getButton(int buttonId) {
        // called only from Button.update();
        return wpilibJoystick.getRawButton(buttonId);
    }


    /**
     * Get the angle in degrees of a POV on the HID.
    *
    * <p>The POV angles start at 0 in the up direction, and increase clockwise (eg right is 90,
    * upper-left is 315).
    *
    * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
    */
    public int getPOV(int povId) {
        return wpilibJoystick.getPOV(povId); 
    }

    public void setRumble(boolean on) {
        wpilibJoystick.setRumble(RumbleType.kRightRumble, on ? 1 : 0);
    }

    public static double applyDeadband(double value, double deadband) {
        return (Math.abs(value) > Math.abs(deadband)) ? value : 0;
    }  
    
    public void log()
    {
        if (mStatusQueue.isPresent()) {
            final int bufferSizeBytes = 128;   // slightly larger than required
            FlatBufferBuilder builder = new FlatBufferBuilder(bufferSizeBytes);        
            double timestamp = Timer.getFPGATimestamp();

            int offset = JoystickStatus.createJoystickStatus(builder,
                timestamp,
                (float)getAxis(0),
                (float)getAxis(1),
                (float)getAxis(2),
                (float)getAxis(3),
                (float)getAxis(4),
                (float)getAxis(5),
                getButton(1),
                getButton(2),
                getButton(3),
                getButton(4),
                getButton(5),
                getButton(6),
                getButton(7),
                getButton(8),
                getButton(9),
                getButton(10),
                getButton(11),
                getButton(12),
                getButton(13),
                getButton(14),
                getButton(15),
                getButton(16),
                getPOV(0));
            mStatusQueue.get().writeMessage(builder, offset);
        }
    }





    public class Button
    {
        protected Controller mController;         // controller with this button
        protected int mId;                        // id of button on controller
        private boolean mCurrent = false;
        private boolean mLast = false;
    
        /**
         * Button has a private constructor so that it can only be created through 
         * Controller.addButton()
         */        
        private Button(final Controller controller, final int id) {
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
    



    /**
     * Use when an axis is used as a button
     */
    public class AxisButton extends Button
    {
        double mThreshold;              // threshold at which to trigger

        /**
         * AxisButton has a private constructor so that it can only be created through 
         * Controller.addAxisButton()
         */  
        private AxisButton(final Controller controller, final int id, double threshold) {
            super(controller, id);
            mThreshold = threshold;
        }

        public void update() {
            double value = mController.getAxis(mId);
            boolean pressed = (Math.signum(value) == Math.signum(mThreshold)) &&
                            (value >= mThreshold);
            update(pressed);
        }
    }    




    /**
     * To use the D-Pad (POV) as up to 8 distinct buttons
     */
    public class PovButton extends Button
    {
        // minimum and maximum values that would result in a button press
        int mMin;
        int mMax;

        /**
         * PovButton has a private constructor so that it can only be created through 
         * Controller.addPovButton()
         */  
        private PovButton(final Controller controller, final int id,
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
};
