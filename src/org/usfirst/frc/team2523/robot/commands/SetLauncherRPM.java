package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set's launcher wheel's RPM to a value
 */
public class SetLauncherRPM extends Command {
	double targetRPM = 0;

    public SetLauncherRPM(double targetRPM) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
        
        this.targetRPM = targetRPM;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.launcherWheels.set(targetRPM);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// stop once BOTH target RPMs are reached (in certain range)
        return Robot.launcherWheels.getCurrentRPMError()[0] < LauncherWheels.TARGET_SPEED_TOLERANCE && 
        	   Robot.launcherWheels.getCurrentRPMError()[1] < LauncherWheels.TARGET_SPEED_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.launcherstatus.setSpooledUp();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	// stop if interrupted
    	Robot.launcherWheels.set(0);
    }
}
