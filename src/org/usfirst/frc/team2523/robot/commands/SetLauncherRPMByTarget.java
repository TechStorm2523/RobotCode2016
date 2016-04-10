package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set's launcher wheel's RPM by the target's range
 */
public class SetLauncherRPMByTarget extends Command {
	double targetRPM = 0;
	double currentRange = 0;

    public SetLauncherRPMByTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// correct for noise in range (only change if reasonably different)
    	// this operates partially on the assumption that we are stationary when doing this
//    	if (Math.abs(Robot.targetTracker.currentRangeToBestTarget - currentRange) > LauncherWheels.RANGE_DIFFERENCE_DEADZONE)
		currentRange = Robot.targetTracker.currentRangeToBestTarget;
    	
    	targetRPM = Robot.launcherWheels.getRPMbyRange(currentRange);
    	System.out.println(targetRPM);
    	
    	Robot.launcherWheels.set(targetRPM);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// stop once BOTH target RPMs are reached (in certain range)
        return targetRPM > LauncherWheels.MAX_RPM ||
        	   Math.abs(Robot.launcherWheels.getCurrentRPMError()[0]) < LauncherWheels.TARGET_SPEED_TOLERANCE && 
        	   Math.abs(Robot.launcherWheels.getCurrentRPMError()[1]) < LauncherWheels.TARGET_SPEED_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.launcherstatus.setSpooledUp();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	// stop if interrupted
    	Robot.launcherWheels.set(0);
    }
}
