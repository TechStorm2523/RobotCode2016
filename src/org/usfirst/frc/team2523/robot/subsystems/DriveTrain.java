
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.CallArcadeDrive;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
	private static final double TURN_KP = 0.5; // when normalized offset is at one, go half speed
	private static final double TURN_KI = 0.01;
	private static final double DRIVE_KP = 0.5;
	private static final double DRIVE_KI = 0.005;
	private static final double ENCODER_DISTANCE_PER_PULSE = 0; // feet
	private static final double ENCODER_RESET_TIME = 0.5; // s
	public static final double TARGET_DISTANCE_TOLERANCE = 0.2; // feet, or same as ENCODER_DISTANCE_PER_PULSE
	public static final double RAMP_UP_DURATION = 0.75; // s
	private static final double EXPONENETIAL_FACTOR = 2; // changes arm too
	private static final double TURN_SPEED_MULTIPLIER = 0.6;
	
	// CONSTANTS (for AUTO)
	public static final double VISION_TARGET_OFFSET_TOLERANCE = 0.04; // normalized units, used in TurnToTarget
	public static final double DISTANCE_TO_DEFENSE_EDGE = 7.5; // feet
	public static final double DISTANCE_TO_DEFENSE_MIDDLE = 8.16;
	public static final double OBSTACLE_CLEAR_SPEED = 0.75; // power
	public static final double OBSTACLE_CLEAR_TIME = 6; // s // at OBSTACLE_CLEAR_SPEED
	public static final double TIME_INTO_COURTYARD_FROM_DEFENSE = 3; // at OBSTACLE_CLEAR_SPEED
	
	RobotDrive drive = new RobotDrive(RobotMap.Lfront, RobotMap.Lback, RobotMap.Rfront, RobotMap.Rback);
//	Talon lfront = new Talon(RobotMap.Lfront);
//	Talon lback = new Talon(RobotMap.Lback);
//	Talon rfront = new Talon(RobotMap.Rfront);
//	Talon rback = new Talon(RobotMap.Rback);

	Encoder driveEncoder = new Encoder(RobotMap.driveEncoder1, RobotMap.driveEncoder2, 
									false, Encoder.EncodingType.k4X);
	public PIDControl turnPID = new PIDControl(TURN_KP, TURN_KI, 0); // PI control intended
	public PIDControl drivePID = new PIDControl(DRIVE_KP, DRIVE_KI, 0); // PI control intended
	
	public DriveTrain()
	{
		// invert necessary motors
		drive.setInvertedMotor(MotorType.kFrontLeft, true);
		drive.setInvertedMotor(MotorType.kFrontRight, true);
		drive.setInvertedMotor(MotorType.kRearLeft, true);
		drive.setInvertedMotor(MotorType.kRearRight, true);	
		
		// ensure robot will stop motors if they do not receive commands for 0.25 seconds
		drive.setSafetyEnabled(true);
		drive.setExpiration(0.25);
		
		driveEncoder.setDistancePerPulse(ENCODER_DISTANCE_PER_PULSE);
	}
	
	public void arcadedrivebyjoystick() {
		drive.arcadeDrive(Robot.oi.DriveStick);
		
		// then make it so joystick output goes to x^2:
		// how to do arcade drive
		// given an x and y speeds:
		// right side power = x - y
		// left side power = x + y
		// set the two motors on each side to the right power
		double forwardSpeed = Robot.oi.DriveStick.getY();
		double turnSpeed = Robot.oi.DriveStick.getZ();
		
		// scale the speeds so they are exponential
		forwardSpeed = getExpodentialValue(forwardSpeed);
		turnSpeed = TURN_SPEED_MULTIPLIER*getExpodentialValue(turnSpeed);
		
		drive.arcadeDrive(forwardSpeed, turnSpeed);
		
//		// convert to arcadedrive power
//		double rightSpeed = forwardSpeed + turnSpeed;
//		double leftSpeed = forwardSpeed - turnSpeed;
//			
//		// set correct motors
//		lfront.set(leftSpeed);
//		lback.set(leftSpeed);
//		rfront.set(rightSpeed);
//		rback.set(rightSpeed);		
	}
	/**
	 * @param driveRate Rate of drive from -1 to 1
	 * @param turnRate Rate of turn from -1 to 1
	 */
	public void set(double driveRate, double turnRate)
	{
		drive.arcadeDrive(driveRate, turnRate);
	}

	/**
	 * Sets the target distance for the wheels to go to
	 * @param target The target distance, in feet 
	 * (or whatever unit DISTANCE_PER_ENCODER_PULSE is in)
	 */
	public void setDriveTarget(double target)
	{
		set(drivePID.getPIoutput(target, getCurrentDistance()), 0);
	}
	
	/**
	 * Get a speed to go at based on the progress within a rampup
	 * @param currentSpeed Speed desired if no ramp
	 * @param rampProgress Progress through ramp, where 0 is just starting and 1 is done
	 * @return
	 */
	public double getSpeedByRamp(double currentSpeed, double rampProgress)
	{
		// squared relationship
		return currentSpeed * (rampProgress * rampProgress);
	}
	
	/**
	 * Using PID Control, set turn rate, where a full (reasonable) power turn will occur
	 * when the parameter normalizedOffset is one.
	 * The assumption is that the target value is zero.
	 */
	public void setTurnRateByNormalizedOffset(double normalizedOffset)
	{
		set(0, turnPID.getPIoutput(0.0, normalizedOffset));
	}
	
	public double getCurrentDistance()
	{
		return driveEncoder.getDistance();
	}
	
	public void resetDistance()
	{
		driveEncoder.reset();
		Timer.delay(ENCODER_RESET_TIME);
	}
	
	/**
	 * Changes the given input to an exponential value
	 * @param input Input to be translated to exponential between -1.0 and 1.0
	 * @return Returns the exponential value between -1.0 and 1.0
	 */
	public static double getExpodentialValue(double input)
	{
		if (input > 0)
		{
			return Math.pow(input, EXPONENETIAL_FACTOR);
		}
		else 
		{
			return -Math.pow(input, EXPONENETIAL_FACTOR);
		}
	}
	
	public void initDefaultCommand() {
		setDefaultCommand(new CallArcadeDrive());
	}
}


