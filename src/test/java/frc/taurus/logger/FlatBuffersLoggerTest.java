package frc.taurus.logger;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelIntf;
import frc.taurus.config.TestConfig;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.logger.generated.Packet;
import frc.taurus.messages.generated.TestMessage1;
import frc.taurus.messages.generated.TestMessage2;

public class FlatBuffersLoggerTest {

  ArrayList<ChannelIntf> channelList = new ArrayList<ChannelIntf>();

  static double eps = 1e-9;

  public ByteBuffer getFileHeader() {
    FlatBufferBuilder builder = new FlatBufferBuilder(256);

    // NOTE: we assume that the Test functions are clearing() and add()ing to 
    // the channelList array appropriately

    // create Channels
    int[] channelOffsets = new int[channelList.size()];
    for (int k = 0; k < channelList.size(); k++) {
      ChannelIntf channel = channelList.get(k);
      channelOffsets[k] = Channel.createChannel(builder, channel.getNum(), builder.createString(channel.getName()),
          builder.createString(channel.getLogFilename()));
    }
    // create Channel vector
    int channelVectorOffset = Configuration.createChannelsVector(builder, channelOffsets);

    // create Config
    int configOffset = Configuration.createConfiguration(builder, channelVectorOffset);

    // create LogFileHeader
    double timestamp = Timer.getFPGATimestamp();
    int offset = LogFileHeader.createLogFileHeader(builder, timestamp, configOffset);
    LogFileHeader.finishSizePrefixedLogFileHeaderBuffer(builder, offset);
    ByteBuffer fileHeader = builder.dataBuffer();

    return fileHeader;
  }




  @Test
  public void writeOneMessageTest() {

    // configure channels
    ChannelIntf channel1 = TestConfig.TEST_MESSAGE_1;
    channelList.clear();
    channelList.add(channel1);

    for (var channel : channelList) {
      channel.getQueue().clear();  // important: need to reset queues between tests (usually done by ChannelManager)
    }

    // configure logger
    String filename = channel1.getLogFilename();


    // configure logger
    FlatBuffersLogger logger = new FlatBuffersLogger(filename, this::getFileHeader);
    logger.register(channel1);

    // configure reader
    FlatBuffersLogReader reader = new FlatBuffersLogReader(filename);


    // sent data over queue
    FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
    int offset1 = TestMessage1.createTestMessage1(builder1, 686);
    TestMessage1.finishTestMessage1Buffer(builder1, offset1);
    channel1.getQueue().write(builder1.dataBuffer());

    // log data
    logger.update(); // write to file
    logger.close(); // close file so we can read it


    // Read log file and check its contents
    ByteBuffer bb = reader.getNextTable();

    LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
    assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100); // we should read the file back within 100
                                                                         // seconds of it being started

    Configuration configuration = logFileHdr.configuration();
    assertEquals(1, configuration.channelsLength());

    int k = 0;
    Channel channel = configuration.channels(k);
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), channel.channelType());
    assertEquals(TestConfig.TEST_MESSAGE_1.getName(), channel.name());
    assertEquals(TestConfig.TEST_MESSAGE_1.getLogFilename(), channel.logFilename());

    bb = reader.getNextTable();
    Packet packet = Packet.getRootAsPacket(bb);
    assertEquals(0, packet.packetCount());
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(686, testMessage1.intValue());

    reader.close();
  }

  @Test
  public void writeTwoMessagesTest() {

    // configure channels
    ChannelIntf channel1 = TestConfig.TEST_MESSAGE_1;
    ChannelIntf channel2 = TestConfig.TEST_MESSAGE_2;
    channelList.clear();
    channelList.add(channel1);
    channelList.add(channel2);

    for (var channel : channelList) {
      channel.getQueue().clear();  // important: need to reset queues between tests (usually done by ChannelManager)
    }

    // configure logger
    String filename = channel1.getLogFilename();
    

    // configure logger
    FlatBuffersLogger logger = new FlatBuffersLogger(filename, this::getFileHeader);
    logger.register(channel1);
    logger.register(channel2);

    // configure reader
    FlatBuffersLogReader reader = new FlatBuffersLogReader(filename);


    // sent data over queue
    FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
    int offset1 = TestMessage1.createTestMessage1(builder1, 686);
    TestMessage1.finishTestMessage1Buffer(builder1, offset1);
    channel1.getQueue().write(builder1.dataBuffer());

    FlatBufferBuilder builder2 = new FlatBufferBuilder(64);
    int offset2 = TestMessage2.createTestMessage2(builder2, 686.0);
    TestMessage2.finishTestMessage2Buffer(builder2, offset2);
    channel2.getQueue().write(builder2.dataBuffer());

    logger.update(); // write to file
    logger.close(); // close file so we can read it

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
    assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
    assertEquals(686, testMessage1.intValue());

    // 2nd packet is TEST_MESSAGE_2
    bb = reader.getNextTable();
    packet = Packet.getRootAsPacket(bb);
    assertEquals(1, packet.packetCount());
    assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), packet.channelType());

    bb = packet.payloadAsByteBuffer();
    TestMessage2 testMessage2 = TestMessage2.getRootAsTestMessage2(bb);
    assertEquals(686.0, testMessage2.dblValue(), eps);

    reader.close();

  }



  @Test
  public void twoChannelsLongInterleavedFileTest() {

    final int numMessages = 1000;

    // configure channels
    ChannelIntf channel1 = TestConfig.TEST_MESSAGE_1;
    ChannelIntf channel2 = TestConfig.TEST_MESSAGE_2;
    channelList.clear();
    channelList.add(channel1);
    channelList.add(channel2);

    for (var channel : channelList) {
      channel.getQueue().clear();  // important: need to reset queues between tests (usually done by ChannelManager)
    }

    // configure logger
    String filename = channel1.getLogFilename();
    

    // configure logger
    FlatBuffersLogger logger = new FlatBuffersLogger(filename, this::getFileHeader);
    logger.register(channel1);
    logger.register(channel2);

    // configure reader
    FlatBuffersLogReader reader = new FlatBuffersLogReader(filename);


    for (int k=0; k<numMessages; k++) {
      // sent data over queue
      FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
      int offset1 = TestMessage1.createTestMessage1(builder1, k);
      TestMessage1.finishTestMessage1Buffer(builder1, offset1);
      channel1.getQueue().write(builder1.dataBuffer());

      FlatBufferBuilder builder2 = new FlatBufferBuilder(64);
      int offset2 = TestMessage2.createTestMessage2(builder2, k);
      TestMessage2.finishTestMessage2Buffer(builder2, offset2);
      channel2.getQueue().write(builder2.dataBuffer());

      logger.update(); // write to file
    }
    logger.close(); // close file so we can read it

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

    for (int k=0; k<numMessages; k++) {
      // 1st packet is TEST_MESSAGE_1
      bb = reader.getNextTable();
      Packet packet = Packet.getRootAsPacket(bb);
      assertEquals(2*k, packet.packetCount());
      assertEquals(TestConfig.TEST_MESSAGE_1.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
      assertEquals(k, testMessage1.intValue());

      // 2nd packet is TEST_MESSAGE_2
      bb = reader.getNextTable();
      packet = Packet.getRootAsPacket(bb);
      assertEquals(2*k+1, packet.packetCount());
      assertEquals(TestConfig.TEST_MESSAGE_2.getNum(), packet.channelType());

      bb = packet.payloadAsByteBuffer();
      TestMessage2 testMessage2 = TestMessage2.getRootAsTestMessage2(bb);
      assertEquals(k, testMessage2.dblValue(), eps);
    }
    
    reader.close();

  }  
}
