package frc.taurus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import frc.taurus.driverstation.DriverStationStatusTest;
import frc.taurus.drivetrain.DrivetrainGoalTest;
import frc.taurus.hal.ControllerHALTest;
import frc.taurus.joystick.ControllerTest;
import frc.taurus.joystick.JoystickStatusTest;
import frc.taurus.joystick.SteeringMethodsTest;
import frc.taurus.logger.FlatBuffersLoggerTest;
import frc.taurus.logger.LoggerManagerTest;
import frc.taurus.messages.GenericQueueTest;
import frc.taurus.messages.MessageQueueTest;

@RunWith(Suite.class)
@SuiteClasses({
  DriverStationStatusTest.class, 
  DrivetrainGoalTest.class,
  ControllerHALTest.class, 
  ControllerTest.class, 
  JoystickStatusTest.class,
  SteeringMethodsTest.class,
  FlatBuffersLoggerTest.class, 
  LoggerManagerTest.class, 
  GenericQueueTest.class, 
  MessageQueueTest.class
})

public class AllTests {
  
}
