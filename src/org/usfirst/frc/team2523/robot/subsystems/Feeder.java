
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
@SuppressWarnings("unused")
public class Feeder extends Subsystem {
    
    Talon feed = new Talon(RobotMap.feeder);
    
    public void initDefaultCommand() {
    }
    	
    	public void gofeed(){
        	feed.set(1);
    	}
    	public void stopfeed(){
        	feed.set(0);
    	}
    	
}

    
