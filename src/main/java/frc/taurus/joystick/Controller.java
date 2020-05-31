package frc.taurus.joystick;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import frc.taurus.joystick.generated.AxisVector;
import frc.taurus.joystick.generated.ButtonVector;
import frc.taurus.joystick.generated.JoystickGoal;
import frc.taurus.joystick.generated.JoystickStatus;
import frc.taurus.messages.MessageQueue;

/**
 * A wrapper for WPILib's Joystick class
 * 
 * This is where we connect our JoystickStatus and JoystickGoal queues
 */

public class Controller {
  static public HashSet<Joystick> joystickList = new HashSet<Joystick>();   // HashSets do not allow duplicate entries
  static public ArrayList<Controller> controllerList = new ArrayList<Controller>();

  public final Joystick wpilibJoystick;
  static ArrayList<Button> buttons;

  public final MessageQueue<ByteBuffer> statusQueue; 
  public final MessageQueue<ByteBuffer> goalQueue;    
  public final MessageQueue<ByteBuffer>.QueueReader mGoalReader;    // for rumble commands


  /**
   * Controller wraps WPILib Joystick class  
   * and connects them with JoystickStatus and JoystickGoal queues
   * @param joystick wpilibj.Joystick() at the correct port
   * @param statusQueue queue where axis and button values will be sent
   * @param goalQueue queue where rumble commands will be read
   * @return new Controller
   */
  public Controller(final Joystick joystick, 
    final MessageQueue<ByteBuffer> statusQueue, final MessageQueue<ByteBuffer> goalQueue) {

    // first, let's keep track of all controllers
    if (joystickList.contains(joystick)) {
      System.err.println("Error: duplicate joystick added to Controller");
      System.exit(-1);
    }
    joystickList.add(joystick);   // joystickList is only used to check for unique controllers
    controllerList.add(this);

    this.wpilibJoystick = joystick;
    buttons = new ArrayList<>();
    this.statusQueue = statusQueue;
    this.goalQueue = goalQueue;
    mGoalReader = goalQueue.makeReader();
  }

  public Button addButton(int buttonId) {
    Button button = new Button(this, buttonId);
    addButton(button); // add to button list
    return button;
  }

  public AxisButton addAxisButton(int axisId, double threshold) {
    AxisButton axisButton = new AxisButton(this, axisId, threshold);
    addButton(axisButton); // add to button list
    return axisButton;
  }

  public PovButton addPovButton(int povId, int minRange, int maxRange) {
    PovButton povButton = new PovButton(this, povId, minRange, maxRange);
    addButton(povButton); // add to button list
    return povButton;
  }

  private void addButton(Button button) {
    /**
     * All buttons should be added to the buttons list as they are constructed
     */
    if (!buttons.contains(button)) {
      buttons.add(button);
    } else {
      System.out.println("Warning: Adding an already existing button");
      System.out.println("         (This is OK if using the same button for multiple things");
      System.out.println("          Otherwise, check your code)");
    }
  }

  /**
   * update() will get the current axis and button values from the Driver Station packet
   * then log these values in the JoystickStatus queues (one for each controller)
   * then check the JoystickGoal queues to see if rumble should be turned on
   */
  public void update() {
    for (var controller : controllerList) {
        controller.updateController();
    }
  }

  // update button pressed / released for all buttons
  // including PovButtons and AxisButtons
  private void updateController() {
    for (var button : buttons) {
      button.update();
    }

    log();

    Optional<ByteBuffer> obb = mGoalReader.readLast();
    if (obb.isPresent()) {
      JoystickGoal joystickGoal = JoystickGoal.getRootAsJoystickGoal(obb.get());
      setRumble(joystickGoal.rumble());
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
   * <p>
   * The POV angles start at 0 in the up direction, and increase clockwise (eg
   * right is 90, upper-left is 315).
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

  int bufferSize = 0;
  public void log() {

    float[] axes = new float[6];
    for (int k = 0; k < axes.length; k++) {
      axes[k] = (float) getAxis(k);   // axis IDs are base 0
    }

    boolean[] buttons = new boolean[16];
    for (int k = 0; k < buttons.length; k++) {
      buttons[k] = getButton(k + 1);  // button IDs are base 1, not base 0
    }

    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);
    JoystickStatus.startJoystickStatus(builder);
    JoystickStatus.addTimestamp(builder, Timer.getFPGATimestamp());
    JoystickStatus.addAxes(builder, AxisVector.createAxisVector(builder, axes));
    JoystickStatus.addButtons(builder, ButtonVector.createButtonVector(builder, buttons));
    JoystickStatus.addPov(builder, getPOV(0));
    int offset = JoystickStatus.endJoystickStatus(builder);

    JoystickStatus.finishSizePrefixedJoystickStatusBuffer(builder, offset);
    ByteBuffer bb = builder.dataBuffer();
    bufferSize = Math.max(bufferSize, bb.remaining()); // correct buffer size for next time

    statusQueue.write(bb);
  }





  public class Button {
    protected Controller mController; // controller with this button
    protected int mId; // id of button on controller
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
      update(mController.getButton(mId));
    }

    public void update(boolean val) {
      mLast = mCurrent;
      mCurrent = val;
    }

    // WPILib defines these functions, but with this Button class
    // we can extend this to PovButtons and AxisButtons

    public boolean isPressed() {
      return mCurrent;
    }

    public boolean posEdge() {
      return mCurrent && !mLast;
    }

    public boolean negEdge() {
      return !mCurrent && mLast;
    }
  }





  /**
   * Use when an axis is used as a button
   */
  public class AxisButton extends Button {
    double mThreshold; // threshold at which to trigger

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
      boolean pressed = (Math.signum(value) == Math.signum(mThreshold)) && (value >= mThreshold);
      update(pressed);
    }
  }




  
  /**
   * To use the D-Pad (POV) as up to 8 distinct buttons
   */
  public class PovButton extends Button {
    // minimum and maximum values that would result in a button press
    int mMin;
    int mMax;

    /**
     * PovButton has a private constructor so that it can only be created through
     * Controller.addPovButton()
     */
    private PovButton(final Controller controller, final int id, final int min, final int max) {
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
        pressed = ((value >= mMin) && (value <= mMax)) || ((negValue >= mMin) && (negValue <= mMax));
      }
      update(pressed);
    }

  }
};
