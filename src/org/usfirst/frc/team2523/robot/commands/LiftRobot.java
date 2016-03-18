package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class LiftRobot extends CommandGroup {
    
    public LiftRobot() {
//        addSequential(new SetArmAngle(100));
        addSequential(new SetWinch(1));
        
        // set arm to hold up so we don't violate 15in rule without winch
        addSequential(new SetArmAngle(130, 1, true));
        
        // wait a bit before stopping (this will rarely happen)
        addSequential(new Wait (15));
        addSequential(new SetWinch(0));
        addSequential(new SetWinchBrake(true));
    }
}
