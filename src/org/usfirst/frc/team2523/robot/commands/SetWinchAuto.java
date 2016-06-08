package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This is the default command of the Winch subsystem. 
 */
public class SetWinchAuto extends Command {

    public SetWinchAuto() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.winch);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// This is another way of setting winch speed, and it just allows
    	// us to handle lots of speed change requests more easily. 
    	// (There are other commands which just set the value of desiredWinchSpeed)
    	// (This isn't the best practice, and it should probably be removed, but
    	//  I used it for some reason back in competition)
    	Robot.winch.set(Robot.winch.desiredWinchSpeed);
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
    	end();
    }
}
