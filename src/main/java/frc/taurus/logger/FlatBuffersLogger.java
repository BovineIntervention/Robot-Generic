package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.google.flatbuffers.FlatBufferBuilder;

import frc.taurus.config.ChannelIntf;
import frc.taurus.logger.generated.Packet;
import frc.taurus.messages.MessageQueue;

/**
 * A log file is a sequence of size prefixed flatbuffers.
 * 
 * The first flatbuffer will be the LogFileHeader, followed by an arbitrary
 * number of MessageHeaders
 */

public class FlatBuffersLogger {

    class ChannelTypeReaderPair {
        public int channelType;
        public MessageQueue<ByteBuffer>.QueueReader reader;

        ChannelTypeReaderPair(final int channelType, final MessageQueue<ByteBuffer>.QueueReader reader) {
            this.channelType = channelType;
            this.reader = reader;
        }
    }

    final String filename;
    final Supplier<ByteBuffer> getFileHeaderCallback;
    ArrayList<ChannelTypeReaderPair> pairList = new ArrayList<ChannelTypeReaderPair>();

    BinaryLogFileWriter writer;
    int maxHeaderSize = 0;
    long packetCount = 0;

    // TODO: add timestamp to filename or folder
    public FlatBuffersLogger(final String filename, final Supplier<ByteBuffer> getFileHeaderCallback) {
        this.filename = filename;
        this.getFileHeaderCallback = getFileHeaderCallback;
        writer = new BinaryLogFileWriter(filename);
    }


    public void register(ChannelIntf channel) {
        ChannelTypeReaderPair pair = new ChannelTypeReaderPair( channel.getNum(), channel.getQueue().makeReader() );
        pairList.add(pair);
    }


    public void update() {
        if (packetCount == 0) {
            writer.write(getFileHeaderCallback.get());
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
        writer.write(bb_packet);
    }

    public void close() {
        writer.close();
    }
}