
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetWinchAuto;
import org.usfirst.frc.team2523.robot.commands.SetWinchByThrottle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Winch extends Subsystem {
	// constants
//	public static final double MAX_RPM = 600;
	public static final double MAX_MANUAL_SPEED = 0.75;
	private static final double POWER_REDUCTION_FACTOR = 1.0; // no reduction
	private static final double GEARBOX_CONVERSION_FACTOR = 50; // 100:1 gearboxq
	//			  feed forward: max pow  |rev per sec  | time conversion |  native units per rot
//	private static final double RPM_PID_KF = 0.05; //10*1023 / (MAX_RPM/60 * 0.1 * 4096) /
//						 GEARBOX_CONVERSION_FACTOR; // TODO: IS THIS BETTER THAN BELOW?
	// private static final double RPM_PID_KF = 0.02713; 
//	private static final double RPM_PID_KP = 0;//0.01 * 1023 / 900.0; // set to 50% of max throttle (1023) when going 900 ticks/0.1s
//	private static final double RPM_PID_KI = 0; // NO NEED
//	private static final double RPM_PID_KD = 0; // NO NEED
	private static final double POS_PID_KP = 0.5; // TODO: MAY BE TOO HIGH (it will still be high because the winch is so slow and is so geared up)
	private static final double POS_PID_KI = 0; //0.005;
	private static final double POS_PID_KD = 0.2;
	private static final double REV_PER_INCH = 1/(2*Math.PI*0.75); // circumference inches in one revolution
	
	public static final double MAX_ARM_EXTENSION = 14; // inches
	public static final double MIN_ARM_EXTENSION = 0.5; // inches, off of initial reset point
	private static final double ARM_PIVOT_TO_15IN = 37.5; // inches
	private static final double ARM_LENGTH = 33; // inches
//	private static final double RETRACT_ANGLE = 25;
	private static final double RPM_PER_INCH_PER_SECOND = 1 * REV_PER_INCH*60; // to convert rev/sec to rpm //5000.0/39.27; // assuming w/v = 1/r where w(rpm) = w / 2*pi
//	public static final int MAX_WINCH_BY_ARM_ANGLE = 60; // using getCurrentDistance() we think
	public static final double ARM_EXTENSION_STOP_TOLERANCE = 0.05; // inches, distance off target winch position to stop at
    
	// variables
	public double revPerInchPerSecCoefficent = 1;
	public boolean winchLimitOverride = false;
	public boolean lowerWinchLimitOverride = false; //For the EOM override
	public boolean canSetWinchByArm = true;
	public double desiredWinchSpeed = 0;
	
	// definitions
	public CANTalon winchMotor = new CANTalon(RobotMap.winch);
    Solenoid winchBrake = new Solenoid(RobotMap.winchBrakeSolenoid);
    PIDControl winchPID;
    
    public Winch()
    {
    	// tell Talon SRX to use encoder (Quadrature Encoder)
    	winchMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	
    	// configure PID control for BOTH modes (we ASSUME ramp rate zero means infinite ramp rate)
//    	winchMotor.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, RPM_PID_KF, 1, 0, 0); // ramp rate is zero, but create 2 profiles
    	winchPID = new PIDControl(POS_PID_KP, POS_PID_KI, POS_PID_KD, -POWER_REDUCTION_FACTOR, POWER_REDUCTION_FACTOR, 1);
//    	winchMotor.setPID(POS_PID_KP, POS_PID_KI, POS_PID_KD, 0, 1, 0, 1); // limit integral accumulation too
//    	winchMotor.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);  // no need with ctreMagEncoder (would be 4096 in quadrature)
    	
    	// ensure braked (Motor brake, not pneumatic brake)
    	winchMotor.enableBrakeMode(true);
    	
//    	winchMotor.configPeakOutputVoltage(POWER_REDUCTION_FACTOR*-12.0, POWER_REDUCTION_FACTOR*12);
    	
    	// set base position to here and reset
    	resetWinchPosition();
//    	winchMotor.reset();
    	winchMotor.reverseSensor(false); // TODO: do we need to??
    	
		// ensure operating by RPM mode and corresponding PID
//    	winchMotor.setProfile(0);
    	winchMotor.changeControlMode(TalonControlMode.PercentVbus); // SPeed
    }
    
    /**
     * @param rpm The rpm to set the motor at
     */
	public void set(double desiredSpeed) {

    	double distance = getCurrentDistance();
    	double setSpeed;
    	
    	// IN is +
    	if (!winchLimitOverride && distance >= getArmConstrainedDistance() &&
    			Robot.armpivot.getArmAngle() < 100) 
    	{
    		if (desiredSpeed > 0) // if going IN, let it go
    			setSpeed = desiredSpeed;
    		else
    			setSpeed = winchPID.getPIDoutput(getArmConstrainedDistance(), getCurrentDistance());
    		
    		/*
    		 * if (Robot.armpivot.getArmAngle() > 100)
    			setSpeed = 0; //winchPID.getPIDoutput(MAX_ARM_EXTENSION, getCurrentDistance());
    		else 
    		 */
    	}
//    	else if (winchMotor.getOutputCurrent() > 40)
//    	{
//    		winchMotor.set(0);
//    	}
    	else
    	{
        	setSpeed = desiredSpeed;
    	}
    	
    	// the winch must be stopped when it is:
    	// too far out while trying to go out,
    	// too far in while trying to go in,
    	// or NOT stopped if these constraints are overridden
    	// IN is +
    	if (!winchLimitOverride &&
    		((distance >= MAX_ARM_EXTENSION && desiredSpeed < 0) || ((distance <= MIN_ARM_EXTENSION && desiredSpeed > 0) && (!lowerWinchLimitOverride)))) //&&// invert speed
// 		RobotMap.MATCH_LENGTH - Timer.getMatchTime() > RobotMap.MATCH_END_PERIOD_LEN)
	 	{
	 		setSpeed = 0;
	 	}
    	
//		System.out.println("Speed: " + setSpeed + "Current RPM: 	" + winchMotor.getSpeed());
    	
		// ensure operating by RPM mode and corresponding PID
    	winchMotor.changeControlMode(TalonControlMode.PercentVbus); // SPeed
    	
    	// SET THE SPEED
    	winchMotor.set(setSpeed);
    	
		 //make sure brake released (only if not zero)
		if (setSpeed != 0)
			releaseBrake();
	}
	
	/**
	 * NOTE: THIS MUST NOT BE CALLED WHILE SET() IS BEING CALLED...
	 * BE SURE TO PUT IN AN INTERUPPTING COMMAND!!!!!
	 * @param distance Distance of extension to go to, in inches
	 */
	public void setDistance(double distance)
	{
		double desiredSpeed = winchPID.getPIDoutput(distance, getCurrentDistance());

    	set(desiredSpeed);
    	
//    	System.out.println(desiredSpeed);
		// make sure brake released
    	releaseBrake();
    	
//		System.out.println(" Desired D:		" + distance + "		Current D: " + getCurrentDistance());
	}
	
	/**
	 * @return The current distance off the no-arm-extension position in inches
	 */
	public double getCurrentDistance()
	{
		// System.out.println(winchMotor.getPosition());
		return winchMotor.getPosition() / (GEARBOX_CONVERSION_FACTOR * REV_PER_INCH);
	}
	
	/** THIS IS DEPRECTED BECAUSE IT IS BETTER TO JUST PASSIVELY REGULATE THE ARM IN THE SET() METHOD
	 * Based on the arm's speed, set the winch to the speed required to make the arm move vertically
	 * @deprecated
	 */
	public void setWinchByArmSpeed()
	{
//		double angle = Robot.armpivot.getArmAngle();
//		if (angle < RETRACT_ANGLE)
//			setDistance(MAX_ARM_EXTENSION - 2);
//		else if (angle > RETRACT_ANGLE && angle < 70) 
//			setDistance(MIN_ARM_EXTENSION);
		
		// get distance we need to get to based on current angle
		// (this is just trig: we want to find the arm distance at theta to stay at 15in out)
		// (but theta must be off horizontal (the axis the distance to 15in is on))
//		setDistance(getArmConstrainedDistance());
		
		// get speed based on current angle
		// set(getWinchSpeed(Robot.armpivot.getArmAngle(), Robot.armpivot.getArmRate()));
	}
	
	public double getArmConstrainedDistance()
	{
		double mathValue = ARM_PIVOT_TO_15IN / 
			    		Math.cos(Math.toRadians(ArmPivot.ARM_STARTING_ANGLE - Robot.armpivot.getArmAngle()))
		    			- ARM_LENGTH;
		
		// ignore crazy values
		if (0 <= mathValue && mathValue <= MAX_ARM_EXTENSION)
			return mathValue;
		else
			return getCurrentDistance();
	}
	
	/**
	 * @param currentAngle Current arm angle, measured from ARM_STARTING_ANGLE
	 * @param angleDelta Rate of angle change, in degrees per second
	 * @return The correctly scaled winch speed (in RPMs, or whatever unit RPM_PER_INCH_PER_SECOND is in)
	 * @deprecated
	 */
	private double getWinchSpeed(double currentAngle, double angleDelta) 
	{
		// BE SURE TO REMOVE ONCE SET
		revPerInchPerSecCoefficent = SmartDashboard.getNumber(" Arm by Winch Coefficent: ", revPerInchPerSecCoefficent);
		
		// only control winch by arm speed when the arm still has travel, to avoid spool out
		// or hook disconnection (THIS IS DONE IN SET)

		// derived from derivative of arm radius ( d/cos(theta) ) with respect to angle multiplied by
		// the derivative of angle with respect to time.
		// (dr/dTheta (i.e. ARM... GLE)) * dtheta/dt (i.e. angleDelta) = dr/dt)
		return revPerInchPerSecCoefficent * RPM_PER_INCH_PER_SECOND *
			   ARM_PIVOT_TO_15IN * 
			   Math.tan(Math.toRadians(currentAngle - ArmPivot.ARM_STARTING_ANGLE)) / 
			   Math.cos(Math.toRadians(currentAngle - ArmPivot.ARM_STARTING_ANGLE)) *
			   angleDelta; // TODO: Should this be in RADIANS????? (And below too)
	}
	
	/**
	 * 
	 */
	public void resetWinchPosition()
	{
		winchMotor.setPosition(0);
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
		winchBrake.set(false);
	}
	
	public void releaseBrake()
	{
		winchBrake.set(true);
	}
	
	public boolean isBraked()
	{
		return !winchBrake.get();
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new SetWinchAuto());
    }
}

    /* ARCHIVE */

