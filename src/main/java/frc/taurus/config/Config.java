package frc.taurus.config;

import frc.taurus.config.ChannelIntf;
import frc.taurus.config.ChannelType;
import frc.taurus.joystick.JoystickGoal;
import frc.taurus.joystick.JoystickStatus;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.TestMessage;
import frc.taurus.messages.TestMessage2;

/**
 * Channels used in the code need to be listed here.  
 * Everything in this list must appear in ChannelType.fbs
 * Not everything in ChannelType.fbs needs to appear here
 * (ChannelType is a superset of Config)
 */

public enum Config implements ChannelIntf {
    DRIVER_JOYSTICK_STATUS      (ChannelType.JoystickStatus,    "joystick.log",     new MessageQueue<JoystickStatus>(){} ),
    DRIVER_JOYSTICK_GOAL        (ChannelType.JoystickGoal,      "joystick.log",     new MessageQueue<JoystickGoal>(){} ),
    OPERATOR_JOYSTICK_STATUS    (ChannelType.JoystickStatus,    "joystick.log",     new MessageQueue<JoystickStatus>(){} ),
    OPERATOR_JOYSTICK_GOAL      (ChannelType.JoystickGoal,      "joystick.log",     new MessageQueue<JoystickGoal>(){} ),
    TEST_MESSAGE    (ChannelType.TestMessage,       "test.log",         new MessageQueue<TestMessage>(){} ),
    TEST_MESSAGE_2  (ChannelType.TestMessage2,      "test.log",         new MessageQueue<TestMessage2>(){} );

    private final int num;
    private final String name;
    private final String logFilename;
    private final MessageQueue<?> queue;

    Config(final int num, final String logFilename, final MessageQueue<?> queue) {
        this.num = num;
        this.name = ChannelType.name(num);        
        this.logFilename = logFilename;
        this.queue = queue;
    }

    public int getNum() { return num; }
    public String getName() { return name; }
    public String getLogFilename() { return logFilename; }
    public MessageQueue<?> getQueue() { return queue; }  
}

