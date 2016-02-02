
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.LauncherComm;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Controls the launcher wheels
 */
public class LauncherWheels extends Subsystem {
	// constants
	public final double PID_KP = 0.1;
	public final double ENCODER_REV_PER_PULSE = 0; // TODO: SET!!!!!!!
	public final double RPM_PER_VELOCITY = 0; // inch/sec
	public final double LAUNCH_ANGLE = 64;
	public final double LAUNCH_HEIGHT = 0; // feet
	public final double TARGET_HEIGHT = 7*12+1 + 12 ; // feet
	// this is a coefficent representing the relative speed required for a given angle increase
	// an increase will mean that a smaller relative rate is needed for the same angle off normal launch
	public final double RELATIVE_RATE_PER_ANGLE_COEFFICENT = 1;
	// the angle off of the launcher angle at which the ball launches when the relative rate is one
	public final double RELATIVE_LAUNCH_ANGLE_AT_SAME_RATE = 0; 
	public final double TARGET_RPM_TOLERANCE = 100; // distance off given RPM before rpm is considered to equal to that value
    
    Talon speedMot = new Talon(RobotMap.launcherSpeedMot);
    Talon angleMot = new Talon(RobotMap.launcherAngleMot);
    
    Encoder speedMotRPMEncoder = new Encoder(RobotMap.launcherSpeedEncoder1, RobotMap.launcherSpeedEncoder2, 
									false, Encoder.EncodingType.k4X);
    Encoder angleMotRPMEncoder = new Encoder(RobotMap.launcherAngleEncoder1, RobotMap.launcherAngleEncoder2, 
									false, Encoder.EncodingType.k4X);
    
    PIDControl rpmPID = new PIDControl(PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// Initialize encoder scaling
    	speedMotRPMEncoder.setDistancePerPulse(ENCODER_REV_PER_PULSE);
    }
    
	public void setByThrottle() {
    	set(OI.UtilStick.getThrottle());
	}
	
	/**
	 * @deprecated
	 */
	public void set(double speed)
	{
		speedMot.set(speed);
    	angleMot.set(-speed);
	}
	
	public void set(double speedMotSpeed, double angleMotSpeed)
	{
		setSpeedMot(speedMotSpeed);
		setAngleMot(angleMotSpeed);
	}
	
	public void setSpeedMot(double speed)
	{
		speedMot.set(speed);
	}
	
	public void setAngleMot(double speed)
	{
		angleMot.set(speed);
	}
	
	public void setTargetRPM(double rpm)
	{
		setSpeedMotTargetRPM(rpm);
		setAngleMotTargetRPM(rpm);
	}
	
	public void setSpeedMotTargetRPM(double rpm)
	{
		if (rpm != 0)
			setSpeedMot(rpmPID.getPoutput(rpm, getCurrentSpeedMotRPM()));
		else
			setSpeedMot(0);
	}
	
	public void setAngleMotTargetRPM(double rpm)
	{
		if (rpm != 0)
			setAngleMot(rpmPID.getPoutput(rpm, getCurrentAngleMotRPM()));
		else
			setAngleMot(0);
	}
	
	public double getCurrentSpeedMotRPM()
	{
		return speedMotRPMEncoder.getRate()*60; // in Rev/Second
	}
	
	public double getCurrentAngleMotRPM()
	{
		return angleMotRPMEncoder.getRate()*60; // in Rev/Second
	}
	
	public double getSpeedMotRPMbyRange(double range)
	{
		return getVelocityByRange(range) * RPM_PER_VELOCITY;
	}
	
	public double getAngleMotRPMbyRange(double range)
	{
		// we multiply the correct speedMot RPM by the angle 
		return getSpeedMotRPMbyRange(range) * getRelativeRateByAngle(getAngleByRange(range));
	}
	
	private double getVelocityByRange(double range)
	{
		return Math.sqrt(2*range*RobotMap.GRAVITY / Math.sin(Math.toRadians(2*getAngleByRange(range))));
	}
	
	private double getAngleByRange(double range)
	{
		return Math.atan( (TARGET_HEIGHT - LAUNCH_HEIGHT) / (range*(1 - 0.5*RobotMap.GRAVITY)) );
	}
	
	private double getRelativeRateByAngle(double angle)
	{
		// the coefficient is just empirical, but the relation between relative rate and angle is inverse, so 
		// an increase in required angle results in a decrease in relative rate (because wheel is above launcher)
		return RELATIVE_RATE_PER_ANGLE_COEFFICENT *(LAUNCH_ANGLE / (angle + RELATIVE_LAUNCH_ANGLE_AT_SAME_RATE));
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new LauncherComm());
    }	
}

    
