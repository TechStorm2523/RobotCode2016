
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
    	Robot.winch.set(0.5*(-Robot.oi.DriveStick.getThrottle() + 1));
    }

    // ake this return true when this Command no longer needs to run execute()
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
