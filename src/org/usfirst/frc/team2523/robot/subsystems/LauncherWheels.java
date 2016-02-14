
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetLauncherByThrottle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Controls the launcher wheels
 */
public class LauncherWheels extends Subsystem {
	// constants
	public final double RPM_PID_KP = 0.1;
	public final double ENCODER_PULSE_PER_REV = 1024; // direct drive (this is the normal rev per pulse)
	public final double MAX_RPM = 13050;
	public final double RPM_PER_VELOCITY = 1 / (Math.PI*2.875/60); // inch/sec - by formula x/v = 1/(pi*d/
	public final double TARGET_RPM_TOLERANCE = 100;
	public final double LAUNCH_ANGLE = 64;
	public final double LAUNCH_HEIGHT = 0; // feet
	public final double TARGET_HEIGHT = 7*12+1 + 12 ; // feet
	public final double CAMERA_DISTANCE_OFF_LAUNCH = 0; // feet

    
    CANTalon launch1 = new CANTalon(RobotMap.launcherMot1);
    CANTalon launch2 = new CANTalon(RobotMap.launcherMot2);
//    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoder1, RobotMap.launcherEncoder2, 
//									false, Encoder.EncodingType.k4X);
//    PIDControl rpmPID = new PIDControl(RPM_PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// tell Talon SRXs to use encoder (Quadrature Encoder)
    	launch1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	launch2.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	
    	// and tell it to operate via RPM commands
    	launch1.changeControlMode(TalonControlMode.Speed);
    	launch2.changeControlMode(TalonControlMode.Speed);
    	
    	// configure PID control
    	launch1.setPID(RPM_PID_KP, 0, 0); // I and D can be zero, it should never have difficulty
    	launch2.setPID(RPM_PID_KP, 0, 0);
    	launch1.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	launch2.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	launch1.setCloseLoopRampRate(0); // we ASSUME ramp rate zero means infinite ramp rate
    	launch2.setCloseLoopRampRate(0); // we ASSUME ramp rate zero means infinite ramp rate
    	
    	// ensure NOT braked
    	launch1.enableBrakeMode(false);
    	launch2.enableBrakeMode(false);
    	
    	// reverse ONE
    	launch1.reverseOutput(true);
    }
    
	public void setByThrottle() {
		// shift so goes from 0 at base to 1 at max (and, because setting RPM, scale to max)
    	set(MAX_RPM*0.5*(-OI.UtilStick.getThrottle() + 1));
	}
	
	/**
	 * @param rpm The rpm of the launcher wheels to set
	 */
	public void set(double rpm)
	{
		launch1.set(rpm);
    	launch2.set(rpm);
	}
	
	/**
	 * @return An array of the current RPMs of the two motor's
	 */
	public double[] getCurrentRPMs()
	{
		double[] rpms = new double[2];
		rpms[0] = launch1.getEncVelocity(); // ?? MAY BE IN revs per SECOND... multiply by 60?
		rpms[1] = launch2.getEncVelocity();
		return rpms; 
	}
	
	/**
	 * @return An array of the current errors of the two motor's PID control systems
	 */
	public double[] getCurrentRPMError()
	{
		double[] errors = new double[2];
		errors[0] = launch1.getClosedLoopError();
		errors[1] = launch2.getClosedLoopError();
		return errors; 
	}
	
	/**
	 * @param range Range to target in feet
	 * @return RPM to set at for that range
	 */
	public double getRPMbyRange(double range)
	{
		return getVelocityByRange(range) * RPM_PER_VELOCITY;
	}
	
	/**
	 * @param range Range to target in feet
	 * @return
	 */
	private double getVelocityByRange(double range)
	{
		return Math.sqrt(2*(range + CAMERA_DISTANCE_OFF_LAUNCH)*RobotMap.GRAVITY / Math.sin(Math.toRadians(2*LAUNCH_ANGLE)));
	}
	
	public boolean inRange(double range)
	{
		return getRPMbyRange(range) <= MAX_RPM;
	}
	
	/**
	 * 
	 * @param rpm
	 */
	/*public void setTargetRPM(double rpm)
	{
		if (rpm != 0)
			set(rpmPID.getPoutput(rpm, getCurrentRPM()));
		else
			set(0);
	}*/
	
    public void initDefaultCommand() {
    	//setDefaultCommand(new SetLauncherByThrottle());
    }	
}
