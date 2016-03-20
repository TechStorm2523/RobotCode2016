package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.Winch;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmRetract extends Command {

    public ArmRetract() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.winch);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.winch.setDistance(Winch.MIN_ARM_EXTENSION);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Math.abs(Robot.winch.getCurrentDistance() - Winch.MIN_ARM_EXTENSION) < Winch.ARM_EXTENSION_STOP_TOLERANCE;
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
