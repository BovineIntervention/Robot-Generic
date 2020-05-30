package frc.taurus.messages;

import frc.taurus.config.ChannelIntf;
import frc.taurus.config.ChannelType;


public enum TestConfig implements ChannelIntf {
    TEST_MESSAGE   (ChannelType.TestMessage,     "test.log",     new MessageQueue<TestMessage>(){} ),
    TEST_MESSAGE_2 (ChannelType.TestMessage2,    "test.log",     new MessageQueue<TestMessage2>(){} );

    private final int num;
    private final String name;
    private final String logFilename;
    private final MessageQueue<?> queue;

    TestConfig(final int num, final String logFilename, final MessageQueue<?> queue) {
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

