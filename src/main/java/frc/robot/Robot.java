// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  TalonSRX _talon = new TalonSRX(0);
  XboxController control00 = new XboxController(1);

  BaseMotorController _follower1 = new TalonSRX(0);

  double targetPos;

  /* Used to build string throughout loop */
  StringBuilder _sb = new StringBuilder();

  /** How much smoothing [0,8] to use during MotionMagic */
  int _smoothing = 0;

  /** save the last Point Of View / D-pad value */
  int _pov = -1;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    _follower1.configFactoryDefault();
    _follower1.follow(_talon);
/* Factory default hardware to prevent unexpected behavior */
_talon.configFactoryDefault();

/* Configure Sensor Source for Pirmary PID */
_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kPIDLoopIdx,
    Constants.kTimeoutMs);

/* set deadband to super small 0.001 (0.1 %).
  The default deadband is 0.04 (4 %) */
_talon.configNeutralDeadband(0.0001, Constants.kTimeoutMs);

/**
 * Configure Talon SRX Output and Sesnor direction accordingly Invert Motor to
 * have green LEDs when driving Talon Forward / Requesting Postiive Output Phase
 * sensor to have positive increment when driving Talon Forward (Green LED)
 */
_talon.setSensorPhase(true);
_talon.setInverted(false);

/* Set relevant frame periods to be at least as fast as periodic rate */
//_talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, Constants.kTimeoutMs);
//_talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, Constants.kTimeoutMs);

/* Set the peak and nominal outputs */
_talon.configNominalOutputForward(0, Constants.kTimeoutMs);
_talon.configNominalOutputReverse(0, Constants.kTimeoutMs);
_talon.configPeakOutputForward(1, Constants.kTimeoutMs);
_talon.configPeakOutputReverse(-1, Constants.kTimeoutMs);

/* Set Motion Magic gains in slot0 - see documentation */
_talon.selectProfileSlot(Constants.kSlotIdx, Constants.kPIDLoopIdx);
_talon.config_kF(Constants.kSlotIdx, Constants.kGains.kF, Constants.kTimeoutMs);
_talon.config_kP(Constants.kSlotIdx, Constants.kGains.kP, Constants.kTimeoutMs);
_talon.config_kI(Constants.kSlotIdx, Constants.kGains.kI, Constants.kTimeoutMs);
_talon.config_kD(Constants.kSlotIdx, Constants.kGains.kD, Constants.kTimeoutMs);

/* Set acceleration and vcruise velocity - see documentation */
_talon.configMotionCruiseVelocity(10000, Constants.kTimeoutMs);
_talon.configMotionAcceleration(20000, Constants.kTimeoutMs);

/* Zero the sensor once on robot boot up */
_talon.setSelectedSensorPosition(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
    //if(control00.getX(Hand.kRight) > .075 || control00.getX(Hand.kRight) < -.075)
    //targetPos = targetPos + control00.getX(Hand.kRight)*200;

    if(control00.getAButtonPressed())
    targetPos = targetPos + ((384 * 4) * 16) + 256;

    //double targetPos = control00.getTriggerAxis(Hand.kRight)*100;
    SmartDashboard.putNumber("Target",targetPos);

    _talon.set(ControlMode.MotionMagic, targetPos);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
