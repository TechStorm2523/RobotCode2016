
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetArmByJoystick;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPivot extends Subsystem {
	
	// constants
	private static final double PID_KP = 0.009;
	private static final double PID_KI = 0; 
	private static final double PID_KD = 0;
	private static final double DEG_PER_SEC_PER_POWER = 336/1;
	public static final double ARM_STARTING_ANGLE = 61; // degrees, positive for down off horizontal
	private static final double POTENTIOMETER_MAX_ANGLE = 315;
	private static final double POTENTIOMETER_ANGLE_PER_VOLTS = -2.307*2*70.4106;
	private static final double POTENTIOMETER_START_DEGREE = -322.2; // raw potentiometer reading at start angle
	private static final double POTENTIOMETER_READ_DEADZONE = 0.25; // degrees
	public static final double MAX_IN_MATCH_ANGLE = 87; //87;
	public static final double ARM_STOP_TOLERANCE = 1; // degrees, roughly leads to arm PID positioning precision
	public static final double MAX_JOYSTICK_SPEED = 0.7;
	public static final double MAX_PID_SPEED = 0.5;
	public static final double JOYSTICK_DEADZONE = 0.01; // normalized units
//	public static final double ARM_PROPS_READ_FREQUENCY = 0.05W;
	
	// variables
	public double currentSpeed;
	public double lastOfficalAngle = 0;
	public double currentTargetAngle;
	public double currentMaxAngle = MAX_IN_MATCH_ANGLE;
	public double currentArmRate = 0;
	public double pastPotentiometerAngle = 0;
	public double lastPotentiometerRateRead = 0;
	public boolean armLimitOverride = false;

	CANTalon arm1 = new CANTalon(RobotMap.lifter1);
	CANTalon arm2 = new CANTalon(RobotMap.lifter2);
	public AnalogPotentiometer armPotentiometer = new AnalogPotentiometer(RobotMap.armPoten1, POTENTIOMETER_ANGLE_PER_VOLTS);
	public PIDControl armPID = new PIDControl(PID_KP, PID_KI, PID_KD);
	
	public ArmPivot()
	{
		setBrake(true);
		armPID.setMaxMin(-MAX_PID_SPEED, MAX_PID_SPEED); // this is stupidly counterintuitive (its min then max)... my bad
		
		currentTargetAngle = getArmAngle(); // don't try to go somewhere right off the bat
	}
	
	public void setArmByJoystick()
	{
		// alternate between pid and throttle controls here based one whether joystick in deadzone
		// consider using armPID and setTargetAngle for this so the arm stays up (also see whether the motors are braking...)
		double commandedSpeed = MAX_JOYSTICK_SPEED*Robot.oi.UtilStick.getY();

		// apply deadzone, and if in it, use PID control
		if (Math.abs(commandedSpeed) < JOYSTICK_DEADZONE)
		{
			// only use PID when off limits (and not trying to get out of limits) to avoid odd behavior there
			double angle = getArmAngle();														// STOP when:
			if ((currentTargetAngle - angle < 0 && angle < ARM_STOP_TOLERANCE) || 					// target angle below current and at bottom
				(currentTargetAngle - angle > 0 && angle > currentMaxAngle - ARM_STOP_TOLERANCE)) 	// target angle above current and at top
			{
				set(0);	
			}
			else
				setTargetAngle(currentTargetAngle);
		}
		else
		{
			// use drivetrain function to get a curved input
			commandedSpeed = DriveTrain.getExpodentialValue(commandedSpeed);
			
			// finally, actually set it
			set(commandedSpeed);
			
			// log current position to hold (and limit it)
			currentTargetAngle = getArmAngle();
			if (!armLimitOverride && currentTargetAngle > currentMaxAngle) currentTargetAngle = currentMaxAngle;
			else if (!armLimitOverride && currentTargetAngle < 0) currentTargetAngle = 0;
			
			// reset PID integral error so old error is ignored
			armPID.resetIntegral();
		}
	}

	public void set(double speed)
	{
		// stop if we are NOT overridden AND we're not too low and trying to go down OR too high and trying to go up
		if (!armLimitOverride && (speed < 0 && getArmAngle() < 0) || (speed > 0 && getArmAngle() > currentMaxAngle))
		{
			arm1.set(0);
			arm2.set(0);
			this.currentSpeed = 0;
		} else {
			// we may need to limit the commanded speed to remain within the winch speed limits
			// (we never tried this... but its based on my calculus and the actual arm speed (which was
			// really noisy) so it would probably never work...)
			speed = speed; //Robot.winch.getLimitedArmSpeed(speed);
			
			arm1.set(-speed);
			arm2.set(speed);
			this.currentSpeed = speed;
		}	
	}
	
	public void setTargetAngle(double angle)
	{
		// ignore crazy values
		if (0 <= angle && angle <= POTENTIOMETER_MAX_ANGLE)		
			set(-armPID.getPIDoutput(angle, getArmAngle()));
	}
	
	public double getArmAngle()
	{
		// change if has exceeded change threshold (to remove noise)
		if (Math.abs(lastOfficalAngle - armPotentiometer.get()) >= POTENTIOMETER_READ_DEADZONE)
			lastOfficalAngle = armPotentiometer.get() - POTENTIOMETER_START_DEGREE;
		
		return lastOfficalAngle;
		
		// OR just give raw values: POTENTIOMETER_MAX_ANGLE - (armPotentiometer.get() + POTENTIOMETER_START_DEGREE);
	}
	
	/**
	 * @return Rate with units in degrees per second
	 */
	public double getArmRate()
	{
//		System.out.println(( getArmAngle() - pastPotentiometerAngle ) / 
//			   ((System.nanoTime() - lastPotentiometerRateRead)/10e9));
		
		return currentArmRate;
	}
	
	/**
	 * Must be called periodically for getArmRate() to work, AND for arm 
	 * max angle to increase at end of match
	 */
	public void updateArmProperties()
	{
		// I was going to not read the arm speed so fast to get more average speed (which might
		// be less noisy), but it screwed it up I think... anyways it would be hard to remove it all
//		if ((System.nanoTime() - lastPotentiometerRateRead)/10e9 > ARM_PROPS_READ_FREQUENCY)  
//		{
			// set max angle based on match time
			if (!armLimitOverride && Timer.getMatchTime() > RobotMap.MATCH_END_PERIOD_LEN)
				currentMaxAngle = MAX_IN_MATCH_ANGLE;
			else
				currentMaxAngle = POTENTIOMETER_MAX_ANGLE;
				
			currentArmRate = (getArmAngle() - pastPotentiometerAngle) /
		   			 ((System.nanoTime() - lastPotentiometerRateRead)/10e9);
			
			// OR currentArmRate = currentSpeed * DEGREE_PER_SEC_PER_POWER // (this would have allowed us to
			// get the arm rate just based on a constant we measured on our own, so we could actually limit 
			// the winch speed with this (to bypass noise issue), but again we never did.)
				
			pastPotentiometerAngle = getArmAngle();
			lastPotentiometerRateRead = System.nanoTime();
//		}
	}
	
	/**
	 * Sets the motor to brake or coast when no power
	 * @param brake Brakes if this is true, coasts if this is false
	 */
	public void setBrake(boolean brake)
	{
		arm1.enableBrakeMode(brake);
		arm2.enableBrakeMode(brake);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		setDefaultCommand(new SetArmByJoystick());
	}
	/* public static void up(){

    	arm1.set(1);
    	arm2.set(-1);
	}
	public void stoparm(){
    	arm1.set(0);
    	arm2.set(0);
	}

	public void down(){
    	arm1.set(-1);
    	arm2.set(1);
	}
	 */

}

