package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ManualAim extends Command {

    public ManualAim() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.launcherWheels);
        requires(Robot.feeder);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.launcherWheels.setByThrottle();
    	Timer.delay(1);
    	Robot.feeder.feed();
    	Timer.delay(.5);
    	Robot.feeder.stop();
    	Robot.launcherWheels.set(0);
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