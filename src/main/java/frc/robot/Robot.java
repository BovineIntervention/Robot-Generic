/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.joystick.IDriveControls;
import frc.robot.joystick.User1DriveControls;
import frc.taurus.messages.DrivetrainGoal;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // User-Controls (joysticks & button boards)
  IDriveControls user1DriveControls = User1DriveControls.getInstance();   // TODO: allow selection of user drive control scheme


  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */@Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once when the autonomous period begins.
   */
  @Override
  public void autonomousInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * This function is called once when the teleop period begins.
   */
  @Override
  public void teleopInit() {
  }

 /**
   * This function is called periodically during teleop.
   */
  @Override
  public void teleopPeriodic() {
    float throttle = (float)user1DriveControls.getThrottle();
    float steering = (float)user1DriveControls.getSteering();
    boolean quickTurn = user1DriveControls.getQuickTurn();
    boolean lowGear = user1DriveControls.getLowGear();    

    FlatBufferBuilder builder = new FlatBufferBuilder(1024);
    long timestamp = 123;
    int drivetrainGoal = DrivetrainGoal.createDrivetrainGoal(builder, timestamp, throttle, steering, !lowGear, quickTurn);
  }

  /**
   * This function is called once when the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  /**
   * This function is called periodically when disabled.
   */
  @Override
  public void disabledPeriodic() {
  }

  /**
   * This function is called once when test mode is enabled.
   */
  @Override
  public void testInit() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

}
