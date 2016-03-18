package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandTesting extends CommandGroup {
    
    public  AutoCommandTesting() {
    	addSequential(new SetWinch(100));
    	addSequential(new Wait(1));
    	addSequential(new SetWinch(0));
    }
}
