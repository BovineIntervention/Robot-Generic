package frc.robot;

public class Constants
{
    static public class ControllerConstants
    {
        static public double kDriveDeadband = 0.05;

        // example joystick config when driver uses single Xbox controller
        static public class ControllerConfig1 {
            static public int kDriveControllerPort = 0;
            static public int kOperatorControllerPort = 1;
        }
        
        // example joystick config when driver uses dual Thrustmaster controllers
        static public class ControllerConfig2 {
            static public int kDriverLeftControllerPort = 0;
            static public int kDriverRightControllerPort = 1;
            static public int kOperatorControllerPort = 2;
        }
    }

    static public class DriveConstants
    {

    }
}