
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
	
	/*
	 * Jack, the following constants were for using a PID Control system (a.k.a closed-loop control) with
	 * the motor speed so we could set the winch to a certain RPM (I thought i needed it to use my complicated
	 * math to set the winch at a certain speed based on where the arm was -- this was all for keeping the arm
	 * in the boundaries), BUT that was before I found out it was much simpler math just to tell the winch
	 * what distance out it should be at a certain arm position, and then let a DISTANCE-BASED (not RPM-based) 
	 * PID Control system do the work of getting it there.
	 * (this is a good use of encapsulation - if we knew that we were telling it the right
	 * distance to go to, and also that it could get to the actual distance it was told to go to, then we knew it would work)
	 */
//	public static final double MAX_RPM = 600;
	//			  feed forward: max pow  |rev per sec  | time conversion |  native units per rot
//	private static final double RPM_PID_KF = 0.05; //10*1023 / (MAX_RPM/60 * 0.1 * 4096) /
//						 GEARBOX_CONVERSION_FACTOR; // TODO: IS THIS BETTER THAN BELOW?
	// private static final double RPM_PID_KF = 0.02713; 
//	private static final double RPM_PID_KP = 0;//0.01 * 1023 / 900.0; // set to 50% of max throttle (1023) when going 900 ticks/0.1s
//	private static final double RPM_PID_KI = 0; // NO NEED
//	private static final double RPM_PID_KD = 0; // NO NEED
	
	// constants
	public static final double MAX_MANUAL_SPEED = 0.75;
	private static final double POWER_REDUCTION_FACTOR = 1.0; // no reduction
	private static final double GEARBOX_CONVERSION_FACTOR = 50; // 100:1 gearboxq
	private static final double POS_PID_KP = 0.5; // TODO: MAY BE TOO HIGH (it will still be high because the winch is so slow and is so geared up)
	private static final double POS_PID_KI = 0; //0.005;
	private static final double POS_PID_KD = 0.4;
	private static final double REV_PER_INCH = 1/(2*Math.PI*0.4375); //1/(2*Math.PI*0.75); // circumference inches in one revolution
	
	public static final double MAX_ARM_EXTENSION = 14; // inches
	public static final double MIN_ARM_EXTENSION = 0.5; // inches, off of initial reset point
	public static final double ABSOLUTE_MIN_ARM_EXTENSION = -19.5;
	private static final double ARM_PIVOT_TO_15IN = 37.5; // inches
	private static final double ARM_LENGTH = 33; // inches
//	private static final double RETRACT_ANGLE = 25; // One of my super-simple contingency solutions was to have it retract all the way at a certain angle, which is 
													// probably what this was for
	private static final double RPM_PER_INCH_PER_SECOND = 1 * REV_PER_INCH*60; // to convert rev/sec to rpm //5000.0/39.27; // assuming w/v = 1/r where w(rpm) = w / 2*pi
//	public static final int MAX_WINCH_BY_ARM_ANGLE = 60; // before we simply would stop the arm once it tried to go past the max/min distances, we didn't need
														// a specific angle for it to stop winching at, so this wasn't needed
	public static final double ARM_EXTENSION_STOP_TOLERANCE = 0.05; // inches, distance off target winch position to stop at
	private static final double MAX_MOTOR_CURRENT = 14;
    
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
    	// tell Talon SRX to use encoder (the fancy one we attached to the VersaPRO gearbox is called a CTRE MAG Encoder)
    	// (the relative part means that it works relative to the zero value that was set when it turned on (it doesn't know
    	// exactly where it is-- you'd have to use absolute mode), but read here for more: 
    	// PAGE 49: http://content.vexrobotics.com/vexpro/pdf/Talon-SRX-Software-Reference-Manual-03302016.pdf )
    	winchMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	
    	// configure PID control for BOTH modes (we ASSUME ramp rate zero means infinite ramp rate - ramp rate is the volts/sec that it can increase power at)
    	winchPID = new PIDControl(POS_PID_KP, POS_PID_KI, POS_PID_KD, -POWER_REDUCTION_FACTOR, POWER_REDUCTION_FACTOR, 1);
    	
    	// these were for using the built-in PID control system, which I ended up stopping because I didn't undertand the units involved.
    	// (this manual for it is horrible: http://content.vexrobotics.com/vexpro/pdf/Talon-SRX-Software-Reference-Manual-03302016.pdf)
    	// so, I just used the one I made above, which just gives you -1 to 1 for power readings and uses inches off target as input)
    	// ALWAYS USE WHAT YOU ARE FAMILIAR WITH IF YOU CAN
