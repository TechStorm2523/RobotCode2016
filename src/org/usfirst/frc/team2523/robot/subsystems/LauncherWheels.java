
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.LauncherComm;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LauncherWheels extends Subsystem {
	// constants
	public final double PID_KP = 0.1;
	public final double ENCODER_REV_PER_PULSE = 0; // TODO: SET!!!!!!!
    
    Talon launch1 = new Talon(RobotMap.launch1);
    Talon launch2 = new Talon(RobotMap.launch2);
    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoderPort1, RobotMap.launcherEncoderPort2, 
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
	
	public void SetTargetRPM(int rpm)
	{
		if (rpm != 0)
			set(rpmPID.getPoutput(rpm, rpmEncoder.getRate()*60)); // in Rev/Second
		else
			set(0);
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new LauncherComm());
    }	
}

    
