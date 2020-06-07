package frc.taurus.config;

import java.nio.ByteBuffer;
import java.util.HashMap;

import frc.taurus.logger.LoggerManager;
import frc.taurus.messages.MessageQueue;

public class ChannelManager {
  // singleton pattern
  private static ChannelManager instance = null;
  public static ChannelManager getInstance() {
    if (instance == null) {
      instance = new ChannelManager();
    }
    return instance;
  }

  // HashMap has high performance contains() and get(), needed by fetch()
  HashMap<ChannelIntf, MessageQueue<ByteBuffer>> channelMap = new HashMap<ChannelIntf, MessageQueue<ByteBuffer>>();
  LoggerManager loggerManager;

  private ChannelManager() {
    loggerManager = LoggerManager.getInstance();    
  }

  /**
   * Call reset at the beginning of every Unit Test to make sure stale data
   * from the previous test causes problems with the current test
   */
  public void reset() {
    channelMap.clear();
    loggerManager.reset();
  }

  private void register(ChannelIntf channel) {
    // add the new channel to our list
    if (!channelMap.keySet().contains(channel)) {
      this.channelMap.put(channel, new MessageQueue<ByteBuffer>());
    }
  }

  public MessageQueue<ByteBuffer> fetch(ChannelIntf channel) {
    if (!channelMap.keySet().contains(channel)) {
      this.register(channel);
      loggerManager.register(channel);
    }
    return channelMap.get(channel);
  }

  public void update() {
    loggerManager.update();
  }

  public void close() {
    loggerManager.close();
  }

}