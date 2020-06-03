package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.google.flatbuffers.FlatBufferBuilder;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
    public short channelType;
    public MessageQueue<ByteBuffer>.QueueReader reader;

    ChannelTypeReaderPair(final short channelType, final MessageQueue<ByteBuffer>.QueueReader reader) {
      this.channelType = channelType;
      this.reader = reader;
    }
  }

  final String filename;
  final Supplier<ByteBuffer> getFileHeaderCallback;
  ArrayList<Pair<Short, MessageQueue<ByteBuffer>.QueueReader>> queueTypeReaderPair = new ArrayList<Pair<Short, MessageQueue<ByteBuffer>.QueueReader>>();

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
    var pair = new ImmutablePair<>(channel.getNum(), channel.getQueue().makeReader());
    queueTypeReaderPair.add(pair);
  }

  public void update() {
    if (packetCount == 0) {
      writer.write(getFileHeaderCallback.get());
    }
    for (var pair : queueTypeReaderPair) {
      var channelType = pair.getKey();
      var reader = pair.getValue();
      while (!reader.isEmpty()) {
        ByteBuffer bb = reader.read().get(); // we know Optional::isPresent() is true because of earlier !isEmpty()
        writePacket(channelType, bb); // write to file
      }
    }
    writer.flush();
  }

  public void writePacket(final short channelType, final ByteBuffer bb_payload) {
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