package org.usfirst.frc.team2523.robot;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Timer;



/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    // public static int leftMotor = 1;
    // public static int rightMotor = 2;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
	
	// GLOBAL CONSTANTS
	public static final double GRAVITY = 32.2; // ft/sec^2
	public static final double MATCH_LENGTH = 150; //2min 30secs
	public static final String JOYSTICK_RECORDINGS_SAVE_LOCATION = "/home/admin/joystickRecordings/"; // on roboorio
	
	// PWM Outputs (Motors)
	public static int Rfront = 0;
	public static int Rback = 1;
	public static int Lfront = 2;
	public static int Lback = 3;
	public static int feeder = 5;
	
	// CAM Ideas (For TalonSRXs)
	public static int launcherMotBack = 6;
	public static int launcherMotFront = 5;
	public static int lifter1 = 2;
	public static int lifter2 = 4;
	public static int winch = 3;
	
	// Solenoid Outputs (PCM)
	public static int launcherSolenoid1 = 1;
	public static int launcherSolenoid2 = 2;
	public static int winchBrakeSolenoid = 0;
	
	// Analog In Ports
	public static int armPoten1 = 0;
	
	// Digital IO Ports (Sensors/Encoders)
	public static int launcherEncoder1 = 2;
	public static int launcherEncoder2 = 3;
	public static int ballDetectorLimSwitch = 0;
	public static int driveEncoder1 = 7;
	public static int driveEncoder2 = 8;
			
}
