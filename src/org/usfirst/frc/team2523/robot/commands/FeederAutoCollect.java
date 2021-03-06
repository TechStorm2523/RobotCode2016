
package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.subsystems.Feeder;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class FeederAutoCollect extends Command {
	
    public FeederAutoCollect() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.feeder);
    }
   
    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.feeder.feed();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (Robot.feeder.ballstate())
    	{
    		return true;
    	} else {
    		return false;
    	}
    	
    	// could do:
//    	return Robot.feeder.ballstate();
    }

    // Called once after isFinished returns true
    protected void end(){
    	Robot.feeder.expelWithLauncher();
    	Timer.delay(Feeder.AUTOCOLLECT_EXPEL_TIME);
    	Robot.feeder.stop();
    	
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.feeder.stop();
    }
}
