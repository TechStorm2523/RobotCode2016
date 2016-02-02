package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Automatically launches a ball at a target
 */
public class AutoLaunch extends CommandGroup {
    
    public  AutoLaunch() {
        // Lower launcher and align with target
    	addParallel(new SetStatusAiming());
    	addParallel(new TurnToTarget());
        addParallel(new LauncherLower());
        addSequential(new SetLauncherRPMByTarget());
        addSequential(new Wait( 0.5 ));
        
        // FIRE!
        
        addSequential(new FeederFire());
        addParallel(new SetStatusFiring());
        // wait, then shutdown
        addSequential(new Wait( 2 ));
        addSequential(new FeederOff());
        addSequential(new SetStatusIdle());
        addSequential(new SetLauncherRPM(0));
    }
}
