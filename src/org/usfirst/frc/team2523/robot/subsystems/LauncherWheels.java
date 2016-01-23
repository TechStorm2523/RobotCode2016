
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.LauncherComm;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
@SuppressWarnings("unused")
public class LauncherWheels extends Subsystem {
    
    static Talon launch1 = new Talon(RobotMap.launch1);
    static Talon launch2 = new Talon(RobotMap.launch2);
    public void initDefaultCommand() {
    	setDefaultCommand(new LauncherComm());
    }
    	
    	public static void SetThrottle(){
        	double t = OI.UtilStick.getThrottle();
        	launch1.set(t);
        	launch2.set(-t);
    	}
    	
}

    
