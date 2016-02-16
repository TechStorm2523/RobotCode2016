
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
	public final double RPM_PID_KP = 0.005;
	public final double RPM_PID_KI = 0;//0.001;
	public final double RPM_PID_KD = 0; //0.05;
	public final double ENCODER_PULSE_PER_REV = 4096; // direct drive (this is the normal rev per pulse)
	public final double MAX_RPM = 10000;
	public final double RPM_PER_VELOCITY = 1 / (Math.PI*2.875/60); // inch/sec - by formula x/v = 1/(pi*d)
	public final double TARGET_RPM_TOLERANCE = 100;
	public final double LAUNCH_ANGLE = 64;
	public final double LAUNCH_HEIGHT = 29.0 / 12.0; // feet
	public final double TARGET_HEIGHT = 7*12+1 + 12 ; // feet (target base + to target center)
	public final double CAMERA_DISTANCE_OFF_LAUNCH = 7 / 12.0; // feet

    
    public CANTalon launchBack = new CANTalon(RobotMap.launcherMotBack);
    CANTalon launchFront = new CANTalon(RobotMap.launcherMotFront);
//    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoder1, RobotMap.launcherEncoder2, 
//									false, Encoder.EncodingType.k4X);
//    PIDControl rpmPID = new PIDControl(RPM_PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// tell Talon SRXs to use encoder (Quadrature Encoder)
    	launchBack.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	launchFront.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	
    	// and tell it to operate via RPM commands
    	launchBack.changeControlMode(TalonControlMode.Speed);
    	launchFront.changeControlMode(TalonControlMode.Speed);
    	
    	// configure PID control
    	launchBack.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD); // I and D can be zero, it should never have difficulty
    	launchFront.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD);
    	launchBack.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	launchFront.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	launchBack.setCloseLoopRampRate(0); // we ASSUME ramp rate zero means infinite ramp rate
    	launchFront.setCloseLoopRampRate(0); // we ASSUME ramp rate zero means infinite ramp rate
    	
    	// ensure NOT braked
    	launchBack.enableBrakeMode(false);
    	launchFront.enableBrakeMode(false);
    	
    	// reverse ONE
    	launchBack.reverseOutput(true);
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
		launchBack.set(rpm);
    	launchFront.set(rpm);
	}
	
	/**
	 * @return An array of the current RPMs of the two motor's
	 */
	public double[] getCurrentRPMs()
	{
		double[] rpms = new double[2];
		rpms[0] = launchBack.getEncVelocity(); // ?? MAY BE IN revs per SECOND... multiply by 60?
		rpms[1] = launchFront.getEncVelocity();
		return rpms; 
	}
	
	/**
	 * @return An array of the current errors of the two motor's PID control systems
	 */
	public double[] getCurrentRPMError()
	{
		double[] errors = new double[2];
		errors[0] = launchBack.getClosedLoopError();
		errors[1] = launchFront.getClosedLoopError();
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
