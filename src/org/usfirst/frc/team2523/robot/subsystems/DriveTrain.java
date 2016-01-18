
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.CallArcadeDrive;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
    
	RobotDrive drive = new RobotDrive(0,1,2,3);
	public void arcadedrivebyjoystick() {
		drive.arcadeDrive(Robot.oi.DriveStick); 
		}
    
    public void initDefaultCommand() {
    setDefaultCommand(new CallArcadeDrive());
    
    }
    
    
    
    }


