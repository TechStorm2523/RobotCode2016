package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoFeed extends CommandGroup {
    
    public  AutoFeed() {
    		addSequential(new LauncherLower());
    		addSequential(new FeederCollect());
    }
}
