package org.usfirst.frc.team2523.robot;

import edu.wpi.first.wpilibj.DigitalSource;



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
	public static int ARM_STARTING_ANGLE = 0; // degrees, positive for down off horizontal
	public static int LAUNCH_ANGLE = 0;
	
	// PWM Outputs (Motors)
	public static int Rfront = 0;
	public static int Rback = 1;
	public static int Lfront = 2;
	public static int Lback = 3;
	public static int feeder = 4;
	public static int launch1 = 5;
	public static int launch2 = 6;
	public static int lifter1 = 7;
	public static int lifter2 = 8;
	public static int winch = 9;
	
	
	// Solenoid Outputs (PCM)
	public static int armSolenoid1 = 0;
	public static int armSolenoid2 = 1;
	public static int launcherSolenoid1 = 2;
	public static int launcherSolenoid2 = 3;
	public static int winchBrakeSolenoid1 = 4;
	public static int winchBrakeSolenoid2 = 5;
	
	// RELAY Outputs (Relays)
	
	// Digital IO Ports (Sensors/Encoders)
	public static int armPoten1 = 0;
	public static int armPoten2 = 1;
	public static int launcherEncoder1 = 2;
	public static int launcherEncoder2 = 3;
			
}
