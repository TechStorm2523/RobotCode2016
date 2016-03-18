package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandChevaldeFrise extends CommandGroup {
    
    public  AutoCommandChevaldeFrise() {
    	// raise arm
    	addSequential(new SetArmAngle( 30 ));
    	addSequential(new DriveForDistance(0.5, DriveTrain.DISTANCE_TO_DEFENSE_EDGE)); //TODO
    	
    	// lower to lower tippy parts
    	addSequential(new SetArmAngle( 5 ));
    	
    	// drive!
    	addSequential(new DriveForTime(DriveTrain.OBSTACLE_CLEAR_SPEED, 0, DriveTrain.TIME_INTO_COURTYARD_FROM_DEFENSE));
    	addSequential(new AutoLaunch());
    }
}
