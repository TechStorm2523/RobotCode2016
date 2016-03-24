
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.SetLauncherByThrottle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Controls the launcher wheels
 */
public class LauncherWheels extends Subsystem {
	// constants
	public static final double MAX_RPM = 14000;
	//						feed forward: max pow  |rev per sec  | time conversion |  native units per rot
	private static final double RPM_PID_KF = 1023 / (MAX_RPM/60 * 0.1 * 4096); // TODO: IS THIS GOOD???????????????
	private static final double RPM_PID_KP = 0.01 * 1023 / 900.0; // set to 10% of max throttle (1023) when going 900 ticks/0.1s
	private static final double RPM_PID_KI = 0;//0.001;
	private static final double RPM_PID_KD = 0;
	private static final double RPM_PID_BACK_KF = RPM_PID_KF; // TODO: IS THIS GOOD???????????????
	private static final double RPM_PID_BACK_KP = RPM_PID_KP; // set to 10% of max throttle (1023) when going 900 ticks/0.1s
	private static final double RPM_PID_BACK_KI = RPM_PID_KI;//0.001;
	private static final double RPM_PID_BACK_KD = RPM_PID_KD;
//	public static final double GEARBOX_CONVERSION_FACTOR = 1; // 1:1 gearbox
	private static final double RPM_PER_VELOCITY = 1 / (Math.PI*2.875/60); // inch/sec - by formula x/v = 1/(pi*d)
	public static final double TARGET_SPEED_TOLERANCE = 100; // actually in native units
	public static final double RANGE_DIFFERENCE_DEADZONE = 1; // feet (changes in range when auto launching that constitute readjustment)
	private static final double LAUNCH_ANGLE = 64;
	public static final double LAUNCH_HEIGHT = 29.0 / 12.0; // feet (height of launch from center of ball)
	private static final double TARGET_HEIGHT = 7*12+1 + 24; // feet (target base + to target center) (SHOOT HIGH FOR AIR RESISTANCE)
	private static final double CAMERA_DISTANCE_OFF_LAUNCH = 7 / 12.0; // feet (horizontal distance)

	// auto constants
	public static final double POST_LAUNCH_WAIT_TIME = 0.8;
	
	// variables for adjusting constants
	public double rpmPerVelocityCoefficent = 1;
	
	// variables
	public double currentTargetRPM = 0;
	
    	public CANTalon launchBack = new CANTalon(RobotMap.launcherMotBack);
    	public CANTalon launchFront = new CANTalon(RobotMap.launcherMotFront);
//    Encoder rpmEncoder = new Encoder(RobotMap.launcherEncoder1, RobotMap.launcherEncoder2, 
//									false, Encoder.EncodingType.k4X);
//    PIDControl rpmPID = new PIDControl(RPM_PID_KP, 0, 0); // we're only going to need proportional control
    	
    public LauncherWheels()
    {
    	// tell Talon SRXs to use encoder (Quadrature Encoder)
    	launchBack.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	launchFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	
    	// and tell it to operate via RPM commands
    	launchBack.changeControlMode(TalonControlMode.Speed);
    	launchFront.changeControlMode(TalonControlMode.Speed);
    	
    	// configure PID control (we ASSUME ramp rate zero means infinite ramp rate)
    	launchBack.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, RPM_PID_KF, 1, 0, 0);
    	// launchBack.setPID(RPM_PID_BACK_KP, RPM_PID_BACK_KI, RPM_PID_BACK_KD, RPM_PID_BACK_KF, 1, 0, 0);
    	launchFront.setPID(RPM_PID_KP, RPM_PID_KI, RPM_PID_KD, RPM_PID_KF, 1, 0, 0);
//    	launchBack.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV); // NO NEED with CtreMagEncoder
//    	launchFront.configEncoderCodesPerRev( (int) ENCODER_PULSE_PER_REV);
    	
    	// ensure NOT braked
    	launchBack.enableBrakeMode(false);
    	launchFront.enableBrakeMode(false);
    	
    	// reverse where needed
//    	launchFront.reverseSensor(true);
    	launchBack.reverseOutput(true);
    	
    	// reset sensors
    	launchBack.setPosition(0);
    	launchFront.setPosition(0);
    }
    
	public void setByThrottle() {
		// shift so goes from 0 at base to 1 at max (and, because setting RPM, scale to max)
//		if (!Robot.feeder.ballstate()) {
        	set(MAX_RPM*0.5*(-Robot.oi.UtilStick.getThrottle() + 1));
//	    } else {
//	        System.out.println("No Ball!");
//	    }
	}
	
	/**
	 * @param rpm The rpm of the launcher wheels to set
	 */
	public void set(double rpm)
	{
		launchBack.set(rpm);
    		launchFront.set(rpm);
    		
    		currentTargetRPM = rpm;
//    	System.out.print("RPM:			" + (int) rpm);
//    	System.out.println(" Front: 		" + (int) launchFront.getSpeed() + 
//    					   " Back: 			" + (int) launchBack.getSpeed());
    	// System.out.println("Current RPM Errors: F: 	" + getCurrentRPMError()[0] + " B: 	" + getCurrentRPMError()[1]);
	}
	
	/**
	 * @return An array of the current RPMs of the two motor's
	 */
	public double[] getCurrentRPMs()
	{
		double[] rpms = new double[2];
		rpms[0] = launchFront.getSpeed();//getEncVelocity(); // look at the 'Talon SRX Software Manual' for explanation
		rpms[1] = launchBack.getSpeed(); 
		return rpms; 
	}
	
	/**
	 * @return An array of the current errors of the two motor's PID control systems
	 */
	public double[] getCurrentRPMError()
	{
		double[] errors = new double[2];
		errors[0] = launchFront.getClosedLoopError();
		errors[1] = launchBack.getClosedLoopError();
		return errors; 
	}
	
	/**
	 * @param range Range to target in feet
	 * @return RPM to set at for that range
	 */
	public double getRPMbyRange(double range)
	{
		// BE SURE TO REMOVE ONCE SET
		rpmPerVelocityCoefficent = SmartDashboard.getNumber(" Launcher RPM to Velocity Coefficent: ", rpmPerVelocityCoefficent);

		return getVelocityByRange(range) * rpmPerVelocityCoefficent * RPM_PER_VELOCITY;
	}
	
	/**
	 * @param range Range to target in feet
	 * @return
	 */
	private double getVelocityByRange(double range)
	{
		// shift range by camera
		range = range + CAMERA_DISTANCE_OFF_LAUNCH;
		
		return range / Math.cos(Math.toRadians(LAUNCH_ANGLE)) *
			   Math.sqrt(RobotMap.GRAVITY / (2*range*Math.tan(Math.toRadians(LAUNCH_ANGLE)) - 2*(TARGET_HEIGHT-LAUNCH_HEIGHT)));

		// to hit at peak (WRONG)
//		return Math.sqrt(2*(range + CAMERA_DISTANCE_OFF_LAUNCH)*RobotMap.GRAVITY / Math.sin(Math.toRadians(2*LAUNCH_ANGLE)));
	}
	
	public boolean inRange(double range)
	{
		return getRPMbyRange(range) <= MAX_RPM;
	}
	
    public void initDefaultCommand() {
    	//setDefaultCommand(new SetLauncherByThrottle());
    }	
}
