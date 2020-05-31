package frc.robot.joystick;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;
import frc.taurus.joystick.ButtonBoardController;
import frc.taurus.joystick.Controller;
import frc.taurus.joystick.XboxController;
import frc.taurus.messages.MessageQueue;

/**
 * The user controls beyond basic drivetrain motion. These will change for every
 * new game.
 */

public class SuperstructureControlsExample implements ISuperstructureControls {

    private final XboxController mDriverController;
    private final Controller.Button mShootButton;
    private final Controller.Button mAutoAimButton;

    private final ButtonBoardController mButtonBoard;
    private final Controller.Button mClimbButton;
    private final Controller.PovButton mTurnNorthPovButton;
    private final Controller.PovButton mTurnSouthPovButton;
    private final Controller.AxisButton mIntakeAxisButton;

    public SuperstructureControlsExample(XboxController driverController, MessageQueue<ByteBuffer> statusQueue, MessageQueue<ByteBuffer> goalQueue) {
        // use Controller.addController() to add controllers to this control method
        Joystick operatorJoystick = new Joystick(Constants.ControllerConstants.ControllerConfig1.kOperatorControllerPort);

        mDriverController = driverController;
        mButtonBoard = new ButtonBoardController( operatorJoystick, Constants.ControllerConstants.kDriveDeadband, statusQueue, goalQueue );

        mShootButton = mDriverController.addButton(XboxController.Button.X.id);
        mAutoAimButton = mDriverController.addButton(XboxController.Button.Y.id);
        mIntakeAxisButton = mDriverController.addAxisButton(XboxController.Axis.R_TRIGGER_AXIS.id, 0.5);

        mClimbButton = mButtonBoard.addButton(ButtonBoardController.Button.SR.id);
        mTurnNorthPovButton = mButtonBoard.addPovButton(0, -45, 45);
        mTurnSouthPovButton = mButtonBoard.addPovButton(0, 135, 215);
    }

    // Driver has control of Intaking, Aiming and Shooting
    public boolean getIntake()  { return mIntakeAxisButton.isPressed(); }
    public boolean getAutoAim() { return mAutoAimButton.isPressed(); }
    public boolean getShoot()   { return mShootButton.isPressed(); }

    // Operator has control of Climbing and Turning
    public boolean getClimb()       { return mClimbButton.negEdge(); }
    public boolean getTurnNorth()   { return mTurnNorthPovButton.posEdge(); }
    public boolean getTurnSouth()   { return mTurnSouthPovButton.posEdge(); }

}