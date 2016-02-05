
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.CallArcadeDrive;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
	final double TURN_KP = 0.5;
	final double TURN_KI = 0.05;
	final double DRIVE_KP = 0.5;
	final double DRIVE_KI = 0.05;
	final double DISTANCE_PER_ENCODER_PULSE = 0; // feet
	public final double TARGET_DISTANCE_TOLERANCE = 0.2; // feet, or same as DISTANCE_PER_ENCODER_PULSE
	public final double RAMP_UP_DURATION = 1;
	
	RobotDrive drive = new RobotDrive(RobotMap.Lfront, RobotMap.Lback, RobotMap.Rfront, RobotMap.Rback);
	Encoder driveEncoder = new Encoder(RobotMap.driveEncoder1, RobotMap.driveEncoder2, 
									false, Encoder.EncodingType.k4X);
	public PIDControl turnPID = new PIDControl(TURN_KP, TURN_KI, 0); // PI control intended
	public PIDControl drivePID = new PIDControl(DRIVE_KP, DRIVE_KI, 0); // PI control intended
	
	public DriveTrain()
	{
		// ensure robot will stop motors if they do not receive commands for 0.1 seconds
		drive.setSafetyEnabled(true);
		drive.setExpiration(0.1);
		
		driveEncoder.setDistancePerPulse(DISTANCE_PER_ENCODER_PULSE);
	}
	
	public void arcadedrivebyjoystick() {
		drive.arcadeDrive(Robot.oi.DriveStick);
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
		Timer.delay(1);
	}
	
	public void initDefaultCommand() {
		setDefaultCommand(new CallArcadeDrive());
	}
}


