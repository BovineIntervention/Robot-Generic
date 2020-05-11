package frc.robot.usercontrols;

/**
 * The user controls beyond basic drivetrain motion.
 * These will change for every new game.
 */

public interface IUserButtonControls {
    void reset();

    // change these functions as needed
    // boolean getAutoAim();
    // boolean getIntake();
    // boolean getShoot();
    // boolean getClimb();

    void setRumble(boolean on);


}