package frc.taurus.config;

import java.nio.ByteBuffer;
import java.util.HashMap;

import frc.taurus.logger.LoggerManager;
import frc.taurus.messages.MessageQueue;

// TODO: see if we can avoid making ChannelManager a singleton, since we are passing it to lots of classes in RobotInit()

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


  public MessageQueue<ByteBuffer> fetch(ChannelIntf channel) {
    if (!channelMap.keySet().contains(channel)) {
      this.register(channel);
      loggerManager.register(channel);
    }
    return channelMap.get(channel);
  }

  public MessageQueue<ByteBuffer> fetchJoystickStatusQueue(int port) {
    switch (port) {
      case 1:   return fetch(Config.JOYSTICK_PORT_1_STATUS);
      case 2:   return fetch(Config.JOYSTICK_PORT_2_STATUS); 
      case 3:   return fetch(Config.JOYSTICK_PORT_3_STATUS); 
      case 4:   return fetch(Config.JOYSTICK_PORT_4_STATUS); 
      default:
        throw new IllegalArgumentException("Joystick port must be 1-4");
    }
  }

  public MessageQueue<ByteBuffer> fetchJoystickGoalQueue(int port) {
    switch (port) {
      case 1:   return fetch(Config.JOYSTICK_PORT_1_GOAL);
      case 2:   return fetch(Config.JOYSTICK_PORT_2_GOAL); 
      case 3:   return fetch(Config.JOYSTICK_PORT_3_GOAL); 
      case 4:   return fetch(Config.JOYSTICK_PORT_4_GOAL); 
      default:
        throw new IllegalArgumentException("Joystick port must be 1-4");
    }
  }  

  private void register(ChannelIntf channel) {
    // add the new channel to our list
    if (!channelMap.keySet().contains(channel)) {
      this.channelMap.put(channel, new MessageQueue<ByteBuffer>());
    }
  }
  
  public void update() {
    loggerManager.update();
  }

  public void close() {
    loggerManager.close();
  }

}