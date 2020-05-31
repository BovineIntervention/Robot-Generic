package frc.taurus.config;

import java.nio.ByteBuffer;
import java.util.HashSet;

import frc.taurus.logger.LoggerManager;
import frc.taurus.messages.MessageQueue;

public class ChannelManager {
	// singleton class
	private static ChannelManager instance = null;
	public static ChannelManager getInstance() {
		if (instance == null) {
			instance = new ChannelManager();
		}
		return instance;
	}

    HashSet<ChannelIntf> channels = new HashSet<ChannelIntf>();     // HashSet has high performance contains() and get(), needed by fetch()
    LoggerManager loggerManager = new LoggerManager();

    private ChannelManager() {}    

    void register(ChannelIntf channel) {
        if (!channels.contains(channel)) {
            this.channels.add(channel);
        }
    }

    public void reset() {
        channels.clear();
        loggerManager.reset();
    }

    public MessageQueue<ByteBuffer> fetch(ChannelIntf channel) {
System.out.println("fetch " + channel.getNum() + " : " + channel.getName());    
        if (!channels.contains(channel)) {
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

    public String getLogFilename(ChannelIntf channel) {
        return loggerManager.getLogFilename(channel);
    }


}