package frc.robot.usercontrols;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.taurus.joystick.AxisButton;
import frc.taurus.joystick.ButtonBoardController;
import frc.taurus.joystick.PovButton;
import frc.taurus.joystick.XboxController;

/**
 * The user controls beyond basic drivetrain motion. These will change for every
 * new game.
 */

public class OperatorControls implements IOperatorControls {

    private static OperatorControls mInstance = null;
    public static OperatorControls getInstance() {
        if (mInstance == null) {
            mInstance = new OperatorControls();
        }

        return mInstance;
    }   
    
    private final XboxController mDriverController;
    private final ButtonBoardController mButtonBoard;
    private final PovButton mTurnNorthPovButton;
    private final PovButton mTurnSouthPovButton;
    private final AxisButton mIntakeAxisButton;

    private OperatorControls() {
        mDriverController = new XboxController( new Joystick(Constants.ControllerConstants.kDriveControllerPort) );
        mButtonBoard = new ButtonBoardController(  new Joystick(Constants.ControllerConstants.kOperatorControllerPort) );
        mTurnNorthPovButton = new PovButton(mButtonBoard, -45, 45);
        mTurnSouthPovButton = new PovButton(mButtonBoard, 135, 215);
        mIntakeAxisButton = new AxisButton(mDriverController, XboxController.Axis.R_TRIGGER_AXIS.id, 0.5);
    }

    public void update() {
        // call update for all AxisButtons and PovButtons
        mIntakeAxisButton.update();
        mTurnNorthPovButton.update();
        mTurnSouthPovButton.update();
    }

    // Driver has control of Intaking, Aiming and Shooting
    public boolean getIntake()  { return mIntakeAxisButton.getButton(); }
    public boolean getAutoAim() { return mDriverController.getButton(XboxController.Button.X); }
    public boolean getShoot()   { return mDriverController.getButton(XboxController.Button.R_BUMPER); }

    // Operator has control of Climbing and Turning
    public boolean getClimb()       { return mButtonBoard.getButtonReleased(ButtonBoardController.Button.SR); }
    public boolean getTurnNorth()   { return mTurnNorthPovButton.getButtonPressed(); }
    public boolean getTurnSouth()   { return mTurnSouthPovButton.getButtonPressed(); }

}