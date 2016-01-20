
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
@SuppressWarnings("unused")
public class LauncherWheels extends Subsystem {
    
    Talon launch1 = new Talon(RobotMap.launch1);
    Talon launch2 = new Talon(RobotMap.launch2);
    public void initDefaultCommand() {
    }
    	
    	public void go1(){
        	launch1.set(1);
    	}
    	public void stop1(){
        	launch1.set(0);
    	}
    	public void go2(){
        	launch2.set(-1);
    	}
    	public void stop2(){
        	launch2.set(0);
    	}
}

    
