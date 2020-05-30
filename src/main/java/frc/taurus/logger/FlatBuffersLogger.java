package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import frc.taurus.config.ChannelIntf;
import frc.taurus.messages.GenericQueue;

/**
 * A log file is a sequence of size prefixed flatbuffers.
 * 
 * The first flatbuffer will be the LogFileHeader, followed by an arbitrary
 * number of MessageHeaders
 */

public class FlatBuffersLogger {
    // Instantiate a new MessageLogger for each thread that needs logging
    // TODO: put logger in its own thread

    class ChannelTypeReaderPair {
        public int channelType;
        public GenericQueue<ByteBuffer>.QueueReader reader;

        ChannelTypeReaderPair(final int channelType, final GenericQueue<ByteBuffer>.QueueReader reader) {
            this.channelType = channelType;
            this.reader = reader;
        }
    }

    String filename;
    ArrayList<ChannelTypeReaderPair> pairList = new ArrayList<ChannelTypeReaderPair>();

    LogFileWriter writer = new LogFileWriter();
    int maxHeaderSize = 0;
    long packetCount = 0;

    // TODO: add timestamp to filename or folder
    public FlatBuffersLogger(String filename) {
        this.filename = filename;
    }


    public void register(ChannelIntf channel) {
        ChannelTypeReaderPair pair = new ChannelTypeReaderPair( channel.getNum(), channel.getQueue().makeReader() );
        pairList.add(pair);
    }


    public void update() {
        if (packetCount == 0) {
            writer.writeBytes(filename, LoggerManager.getFileHeader());
        }
        for (var pair : pairList) {
            while (!pair.reader.isEmpty()) {
                ByteBuffer bb = pair.reader.read().get();    // we know Optional::isPresent() is true because of earlier !isEmpty()
                writePacket(pair.channelType, bb);           // write to file
            }
        }
        writer.flush();
    }

    public void writePacket(final int channelType, final ByteBuffer bb_payload) {
        int payloadSize = bb_payload.remaining();

        FlatBufferBuilder builder = new FlatBufferBuilder(maxHeaderSize + payloadSize);

        // Create Payload
        int dataOffset = Packet.createPayloadVector(builder, bb_payload);

        // Create Packet
        int offset = Packet.createPacket(builder, packetCount++, channelType, dataOffset);
        Packet.finishSizePrefixedPacketBuffer(builder, offset); // add size prefix to files
        ByteBuffer bb_packet = builder.dataBuffer();

        maxHeaderSize = Math.max(maxHeaderSize, bb_packet.remaining() - payloadSize);

        // write Packet to file
        writer.writeBytes(filename, bb_packet);
    }

    public void close() {
        writer.close();
    }


    public String getBasePath() {
        return writer.getBasePath();
    }

}