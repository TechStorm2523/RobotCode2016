
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

/**
 *
 */
public class SetLauncherByThrottle extends Command {
	private double elapsedSinceAtTarget = 0; // sec
	private double lastExecutionTime = 0;

    public SetLauncherByThrottle() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
        requires(Robot.launcherPneumatics);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.launcherPneumatics.lower();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.launcherWheels.setByThrottle();
    	
//    	if (Robot.launcherWheels.getCurrentRPMError()[0] < LauncherWheels.TARGET_SPEED_TOLERANCE && 
//        	Robot.launcherWheels.getCurrentRPMError()[1] < LauncherWheels.TARGET_SPEED_TOLERANCE)
//    	{
//    		elapsedSinceAtTarget += (System.nanoTime() - lastExecutionTime) / 10e9;
//    	}
//    	
//    	lastExecutionTime = System.nanoTime();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {   	
    	// stop once BOTH target RPMs are reached (in certain range) AND we have waited enough
        return true;
//        		elapsedSinceAtTarget >= LauncherWheels.POST_SPOOL_UP_WAIT_TIME && 
//        	   Robot.launcherWheels.getCurrentRPMError()[0] < LauncherWheels.TARGET_SPEED_TOLERANCE && 
//        	   Robot.launcherWheels.getCurrentRPMError()[1] < LauncherWheels.TARGET_SPEED_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.launcherstatus.setSpooledUp();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.launcherWheels.set(0);
    }
}
