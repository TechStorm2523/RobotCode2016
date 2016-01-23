
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.ArmPivotComm;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPivot extends Subsystem {
    
	 static Talon arm1 = new Talon(RobotMap.lifter1);
	 static Talon arm2 = new Talon(RobotMap.lifter2);

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ArmPivotComm());
    }
    public static void SetArm(){
    	double y = OI.UtilStick.getY();
    	arm1.set(y);
    	arm2.set(-y);
    }
    
   /* public static void up(){
   
    	arm1.set(1);
    	arm2.set(-1);
	}
	public void stoparm(){
    	arm1.set(0);
    	arm2.set(0);
	}
	
	public void down(){
    	arm1.set(-1);
    	arm2.set(2);
	}
	*/
    
}
