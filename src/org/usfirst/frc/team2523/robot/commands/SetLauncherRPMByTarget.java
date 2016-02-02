package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set's launcher wheel's RPM by the target's range
 */
public class SetLauncherRPMByTarget extends Command {
	double speedMotTargetRPM = 0;
	double angleMotTargetRPM = 0;

    public SetLauncherRPMByTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double range = Robot.targetTracker.getRangeToTarget();
    	speedMotTargetRPM = Robot.launcherWheels.getSpeedMotRPMbyRange(range);
    	angleMotTargetRPM = Robot.launcherWheels.getAngleMotRPMbyRange(range);
    	
    	Robot.launcherWheels.setSpeedMotTargetRPM(speedMotTargetRPM);
    	Robot.launcherWheels.setAngleMotTargetRPM(angleMotTargetRPM);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// stop once RPM reached (in certain range) for BOTH wheels
        return Math.abs(speedMotTargetRPM - Robot.launcherWheels.getCurrentSpeedMotRPM()) < Robot.launcherWheels.TARGET_RPM_TOLERANCE &&
        	   Math.abs(angleMotTargetRPM - Robot.launcherWheels.getCurrentAngleMotRPM()) < Robot.launcherWheels.TARGET_RPM_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	// stop if interrupted
    	Robot.launcherWheels.set(0);
    }
}
