
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Winch extends Subsystem {
	// constants
	public static int ARM_PIVOT_TO_15IN = 0; // TODO: CALCULATE!!!!
	public static int POWER_PER_INCH_PER_SECOND = 0;
	public static int MAX_WINCH_BY_ARM_ANGLE = 60;
    
	// definitions
    Jaguar winchMotor = new Jaguar(RobotMap.winch);
    DoubleSolenoid winchBrake = new DoubleSolenoid(RobotMap.winchBrakeSolenoid1, RobotMap.winchBrakeSolenoid2);
    
	public void set(double speed) {
		winchMotor.set(speed);
		
		// make sure brake released (only if not zero)
		if (speed != 0)
			releaseBrake();
	}
	
	/**
	 * Based on the arm's speed, set the winch to the speed required to make the arm move vertically
	 */
	public void setWinchByArmSpeed()
	{
		// get speed based on current angle
		set(getWinchSpeed(Robot.armpivot.getArmAngle(), Robot.armpivot.getArmRate()));
	}
	
	/**
	 * @param currentAngle Current arm angle, measured from ARM_STARTING_ANGLE
	 * @param angleDelta Rate of angle change, in degrees per second
	 * @return The correctly scaled winch speed
	 */
	private double getWinchSpeed(double currentAngle, double angleDelta) 
	{
		// disable if above max angle to avoid infinite speed issues and cable slack
		if (currentAngle > MAX_WINCH_BY_ARM_ANGLE)
			return 0;
		else
		{
			// derived from derivative of arm radius ( d/cos(theta) ) with respect to angle multiplied by
			// the derivative of angle with respect to time.
			// (dr/dTheta * dtheta/dt = dr/dt)
			return POWER_PER_INCH_PER_SECOND *
				   ARM_PIVOT_TO_15IN * 
				   Math.tan(Math.toRadians(currentAngle - RobotMap.ARM_STARTING_ANGLE)) / 
				   Math.cos(Math.toRadians(currentAngle - RobotMap.ARM_STARTING_ANGLE)) *
				   angleDelta;
		}
	}

	/**
	 * Check if given arm speed must be limited to meet winch speed requirements,
	 * then return the new value if necessary (otherwise just return old one)
	 */
	public double getLimitedArmSpeed(double commandedSpeed) 
	{
		double winchSpeed = getWinchSpeed(Robot.armpivot.getArmAngle(),
										  Robot.armpivot.getArmRate());
		
		if (winchSpeed > 1.0)
			return 1.0;
		else if (winchSpeed < -1.0)
			return -1.0;
		else
			return winchSpeed;
	}
	
	public void fullextend(){
    	set(1);
	}
	
	public void fullcontract(){
    	set(-1);
	}
	
	public void fullstop(){
    	set(0);
	}
	
	// brake functions
	public void setBrake()
	{
		winchBrake.set(DoubleSolenoid.Value.kForward);
	}
	
	public void releaseBrake()
	{
		winchBrake.set(DoubleSolenoid.Value.kReverse);
	}
	
    public void initDefaultCommand() {
    }
}

    
