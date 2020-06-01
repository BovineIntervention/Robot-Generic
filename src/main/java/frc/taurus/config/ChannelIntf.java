package frc.taurus.config;

import java.nio.ByteBuffer;

import frc.taurus.messages.MessageQueue;

public interface ChannelIntf {
    public int getNum();
    public String getName();
    public String getLogFilename();
    public MessageQueue<ByteBuffer> getQueue();
}
