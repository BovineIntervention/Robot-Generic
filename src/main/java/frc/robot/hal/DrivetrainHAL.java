package frc.robot.hal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMUConfiguration;
import com.google.flatbuffers.FlatBufferBuilder;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.DriveConstants;
import frc.taurus.driverstation.generated.DriverStationStatus;
import frc.taurus.drivetrain.generated.DrivetrainInput;
import frc.taurus.drivetrain.generated.DrivetrainOutput;
import frc.taurus.drivetrain.generated.TalonControlMode;
import frc.taurus.messages.MessageQueue;

/**
 * Drivetrain Hardware Abstraction Layer (HAL)
 * 
 * This is the layer provides separation from the core robot code and the actual
 * hardware.
 * 
 * This allows us to reuse the core robot code from year to year. It allows us
 * to write unit tests for the core robot code. It should lead to some pretty
 * good core robot code. It should make the initial software integration go
 * faster (you know, when build finally finishes).
 * 
 * This HAL code will probably need to be rewritten each year.
 */
public class DrivetrainHAL implements IHAL {

  //-----------------------------------	
  // Wheel Encoders
  //-----------------------------------	
  public static double kQuadEncoderGain = 1.0; 	// number of drive shaft rotations per encoder shaft rotation
													                      // 1.0 if encoder is directly coupled to the drive shaft
	public static int kQuadEncoderCodesPerRev = 1024;
	public static int kQuadEncoderUnitsPerRev = (int) (4 * kQuadEncoderCodesPerRev / kQuadEncoderGain);
	public static double kQuadEncoderStatusFramePeriod = 0.100; // 100 ms

  //-----------------------------------
  // Message Queues & Readers
  //-----------------------------------
  MessageQueue<ByteBuffer> inputQueue; // to store sensor values
  MessageQueue<ByteBuffer>.QueueReader driverStationReader; // to store sensor values
  MessageQueue<ByteBuffer>.QueueReader outputReader; // to read actuator values

  //-----------------------------------
  // Driver Motor Controllers
  //-----------------------------------
  final int kDeviceIdLeftMaster  = 1;
  final int kDeviceIdLeftSlaveA  = 2;
  final int kDeviceIdLeftSlaveB  = 3;

  final int kDeviceIdRightMaster = 4;
  final int kDeviceIdRightSlaveA = 5;
  final int kDeviceIdRightSlaveB = 6;

  public final  TalonSRX lMaster = new  TalonSRX(kDeviceIdLeftMaster);
  public final VictorSPX lSlaveA = new VictorSPX(kDeviceIdLeftSlaveA);
  public final VictorSPX lSlaveB = new VictorSPX(kDeviceIdLeftSlaveB);

  public final  TalonSRX rMaster = new  TalonSRX(kDeviceIdRightMaster);
  public final VictorSPX rSlaveA = new VictorSPX(kDeviceIdRightSlaveA);
  public final VictorSPX rSlaveB = new VictorSPX(kDeviceIdRightSlaveB);
  
  private static final int kPrimaryPidIdx   = 0;
  private static final int kAuxiliaryPidIdx = 1;

  private static final int kVelocitySlot = 0;
  private static final int kPositionSlot = 1;
  private static final int kTurningSlot  = 2;

  private static final double kCruiseVelocity = 60;		    // in/sec  cruise below top speed
  private static final double timeToCruiseVelocity = 0.5; // seconds
  private static final double kMaxAcceleration = kCruiseVelocity / timeToCruiseVelocity; 

  private static final double kOpenLoopRampRate = 0.5;  // min time from neutral to max velocity
  private static final double kClosedLoopRampRate = 0;  // ramp rate controlled by velocity profile

  //-----------------------------------
  // Shifter
  //-----------------------------------
  final int kShifterForwardChannel = 0;
  Solenoid shifter = new Solenoid(kShifterForwardChannel);

  //-----------------------------------
  // Gyro
  //-----------------------------------
  final int kDeviceIdPigeon = 0;
  PigeonIMU pigeon = new PigeonIMU(kDeviceIdPigeon);
  double pigeonCalOffset = 0;

  //-----------------------------------
  // Air compressor
  //-----------------------------------
  Compressor compressor = new Compressor();


  private static final int kSetupTimeoutMs = 100;