//    	winchMotor.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, RPM_PID_KF, 1, 0, 0); // ramp rate is zero, but create 2 profiles
//    	winchMotor.setPID(POS_PID_KP, POS_PID_KI, POS_PID_KD, 0, 1, 0, 1); // limit integral accumulation too
    	
    	// Should set encoder pulse per rev, BUT no need with ctreMagEncoder (would be 4096 in quadrature mode)
//    	winchMotor.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);  
    	
    	// ensure braked (Motor brake, not pneumatic brake)
    	winchMotor.enableBrakeMode(true);
    	
    	// was used for making sure things did't blow up
//    	winchMotor.configPeakOutputVoltage(POWER_REDUCTION_FACTOR*-12.0, POWER_REDUCTION_FACTOR*12);
    	
    	// set base position to here and reset
    	resetWinchPosition();
//    	winchMotor.reset();
    	winchMotor.reverseSensor(false); // we didn't need to reverse sensor (if you did, the PID would be inverted,
    									// so instead of moving closer to a target, it would try to get away from it)
    									// This turns out to be a very bad thing with a 250lb winch.
    	
		// we used to set this to TalonControlMode.Speed, but see note above for why we ditched that, which was the RPM PID
    	// PercentVBus is just like a normal, non-talon motor (.set(1) is max, .set(-1) in min, etc.)
    	winchMotor.changeControlMode(TalonControlMode.PercentVbus); 
    }
    
    /**
     * @param rpm The rpm to set the motor at
     */
	public void set(double desiredSpeed) {

    	double distance = getCurrentDistance();
    	double setSpeed;
    	
    	// This was the code that had the arm move back in if it was too far out at a given arm angle.
    	// It was disabled because the arm potentiometer broke, so we couldn't tell arm angle.
//    	// IN is +
//    	if (!winchLimitOverride && distance >= getArmConstrainedDistance() &&
//    			Robot.armpivot.getArmAngle() < 100) 
//    	{
//    		if (desiredSpeed > 0) // if going IN, let it go
//    			setSpeed = desiredSpeed;
//    		else
//    			// otherwise, ignore any input and have it use a PID to move back to the max distance,
    			// as given by the arm's current angle.
//    			setSpeed = winchPID.getPIDoutput(getArmConstrainedDistance(), getCurrentDistance());
//    	}
    	// un-over-rideable limits: (stop if it's stalled, in which case it would be drawing a bunch of current)
    	// ABSOLUTE_MIN_ARM_EXTENSION is the distance when the winch has broken off and is lifting the robot.
    	// (where they hit the cable guides, basically)
//    	else if (winchMotor.getOutputCurrent() > MAX_MOTOR_CURRENT) //|| distance < ABSOLUTE_MIN_ARM_EXTENSION)
//    	{
//    		winchMotor.set(0);
//    	}
    	// if there are no limits applied, just set what the user wants
//    	else
//    	{
        	setSpeed = desiredSpeed;
//    	}
    	
        // SET distance limits here (subject to an override):
    	// the winch must be stopped when it is:
    	// too far out while trying to go out,
    	// too far in while trying to go in,
    	// or NOT stopped if these constraints are overridden
    	// IN is +
    	if (!winchLimitOverride &&
    		((distance >= MAX_ARM_EXTENSION && desiredSpeed < 0) || ((distance <= MIN_ARM_EXTENSION && desiredSpeed > 0) && !lowerWinchLimitOverride))) //  &&
//    			Timer.getMatchTime() > RobotMap.MATCH_END_PERIOD_LEN)))
	 	{
	 		setSpeed = 0;
	 	}
    	
		// ensure operating by simple mode (NOT RPM or position mode)
    	winchMotor.changeControlMode(TalonControlMode.PercentVbus);
    	
    	// SET THE SPEED
    	winchMotor.set(setSpeed);
    	
		 //make sure brake released (only if not zero)
		if (setSpeed != 0)
			releaseBrake();
		
    	// diagnostics
//		System.out.println("Speed: " + setSpeed + "Current RPM: 	" + winchMotor.getSpeed());
//    	System.out.println("Distance: " + distance + " " + setSpeed + " " + desiredSpeed);
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
    	
		// make sure brake released
    	releaseBrake();
    	
    	// diagnostics
//    	System.out.println(desiredSpeed);
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
	
	/**
	 * @return The distance, in inches, that represents the max distance
	 * the arm could extend to, AT THE CURRENT ARM ANGLE, and still be within the 15in boundary.
	 * Used to keep the arm from going outside that boundary using the PID Distance control system.
	 */
	public double getArmConstrainedDistance()
	{
		double mathValue = ARM_PIVOT_TO_15IN / 
			    		Math.cos(Math.toRadians(ArmPivot.ARM_STARTING_ANGLE - Robot.armpivot.getArmAngle()))
		    			- ARM_LENGTH;
		
		// ignore crazy values
		if (0 <= mathValue && mathValue <= MAX_ARM_EXTENSION)
			return mathValue;
		else
			return MAX_ARM_EXTENSION;
	}
	
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
    
	/** THIS IS DEPRECTED BECAUSE IT IS BETTER TO JUST PASSIVELY REGULATE THE ARM IN THE SET() METHOD
	 * Based on the arm's speed, set the winch to the speed required to make the arm move vertically
	 * @deprecated
	 */
	public void setWinchByArmSpeed()
	{
		// the following was a last-minute basic solution to keep the arm inside the boundary
//		double angle = Robot.armpivot.getArmAngle();
//		if (angle < RETRACT_ANGLE)
//			setDistance(MAX_ARM_EXTENSION - 2);
//		else if (angle > RETRACT_ANGLE && angle < 70) 
//			setDistance(MIN_ARM_EXTENSION);
		
		// get distance we need to get to based on current angle
		// (this is just trig: we want to find the arm distance at theta to stay at 15in out)
		// (but theta must be off horizontal (the axis the distance to 15in is on))
		setDistance(getArmConstrainedDistance());
		
		// get speed based on current angle (this was the old calculus-based way using the function below)
		// set(getWinchSpeed(Robot.armpivot.getArmAngle(), Robot.armpivot.getArmRate()));
	}
	
	/**
	 * THIS IS DEPRECATED BECAUSE IT WAS SUPPOSED TO USE CALCULUS TO SET THE WINCH SPEED
	 * BASED ON JUST THE ARM SPEED (setWinchByArmSpeed as it is above is MUCH better)
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
		// or hook disconnection (THIS IS DONE IN SET with distance limits)

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
   * THIS COULD HAVE BEEN USED EVEN WITH OUR FINAL SYSTEM TO STOP THE ARM SPEED
   * FROM EXCEEDING THE CORRECTION SPEED OF THE PID SYSTEM, BUT IT WOULD BE VERY COMPLICATED TO GET
   * IT WORKING (and there's still the noise issue in getArmRate())
   * Check if given arm speed must be limited to meet winch speed requirements,
   * then return the new value if necessary (otherwise just return old one)
   * @deprecated
  */
  public double getLimitedArmSpeed(double commandedSpeed) 
  {
  	// BE SURE TO REMOVE ONCE SET
  	revPerInchPerSecCoefficent = SmartDashboard.getNumber(" Arm by Winch Coefficent: ", revPerInchPerSecCoefficent);
  	
  	// max winch speed = 1.0, and winch=k*arm, so (max) arm=1.0/k,
  	// where k is the conversion factor in above function
  	// (this will be in degrees per second - we're getting angleDelta above)
  	double currentMaxArmSpeed = 1.0 / 
  						   (revPerInchPerSecCoefficent * RPM_PER_INCH_PER_SECOND *
  						    ARM_PIVOT_TO_15IN * 
  						    Math.tan(Math.toRadians(Robot.armpivot.getArmAngle() - ArmPivot.ARM_STARTING_ANGLE)) / 
  						    Math.cos(Math.toRadians(Robot.armpivot.getArmAngle() - ArmPivot.ARM_STARTING_ANGLE)));
  	
  	double winchSpeed = getWinchSpeed(Robot.armpivot.getArmAngle(),
  			  						  Robot.armpivot.getArmRate());				
  	
  	if (winchSpeed > 1.0)
  		return currentMaxArmSpeed / 6.0; // 1 rev/min = 6 degrees/sec
  	else if (winchSpeed < -1.0)
  		return -currentMaxArmSpeed / 6.0;
  	else
  		return commandedSpeed;
  }
}