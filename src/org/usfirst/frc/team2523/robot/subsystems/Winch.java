
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetWinchByThrottle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Winch extends Subsystem {
	// constants
	public static final double MAX_RPM = 62.5;
	//						feed forward: max pow  |rev per sec  | time conversion |  native units per rot
	private static final double RPM_PID_KF = 1023 / (MAX_RPM/60 * 0.1 * 4096); 
	private static final double RPM_PID_KP = 0.5 * 1023 / 900.0; // set to 50% of max throttle (1023) when going 900 ticks/0.1s
	private static final double RPM_PID_KI = 0; // NO NEED
	private static final double RPM_PID_KD = 0; // NO NEED
	private static final double POS_PID_KP = 0.1;
	private static final double POS_PID_KI = 0.01;
	private static final double POS_PID_KD = 0; // NO NEED
	private static final double GEARBOX_CONVERSION_FACTOR = 100; // 100:1 gearbox
	private static final double REV_PER_INCH = 1/(2*Math.PI*0.75); // circumference inches in one revolution
	
	public static final double MAX_ARM_EXTENSION = 18; // inches
	private static final double ARM_PIVOT_TO_15IN = 39.5; // inches
	private static final double RPM_PER_INCH_PER_SECOND = REV_PER_INCH*60; // to convert rev/sec to rpm //5000.0/39.27; // assuming w/v = 1/r where w(rpm) = w / 2*pi
//	public static final int MAX_WINCH_BY_ARM_ANGLE = 60; // using getCurrentDistance() we think
	public static final double ARM_EXTENSION_STOP_TOLERANCE = 0.05; // inches, distance off target winch position to stop at
    
	// variables for adjusting constants
	public double revPerInchPerSecCoefficent = 1;
	
	// definitions
	public CANTalon winchMotor = new CANTalon(RobotMap.winch);
    Solenoid winchBrake = new Solenoid(RobotMap.winchBrakeSolenoid);
    
    public Winch()
    {
    	// tell Talon SRX to use encoder (Quadrature Encoder)
    	winchMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	winchMotor.reverseSensor(false);
    	
    	// configure PID control for BOTH modes (we ASSUME ramp rate zero means infinite ramp rate)
    	winchMotor.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, RPM_PID_KF, 1, 0, 0); // ramp rate is zero, but create 2 profiles
    	winchMotor.setPID(POS_PID_KP, POS_PID_KI, POS_PID_KD, 0, 1, 0, 1); // limit integral accumulation too
//    	winchMotor.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);  // no need with ctreMagEncoder (would be 4096 in quadrature)
    	
    	// ensure braked (Motor brake, not pneumatic brake)
    	winchMotor.enableBrakeMode(true);
    	
    	// set base position to here and reset
    	winchMotor.setPosition(0);
    	winchMotor.reset();
    }
    
    /**
     * @param rpm The rpm to set the motor at
     */
	public void set(double rpm) {
		// ensure operating by RPM mode and corresponding PID
    	winchMotor.changeControlMode(TalonControlMode.Speed);
    	winchMotor.setProfile(0);
    	
		winchMotor.set(rpm*GEARBOX_CONVERSION_FACTOR);
		
		System.out.println("RPM: " + rpm + "		Current RPM: " + winchMotor.getSpeed() + "		Enc Velocity: " + winchMotor.getEncVelocity());
		
		// make sure brake released (only if not zero)
		if (rpm != 0)
			releaseBrake();
	}
	
	/**
	 * @param distance Distance of extension to go to, in inches
	 */
	public void setDistance(double distance)
	{
		// ensure operating by Distance mode and corresponding PID
    	winchMotor.changeControlMode(TalonControlMode.Position);
    	winchMotor.setProfile(1);
    	
    	// set in revolutions
    	winchMotor.set(distance * GEARBOX_CONVERSION_FACTOR * REV_PER_INCH);
    	
		// make sure brake released
    	releaseBrake();
	}
	
	/**
	 * @return The current distance off the no-arm-extension position in inches
	 */
	public double getCurrentDistance()
	{
		return winchMotor.getPosition() / (GEARBOX_CONVERSION_FACTOR * REV_PER_INCH);
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
	 * @return The correctly scaled winch speed (in RPMs, or whatever value RPM_PER_INCH_PER_SECOND has in it)
	 */
	private double getWinchSpeed(double currentAngle, double angleDelta) 
	{
		// disable if above max angle to avoid infinite speed issues and cable slack
		// !!! TODO: Maybe juse use getCurrentDistance and stop whenever the winch has extended to full arm length
		if (getCurrentDistance() > MAX_ARM_EXTENSION) // (currentAngle > MAX_WINCH_BY_ARM_ANGLE)
			return 0;
		else
		{
			// derived from derivative of arm radius ( d/cos(theta) ) with respect to angle multiplied by
			// the derivative of angle with respect to time.
			// (dr/dTheta (i.e. ARM... GLE)) * dtheta/dt (i.e. angleDelta) = dr/dt)
			return revPerInchPerSecCoefficent * RPM_PER_INCH_PER_SECOND *
				   ARM_PIVOT_TO_15IN * 
				   Math.tan(Math.toRadians(currentAngle - ArmPivot.ARM_STARTING_ANGLE)) / 
				   Math.cos(Math.toRadians(currentAngle - ArmPivot.ARM_STARTING_ANGLE)) *
				   angleDelta;
		}
	}

	/**
	 * Check if given arm speed must be limited to meet winch speed requirements,
	 * then return the new value if necessary (otherwise just return old one)
	 */
	public double getLimitedArmSpeed(double commandedSpeed) 
	{
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
		
		if (winchSpeed > MAX_RPM)
			return currentMaxArmSpeed / 6.0; // 1 rev/min = 6 degrees/sec
		else if (winchSpeed < -MAX_RPM)
			return -currentMaxArmSpeed / 6.0;
		else
			return commandedSpeed;
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
    	setDefaultCommand(new SetWinchByThrottle());
    }
}

    
