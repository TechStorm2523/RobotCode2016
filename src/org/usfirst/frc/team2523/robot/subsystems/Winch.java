
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
	public double ENCODER_PULSE_PER_REV = 1024/10.0; // encoder is 1024 pulses per rev, but is before a 10:1 gearbox
	public double ARM_PIVOT_TO_15IN = 39.5; // TODO: CALCULATE!!!!
	public double RPM_PER_INCH_PER_SECOND = 2*Math.PI / 0.75; // assuming w/v = 1/r where w(rpm) = w / 2*pi
	public int MAX_WINCH_BY_ARM_ANGLE = 60;
    
	// definitions
	CANTalon winchMotor = new CANTalon(RobotMap.winch);
    DoubleSolenoid winchBrake = new DoubleSolenoid(RobotMap.winchBrakeSolenoid1, RobotMap.winchBrakeSolenoid2);
    
    public Winch()
    {
    	// tell Talon SRX to use encoder (Quadrature Encoder)
    	winchMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	
    	// and tell it to operate via RPM commands
    	winchMotor.changeControlMode(TalonControlMode.Speed);
    	
    	// configure PID control
    	winchMotor.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD);
    	winchMotor.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	winchMotor.setCloseLoopRampRate(0); // we ASSUME ramp rate zero means infinite ramp rate
    	
    	// ensure braked (Motor brake, not pneumatic brake)
    	winchMotor.enableBrakeMode(true);
    }
    
    /**
     * 
     * @param rpm The rpm to set the motor at
     */
	public void set(double rpm) {
		winchMotor.set(rpm);
		
		// make sure brake released (only if not zero)
		if (rpm != 0)
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

    
