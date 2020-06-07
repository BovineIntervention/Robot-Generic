package frc.taurus.joystick;

import java.util.ArrayList;

import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Timer;
import frc.taurus.config.ChannelManager;
import frc.taurus.config.Config;
import frc.taurus.drivetrain.generated.DrivetrainGoal;

/**
 * The user controls required for drivetrain motion.
 * These are likely to remain the same from year to year, but some special functions may be needed some years.
 */

public abstract class DriverControlsBase implements IDriverControls {

  protected SteeringMethods.LeftRightMotor lrMotor;

  int bufferSize = 0;

  public void writeDrivetrainGoalMessage() {
    // send a DrivetrainGoal message
    float lMotor = (float)getLeft();
    float rMotor = (float)getRight();
    boolean quickTurn = getQuickTurn();
    boolean lowGear = getLowGear();    

    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);
    double timestamp = Timer.getFPGATimestamp();
    int offset = DrivetrainGoal.createDrivetrainGoal(builder, timestamp, lMotor, rMotor, !lowGear, quickTurn);
    builder.finish(offset);

    ChannelManager.getInstance().fetch(Config.DRIVETRAIN_GOAL).write(builder.dataBuffer()); 
  }

  /**
   * List of controllers to be registered with the ControllerManager
   * @return list of all physical controllers utilized by this Driver/Operator control scheme
   */
  abstract public ArrayList<Controller> getControllersList();
}