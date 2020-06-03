package frc.taurus.config;

import java.nio.ByteBuffer;
import java.util.HashSet;

import frc.taurus.logger.LoggerManager;
import frc.taurus.messages.MessageQueue;

public class ChannelManager {

  HashSet<ChannelIntf> channelList = new HashSet<ChannelIntf>(); // HashSet has high performance contains() and get(), needed by fetch()
  LoggerManager loggerManager = new LoggerManager();

  public ChannelManager() {
  }

  private void register(ChannelIntf channel) {
    // important: need to reset queues before use (otherwise you will read last session's data)
    channel.getQueue().clear();     

    // add the new channel to our list
    if (!channelList.contains(channel)) {
      this.channelList.add(channel);
    }
  }

  public MessageQueue<ByteBuffer> fetch(ChannelIntf channel) {
    if (!channelList.contains(channel)) {
      this.register(channel);
      loggerManager.register(channel);
    }
    return channel.getQueue();
  }

  public void update() {
    loggerManager.update();
  }

  public void close() {
    loggerManager.close();
  }

}