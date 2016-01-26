package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IdentifyBestTarget extends Command {

    public IdentifyBestTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.targetTracker);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.targetTracker.retrieveBestTarget();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// run once each cycle
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
