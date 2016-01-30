
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;

/**
 * Command to pause the command queue for the given time
 */
public class Wait extends Command 
{
	public double time;
	
	/**
	 * Pauses the command queue for the given time
	 * @param seconds Time in seconds to pause
	 */
    public Wait(double seconds)
    {
        // Use requires() here to declare subsystem dependencies
        //requires(Robot.exampleSubsystem);
    	
    	this.time = seconds;
    }

    // Called just before this Command runs the first time
    protected void initialize() {}

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	Timer.delay(this.time);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
    	// only runs once
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {}

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {}
}
