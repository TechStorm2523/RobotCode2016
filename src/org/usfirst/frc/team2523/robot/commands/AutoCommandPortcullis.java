package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.subsystems.ArmPivot;
import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandPortcullis extends CommandGroup {
    
    public  AutoCommandPortcullis() {
    	// raise arm
    	addSequential(new SetArmAngle( 5 ));
    	addSequential(new DriveForDistance(0.5, DriveTrain.DISTANCE_TO_DEFENSE_MIDDLE));
    	
    	// raise to raise gate (as high as we can)
    	addSequential(new SetArmAngle(ArmPivot.MAX_IN_MATCH_ANGLE));
    	
    	// drive!
    	addSequential(new DriveForTime(DriveTrain.OBSTACLE_CLEAR_SPEED, 0, DriveTrain.TIME_INTO_COURTYARD_FROM_DEFENSE));
    	addSequential(new AutoLaunch());
    }
}
