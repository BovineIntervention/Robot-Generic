package frc.robot.usercontrols;

/**
 * The user controls required for drivetrain motion.
 * These are likely to remain the same from year to year, but some special functions may be needed some years.
 */

public interface IUserDriveControls {
    double getThrottle();
    double getSteering();
    boolean getQuickTurn();
    boolean getLowGear();
}