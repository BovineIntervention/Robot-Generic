package frc.taurus.config;

/**
 * TestConfig is used for unit testing in place of frc.taurus.config.Config
 */

public enum TestConfig implements ChannelIntf {
  TEST_MESSAGE_1((short) 10001, "TestMessage1", "test12.log"), // using large num to avoid conflicts with the normal Config
  TEST_MESSAGE_2((short) 10002, "TestMessage2", "test12.log"),
  TEST_MESSAGE_3((short) 10003, "TestMessage3", "test3.log"),
  TEST_MESSAGE_4((short) 10004, "TestMessage4", "test4.log");

  private final short num;
  private final String name;
  private final String logFilename;

  TestConfig(final short num, final String name, final String logFilename) {
    this.num = num;
    this.name = name; // can't use ChannelType.name() for test
    this.logFilename = logFilename;
  }

  public short getNum() {
    return num;
  }

  public String getName() {
    return name;
  }

  public String getLogFilename() {
    return logFilename;
  }

  public static TestConfig findTestConfig(final short findNum) {
    for (TestConfig config : values()) {
      if (config.num == findNum) {
        return config;
      }
    }
    throw new IllegalArgumentException(String.valueOf(findNum));
  }
}
