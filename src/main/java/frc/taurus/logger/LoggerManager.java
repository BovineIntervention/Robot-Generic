package frc.taurus.logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.taurus.config.ChannelIntf;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.config.generated.Channel;
import frc.taurus.config.generated.Configuration;
import frc.taurus.driverstation.generated.DriverStationStatus;
import frc.taurus.logger.generated.LogFileHeader;
import frc.taurus.messages.MessageQueue;

public class LoggerManager {

  ChannelManager channelManager;

  // synchronized methods in this class to protect channelList and loggerMap
  ArrayList<ChannelIntf> channelList = new ArrayList<ChannelIntf>();
  HashMap<String, FlatBuffersLogger> loggerMap = new HashMap<>();

  private final Notifier notifier;  // notifier will execute run() with a period of kLoopDt
  private boolean running;

  private final Runnable runnable = new Runnable() {
    @Override
    public void run() {
      if (running) {
        update();
      }
    }
  };

  public synchronized void start() {
    if (!running) {
      running = true;
      notifier.startPeriodic(Constants.kLoopDt);
    }
  }

  public synchronized void stop() {
    if (running) {
      notifier.stop();
      running = false;
    }
  }

  public LoggerManager(ChannelManager channelManager) {
    this.channelManager = channelManager;
    notifier = new Notifier(runnable);
    running = false;
  }

  // called when ChannelManager.fetch() is called by robot code
  public synchronized void register(ChannelIntf channel) {
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
        loggerMap.put(filename, new FlatBuffersLogger(channelManager, filename, this::getFileHeader));
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

  public synchronized void update() {
    updateLogFolderTimestamp();

    for (var logger : loggerMap.values()) {
      logger.update();
    }
  }

 
  public synchronized void close() {
    for (var logger : loggerMap.values()) {
      logger.close();
    }
  }

  public synchronized void reset() {
    close();
    channelList.clear();
    loggerMap.clear();
  }


  boolean enabledLast = true; // set so that we will immediately detect being disabled
  boolean autoLast = false;
  boolean teleopLast = false;
  boolean testLast = false;

  public void updateLogFolderTimestamp() {
    MessageQueue<ByteBuffer> driverStationStatusQueue = channelManager.fetch(Config.DRIVER_STATION_STATUS);
    Optional<ByteBuffer> obb = driverStationStatusQueue.readLast();
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

  public synchronized ByteBuffer getFileHeader() {
  
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