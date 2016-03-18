
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;
import org.usfirst.frc.team2523.robot.subsystems.LauncherWheels;

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
		SmartDashboard.putNumber(" Arm Rate ", Robot.armpivot.getArmRate());
		SmartDashboard.putBoolean(" Ball? ", Robot.feeder.ballstate());
		SmartDashboard.putBoolean(" Winch Braked? ", Robot.winch.isBraked());
		SmartDashboard.putBoolean(" Launcher Lowered? ", Robot.launcherPneumatics.getState());
		SmartDashboard.putBoolean(" In Range ", Robot.launcherstatus.inRange);
		SmartDashboard.putBoolean(" Aligned ", Robot.launcherstatus.aligned ||
					Math.abs(Robot.targetTracker.getTargetDistanceFromCenter()[0]) < DriveTrain.VISION_TARGET_OFFSET_TOLERANCE);
		SmartDashboard.putBoolean(" Spooled Up ", Robot.launcherstatus.spooledUp);
		SmartDashboard.putNumber(" Range to Best Target ", Robot.targetTracker.currentRangeToBestTarget);
		SmartDashboard.putNumber(" Launcher Mot RPM (Front) ", Robot.launcherWheels.getCurrentRPMs()[0]);
		SmartDashboard.putNumber(" Launcher Mot RPM (Back) ", Robot.launcherWheels.getCurrentRPMs()[1]);
		SmartDashboard.putNumber(" Potentiometer Reading", Robot.armpivot.armPotentiometer.get());
		SmartDashboard.putNumber(" Current Thottle RPM Target ", LauncherWheels.MAX_RPM*0.5*(-Robot.oi.UtilStick.getThrottle() + 1)); // from 0 to 100
		
		// DIAGNOSTICS
		SmartDashboard.putString(" Status of Launcher ",Robot.launcherstatus.Status );
		SmartDashboard.putNumber(" Winch (Arm) Extension ", Robot.winch.getCurrentDistance());
		SmartDashboard.putNumber(" Winch Speed ", Robot.winch.winchMotor.get());
		
		// SETTINGS from THE DASHBOARD
		Robot.launcherWheels.rpmPerVelocityCoefficent = SmartDashboard.getNumber(" Launcher RPM to Velocity Coefficent: ", Robot.launcherWheels.rpmPerVelocityCoefficent);
		Robot.winch.revPerInchPerSecCoefficent = SmartDashboard.getNumber(" Arm by Winch Coefficent: ", Robot.winch.revPerInchPerSecCoefficent);
		
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
