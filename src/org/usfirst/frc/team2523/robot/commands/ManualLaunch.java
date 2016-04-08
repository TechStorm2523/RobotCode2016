package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class ManualLaunch extends CommandGroup {
    
    public  ManualLaunch() {
        addSequential(new SetLauncherByThrottle());
        addSequential(new Wait(LauncherWheels.POST_SPOOL_UP_WAIT_TIME));
        addSequential(new FeederFire());
        addSequential(new Wait(LauncherWheels.POST_LAUNCH_WAIT_TIME));
        
        addSequential(new FeederOff());
        addSequential(new SetLauncherRPM(0));
        addParallel(new LauncherRaise());
    }
}
