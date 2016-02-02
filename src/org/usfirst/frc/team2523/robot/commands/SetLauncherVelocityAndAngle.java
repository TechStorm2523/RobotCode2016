package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set's launcher wheel's RPM based on velocity and angle MAINLY FOR TESTING
 */
public class SetLauncherVelocityAndAngle extends Command {
	double velocity;
	double angle;
	double speedMotTargetRPM = 0;
	double angleMotTargetRPM = 0;

    public SetLauncherVelocityAndAngle(double velocity, double angle) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
        
        this.velocity = velocity;
        this.angle = angle;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	speedMotTargetRPM = Robot.launcherWheels.RPM_PER_VELOCITY * velocity;
    	angleMotTargetRPM = speedMotTargetRPM * Robot.launcherWheels.getRelativeRateByAngle(angle);
    	
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
    	Robot.launcherstatus.setSpooledUp();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	// stop if interrupted
    	Robot.launcherWheels.set(0);
    }
}
