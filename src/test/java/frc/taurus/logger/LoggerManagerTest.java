package frc.taurus.logger;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.Channel;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.ChannelType;
import frc.taurus.config.Config;
import frc.taurus.config.Configuration;
import frc.taurus.logger.LogFileReader;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.TestMessage;

public class LoggerManagerTest {

    final int BUFFER_SIZE = 1024;

    @SuppressWarnings("unchecked")
    @Test
    public void writeOneMessageTest() {
        ChannelManager channelManager = ChannelManager.getInstance();
        MessageQueue<TestMessage> queue = (MessageQueue<TestMessage>)channelManager.fetch(Config.TEST_MESSAGE);

        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);      
        
        channelManager.update();    // write to file
        channelManager.close();     // close file so we can read it


        LogFileReader parser = new LogFileReader(Config.TEST_MESSAGE);
        ByteBuffer bb = parser.getNextTable();

        LogFileHeader logFileHdr = LogFileHeader.getRootAsLogFileHeader(bb);
        assertEquals(logFileHdr.timestamp(), Timer.getFPGATimestamp(), 10e9);

        Configuration configuration = logFileHdr.configuration();
        assertEquals(1, configuration.channelsLength());
        for (int k=0; k<configuration.channelsLength(); k++) {
            Channel channel = configuration.channels(k);
            assertEquals(ChannelType.TestMessage, channel.channelType());
            assertEquals("TestMessage", channel.name());
            assertEquals("test.log", channel.logFilename());
        }

        bb = parser.getNextTable();
        Packet packet = Packet.getRootAsPacket(bb);
        assertEquals(0, packet.packetCount());
        assertEquals(ChannelType.TestMessage, packet.channelType());

        bb = packet.payloadAsByteBuffer(); 
        TestMessage testMessage = TestMessage.getRootAsTestMessage(bb);
        assertEquals(686, testMessage.intValue());

    }


}
