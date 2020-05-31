package frc.taurus.logger;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.ChannelType;
import frc.taurus.config.generated.Configuration;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.logger.generated.Packet;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.generated.TestMessage1;
import frc.taurus.messages.generated.TestMessage2;

public class LoggerManagerTest {

    ChannelManager channelManager = ChannelManager.getInstance();
    static double eps = 1e-9;


    @Test
    public void writeOneMessageTest() {
System.out.println("writeOneMessageTest");        
        channelManager.reset();        
        MessageQueue<ByteBuffer> queue1 = channelManager.fetch(Config.TEST_MESSAGE_1);

        FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
        int offset1 = TestMessage1.createTestMessage1(builder1, 686);
        TestMessage1.finishSizePrefixedTestMessage1Buffer(builder1, offset1);
        // TestMessage1.finishTestMessage1Buffer(builder1, offset);
        queue1.write(builder1.dataBuffer());    
        
        channelManager.update();    // write to file
        channelManager.close();     // close file so we can read it



        // Read log file and check its contents
        LogFileReader parser = new LogFileReader(Config.TEST_MESSAGE_1);
        ByteBuffer bb = parser.getNextTable();

        LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
        assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100);     // we should read the file back within 100 seconds of it being started

        Configuration configuration = logFileHdr.configuration();
        assertEquals(1, configuration.channelsLength());

        int k = 0;
        Channel channel = configuration.channels(k);
        assertEquals(ChannelType.TestMessage1, channel.channelType());
        assertEquals("TestMessage1", channel.name());
        assertEquals("test.log", channel.logFilename());

        bb = parser.getNextTable();
        Packet packet = Packet.getRootAsPacket(bb);
        assertEquals(0, packet.packetCount());
        assertEquals(ChannelType.TestMessage1, packet.channelType());

        bb = packet.payloadAsByteBuffer(); 
        TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
        assertEquals(686, testMessage1.intValue());

        parser.close();
    }


    @Test
    public void writeTwoMessagesTest() {
System.out.println("writeTwoMessagesTest");        
        
        channelManager.reset();
        MessageQueue<ByteBuffer> queue1 = channelManager.fetch(Config.TEST_MESSAGE_1);
        MessageQueue<ByteBuffer> queue2 = channelManager.fetch(Config.TEST_MESSAGE_2);

        FlatBufferBuilder builder1 = new FlatBufferBuilder(64);
        int offset1 = TestMessage1.createTestMessage1(builder1, 686);
        TestMessage1.finishSizePrefixedTestMessage1Buffer(builder1, offset1);
        // TestMessage1.finishTestMessage1Buffer(builder1, offset);
        queue1.write(builder1.dataBuffer());      
        
        FlatBufferBuilder builder2 = new FlatBufferBuilder(64);
        int offset2 = TestMessage2.createTestMessage2(builder2, 686.0);
        // TestMessage2.finishSizePrefixedTestMessage2Buffer(builder2, offset2);
        TestMessage2.finishTestMessage2Buffer(builder2, offset2);
        queue2.write(builder2.dataBuffer());  

        channelManager.update();    // write to file
        channelManager.close();     // close file so we can read it



        // Read log file and check its contents
        LogFileReader parser = new LogFileReader(Config.TEST_MESSAGE_1);
        ByteBuffer bb = parser.getNextTable();

        LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
        assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 100);     // we should read the file back within 100 seconds of it being started


        // Check Configuration
        Configuration configuration = logFileHdr.configuration();
        assertEquals(2, configuration.channelsLength());

        // 1st channel in configuration is TEST_MESSAGE_1
        Channel channel = configuration.channels(0);
        assertEquals(ChannelType.TestMessage1, channel.channelType());
        assertEquals("TestMessage1", channel.name());
        assertEquals("test.log", channel.logFilename());
        
        // 2nd channel in configuration is TEST_MESSAGE_2
        channel = configuration.channels(1);
        assertEquals(ChannelType.TestMessage2, channel.channelType());
        assertEquals("TestMessage2", channel.name());
        assertEquals("test.log", channel.logFilename());        


        
        
        // 1st packet is TEST_MESSAGE_1
        bb = parser.getNextTable();
        Packet packet = Packet.getRootAsPacket(bb);
        assertEquals(0, packet.packetCount());
        assertEquals(ChannelType.TestMessage1, packet.channelType());

        bb = packet.payloadAsByteBuffer(); 
        TestMessage1 testMessage1 = TestMessage1.getRootAsTestMessage1(bb);
        assertEquals(686, testMessage1.intValue());


        // 2nd packet is TEST_MESSAGE_2
        bb = parser.getNextTable();
        packet = Packet.getRootAsPacket(bb);
        assertEquals(1, packet.packetCount());
        assertEquals(ChannelType.TestMessage2, packet.channelType());

        bb = packet.payloadAsByteBuffer(); 
        TestMessage2 testMessage2 = TestMessage2.getRootAsTestMessage2(bb);
        assertEquals(686.0, testMessage2.dblValue(), eps);

        parser.close();

    }    
}
