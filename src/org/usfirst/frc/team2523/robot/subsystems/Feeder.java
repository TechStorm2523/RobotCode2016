
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Feeder extends Subsystem {
	private double FEED_SPEED = 1;
	
	Victor feed = new Victor(RobotMap.feeder);
    
    DigitalInput balldetector = new DigitalInput(RobotMap.ballDetectorLimSwitch);
    
    public boolean ballstate(){
    	return balldetector.get();
    }
	
    public void feed(){
    	feed.set(FEED_SPEED);
	}
	
    public void expel(){
    	feed.set(-FEED_SPEED);
	}	
	
    public void stop(){
    	feed.set(0);
	}
    

    public void initDefaultCommand() {
    }
}

    
