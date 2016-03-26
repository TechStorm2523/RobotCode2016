package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class RunAppropriateVisionTasks extends Command {

    public RunAppropriateVisionTasks() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.targetTracker);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
		NIVision.IMAQdxStartAcquisition(Robot.targetTracker.session); // Robot.camera.session
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.targetTracker.runAppropriateVisionTasks(); // Robot.camera.transferImagetoDS();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// run continuously
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	NIVision.IMAQdxStopAcquisition(Robot.targetTracker.session); // Robot.camera.session
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
