package frc.taurus.config;

import java.nio.ByteBuffer;

import frc.taurus.messages.MessageQueue;

/**
 * TestConfig is used for unit testing in place of frc.taurus.config.Config
 */

public enum TestConfig implements ChannelIntf {
    TEST_MESSAGE_1              (10001,      "TestMessage1", "test.log" ),  // using large num to avoid conflicts with the normal Config
    TEST_MESSAGE_2              (10002,      "TestMessage2", "test.log" ),
    TEST_MESSAGE_3              (10003,      "TestMessage3", "test3.log" ),
    TEST_MESSAGE_4              (10004,      "TestMessage4", "test4.log" );

    private final int num;
    private final String name;
    private final String logFilename;
    private final MessageQueue<ByteBuffer> queue;

    TestConfig(final int num, final String name, final String logFilename) {
        this.num = num;
        this.name = name;       // can't use ChannelType.name() for test  
        this.logFilename = logFilename;
        this.queue = new MessageQueue<ByteBuffer>(){};
    }

    public int getNum() { return num; }
    public String getName() { return name; }
    public String getLogFilename() { return logFilename; }
    public MessageQueue<ByteBuffer> getQueue() { return queue; }  
}

