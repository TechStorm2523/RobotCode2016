
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

/**
 *
 */
public class SetDashboard extends Command 
{
    public SetDashboard() 
    {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.dashboard);
    }

    // Called just before this Command runs the first time
    protected void initialize() {}

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
		// Configure SmartDashboard displays
    	// ESSENTIALS
        SmartDashboard.putNumber(" Arm Speed ", Math.abs(Robot.armpivot.currentSpeed));
		SmartDashboard.putNumber(" Arm Angle ", Robot.armpivot.getArmAngle());
		SmartDashboard.putBoolean(" Claw Closed ", Robot.claw.isClawClosed());
		SmartDashboard.putBoolean(" Crate Present ", Robot.lift.isCratePresent());
		SmartDashboard.putBoolean(" Lift Calibrating ", Robot.lift.liftCalibrating);
		SmartDashboard.putNumber(" Actual Lift Height ", Robot.lift.getLiftPosition());
		SmartDashboard.putNumber(" Drive Distance Y ", Robot.accelerometer.distanceY);
		SmartDashboard.putBoolean(" Slow Speed Mode On", Robot.chassis.globalSpeedChange == RobotMap.DEFAULT_SPEED_REDUCTION);

		// DIAGNOSTICS
		SmartDashboard.putNumber(" Encoder Reading ", RobotMap.liftEncoder.get());
		SmartDashboard.putNumber(" Encoder Distance ", Robot.lift.getLiftPosition());
		//SmartDashboard.putNumber(" Direction ", RobotMap.chassisGyro.getAngle());
		SmartDashboard.putNumber(" Axis value ", RobotMap.primaryStick.getPOV());
		SmartDashboard.putBoolean(" Limit Switches Overridden ", Robot.lift.limitSwitchOverride);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
    	// only needs to run once, constantly called
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {}

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {}
}
