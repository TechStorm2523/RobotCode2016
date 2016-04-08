
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Feeder extends Subsystem {
	private static final double FEED_IN_SPEED = 1.0;
	private static final double FEED_OUT_SPEED = 1.0;
	public static final double AUTOCOLLECT_EXPEL_TIME = 0.2;
	
	Victor feed = new Victor(RobotMap.feeder);
    
    DigitalInput balldetector = new DigitalInput(RobotMap.ballDetectorLimSwitch);

    public boolean ballstate(){
    	return !balldetector.get();
    }
	
    public void feed(){
    	feed.set(-FEED_IN_SPEED);
	}
	
    public void expel(){
    	feed.set(FEED_OUT_SPEED);
    	Robot.launcherWheels.launchFront.set(-LauncherWheels.MAX_RPM/8);	
    }
	
    public void stop(){
    	// emulate braking?
    	feed.set(0);
    	Robot.launcherWheels.launchFront.set(0);
	}
    

    public void initDefaultCommand() {
    }
}

    
