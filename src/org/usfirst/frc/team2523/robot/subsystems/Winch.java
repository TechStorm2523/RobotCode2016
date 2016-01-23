
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
    	public void SetWinch() {
    	//	winchmotor.set();
    		// Use the above to set the winchmotor's speed any value, for instance to do the "contract the extension automatically" thing
		}
    	
    	public void fullextend(){
        	winchmotor.set(1);
    	}
    	public void fullcontract(){
        	winchmotor.set(-1);
    	}
    	public void fullstop(){
        	winchmotor.set(0);
    	}
}

    
