package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set's launcher wheel's RPM by the target's range
 */
public class SetLauncherRPMByTarget extends Command {
	final int RANGE_DIFFERENCE_THRESHOLD = 1; // feet
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
    	if (Math.abs(Robot.targetTracker.currentRangeToBestTarget - currentRange) > 1.0)
    		currentRange = Robot.targetTracker.currentRangeToBestTarget;
    	
    	targetRPM = Robot.launcherWheels.getRPMbyRange(currentRange);
    	
    	Robot.launcherWheels.set(targetRPM);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// stop once BOTH target RPMs are reached (in certain range)
        return Robot.launcherWheels.getCurrentRPMError()[0] < Robot.launcherWheels.TARGET_RPM_TOLERANCE && 
        	   Robot.launcherWheels.getCurrentRPMError()[1] < Robot.launcherWheels.TARGET_RPM_TOLERANCE;
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