  public DrivetrainHAL(MessageQueue<ByteBuffer> dsQueue, MessageQueue<ByteBuffer> inputQueue,
      MessageQueue<ByteBuffer> outputQueue) {

    this.inputQueue = inputQueue;
    this.outputReader = outputQueue.makeReader();

    ArrayList<TalonSRX> masters = new ArrayList<>();
    masters.add(lMaster);
    masters.add(rMaster);

    ArrayList<VictorSPX> slaves = new ArrayList<>();
    slaves.add(lSlaveA);    
    slaves.add(lSlaveB);
    slaves.add(rSlaveA);    
    slaves.add(rSlaveB);



    //-----------------------------------
    // Configure Master Motor Controllers
    //-----------------------------------
    for (var master : masters) {
      // configures all persistent settings to a default value
      TalonSRXConfiguration config = new TalonSRXConfiguration();      
      master.configAllSettings(config);        

      // encoders used for drivtrain feedback
      master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kPositionSlot, kSetupTimeoutMs);      
      master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kVelocitySlot, kSetupTimeoutMs);      
      // no limit switches for drivetrain
      master.overrideLimitSwitchesEnable(false);

      // enable voltage compensation
      master.enableVoltageCompensation(true);
      master.configVoltageCompSaturation(12.0, kSetupTimeoutMs);
      master.configVoltageMeasurementFilter(32);  // samples

      // rampm rates
      master.configOpenloopRamp(kOpenLoopRampRate, kSetupTimeoutMs);
      master.configClosedloopRamp(kClosedLoopRampRate, kSetupTimeoutMs);  // controlled by profile

      // Motion Magic
      master.configMotionCruiseVelocity(inchesPerSecondToEncoderUnitsPerFrame(kCruiseVelocity), kSetupTimeoutMs);
      master.configMotionAcceleration(inchesPerSecondToEncoderUnitsPerFrame(kMaxAcceleration), kSetupTimeoutMs);

      master.configPeakCurrentLimit(35, kSetupTimeoutMs);       // amps
      master.configPeakCurrentDuration(200, kSetupTimeoutMs);   // ms
      master.configContinuousCurrentLimit(35, kSetupTimeoutMs); // amps

      // auxiliary PID for point turns
      master.configAuxPIDPolarity(false, kSetupTimeoutMs);
      master.configClosedLoopPeakOutput(kTurningSlot, 1.0, kSetupTimeoutMs);

