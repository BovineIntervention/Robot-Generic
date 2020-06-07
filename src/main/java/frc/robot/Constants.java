package frc.robot;

public class Constants {
  public static double kLoopDt = 0.010; // 100x per second

  public static class ControllerConstants {
    public static double kDriveDeadband = 0.05;

    // example joystick config when driver uses single Xbox controller
    public static class ControllerConfig1 {
      public static int kDriveControllerPort = 0;
      public static int kOperatorControllerPort = 1;
    }

    // example joystick config when driver uses dual Thrustmaster controllers
    public static class ControllerConfig2 {
      public static int kDriverLeftControllerPort = 0;
      public static int kDriverRightControllerPort = 1;
      public static int kOperatorControllerPort = 2;
    }
  }

  public static class DriveConstants {

  }
}