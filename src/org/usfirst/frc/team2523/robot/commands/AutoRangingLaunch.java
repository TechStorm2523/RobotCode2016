package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;
import org.usfirst.frc.team2523.robot.subsystems.TargetTracker;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoRangingLaunch extends CommandGroup {
    
    public  AutoRangingLaunch() {
//    	addSequential(new IdentifyBestTarget());
    	addSequential(new LauncherLower());
    	addSequential(new StartTargetTracking());
    	addSequential(new Wait(TargetTracker.TARGET_ACQUIRE_TIME));
    	addSequential(new SetLauncherRPMByTarget());
    	addSequential(new Wait(LauncherWheels.POST_SPOOL_UP_WAIT_TIME));
        addSequential(new FeederFire());
        addSequential(new Wait(LauncherWheels.POST_LAUNCH_WAIT_TIME));
        
        addSequential(new FeederOff());
        addSequential(new SetLauncherRPM(0));
        addSequential(new LauncherRaise());
        addSequential(new StopTargetTracking());
    }
}
