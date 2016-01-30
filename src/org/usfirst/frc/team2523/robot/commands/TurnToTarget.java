package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Turn's the robot's chassis to align with the target
 */
public class TurnToTarget extends Command {
	double currentXOffset = 0;
	public final double TARGET_OFFSET_TOLERANCE = 0.02;

    public TurnToTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.turnPID.resetIntegral();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	currentXOffset = Robot.targetTracker.getTargetDistanceFromCenter()[0];
    	Robot.drivetrain.setTurnRateByNormalizedOffset(currentXOffset);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// we measure distance relative to 0, so it's simple
        return Math.abs(currentXOffset) < TARGET_OFFSET_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.set(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
