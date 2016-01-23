
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
    
     static Relay feed = new Relay(RobotMap.feeder);
    
    public void initDefaultCommand() {
    }
    	
    	public static void gofeed(){
        	feed.set(Relay.Value.kForward);
    	}
    	public static void stopfeed(){
        	feed.set(Relay.Value.kOff);
    	}
    	
}

    
