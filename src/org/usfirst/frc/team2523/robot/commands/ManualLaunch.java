package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class ManualLaunch extends CommandGroup {
    
    public  ManualLaunch() {
        addSequential(new SetLauncherByThrottle());
        addSequential(new FeederFire());
        addSequential(new Wait(1));
        
        addSequential(new FeederOff());
        addSequential(new SetLauncherSpeed(0));
        addSequential(new LauncherRaise());
    }
}
