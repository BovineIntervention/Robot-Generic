package frc.taurus.config;

import java.util.ArrayList;

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

    ArrayList<ChannelIntf> channels = new ArrayList<ChannelIntf>();
    LoggerManager loggerManager = new LoggerManager();

    private ChannelManager() {}    

    void register(ChannelIntf channel) {
        if (!channels.contains(channel)) {
            this.channels.add(channel);
        }
    }

    public MessageQueue<?> fetch(ChannelIntf channel) {
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