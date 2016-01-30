
package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LauncherStatus extends Subsystem {
    
	public String Status; 

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void setidle() {
    	Status = "Idle";
    }
    public void setaiming() {
    	Status = "Aiming...";
    }
    public void setfiring() {
    	Status = "Firing!";
    }
    public void setotheroccupied() {
    	Status = "Otherwise Occupied";
    }
    
}



