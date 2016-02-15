package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandBasicDefense extends CommandGroup {
    
    public  AutoCommandBasicDefense() {   	
    	// just drive straight for a bit to get over a basic a defense
    	addSequential(new DriveForTime(0.5, 0,  5 ));
    	addSequential(new AutoLaunch());
    }
}
