package org.usfirst.frc.team2523.robot;

import org.usfirst.frc.team2523.robot.commands.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
//    public Joystick DriveStick = new Joystick(0);
    public RecordingJoystick DriveStick = new RecordingJoystick(0);
    Button driveButton1 = new JoystickButton(DriveStick, 1),
			driveButton2 = new JoystickButton(DriveStick, 2),
			driveButton3 = new JoystickButton(DriveStick, 3),
			driveButton4 = new JoystickButton(DriveStick, 4),
			driveButton5 = new JoystickButton(DriveStick, 5),
			driveButton6 = new JoystickButton(DriveStick, 6),
			driveButton7 = new JoystickButton(DriveStick, 7),
			driveButton8 = new JoystickButton(DriveStick, 8),
			driveButton9 = new JoystickButton(DriveStick, 9),
			driveButton10 = new JoystickButton(DriveStick, 10),
			driveButton11 = new JoystickButton(DriveStick, 11),
			driveButton12 = new JoystickButton(DriveStick, 12);
    
    
//    public Joystick UtilStick = new Joystick (1);
    public RecordingJoystick UtilStick = new RecordingJoystick(1);
    Button utilButton1 = new JoystickButton(UtilStick, 1),
			utilButton2 = new JoystickButton(UtilStick, 2),
			utilButton3 = new JoystickButton(UtilStick, 3),
			utilButton4 = new JoystickButton(UtilStick, 4),
			utilButton5 = new JoystickButton(UtilStick, 5),
			utilButton6 = new JoystickButton(UtilStick, 6),
			utilButton7 = new JoystickButton(UtilStick, 7),
			utilButton8 = new JoystickButton(UtilStick, 8),
			utilButton9 = new JoystickButton(UtilStick, 9),
			utilButton10 = new JoystickButton(UtilStick, 10),
			utilButton11 = new JoystickButton(UtilStick, 11),
			utilButton12 = new JoystickButton(UtilStick, 12);
    
    public OI()
    {
    	// DRIVE
    	driveButton1.whenPressed(new AutoCollect());
    	driveButton2.whileHeld(new FeederExpel()); // AutoExpel()????
    	
   		driveButton3.whenPressed(new LauncherLower());
   		driveButton5.whenPressed(new LauncherRaise());
    	
    	// UTIL
   		utilButton1.whileHeld(new ManualLaunch());
//   		utilButton1.whileHeld(new SetLauncherByThrottle());
    	utilButton2.whenPressed(new AutoLaunch());
    	
    	utilButton3.whileHeld(new FeederExpel());
    	utilButton5.whileHeld(new FeederFire());

    	// TODO: THIS COULD SCREW UP THE NORMAL WINCH-ARM FUNCTION!!! SET BASED ON HOW WELL THE LIFT COMMAND WORKS
    	utilButton4.whenPressed(new SetWinch(-0.5)); // slower
    	utilButton4.whenReleased(new SetWinch(0));
    	utilButton6.whenPressed(new SetWinch(1));
    	utilButton6.whenReleased(new SetWinch(0));
    	
		utilButton7.whenPressed(new SetWinchBrake(true));
		utilButton8.whenPressed(new SetWinchBrake(false));
    	
//    	utilButton8.whenPressed(new LiftRobot());
//	    utilButton12.whenPressed(new GateMacro());
    
	    // There are a few additional built in buttons you can use. Additionally,
	    // by subclassing Button you can create custom triggers and bind those to
	    // commands the same as any other Button.
	    
	    //// TRIGGERING COMMANDS WITH BUTTONS
	    // Once you have a button, it's trivial to bind it to a button in one of
	    // three ways:
	    
	    // Start the command when the button is pressed and let it run the command
	    // until it is finished as determined by it's isFinished method.  
	    
	    // Start the command when the button is released  and let it run the command
	    // until it is finished as determined by it's isFinished method.
	}
}

