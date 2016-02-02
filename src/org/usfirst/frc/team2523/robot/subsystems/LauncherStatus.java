
package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LauncherStatus extends Subsystem {
    
	public String Status = "";
	public boolean inRange = false;
	public boolean aligned = false;
	public boolean spooledUp = false;
	public boolean lowered = false;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void setOutOfRange()
    {
    	Status = "Out of Range";
    	inRange = false;
    }
    public void setInRange()
    {
    	Status = "In Range";
    	inRange = true;
    }
    
    public void setAiming()
    {
    	Status = "Aiming";
    }
    
    public void setFiring() {
    	Status = "Firing!";
    }
    
    public void setSpooledUp()
    {
    	Status = "Spooled up";
    	spooledUp = true;
    }
    
    public void setAligned()
    {
    	Status = "Aligned";
    	aligned = true;
    }
    
    public void setLowered()
    {
    	lowered = true;
    }
    
    public void setRaised()
    {
    	lowered = false;
    }
    
    public void setIdle() {
    	Status = "Idle";
    	aligned = false;
    	spooledUp = false;
    }
    
}



