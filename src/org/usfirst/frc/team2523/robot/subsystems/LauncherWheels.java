
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetLauncherByThrottle;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Controls the launcher wheels
 */
public class LauncherWheels extends Subsystem {
	// constants
	public final double PID_KP = 0.1;
	public final double ENCODER_REV_PER_PULSE = 0; // TODO: SET!!!!!!!
	public final double MAX_RPM = 13050;
	public final double RPM_PER_VELOCITY = 1 / (Math.PI*2.875/60); // inch/sec - by formula x/v = 1/(pi*d/
	public final double TARGET_RPM_TOLERANCE = 100;
	public final double LAUNCH_ANGLE = 64;
	public final double LAUNCH_HEIGHT = 0; // feet
	public final double TARGET_HEIGHT = 7*12+1 + 12 ; // feet
	public final double CAM_DISTANCE_OFF_LAUNCH = 0; // feet

    
    Victor launch1 = new Victor(RobotMap.launcherMot1);
    Victor launch2 = new Victor(RobotMap.launcherMot2);
    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoder1, RobotMap.launcherEncoder2, 
									false, Encoder.EncodingType.k4X);
    PIDControl rpmPID = new PIDControl(PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// Initialize encoder scaling
    	rpmEncoder.setDistancePerPulse(ENCODER_REV_PER_PULSE);
    	set(0);
    }
    
	public void setByThrottle() {
    	set(-OI.UtilStick.getThrottle());
	}
	
	public void set(double speed)
	{
		launch1.set(-speed);
    	launch2.set(speed);
	}
	
	public void setTargetRPM(double rpm)
	{
		if (rpm != 0)
			set(rpmPID.getPoutput(rpm, getCurrentRPM()));
		else
			set(0);
	}
	
	public double getCurrentRPM()
	{
		return rpmEncoder.getRate()*60; // in Rev/Second
	}
	
	/**
	 * @param range Range to target in feet
	 * @return
	 */
	public double getRPMbyRange(double range)
	{
		return getVelocityByRange(range) * RPM_PER_VELOCITY;
	}
	
	/**
	 * @param range Range to target in feet
	 * @return
	 */
	private double getVelocityByRange(double range)
	{
		return Math.sqrt(2*(range + CAM_DISTANCE_OFF_LAUNCH)*RobotMap.GRAVITY / Math.sin(Math.toRadians(2*LAUNCH_ANGLE)));
	}
	
	public boolean inRange(double range)
	{
		return getRPMbyRange(range) <= MAX_RPM;
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new SetLauncherByThrottle());
    }	
}
