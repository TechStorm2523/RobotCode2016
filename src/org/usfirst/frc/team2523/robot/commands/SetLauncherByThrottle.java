
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;

/**
 *
 */
public class SetLauncherByThrottle extends Command {

    public SetLauncherByThrottle() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
        requires(Robot.launcherPneumatics);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.launcherPneumatics.lower();
    	Robot.launcherWheels.setByThrottle();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {   	
    	// stop once BOTH target RPMs are reached (in certain range)
        return Robot.launcherWheels.getCurrentRPMError()[0] < Robot.launcherWheels.TARGET_RPM_TOLERANCE && 
        	   Robot.launcherWheels.getCurrentRPMError()[1] < Robot.launcherWheels.TARGET_RPM_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.launcherWheels.set(0);
    }
}
