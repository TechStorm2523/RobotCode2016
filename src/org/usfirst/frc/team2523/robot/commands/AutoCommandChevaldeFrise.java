package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandChevaldeFrise extends CommandGroup {
    
    public  AutoCommandChevaldeFrise() {
    	// raise arm
    	addSequential(new SetArmAngle( 30 ));
    	addSequential(new ArmExtend());
    	addSequential(new DriveForDistance(0.5, RobotMap.DISTANCE_TO_DEFENSE_EDGE)); //TODO
    	
    	// lower to lower tippy parts
    	addSequential(new SetArmAngle( 5 ));
    	
    	// drive!
    	addSequential(new DriveForTime(1, 0, 4));
    	addSequential(new AutoLaunch());
    }
}
