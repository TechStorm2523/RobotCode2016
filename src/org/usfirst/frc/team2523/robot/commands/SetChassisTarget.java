package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetChassisTarget extends Command {
	double target;
	double maxSpeed;

	/**
	 * Sets the target distance for the wheels to go to
	 * @param target The target distance, in feet 
	 * (or whatever unit DISTANCE_PER_ENCODER_PULSE is in)
	 */
    public SetChassisTarget(double target, double maxSpeed) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drivetrain);
        
        this.target = target;
        this.maxSpeed = maxSpeed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.resetDistance();
    	Robot.drivetrain.drivePID.setMaxMin(-maxSpeed, maxSpeed); 	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drivetrain.setDriveTarget(target);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Math.abs(target - Robot.drivetrain.getCurrentDistance()) < Robot.drivetrain.TARGET_DISTANCE_TOLERANCE;
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
