package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelIntf;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.driverstation.generated.DriverStationStatus;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.messages.MessageQueue;

// TODO: separate single thread for LoggerManager and Loggers

public class LoggerManager {

  SortedMap<String, FlatBuffersLogger> loggerMap = new TreeMap<>();
  ArrayList<ChannelIntf> channelList = new ArrayList<ChannelIntf>();
  MessageQueue<ByteBuffer> dsStatusQueue;

  public LoggerManager(ChannelIntf driverStationStatusChannel) {
    dsStatusQueue = driverStationStatusChannel.getQueue();
  }

  // called when ChannelManager.fetch() is called by robot code
  public void register(ChannelIntf channel) {
    if (!channelList.contains(channel)) {
      channelList.add(channel);
      openLogger(channel);
    }
  }

  private void openLogger(ChannelIntf channel) {
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

  // called when we switch folders between auto/teleop/test
  private void relocateLoggers(final String suffix) {
    for (var logger : loggerMap.values()) {
      logger.relocate(suffix);
    }
  }  

  public void update() {
    updateLogFolderTimestamp();

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


  boolean enabledLast = true; // set so that we will immediately detect being disabled
  boolean autoLast = false;
  boolean teleopLast = false;
  boolean testLast = false;

  public void updateLogFolderTimestamp() {
    Optional<ByteBuffer> obb = dsStatusQueue.readLast();
    if (obb.isPresent()) {
      DriverStationStatus dsStatus = DriverStationStatus.getRootAsDriverStationStatus(obb.get());
  
      boolean enabled = dsStatus.enabled();
      boolean auto = dsStatus.autonomous();
      boolean teleop = dsStatus.teleop();
      boolean test = dsStatus.test();

      // if we start autonomous, teleop, or test, create a new folder
      if (!enabled && enabledLast) {
        close();
        relocateLoggers("");
      } else if (auto && !autoLast) {
        relocateLoggers("auto");
      } else if (teleop && !teleopLast) {
        relocateLoggers("teleop");
      } else if (test && !testLast) {
        relocateLoggers("test");
      }

      enabledLast = enabled;
      autoLast = auto;
      teleopLast = teleop;
      testLast = test;
    }
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