package frc.robot.usercontrols;

/**
 * The user controls beyond basic drivetrain motion.
 * These will change for every new game.
 */

public interface IOperatorControls {

    void update();
    
    // change these functions as needed
    boolean getAutoAim();
    boolean getIntake();
    boolean getShoot();
    boolean getClimb();
    boolean getTurnNorth();

}