/* SET() */
//// IN is +
//if (!winchLimitOverride &&
//	((distance >= MAX_ARM_EXTENSION && desiredSpeed < 0) || (distance <= MIN_ARM_EXTENSION && desiredSpeed > 0))) //&&// invert speed
////	RobotMap.MATCH_LENGTH - Timer.getMatchTime() > RobotMap.MATCH_END_PERIOD_LEN)
//	{
//		winchMotor.set(0);
//	}
//else
//{
//	// ensure operating by RPM mode and corresponding PID
//	winchMotor.changeControlMode(TalonControlMode.PercentVbus); // SPeed
////	winchMotor.setProfile(0);
//
//	winchMotor.set(desiredSpeed);
//
////	System.out.println("Speed: " + speed);
////	System.out.println("RPM: " + rpm*GEARBOX_CONVERSION_FACTOR + "		Current RPM: " + winchMotor.getSpeed());
//	
//	// make sure brake released (only if not zero)
//	if (desiredSpeed != 0)
//		releaseBrake();
//}


///**
// * Check if given arm speed must be limited to meet winch speed requirements,
// * then return the new value if necessary (otherwise just return old one)
// * @deprecated
//// */
//public double getLimitedArmSpeed(double commandedSpeed) 
//{
//	// BE SURE TO REMOVE ONCE SET
//	revPerInchPerSecCoefficent = SmartDashboard.getNumber(" Arm by Winch Coefficent: ", revPerInchPerSecCoefficent);
//	
//	// max winch speed = 1.0, and winch=k*arm, so (max) arm=1.0/k,
//	// where k is the conversion factor in above function
//	// (this will be in degrees per second - we're getting angleDelta above)
//	double currentMaxArmSpeed = 1.0 / 
//						   (revPerInchPerSecCoefficent * RPM_PER_INCH_PER_SECOND *
//						    ARM_PIVOT_TO_15IN * 
//						    Math.tan(Math.toRadians(Robot.armpivot.getArmAngle() - ArmPivot.ARM_STARTING_ANGLE)) / 
//						    Math.cos(Math.toRadians(Robot.armpivot.getArmAngle() - ArmPivot.ARM_STARTING_ANGLE)));
//	
//	double winchSpeed = getWinchSpeed(Robot.armpivot.getArmAngle(),
//			  						  Robot.armpivot.getArmRate());				
//	
//	if (winchSpeed > 1.0)
//		return currentMaxArmSpeed / 6.0; // 1 rev/min = 6 degrees/sec
//	else if (winchSpeed < -1.0)
//		return -currentMaxArmSpeed / 6.0;
//	else
//		return commandedSpeed;
//}



/* Set Distance */

//// ensure operating by Distance mode and corresponding PID
//winchMotor.changeControlMode(TalonControlMode.Position);
//winchMotor.setProfile(1);
//
//// set in revolutions
//winchMotor.set(-distance * GEARBOX_CONVERSION_FACTOR * REV_PER_INCH);
//
//// BUT the winch must be stopped when it is:
//// too far out while trying to go out,
//// too far in while trying to go in,
//// BUT, in the last 20 seconds of the match, these constraints are overridden
