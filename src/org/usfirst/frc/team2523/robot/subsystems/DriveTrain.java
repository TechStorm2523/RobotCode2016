
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.CallArcadeDrive;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
	final double TURN_KP = 0.5;
	final double TURN_KI = 0.05;

	RobotDrive drive = new RobotDrive(RobotMap.Lfront, RobotMap.Lback, RobotMap.Rfront, RobotMap.Rback);
	public PIDControl turnPID = new PIDControl(TURN_KP, TURN_KI, 0); // PI control intended
	
	public void arcadedrivebyjoystick() {
		drive.arcadeDrive(Robot.oi.DriveStick);
	}
	
	/**
	 * @param driveRate Rate of drive from -1 to 1
	 * @param turnRate Rate of turn from -1 to 1
	 */
	public void set(double driveRate, double turnRate)
	{
		drive.arcadeDrive(driveRate, turnRate);
	}

	/**
	 * Using PID Control, set turn rate, where a full (reasonable) power turn will occur
	 * when the parameter normalizedOffset is one.
	 * The assumption is that the target value is zero.
	 */
	public void setTurnRateByNormalizedOffset(double normalizedOffset)
	{
		set(0, turnPID.getPIoutput(0.0, normalizedOffset));
	}
	
	public void initDefaultCommand() {
		setDefaultCommand(new CallArcadeDrive());
	}
}


