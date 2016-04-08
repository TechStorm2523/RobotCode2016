package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandBasicDefense extends CommandGroup {
    
    public  AutoCommandBasicDefense() {   	
    	// just drive straight for a bit to get over a basic a defense
    	addSequential(new LauncherRaise());
    	addSequential(new DriveForTime(DriveTrain.OBSTACLE_CLEAR_TIME, DriveTrain.OBSTACLE_CLEAR_SPEED, 0));
//    	addSequential(new DriveForTime(0.5, 0, 0.7));
//    	addSequential(new AutoLaunch());
    }
}
