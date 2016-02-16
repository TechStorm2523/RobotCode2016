package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandPortcullis extends CommandGroup {
    
    public  AutoCommandPortcullis() {
    	// raise arm
    	addSequential(new SetArmAngle( 5 ));
    	addSequential(new ArmExtend());
    	addSequential(new DriveForDistance(0.5, RobotMap.DISTANCE_TO_DEFENSE_MIDDLE)); //TODO
    	
    	// raise to raise gate (as high as we can)
    	addSequential(new SetArmAngle( Robot.armpivot.MAX_IN_MATCH_ANGLE ));
    	
    	// drive!
    	addSequential(new DriveForTime(1, 0, 4));
    	addSequential(new AutoLaunch());
    }
}
