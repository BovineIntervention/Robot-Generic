package frc.taurus.logger;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.TestConfig;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.logger.generated.Packet;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.generated.TestMessage1;
import frc.taurus.messages.generated.TestMessage2;

public class LoggerManagerTest {

  static double eps = 1e-9;

  @Test
  public void writeOneMessageTest() {

    ChannelManager channelManager = new ChannelManager();
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

    ChannelManager channelManager = new ChannelManager();
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

  // TODO: test that 2 different log files can be written

  // TODO: threads writing to files w/ collisions
}
