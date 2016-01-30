
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;

/**
 * Drives the robot in the given direction for the given time
 */
public class DriveForTime extends Command 
{
	// initialize global inputs
	double time;
	double driveRate;
	double turnRate;
	
	// initialize global time handler
	long startTime;
	
	/**
	 * Drives the robot in the given direction for the given time
	 * @param time Time to drive for in seconds
	 * @param driveRate Speed to drive at (between -1.0 and 1.0)
	 * @param turnRate Turn speed (between -1.0 and 1.0) (clockwise is positive)
	 */
    public DriveForTime(double time, double driveRate, double turnRate) 
    {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drivetrain);
        
        // translate to globals
        this.time = time;
        this.driveRate = driveRate;
        this.turnRate = turnRate;        
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	// set the start time
    	this.startTime = System.nanoTime();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	// set the motors accordingly if we are still in the time limit
    	Robot.drivetrain.set(this.driveRate, this.turnRate);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// stop if the elapsed time is greater than the given time (multiply by 1000000000 to convert from seconds to nanoseconds)
        return System.nanoTime() - startTime >= this.time * 1000000000;
    }

    // Called once after isFinished returns true
    protected void end() 
    {
    	// stop motors
    	Robot.drivetrain.set(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	end();
    }
}
