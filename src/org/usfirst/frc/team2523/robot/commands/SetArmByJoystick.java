
package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetArmByJoystick extends Command {

	public SetArmByJoystick() {
		// Use requires() here to declare subsystem dependencies
		requires(Robot.armpivot);
//		requires(Robot.winch);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.armpivot.setArmByJoystick();
		// when we used to try to set winch speed by arm speed (now it's all done inside Winch.java)
//		if (Robot.winch.canSetWinchByArm)
//		{
//			Robot.winch.setWinchByArmSpeed();
//		}
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
		end();
	}
}
