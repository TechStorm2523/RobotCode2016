package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandBasicDefense extends CommandGroup {
    
    public  AutoCommandBasicDefense() {   	
    	// just drive straight for a bit to get over a basic a defense
    	addSequential(new DriveForTime(DriveTrain.OBSTACLE_CLEAR_SPEED, 0, DriveTrain.OBSTACLE_CLEAR_TIME));
//    	addSequential(new AutoLaunch());
    }
}
