package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Automatically launches a ball at a target
 */
public class AutoLaunch extends CommandGroup {
    
    public  AutoLaunch() {
        // Lower launcher and align with target
    	addParallel(new TurnToTarget());
        addParallel(new LauncherLower());
        addSequential(new SetLauncherRPMByTarget());
        
        // FIRE!

        addSequential(new FeederCollect(true));
        
        // wait, then shutdown
        addSequential(new Wait( 2 ));
        addSequential(new FeederOff());
        addSequential(new SetLauncherRPM(0));
    }
}
