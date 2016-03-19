//package org.usfirst.frc.team2523.robot.commands;
//
//import edu.wpi.first.wpilibj.command.Command;
//
//import org.usfirst.frc.team2523.robot.Robot;
//import org.usfirst.frc.team2523.robot.subsystems.TargetReport;
//
//import com.ni.vision.NIVision;
//
///**
// * @deprecated
// */
//public class DrawTargetsOnCamera extends Command 
//{
//	// get variables
//	int session = Robot.camera.session;
//	
//    public DrawTargetsOnCamera() 
//    {
//        // Use requires() here to declare subsystem dependencies
//        requires(Robot.camera);
//    }
//
//    // Called just before this Command runs the first time
//    protected void initialize() 
//    {
//    	// start the movie!
//        NIVision.IMAQdxStartAcquisition(session);
//    }
//
//    // Called repeatedly when this Command is scheduled to run
//    protected void execute() 
//    {
//    	if (Robot.targetTracker.allTargets != null)
//    	{
//    		// draw crosshairs for all targets
//    		for (TargetReport report : Robot.targetTracker.allTargets)
//    		{
//    			Robot.camera.drawTargetIndicator((int) report.centerX, (int) report.centerY);
//    		}
//    	}
//    }
//    
//    // Make this return true when this Command no longer needs to run execute()
//    protected boolean isFinished()
//    {
//        return false;
//    }
//
//    // Called once after isFinished returns true
//    protected void end() 
//    {
//    	// end the movie!
//    	NIVision.IMAQdxStopAcquisition(session);
//    }
//
//    // Called when another command which requires one or more of the same
//    // subsystems is scheduled to run
//    protected void interrupted() 
//    {
//    	end();
//    }
//}
