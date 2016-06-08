package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveForDistance extends Command {
	double target;
	double maxSpeed;
	double rampUpStartTime = 0;

	/**
	 * Sets the target distance for the wheels to go to
	 * @param target The target distance, in feet 
	 * (or whatever unit DISTANCE_PER_ENCODER_PULSE is in)
	 */
    public DriveForDistance(double maxSpeed, double targetDistance) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drivetrain);
        
        this.target = targetDistance;
        this.maxSpeed = maxSpeed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.resetDistance();
    	Robot.drivetrain.drivePID.setMinMax(-maxSpeed, maxSpeed); 	
    	rampUpStartTime = System.nanoTime();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    	// if ramp up has not expired, apply a ramp
    	if((System.nanoTime() - rampUpStartTime) * 10e9 
		 	< DriveTrain.RAMP_UP_DURATION)
    	{
    		// use PID max/min to set speed, to allow for really short travel distances (ramp is not min)
        	Robot.drivetrain.drivePID.setMinMax(
        			Robot.drivetrain.getSpeedByRamp(maxSpeed, System.nanoTime() - rampUpStartTime) * 10e9 
    														 / DriveTrain.RAMP_UP_DURATION);
    	}
    	
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
