package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;
import org.usfirst.frc.team2523.robot.subsystems.TargetTracker;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Automatically launches a ball at a target
 */
public class AutoLaunch extends CommandGroup {
    
    public  AutoLaunch() {
        // Lower launcher and align with target
    	addSequential(new LauncherLower());
    	addSequential(new SetTrackingToAutoAligned());

//    	addSequential(new StartTargetTracking()); // OUT WITH NEW VISION
//    	addSequential(new Wait(TargetTracker.TARGET_ACQUIRE_TIME));
//    	addSequential(new SeekTarget()); POSSIBLY....?
    	addParallel(new SetStatusAiming());
    	addParallel(new TurnToTarget());
        addParallel(new LauncherLower());
        addSequential(new SetLauncherRPMByTarget());
        addSequential(new Wait(LauncherWheels.POST_SPOOL_UP_WAIT_TIME));
        
        // FIRE!
        addSequential(new FeederFire());
        addParallel(new SetStatusFiring());
        
        // wait, then shutdown
        addSequential(new Wait(LauncherWheels.POST_LAUNCH_WAIT_TIME));
        addSequential(new FeederOff());
        addSequential(new SetStatusIdle());
        addSequential(new SetLauncherRPM(0));
        addSequential(new LauncherRaise());
    	addSequential(new SetTrackingToDriverAligned()); // NEW VISION
//        addSequential(new StopTargetTracking()); // OUT WITH NEW VISION
    }
}
