package org.usfirst.frc.team2523.robot;

import org.usfirst.frc.team2523.robot.commands.*;
import org.usfirst.frc.team2523.robot.subsystems.Winch;

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
	
	// the recording joystick simply replaces the normal joystick so it can impersonate it in auto
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
   		
//   		driveButton4.whileHeld(new SetWinchByThrottle());
   		driveButton9.whileHeld(new OverrideLimits());
//   		driveButton10.whenPressed(new ResetWinchPosition());
   		
   		driveButton11.whileHeld(new SetWinch(Winch.MAX_MANUAL_SPEED*2));
    	driveButton12.whileHeld(new SetWinch(-Winch.MAX_MANUAL_SPEED));
    	
		driveButton7.whenPressed(new SetWinchBrake(true));
		driveButton8.whenPressed(new SetWinchBrake(false));
//		driveButton10.whileHeld(new IdentifyBestTarget());
    	
    	// UTIL
   		utilButton1.whileHeld(new ManualLaunch());
    	utilButton2.whenPressed(new AutoRangingLaunch());
    	
//    	utilButton5.whenPressed(new AutoExpel());
    	utilButton5.whileHeld(new FeederExpel());
    	utilButton3.whenPressed(new AutoCollect()); 
    	
    	utilButton11.whenPressed(new LauncherLower());
   		utilButton12.whenPressed(new LauncherRaise());

   		utilButton4.whileHeld(new SetWinch(Winch.MAX_MANUAL_SPEED*2)); // faster
    	utilButton6.whileHeld(new SetWinch(-Winch.MAX_MANUAL_SPEED));
//   		utilButton4.whenPressed(new ContractArmWithSolenoid());
//   		utilButton6.whenPressed(new ExtendArmWithSolenoid());
    	
		utilButton7.whenPressed(new SetWinchBrake(true));
		utilButton8.whenPressed(new SetWinchBrake(false));
		
	//	utilButton9.whileHeld(new OverrideLimits());
		utilButton9.whenPressed(new EndOfMatchOverride());
		utilButton10.whenPressed(new AutoLaunch());
//		utilButton10.whenPressed(new ResetWinchPosition());
		
//		utilButton12.whileHeld(new IdentifyBestTarget());
//   		utilButton11.whileHeld(new SetLauncherByThrottle());
    
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

