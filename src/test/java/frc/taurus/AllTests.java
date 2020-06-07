package frc.taurus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import frc.taurus.driverstation.DriverStationStatusTest;
import frc.taurus.joystick.ControllerManagerTest;
import frc.taurus.joystick.ControllerTest;
import frc.taurus.logger.FlatBuffersLoggerTest;
import frc.taurus.logger.LoggerManagerTest;
import frc.taurus.messages.GenericQueueTest;
import frc.taurus.messages.MessageQueueTest;

@RunWith(Suite.class)
@SuiteClasses({
  DriverStationStatusTest.class, 
  ControllerManagerTest.class, 
  ControllerTest.class, 
  FlatBuffersLoggerTest.class, 
  LoggerManagerTest.class, 
  GenericQueueTest.class, 
  MessageQueueTest.class
})

public class AllTests {
  
}
