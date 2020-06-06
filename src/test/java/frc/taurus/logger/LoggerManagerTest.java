package frc.taurus.logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.config.TestConfig;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.driverstation.DriverStationData;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.logger.generated.Packet;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.generated.TestMessage1;
import frc.taurus.messages.generated.TestMessage2;
import frc.taurus.messages.generated.TestMessage3;
import frc.taurus.messages.generated.TestMessage4;

public class LoggerManagerTest {

  static double eps = 1e-9;

  @Test
  public void writeOneMessageTest() {

    // we only log when enabled, so we must mock enable during unit tests
    DriverStation mockDriverStation = mock(DriverStation.class);
    when(mockDriverStation.isEnabled()).thenReturn(true);
    
    ChannelManager channelManager = new ChannelManager(Config.DRIVER_STATION_STATUS);
    MessageQueue<ByteBuffer> queue1 = channelManager.fetch(TestConfig.TEST_MESSAGE_1);

    FlatBuffersLogReader reader = new FlatBuffersLogReader(TestConfig.TEST_MESSAGE_1.getLogFilename());

    FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
    int offset1 = TestMessage1.createTestMessage1(builder1, 686);
    TestMessage1.finishTestMessage1Buffer(builder1, offset1);
    queue1.write(builder1.dataBuffer());

    channelManager.update(); // write to file
    channelManager.close(); // close file so we can read it

    // Read log file and check its contents
    ByteBuffer bb = reader.getNextTable();

    LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started

    Configuration configuration = logFileHdr.configuration();
    assertEquals(1, configuration.channelsLength());

    Channel channel = configuration.channels(0);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_1.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_1.getLogFilename(), channel.logFilename());

    bb = reader.getNextTable();
    Packet packet = Packet.getRootAsPacket(bb);
    assertEquals(0, packet.packetCount());
    assertEquals(1, packet.queueSize());
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(686, testMessage1.intValue());

