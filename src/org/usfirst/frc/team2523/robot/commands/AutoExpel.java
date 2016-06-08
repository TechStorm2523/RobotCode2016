package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoExpel extends CommandGroup {
    
    public  AutoExpel() {
		addSequential(new LauncherLower()); // TODO: Do we want to actually lower, or will it still go into the goal?
		addSequential(new FeederAutoExpel());
		addSequential(new LauncherRaise()); // TODO
    }
}