      // increase status update rate to 100Hz (default 50 Hz)
      master.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, kSetupTimeoutMs);
      // master.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, kSetupTimeoutMs);
      // master.configVelocityMeasurementWindow(32, kSetupTimeoutMs);

      loadGains(master);
    }

    // configure polarities
    lMaster.setInverted(false);
    lMaster.setSensorPhase(false);
    rMaster.setInverted(false);
    rMaster.setSensorPhase(false);


    //-----------------------------------
    // Configure Slave Motor Controllers
    //-----------------------------------

    for (var slave : slaves) {
      VictorSPXConfiguration config = new VictorSPXConfiguration();
      slave.configAllSettings(config);  // configures all persistent settings to a default value
    }

    // configure followers
    lSlaveA.follow(lMaster);
    lSlaveB.follow(lMaster);
    rSlaveA.follow(rMaster);
    rSlaveB.follow(rMaster);
    lSlaveA.setInverted(InvertType.FollowMaster);
    lSlaveB.setInverted(InvertType.FollowMaster);
    rSlaveA.setInverted(InvertType.FollowMaster);
    rSlaveB.setInverted(InvertType.FollowMaster);
    

    //-----------------------------------
    // Configure Gyro
    //-----------------------------------
    PigeonIMUConfiguration pigeonConfig = new PigeonIMUConfiguration();
    pigeon.configAllSettings(pigeonConfig);



    //-----------------------------------
    // Zero Sensors
    //-----------------------------------
    zeroSensors();

    //-----------------------------------
    // Initial Actuator Settings
    //-----------------------------------
    setBrakeMode(false);
    stop();
  }


  private static final double kHighGearPositionP = 1.0;
  private static final double kHighGearPositionI = 0;
  private static final double kHighGearPositionD = 0;
  private static final double kHighGearPositionF = 0;
  private static final int kPositionIZone = 0;
  private static final int kPositionAllowableError = 0;

  private static final double kHighGearVelocityP = 1.0;
  private static final double kHighGearVelocityI = 0;
  private static final double kHighGearVelocityD = 0;
  private static final double kHighGearVelocityF = 0;
  private static final int kVelocityIZone = 0;
  private static final int kVelocityAllowableError = 0;

  private static final double kHighGearTurningP = 1.0;
  private static final double kHighGearTurningI = 0;
  private static final double kHighGearTurningD = 0;
  private static final double kHighGearTurningF = 0;
  private static final int kTurningIZone = 400;
  private static final int kTurningAllowableError = 0;

  public void loadGains(TalonSRX master) {
    // Position PID gains
    master.config_kP(kPositionSlot, kHighGearPositionP, kSetupTimeoutMs);
    master.config_kI(kPositionSlot, kHighGearPositionI, kSetupTimeoutMs);
    master.config_kD(kPositionSlot, kHighGearPositionD, kSetupTimeoutMs);
    master.config_kF(kPositionSlot, kHighGearPositionF, kSetupTimeoutMs);
    master.config_IntegralZone(kPositionSlot, kPositionIZone, kSetupTimeoutMs);
    master.configAllowableClosedloopError(kPositionSlot, kPositionAllowableError, kSetupTimeoutMs);

    // Velocity PID gains
    master.config_kP(kVelocitySlot, kHighGearVelocityP, kSetupTimeoutMs);
    master.config_kI(kVelocitySlot, kHighGearVelocityI, kSetupTimeoutMs);
    master.config_kD(kVelocitySlot, kHighGearVelocityD, kSetupTimeoutMs);
    master.config_kF(kVelocitySlot, kHighGearVelocityF, kSetupTimeoutMs);
    master.config_IntegralZone(kVelocitySlot, kVelocityIZone, kSetupTimeoutMs);
    master.configAllowableClosedloopError(kVelocitySlot, kVelocityAllowableError, kSetupTimeoutMs);

    // Turning PID gains
    master.config_kP(kTurningSlot, kHighGearTurningP, kSetupTimeoutMs);
    master.config_kI(kTurningSlot, kHighGearTurningI, kSetupTimeoutMs);
    master.config_kD(kTurningSlot, kHighGearTurningD, kSetupTimeoutMs);
    master.config_kF(kTurningSlot, kHighGearTurningF, kSetupTimeoutMs);
    master.config_IntegralZone(kTurningSlot, kTurningIZone, kSetupTimeoutMs);
    master.configAllowableClosedloopError(kTurningSlot, kTurningAllowableError, kSetupTimeoutMs);
  }


  
  //-----------------------------------
  // Sensor Input
  //-----------------------------------

  public void zeroSensors() {
    lMaster.setSelectedSensorPosition(0, kPrimaryPidIdx, kSetupTimeoutMs);
    rMaster.setSelectedSensorPosition(0, kPrimaryPidIdx, kSetupTimeoutMs);
    lMaster.setSelectedSensorPosition(0, kAuxiliaryPidIdx, kSetupTimeoutMs);    
    rMaster.setSelectedSensorPosition(0, kAuxiliaryPidIdx, kSetupTimeoutMs);
    pigeon.setYaw(0, kSetupTimeoutMs);    
    pigeonCalOffset = pigeon.getFusedHeading();
  }

  


  int bufferSize = 0;

  public void readSensors() {
    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSize);

    float gyroAngleRad = (float)(-(pigeon.getFusedHeading() - pigeonCalOffset) * Math.PI / 180);

    DrivetrainInput.startDrivetrainInput(builder);
    DrivetrainInput.addTimestamp(builder,       Timer.getFPGATimestamp());
    DrivetrainInput.addGyroAngleRad(builder,    gyroAngleRad);
    DrivetrainInput.addRightCurrent(builder,    (float)rMaster.getStatorCurrent());
    DrivetrainInput.addLeftCurrent(builder,     (float)lMaster.getStatorCurrent());
    DrivetrainInput.addRightBusVoltage(builder, (float)rMaster.getBusVoltage());
    DrivetrainInput.addLeftBusVoltage(builder,  (float)lMaster.getBusVoltage());
    DrivetrainInput.addRightVoltage(builder,    (float)rMaster.getMotorOutputVoltage());
    DrivetrainInput.addLeftVoltage(builder,     (float)lMaster.getMotorOutputVoltage());
    DrivetrainInput.addRightVelocity(builder,   (float)encoderUnitsPerFrameToInchesPerSecond(rMaster.getSelectedSensorVelocity(kPrimaryPidIdx)));
    DrivetrainInput.addLeftVelocity(builder,    (float)encoderUnitsPerFrameToInchesPerSecond(lMaster.getSelectedSensorVelocity(kPrimaryPidIdx)));
    DrivetrainInput.addRightDistance(builder,   (float)encoderUnitsToInches(rMaster.getSelectedSensorPosition(kPrimaryPidIdx)));
    DrivetrainInput.addLeftDistance(builder,    (float)encoderUnitsToInches(lMaster.getSelectedSensorPosition(kPrimaryPidIdx)));
    int offset = DrivetrainInput.endDrivetrainInput(builder);
    DrivetrainInput.finishDrivetrainInputBuffer(builder, offset);
    ByteBuffer bb = builder.dataBuffer();
    inputQueue.write(bb);

    bufferSize = Math.max(bufferSize, bb.remaining());   
  }



  //-----------------------------------
  // Actuator Output
  //-----------------------------------

  public void writeActuators() {

    // first, disable motors if robot is disabled
    Optional<ByteBuffer> obb = driverStationReader.readLast();
    if (obb.isPresent()) {
      DriverStationStatus dsStatus = DriverStationStatus.getRootAsDriverStationStatus(obb.get());
      if (!dsStatus.enabled()) {
        stop();
        return;
      }
    }

    // read the output queue to see what setting the core robot code wants
    obb = outputReader.readLast();
    if (obb.isPresent()) {
      // found a message. unpack it.
      DrivetrainOutput msg = DrivetrainOutput.getRootAsDrivetrainOutput(obb.get());
      byte talonControlMode = msg.talonControlMode();
      float lSetpoint = msg.leftSetpoint();
      float rSetpoint = msg.rightSetpoint();
      boolean highGear = msg.highGear();

      switch (talonControlMode) {
      case TalonControlMode.PercentOutput:
        // Mode used for joystick control
        compressorOn(); // compressor only on during driver control
        setBrakeMode(false);
        lMaster.set(ControlMode.PercentOutput, lSetpoint);
        rMaster.set(ControlMode.PercentOutput, rSetpoint);
        break;

      case TalonControlMode.Position:
        // This mode can be used to hold a certain position,
        // execute point turns, or drive a fixed distance
        compressorOff(); // compressor off during autonomous
        setBrakeMode(true);
        lMaster.selectProfileSlot(kPositionSlot, kPrimaryPidIdx);
        rMaster.selectProfileSlot(kPositionSlot, kPrimaryPidIdx);
        lMaster.set(ControlMode.Position, inchesToEncoderUnits(lSetpoint));
        rMaster.set(ControlMode.Position, inchesToEncoderUnits(rSetpoint));
        break;

      case TalonControlMode.Velocity:
        compressorOff(); // compressor off during autonomous
        setBrakeMode(true);
        lMaster.selectProfileSlot(kVelocitySlot, kPrimaryPidIdx);
        rMaster.selectProfileSlot(kVelocitySlot, kPrimaryPidIdx);
        lMaster.set(ControlMode.Velocity, inchesPerSecondToEncoderUnitsPerFrame(lSetpoint));
        rMaster.set(ControlMode.Velocity, inchesPerSecondToEncoderUnitsPerFrame(rSetpoint));
        break;

      // TODO: work on zero-point turn
      // case TalonControlMode.MotionProfileArc:
      // compressorOff(); // compressor off during autonomous
      // setBrakeMode(true);
      // lMaster.selectProfileSlot(kPositionSlot, kTalonPidIdx);
      // lMaster.selectProfileSlot(kTurningSlot, kAuxiliaryPidIdx);
      // lMaster.set(ControlMode.PercentOutput,
      // inchesPerSecondToEncoderUnitsPerFrame(lSetpoint));
      // rMaster.follow(lMaster, FollowerType.AuxOutput1);
      // break;

      default:
        break;
      }


      shifter.set( !highGear );

    }
  }



  public void stop() {
    lMaster.set(ControlMode.PercentOutput, 0.0);
    rMaster.set(ControlMode.PercentOutput, 0.0);
    setBrakeMode(false);
  }



  public void setBrakeMode(boolean mode) {
    NeutralMode neutralMode = mode ? NeutralMode.Brake : NeutralMode.Coast;
    lMaster.setNeutralMode(neutralMode);
    lSlaveA.setNeutralMode(neutralMode);
    lSlaveB.setNeutralMode(neutralMode);

    rMaster.setNeutralMode(neutralMode);
    rSlaveA.setNeutralMode(neutralMode);
    rSlaveB.setNeutralMode(neutralMode);
  }



  // Talon SRX reports position in rotations while in closed-loop Position mode
  private static double encoderUnitsToInches(int _encoderPosition) {
    return (double) _encoderPosition / (double) kQuadEncoderUnitsPerRev * DriveConstants.kDriveWheelCircumInches;
  }

  private static int inchesToEncoderUnits(double _inches) {
    return (int) (_inches / DriveConstants.kDriveWheelCircumInches * kQuadEncoderUnitsPerRev);
  }

  // Talon SRX reports speed in RPM while in closed-loop Speed mode
  private static double encoderUnitsPerFrameToInchesPerSecond(int _encoderEdgesPerFrame) {
    return encoderUnitsToInches(_encoderEdgesPerFrame) / kQuadEncoderStatusFramePeriod;
  }

  private static int inchesPerSecondToEncoderUnitsPerFrame(double _inchesPerSecond) {
    return (int) (inchesToEncoderUnits(_inchesPerSecond) * kQuadEncoderStatusFramePeriod);
  }



  
  public void compressorOn() {
    // do this checking to avoid extra JNI calls when not needed
    if (!compressor.enabled()) {
      compressor.start();
    }
  }

  public void compressorOff() {
    // do this checking to avoid extra JNI calls when not needed
    if (compressor.enabled()) {
      compressor.stop();
    }
  }

}