
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Winch extends Subsystem {
	// constants
	public double RPM_PID_KP = 0.1;
	public double RPM_PID_KI = 0; // NO NEED
	public double RPM_PID_KD = 0; // NO NEED
	public double POS_PID_KP = 0.1;
	public double POS_PID_KI = 0.01;
	public double POS_PID_KD = 0; // NO NEED
	public double ENCODER_PULSE_PER_REV = 1024/10.0; // encoder is 1024 pulses per rev, but is before a 10:1 gearbox
	public double REV_PER_INCH = 1/2*Math.PI*0.75; // circumference inches in one revolution
	
	public double MAX_ARM_EXTENSION = 18; // inches
	public double ARM_PIVOT_TO_15IN = 39.5; // inches
	public double RPM_PER_INCH_PER_SECOND = 2*Math.PI / 0.75; // assuming w/v = 1/r where w(rpm) = w / 2*pi
	public int MAX_WINCH_BY_ARM_ANGLE = 60;
	public double ARM_EXTENSION_STOP_TOLERANCE = 0.1; // inches, distance off target winch position to stop at
    
	// definitions
	CANTalon winchMotor = new CANTalon(RobotMap.winch);
    DoubleSolenoid winchBrake = new DoubleSolenoid(RobotMap.winchBrakeSolenoid1, RobotMap.winchBrakeSolenoid2);
    
    public Winch()
    {
    	// tell Talon SRX to use encoder (Quadrature Encoder)
    	winchMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	
    	// and tell it to operate via RPM commands (for now)
    	winchMotor.changeControlMode(TalonControlMode.Speed);
    	
    	// configure PID control for BOTH modes (we ASSUME ramp rate zero means infinite ramp rate)
    	winchMotor.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, 0, 0, 0, 0); // all other values are zero, but create 2 profiles
    	winchMotor.setPID(POS_PID_KP, POS_PID_KI, POS_PID_KD, 0, 1, 0, 1); // limit integral accumulation for position
    	winchMotor.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV); 	
    	
    	// ensure braked (Motor brake, not pneumatic brake)
    	winchMotor.enableBrakeMode(true);
    	
    	// set base position to here and reset
    	winchMotor.setPosition(0);
    	winchMotor.reset();
    }
    
    /**
     * 
     * @param rpm The rpm to set the motor at
     */
	public void set(double rpm) {
		// ensure operating by RPM mode and corresponding PID
    	winchMotor.changeControlMode(TalonControlMode.Speed);
    	winchMotor.setProfile(0);
    	
		winchMotor.set(rpm);
		
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
    	winchMotor.set(distance*REV_PER_INCH);
    	
		// make sure brake released (only if not zero)
    	releaseBrake();
	}
	
	/**
	 * @return The current distance off the no-arm-extension position in inches
	 */
	public double getCurrentDistance()
	{
		return winchMotor.getEncPosition() / REV_PER_INCH;
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
		if (currentAngle > MAX_WINCH_BY_ARM_ANGLE)
			return 0;
		else
		{
			// derived from derivative of arm radius ( d/cos(theta) ) with respect to angle multiplied by
			// the derivative of angle with respect to time.
			// (dr/dTheta * dtheta/dt = dr/dt)
			return RPM_PER_INCH_PER_SECOND *
				   ARM_PIVOT_TO_15IN * 
				   Math.tan(Math.toRadians(currentAngle - Robot.armpivot.ARM_STARTING_ANGLE)) / 
				   Math.cos(Math.toRadians(currentAngle - Robot.armpivot.ARM_STARTING_ANGLE)) *
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

    
