package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Automatically launches a ball at a target
 */
public class AutoLaunch extends CommandGroup {
    
    public  AutoLaunch() {
        // Lower launcher and align with target
    	addSequential(new IdentifyBestTarget());
    	addSequential(new Wait(.5));
    	addSequential(new SeekTarget());
    	addParallel(new SetStatusAiming());
    	addParallel(new TurnToTarget());
        addParallel(new LauncherLower());
        addSequential(new SetLauncherRPMByTarget());
        addSequential(new Wait( 0.25 )); // we'll probably be stalled a bit by turning to target
        
        // FIRE!
        addSequential(new FeederFire());
        addParallel(new SetStatusFiring());
        // wait, then shutdown
        addSequential(new Wait(LauncherWheels.POST_LAUNCH_WAIT_TIME));
        addSequential(new FeederOff());
        addSequential(new SetStatusIdle());
        addSequential(new SetLauncherRPM(0));
        addSequential(new ShutUpCamera());
    }
}
