
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
@SuppressWarnings("unused")
public class Feeder extends Subsystem {
    
    Relay feed = new Relay(RobotMap.feeder);
    
    public void initDefaultCommand() {
    }
    	
    	public void gofeed(){
        	feed.set(Relay.Value.kForward);
    	}
    	public void stopfeed(){
        	feed.set(Relay.Value.kOff);
    	}
    	
}

    
