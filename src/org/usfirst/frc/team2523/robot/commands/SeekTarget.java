package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Turn's the robot's chassis until it finds a target
 */
public class SeekTarget extends Command {
	final double SEEK_SPEED = 0.5;
	final double FIRST_TURN_TIME = 0.5;
	final double MAX_TURN_TIME = 5; // turn time to give up
	final double TURN_TIME_INCREMENT = 0.5; // time to turn more on each cycle of 
	
	// can be used to change initial turn direction
	boolean turningRight = true;
	
	double currentTurnTime = FIRST_TURN_TIME;
	double timeAtTurnSwitch = 0;

    public SeekTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	// start timer, in essence
    	timeAtTurnSwitch = System.nanoTime();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// switch direction after turn time
    	if ((System.nanoTime() - timeAtTurnSwitch) * 10e9 >= currentTurnTime)
    	{
    		// add time if we have completed a sweep 
    		// (this happens after a complete initial sweep because it's opposite of right turn)
    		if (!turningRight)
    			currentTurnTime += TURN_TIME_INCREMENT;
    		
    		// switch directions after timeout
    		turningRight = !turningRight;
    		
    		// reset timer
    		timeAtTurnSwitch = System.nanoTime();
    	}
    	
    	// turn based on direction
    	if (turningRight)
    		Robot.drivetrain.set(0, SEEK_SPEED);
    	else
    		Robot.drivetrain.set(0, -SEEK_SPEED);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	// if target is found, there will be a non-zero target offset (also quit if we need to)
        return Robot.targetTracker.getTargetDistanceFromCenter()[0] != 0 ||
        		currentTurnTime > MAX_TURN_TIME;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.set(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
