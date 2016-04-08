package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class StartJoystickRecording extends Command {
    private String joystickRecording;

	public StartJoystickRecording() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	joystickRecording = (String) Robot.recordingChooser.getSelected();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// record for length of auto
    	Robot.oi.DriveStick.startRecording(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_drive", 15); // recordLen
    	Robot.oi.UtilStick.startRecording(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_util", 15);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; // the recording joystick auto stops
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.oi.DriveStick.stopRecording();
    	Robot.oi.UtilStick.stopRecording();
    }
}
