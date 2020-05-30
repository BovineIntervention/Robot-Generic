package frc.taurus.config;

import java.nio.ByteBuffer;

import frc.taurus.messages.MessageQueue;

/**
 * Channels used in the code need to be listed here.  
 * Everything in this list must appear in ChannelType.fbs
 * Not everything in ChannelType.fbs needs to appear here
 * (ChannelType is a superset of Config)
 */

public enum Config implements ChannelIntf {
    DRIVER_JOYSTICK_STATUS      (ChannelType.JoystickStatus,    "joystick.log" ),
    DRIVER_JOYSTICK_GOAL        (ChannelType.JoystickGoal,      "joystick.log" ),
    OPERATOR_JOYSTICK_STATUS    (ChannelType.JoystickStatus,    "joystick.log" ),
    OPERATOR_JOYSTICK_GOAL      (ChannelType.JoystickGoal,      "joystick.log" ),
    TEST_MESSAGE                (ChannelType.TestMessage,       "test.log" ),
    TEST_MESSAGE_2              (ChannelType.TestMessage2,      "test.log" );

    private final int num;
    private final String name;
    private final String logFilename;
    private final MessageQueue<ByteBuffer> queue;

    Config(final int num, final String logFilename) {
        this.num = num;
        this.name = ChannelType.name(num);        
        this.logFilename = logFilename;
        this.queue = new MessageQueue<ByteBuffer>(){};
    }

    public int getNum() { return num; }
    public String getName() { return name; }
    public String getLogFilename() { return logFilename; }
    public MessageQueue<ByteBuffer> getQueue() { return queue; }  
}