    reader.close();
  }

  @Test
  public void writeTwoMessagesTest() {
    
    // we only log when enabled, so we must mock enable during unit tests
    DriverStation mockDriverStation = mock(DriverStation.class);
    when(mockDriverStation.isEnabled()).thenReturn(true);
    
    ChannelManager channelManager = new ChannelManager(Config.DRIVER_STATION_STATUS);
    MessageQueue<ByteBuffer> queue1 = channelManager.fetch(TestConfig.TEST_MESSAGE_1);
    MessageQueue<ByteBuffer> queue2 = channelManager.fetch(TestConfig.TEST_MESSAGE_2);

    FlatBuffersLogReader reader = new FlatBuffersLogReader(TestConfig.TEST_MESSAGE_1.getLogFilename());

    FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
    int offset1 = TestMessage1.createTestMessage1(builder1, 686);
    TestMessage1.finishTestMessage1Buffer(builder1, offset1);
    queue1.write(builder1.dataBuffer());

    FlatBufferBuilder builder2 = new FlatBufferBuilder(64);
    int offset2 = TestMessage2.createTestMessage2(builder2, 686.0);
    TestMessage2.finishTestMessage2Buffer(builder2, offset2);
    queue2.write(builder2.dataBuffer());

    channelManager.update(); // write to file
    channelManager.close(); // close file so we can read it

    // Read log file and check its contents
    ByteBuffer bb = reader.getNextTable();

    LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started

    // Check Configuration
    Configuration configuration = logFileHdr.configuration();
    assertEquals(2, configuration.channelsLength());

    // 1st channel in configuration is TEST_MESSAGE_1
    Channel channel = configuration.channels(0);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_1.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_1.getLogFilename(), channel.logFilename());

    // 2nd channel in configuration is TEST_MESSAGE_2
    channel = configuration.channels(1);
    assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_2.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_1.getLogFilename(), channel.logFilename());

    // 1st packet is TEST_MESSAGE_1
    bb = reader.getNextTable();
    Packet packet = Packet.getRootAsPacket(bb);
    assertEquals(0, packet.packetCount());
    assertEquals(1, packet.queueSize());
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(686, testMessage1.intValue());

    // 2nd packet is TEST_MESSAGE_2
    bb = reader.getNextTable();
    packet = Packet.getRootAsPacket(bb);
    assertEquals(1, packet.packetCount());
    assertEquals(1, packet.queueSize());
    assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage2 testMessage2 = TestMessage2.getRootAsTestMessage2(bb);
    assertEquals(686.0, testMessage2.dblValue(), eps);

    reader.close();

  }



  @Test
  public void threeFiles() {

    final int numMessages = 1000;

    // we only log when enabled, so we must mock enable during unit tests
    DriverStation mockDriverStation = mock(DriverStation.class);
    when(mockDriverStation.isEnabled()).thenReturn(true);
    
    // configure channels
    ChannelManager channelManager = new ChannelManager(Config.DRIVER_STATION_STATUS);
    MessageQueue<ByteBuffer> queue1 = channelManager.fetch(TestConfig.TEST_MESSAGE_1);
    MessageQueue<ByteBuffer> queue2 = channelManager.fetch(TestConfig.TEST_MESSAGE_2);
    MessageQueue<ByteBuffer> queue3 = channelManager.fetch(TestConfig.TEST_MESSAGE_3);
    MessageQueue<ByteBuffer> queue4 = channelManager.fetch(TestConfig.TEST_MESSAGE_4);

    // channels 1&2 share the same log file, 3 & 4 have their own
    FlatBuffersLogReader reader12 = new FlatBuffersLogReader(TestConfig.TEST_MESSAGE_1.getLogFilename());
    FlatBuffersLogReader reader3 = new FlatBuffersLogReader(TestConfig.TEST_MESSAGE_3.getLogFilename());
    FlatBuffersLogReader reader4 = new FlatBuffersLogReader(TestConfig.TEST_MESSAGE_4.getLogFilename());

    for (int k=0; k<numMessages; k++) {
      // sent data over queue
      FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
      int offset1 = TestMessage1.createTestMessage1(builder1, k);
      TestMessage1.finishTestMessage1Buffer(builder1, offset1);
      queue1.write(builder1.dataBuffer());

      FlatBufferBuilder builder2 = new FlatBufferBuilder(64);
      int offset2 = TestMessage2.createTestMessage2(builder2, k);
      TestMessage2.finishTestMessage2Buffer(builder2, offset2);
      queue2.write(builder2.dataBuffer());

      FlatBufferBuilder builder3 = new FlatBufferBuilder(64);
      int offset3 = TestMessage3.createTestMessage3(builder3, k);
      TestMessage3.finishTestMessage3Buffer(builder3, offset3);
      queue3.write(builder3.dataBuffer());

      FlatBufferBuilder builder4 = new FlatBufferBuilder(64);
      int offset4 = TestMessage4.createTestMessage4(builder4, k);
      TestMessage4.finishTestMessage4Buffer(builder4, offset4);
      queue4.write(builder4.dataBuffer());

      channelManager.update(); // write to file
    }
    channelManager.close(); // close file so we can read it

    threeFilesConfigCheck(reader12);
    threeFilesConfigCheck(reader3);
    threeFilesConfigCheck(reader4);


    for (int k=0; k<numMessages; k++) {
      // 1st packet is TEST_MESSAGE_1
      ByteBuffer bb = reader12.getNextTable();
      Packet packet = Packet.getRootAsPacket(bb);
      assertEquals(2*k, packet.packetCount());
      assertEquals(1, packet.queueSize());
      assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
      assertEquals(k, testMessage1.intValue());

      // 2nd packet is TEST_MESSAGE_2
      bb = reader12.getNextTable();
      packet = Packet.getRootAsPacket(bb);
      assertEquals(2*k+1, packet.packetCount());
      assertEquals(1, packet.queueSize());
      assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage2 testMessage2 = TestMessage2.getRootAsTestMessage2(bb);
      assertEquals(k, testMessage2.dblValue(), eps);
    }
    
    for (int k=0; k<numMessages; k++) {
      ByteBuffer bb = reader3.getNextTable();
      Packet packet = Packet.getRootAsPacket(bb);
      assertEquals(k, packet.packetCount());
      assertEquals(1, packet.queueSize());
      assertEquals(TestConfig.TEST_MESSAGE_3.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage3 testMessage3 = TestMessage3.getRootAsTestMessage3(bb);
      assertEquals(k, testMessage3.intValue());
    }
           
    for (int k=0; k<numMessages; k++) {
      ByteBuffer bb = reader4.getNextTable();
      Packet packet = Packet.getRootAsPacket(bb);
      assertEquals(k, packet.packetCount());
      assertEquals(1, packet.queueSize());
      assertEquals(TestConfig.TEST_MESSAGE_4.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage4 testMessage4 = TestMessage4.getRootAsTestMessage4(bb);
      assertEquals(k, testMessage4.intValue());
    }
        
    reader12.close();
    reader3.close();
    reader4.close();

  }  

  
  public void threeFilesConfigCheck(FlatBuffersLogReader reader) {
    // Read log file and check its contents
    ByteBuffer bb = reader.getNextTable();

    LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started

    // Check Configuration
    Configuration configuration = logFileHdr.configuration();
    assertEquals(4, configuration.channelsLength());

    // 1st channel in configuration is TEST_MESSAGE_1
    Channel channel = configuration.channels(0);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_1.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_1.getLogFilename(), channel.logFilename());

    // 2nd channel in configuration is TEST_MESSAGE_2
    channel = configuration.channels(1);
    assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_2.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_2.getLogFilename(), channel.logFilename());

    // 3rd channel in configuration is TEST_MESSAGE_3
    channel = configuration.channels(2);
    assertEquals(TestConfig.TEST_MESSAGE_3.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_3.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_3.getLogFilename(), channel.logFilename());

    // 4th channel in configuration is TEST_MESSAGE_4
    channel = configuration.channels(3);
    assertEquals(TestConfig.TEST_MESSAGE_4.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_4.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_4.getLogFilename(), channel.logFilename());
  }


  // cycle through the operational modes
  // DISABLED --> AUTO --> DISABLED --> TELEOP --> DISABLED --> TEST --> DISABLED
  // make sure folders are created correctly and files written to the right place
  @Test
  public void relocateLogFolders() {

    // DISABLED
    DriverStation mockDriverStation = mock(DriverStation.class);
    when(mockDriverStation.isEnabled()).thenReturn(false);
    when(mockDriverStation.isAutonomous()).thenReturn(false);
    when(mockDriverStation.isOperatorControl()).thenReturn(false);
    when(mockDriverStation.isTest()).thenReturn(false);
    // fill out a few more parameters to make sure logging is successful
    when(mockDriverStation.isDSAttached()).thenReturn(false);
    when(mockDriverStation.isFMSAttached()).thenReturn(false);
    when(mockDriverStation.getGameSpecificMessage()).thenReturn("");
    when(mockDriverStation.getMatchTime()).thenReturn(123.0);

    ChannelManager channelManager = new ChannelManager(Config.DRIVER_STATION_STATUS);
    MessageQueue<ByteBuffer> testQueue = channelManager.fetch(TestConfig.TEST_MESSAGE_1);
    MessageQueue<ByteBuffer> dsStatusQueue = channelManager.fetch(Config.DRIVER_STATION_STATUS);
    
    DriverStationData driverStationData = new DriverStationData(mockDriverStation, dsStatusQueue);

    FlatBufferBuilder builder = new FlatBufferBuilder(64);
    int offset = TestMessage1.createTestMessage1(builder, 001);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update(); // update to DriverStation status queue
    channelManager.update();    // write to file (won't happen when DISABLED)
 
    // AUTO
    when(mockDriverStation.isEnabled()).thenReturn(true);
    when(mockDriverStation.isAutonomous()).thenReturn(true);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 002);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (in "unit_test_auto" folder)

    // DISABLED
    when(mockDriverStation.isEnabled()).thenReturn(false);
    when(mockDriverStation.isAutonomous()).thenReturn(false);
    when(mockDriverStation.isOperatorControl()).thenReturn(false);
    when(mockDriverStation.isTest()).thenReturn(false);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 003);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (won't happen when DISABLED)
 
    // TELEOP
    when(mockDriverStation.isEnabled()).thenReturn(true);
    when(mockDriverStation.isOperatorControl()).thenReturn(true);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 004);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (in "unit_test_auto" folder)

    // DISABLED
    when(mockDriverStation.isEnabled()).thenReturn(false);
    when(mockDriverStation.isAutonomous()).thenReturn(false);
    when(mockDriverStation.isOperatorControl()).thenReturn(false);
    when(mockDriverStation.isTest()).thenReturn(false);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 005);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (won't happen when DISABLED) 

    // TEST
    when(mockDriverStation.isEnabled()).thenReturn(true);
    when(mockDriverStation.isTest()).thenReturn(true);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 006);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (in "unit_test_teleop" folder)

    // DISABLED
    when(mockDriverStation.isEnabled()).thenReturn(false);
    when(mockDriverStation.isAutonomous()).thenReturn(false);
    when(mockDriverStation.isOperatorControl()).thenReturn(false);
    when(mockDriverStation.isTest()).thenReturn(false);

    builder = new FlatBufferBuilder(64);
    offset = TestMessage1.createTestMessage1(builder, 007);
    TestMessage1.finishTestMessage1Buffer(builder, offset);
    testQueue.write(builder.dataBuffer());

    driverStationData.update();
    channelManager.update(); // write to file (won't happen when DISABLED)
    channelManager.close();



    // Check AUTO file (Test Message 1 contains the number 2)
    FlatBuffersLogReader reader = new FlatBuffersLogReader("unit_test_auto" + File.separator + TestConfig.TEST_MESSAGE_1.getLogFilename(), true);

    // Read log file and check its contents
    ByteBuffer bb = reader.getNextTable();

    LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started
    // skip checking configuration

    bb = reader.getNextTable();
    Packet packet = Packet.getRootAsPacket(bb);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(002, testMessage1.intValue());

    reader.close();


    // Check TELEOP file (Test Message 1 contains the number 4)
    reader = new FlatBuffersLogReader("unit_test_teleop" + File.separator + TestConfig.TEST_MESSAGE_1.getLogFilename(), true);

    // Read log file and check its contents
    bb = reader.getNextTable();

    logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                          // seconds of it being started
    // skip checking configuration

    bb = reader.getNextTable();
    packet = Packet.getRootAsPacket(bb);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(004, testMessage1.intValue());

    reader.close();

    

    // Check TEST file (Test Message 1 contains the number 6)
    reader = new FlatBuffersLogReader("unit_test_test" + File.separator + TestConfig.TEST_MESSAGE_1.getLogFilename(), true);

    // Read log file and check its contents
    bb = reader.getNextTable();

    logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started
    // skip checking configuration

    bb = reader.getNextTable();
    packet = Packet.getRootAsPacket(bb);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(006, testMessage1.intValue());

    reader.close();

  }
}
