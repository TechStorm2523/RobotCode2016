package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandDrawbridge extends CommandGroup {
    
    public  AutoCommandDrawbridge() {
    	// just drive straight for a bit to reach a defense
    	addSequential(new DriveForTime(0.5, 0, 2));
    }
}
