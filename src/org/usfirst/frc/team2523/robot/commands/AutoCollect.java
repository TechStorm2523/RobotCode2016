package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCollect extends CommandGroup {
    
    public  AutoCollect() {
    		addSequential(new LauncherLower());
    		addSequential(new FeederAutoCollect());
    		
    		// wait, then raise launcher
    		addSequential(new Wait( 1 ));
    		addSequential(new LauncherRaise());
    }
}
