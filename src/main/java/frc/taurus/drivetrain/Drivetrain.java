package frc.taurus.drivetrain;

import java.nio.ByteBuffer;
import java.util.Optional;

import frc.taurus.drivetrain.generated.DrivetrainGoal;
import frc.taurus.drivetrain.generated.GoalType;
import frc.taurus.drivetrain.generated.TeleopGoal;
import frc.taurus.messages.MessageQueue;
import frc.taurus.messages.QueueListener;

public class Drivetrain implements QueueListener {

  MessageQueue<ByteBuffer>.QueueReader goalReader;

  public Drivetrain(MessageQueue<ByteBuffer> goalQueue) {
    goalReader = goalQueue.makeReader();
  }

  public void newMessage() {
    // wait for both Drivetrain Status and Drivetrain Goal messages 
    // Drivetrain Status: robot pose, sensor readings
    // Drivetrain Goal: desired actions from autonomous control or operator control

  }

  public void update() {
    Optional<ByteBuffer> obb = goalReader.readLast();

    if (obb.isPresent()) {
      DrivetrainGoal drivetrainGoal = DrivetrainGoal.getRootAsDrivetrainGoal(obb.get());
      boolean highGear = drivetrainGoal.highGear();
      boolean quickTurn = drivetrainGoal.quickTurn();

      switch (drivetrainGoal.goalType()) {
        case GoalType.NONE:
          break;

        case GoalType.TeleopGoal:
          TeleopGoal teleopGoal = (TeleopGoal)drivetrainGoal.goal(new TeleopGoal());
          openLoop(teleopGoal.leftSpeed(), teleopGoal.rightSpeed(), highGear, quickTurn);
          break;
      }
    }
  }

  private void openLoop(double left, double right, boolean highGear, boolean quickTurn) {

  }

}