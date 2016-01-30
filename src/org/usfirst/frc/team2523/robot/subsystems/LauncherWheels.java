
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
	public final double TARGET_HEIGHT = 1.93; // meters (*3.28084=feet)
	public final double TARGET_RPM_TOLERANCE = 100;
    
    Talon launch1 = new Talon(RobotMap.launch1);
    Talon launch2 = new Talon(RobotMap.launch2);
    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoder1, RobotMap.launcherEncoder2, 
									false, Encoder.EncodingType.k4X);
    PIDControl rpmPID = new PIDControl(PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// Initialize encoder scaling
    	rpmEncoder.setDistancePerPulse(ENCODER_REV_PER_PULSE);
    }
    
	public void setByThrottle() {
    	set(OI.UtilStick.getThrottle());
	}
	
	public void set(double speed)
	{
		launch1.set(speed);
    	launch2.set(-speed);
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
	
	public double getRPMbyRange(double range)
	{
		return getVelocityByRange(range) * RPM_PER_VELOCITY;
	}
	
	private double getVelocityByRange(double range)
	{
		return 25.5*(range*range) / (2.05*range - TARGET_HEIGHT);
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new LauncherComm());
    }	
}

    
