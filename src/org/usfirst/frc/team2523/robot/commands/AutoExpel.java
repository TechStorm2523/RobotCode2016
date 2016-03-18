package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoExpel extends CommandGroup {
    
    public  AutoExpel() {
		addSequential(new LauncherLower());
		addSequential(new FeederExpel());
    }
}
