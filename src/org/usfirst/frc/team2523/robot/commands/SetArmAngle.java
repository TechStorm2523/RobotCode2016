
package org.usfirst.frc.team2523.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;


/**
 * Used to instruct the arm to travel at some speed to a target angle
 */
public class SetArmAngle extends Command 
{	
	// define globals
	public double speed;
	public double target;
	
	/**
	 * Constructor to instruct the arm to travel at max speed to target, the height of the lift
	 * @param target The targeted arm angle in degrees
	 */
    public SetArmAngle(double target) 
    {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.armpivot);
        
        // apply target to the arm
        this.target = target;
        
        // set speed
        this.speed = 1.0;
    }
    
	/**
	 * Constructor to instruct the lift to travel at a certain speed to target, the height of the lift
	 * @param target The targeted lift height in inches
	 * @param speed Max speed to move the lift at. Between 0.0 and 1.0
	 */
    public SetArmAngle(double target, double speed) 
    {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.armpivot);
        
        // apply target to the lift
        this.target = target;
        
        // set speed
        this.speed = speed;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() 
    {
        // set arm speed according to input
        Robot.armpivot.armPID.setMaxMin(-this.speed, this.speed);
        
    	// reset integral value
        Robot.armpivot.armPID.resetIntegral();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {   
    	// limit arm angle target
		if (this.target > Robot.armpivot.currentMaxAngle) this.target = Robot.armpivot.currentMaxAngle;
		else if (this.target < 0.0) this.target = 0.0;
    	
		// set target speed (PID Control)
		Robot.armpivot.setTargetAngle(target);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
    	// return true if the robot is at limits OR we are less than STOP_TOLERANCE from the target
        return Math.abs(target - Robot.armpivot.getArmAngle()) <= Robot.armpivot.ARM_PID_STOP_TOLERANCE;
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	// ensure motor is stopped
    	Robot.armpivot.set(0);
    	
    	// reset integral value
    	Robot.armpivot.armPID.resetIntegral();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	end();
    }
}
