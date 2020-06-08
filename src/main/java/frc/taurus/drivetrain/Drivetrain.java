package frc.taurus.drivetrain;

import frc.taurus.messages.QueueListener;

public class Drivetrain implements QueueListener {
  // singleton pattern
  private static Drivetrain instance = null;
  public static Drivetrain getInstance() {
    if (instance == null) {
      instance = new Drivetrain();
    }
    return instance;
  }

  private Drivetrain() {

  }

  public void newMessage() {
    // wait for both Drivetrain Status and Drivetrain Goal messages 
    // Drivetrain Status: robot pose, sensor readings
    // Drivetrain Goal: desired actions from autonomous control or operator control

  }
}