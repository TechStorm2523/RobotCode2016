
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;

/*
 * Jack, a better way to do this would be to have a single command SetStatus(int status) 
 * That used a bunch of numbers and a switch or if/else statement to decide which status to set.
 * You would also want to use constants for those numbers in the same class, like: int STATUS_IDLE = 4; 
 * so you can do things like: new SetStatus(SetStatus.STATUS_IDLE); 
 */

/**
 *
 */
public class SetStatusFiring extends Command {

    public SetStatusFiring() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherstatus);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.launcherstatus.setFiring();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
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
