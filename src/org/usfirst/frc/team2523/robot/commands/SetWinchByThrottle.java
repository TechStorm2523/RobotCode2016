
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.Winch;

/**
 *
 */
public class SetWinchByThrottle extends Command {

    public SetWinchByThrottle() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.winch);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// Because the throttle gives a value from -1 to +1 where +1 is all the way DOWN,
    	// shift it down to be from -2 to 0 and then halve it so we go from 0 at the DOWN part
    	// to 1 at the up part for winch speed
    	Robot.winch.set(0.5*(-Robot.oi.DriveStick.getThrottle() + 1));
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {   	
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.winch.set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
