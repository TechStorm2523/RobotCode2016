
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2523.robot.Robot;

import com.ni.vision.NIVision;

/**
 *
 */
public class DrawBarsOnCamera extends Command 
{
	// get variables
	int session = Robot.camera.session;
	
    public DrawBarsOnCamera() 
    {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.camera);
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	// start the movie!
        NIVision.IMAQdxStartAcquisition(session);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	// call function in Camera
    	//Robot.camera.drawBar();
    }
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() 
    {
    	// end the movie!
    	NIVision.IMAQdxStopAcquisition(session);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	end();
    }
}
