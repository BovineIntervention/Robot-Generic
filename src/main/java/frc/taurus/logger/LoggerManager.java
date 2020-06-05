package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelIntf;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.messages.MessageQueue;

// TODO: separate single thread for LoggerManager and Loggers

public class LoggerManager {

  SortedMap<String, FlatBuffersLogger> loggerMap = new TreeMap<>();
  ArrayList<ChannelIntf> channelList = new ArrayList<ChannelIntf>();
  MessageQueue<ByteBuffer> statusQueue;

  public LoggerManager(ChannelIntf loggerStatusChannel) {
    statusQueue = loggerStatusChannel.getQueue();
    register(loggerStatusChannel);
  }

  // TODO: add timestamp to filename or folder
  public void register(ChannelIntf channel) {
    if (!channelList.contains(channel)) {
      channelList.add(channel);
      createLogger(channel);
    }
  }

  void createLogger(ChannelIntf channel) {
    // get the filename listed in Config
    String filename = channel.getLogFilename();

    // if log filename is not empty, log it
    if (!filename.isEmpty()) {
      // if filename has not been seen before, create a logger for that file
      if (!loggerMap.containsKey(filename)) {
        loggerMap.put(filename, new FlatBuffersLogger(filename, this::getFileHeader));
      }
      FlatBuffersLogger logger = loggerMap.get(filename);
      logger.register(channel);
    }
  }

  public void update() {
    for (var logger : loggerMap.values()) {
      logger.update();
    }
  }

 
  public void close() {
    for (var logger : loggerMap.values()) {
      logger.close();
    }
  }

  public void reset() {
    close();
    channelList.clear();
    loggerMap.clear();
  }


  int bufferSize = 0;

  public ByteBuffer getFileHeader() {
  
    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);

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
    int offset = LogFileHeader.createLogFileHeader(builder, Timer.getFPGATimestamp(), configOffset);
    LogFileHeader.finishSizePrefixedLogFileHeaderBuffer(builder, offset);
    ByteBuffer fileHeader = builder.dataBuffer();

    bufferSize = Math.max(bufferSize, fileHeader.remaining());

    return fileHeader;
  }

}