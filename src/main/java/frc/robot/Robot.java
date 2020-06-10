/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.hal.DrivetrainHAL;
import frc.robot.hal.SuperstructureHAL;
import frc.robot.joystick.DriverControlsXboxExample;
import frc.robot.joystick.SuperstructureControlsExample;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.driverstation.DriverStationData;
import frc.taurus.drivetrain.Drivetrain;
import frc.taurus.joystick.ControllerManager;
import frc.taurus.joystick.XboxController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // Get the ChannelManager instance for fetching the various queues
  ChannelManager channelManager = ChannelManager.getInstance();
  DriverStationData driverStationData = new DriverStationData(DriverStation.getInstance(), channelManager);
  
  // User-Controls (joysticks & button boards)
  // TODO: allow selection of user drive control scheme
  ControllerManager controllerManager = new ControllerManager();
  DriverControlsXboxExample driverControls = new DriverControlsXboxExample(channelManager);
  SuperstructureControlsExample superstructureControls = new SuperstructureControlsExample(channelManager, (XboxController)driverControls.getControllersList().get(0));

  Drivetrain drivetrain = new Drivetrain(channelManager);
  
  DrivetrainHAL drivetrainHAL = new DrivetrainHAL(channelManager);
  SuperstructureHAL superstructureHAL = new SuperstructureHAL();


  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Register all physical controllers with ControllerManager
    controllerManager.register(driverControls.getControllersList());
    controllerManager.register(superstructureControls.getControllersList());   
    
    channelManager.fetch(Config.DRIVETRAIN_GOAL).subscribe(drivetrain);
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
    drivetrainHAL.readSensors();
    superstructureHAL.readSensors();

    driverStationData.update();   // get driverstation inputs
    controllerManager.update();   // get joystick inputs

    driverControls.update();          // generates DrivetrainGoal message
    superstructureControls.update();  // generate ... messages

    drivetrain.update();

    drivetrainHAL.writeActuators();
    superstructureHAL.writeActuators();

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
