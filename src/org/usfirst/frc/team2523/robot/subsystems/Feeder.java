
package org.usfirst.frc.team2523.robot.subsystems;

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
	private static final double FEED_SPEED = 1;
	public static final double AUTOCOLLECT_EXPEL_TIME = 0.12;
	
	Victor feed = new Victor(RobotMap.feeder);
    
    DigitalInput balldetector = new DigitalInput(RobotMap.ballDetectorLimSwitch);

    public boolean ballstate(){
    	return !balldetector.get();
    }
	
    public void feed(){
    	feed.set(FEED_SPEED);
	}
	
    public void expel(){
    	feed.set(-FEED_SPEED);
	}	
	
    public void stop(){
    	// emulate braking?
    	feed.set(0);
	}
    

    public void initDefaultCommand() {
    }
}

    
