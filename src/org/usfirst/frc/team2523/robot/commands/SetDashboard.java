
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
		SmartDashboard.putNumber(" Arm Angle ", Robot.armpivot.getArmAngle());
		SmartDashboard.putBoolean(" Ball? ", Robot.feeder.ballstate());
		SmartDashboard.putBoolean(" In Range ", Robot.launcherstatus.inRange);
		SmartDashboard.putBoolean(" Aligned ", Robot.launcherstatus.aligned);
		SmartDashboard.putBoolean(" Spooled Up ", Robot.launcherstatus.spooledUp);
		SmartDashboard.putNumber(" Range to Best Target ", Robot.targetTracker.currentRangeToBestTarget);
		
		// DIAGNOSTICS
		SmartDashboard.putString("Status of Launcher",Robot.launcherstatus.Status );
		SmartDashboard.putNumber(" Launcher Speed Mot RPM ", Robot.launcherWheels.speedMotRPM);
		SmartDashboard.putNumber(" Launcher Angle Mot RPM ", Robot.launcherWheels.angleMotRPM);
		
		// COMMANDS
		//SmartDashboard.putData("Set Launch Properties", new SetLauncherVelocityAndAngle());
		
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
