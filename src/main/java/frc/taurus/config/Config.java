package frc.taurus.config;

import frc.taurus.config.generated.ChannelType;

/**
 * Channels used in the code need to be listed here.  
 * Everything in this list must appear in ChannelType.fbs
 * Not everything in ChannelType.fbs needs to appear here
 * (ChannelType is a superset of Config)
 */

public enum Config implements ChannelIntf {
    DRIVER_STATION_STATUS           (ChannelType.DriverStationStatus, "driver_station.log"),
    DRIVER_JOYSTICK_STATUS          (ChannelType.JoystickStatus,      "joystick_driver.log" ),
    DRIVER_JOYSTICK_GOAL            (ChannelType.JoystickGoal,        "joystick_driver.log" ),
    OPERATOR_JOYSTICK_STATUS        (ChannelType.JoystickStatus,      "joystick_operator.log" ),
    OPERATOR_JOYSTICK_GOAL          (ChannelType.JoystickGoal,        "joystick_operator.log" ),
    DRIVETRAIN_GOAL                 (ChannelType.DrivetrainGoal,      "drivetrain.log");

    private final byte num;
    private final String name;
    private final String logFilename;

    Config(final byte num, final String logFilename) {
        this.num = num;
        this.name = ChannelType.name(num);        
        this.logFilename = logFilename;
    }

    public byte getNum() { return num; }
    public String getName() { return name; }
    public String getLogFilename() { return logFilename; }
}

