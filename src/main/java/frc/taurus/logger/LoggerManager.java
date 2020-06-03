package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelIntf;
import frc.taurus.config.Config;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.logger.generated.LoggerStatus;

// TODO: separate thread for loggers



public class LoggerManager {

  SortedMap<String, FlatBuffersLogger> loggerMap = new TreeMap<>();
  ArrayList<ChannelIntf> channelList = new ArrayList<ChannelIntf>();

  public LoggerManager() {
    register(Config.LOGGER_STATUS);
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
    updateLoggerStatus();

    for (var logger : loggerMap.values()) {
      logger.update();
    }
  }

  int bufferSize = 0;
  
  public void updateLoggerStatus() {
    // FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);
    // LoggerStatus.startLoggerStatus(builder);

    // int offsets[] = new int[loggerMap.size()];
    // for (var logger : loggerMap) {
    //   LogStatus.createLogStatus(builder, logger..getType(), logger) {

    //   short[] numMessagesUnread = new short[loggerMap.size()];
    // for (int k = 0; k < axes.length; k++) {
    //   axes[k] = (float) getAxis(k);   // axis IDs are base 0
    // }

    // boolean[] buttons = new boolean[16];
    // for (int k = 0; k < buttons.length; k++) {
    //   buttons[k] = getButton(k + 1);  // button IDs are base 1, not base 0
    // }

    // JoystickStatus.addTimestamp(builder, Timer.getFPGATimestamp());
    // JoystickStatus.addPort(builder, getPort());
    // JoystickStatus.addAxes(builder, AxisVector.createAxisVector(builder, axes));
    // JoystickStatus.addButtons(builder, ButtonVector.createButtonVector(builder, buttons));
    // JoystickStatus.addPov(builder, getPOV(0));
    // int offset = JoystickStatus.endJoystickStatus(builder);

    // JoystickStatus.finishJoystickStatusBuffer(builder, offset);
    // ByteBuffer bb = builder.dataBuffer();
    // bufferSize = Math.max(bufferSize, bb.remaining()); // correct buffer size for next time

    // statusQueue.write(bb);    
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

  public ByteBuffer getFileHeader() {
    FlatBufferBuilder builder = new FlatBufferBuilder(256);

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

}