
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Winch extends Subsystem {
    
    Jaguar winchmotor = new Jaguar(RobotMap.winch);

    public void initDefaultCommand() {
    }
    	
    	public void extend(){
        	winchmotor.set(1);
    	}
    	public void contract(){
        	winchmotor.set(-1);
    	}
    	public void stop(){
        	winchmotor.set(0);
    	}
}

    
