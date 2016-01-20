
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPivot extends Subsystem {
    
	 Talon arm1 = new Talon(RobotMap.lifter1);
	 Talon arm2 = new Talon(RobotMap.lifter2);

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    
    public void up1(){
    	arm1.set(1);
	}
	public void up2(){
    	arm2.set(-1);
	}
	public void stoparm1(){
    	arm1.set(0);
	}
	public void stoparm2(){
    	arm2.set(0);
	}
	public void down1(){
    	arm1.set(-1);
	}
	public void down2(){
    	arm2.set(2);
	}
}

