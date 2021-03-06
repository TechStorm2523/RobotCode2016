package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import com.ni.vision.NIVision;

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
    	// run continuously
        return false;

        // Could run just if we had a target 
        // return Robot.targetTracker.currentRangeToBestTarget != 0;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
