package org.usfirst.frc.team2523.robot;

import org.usfirst.frc.team2523.robot.commands.FeederOn;

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
    public Joystick DriveStick = new Joystick(1);
    public static Joystick UtilStick = new Joystick (2);
    Button button1 = new JoystickButton(UtilStick, 1),
			button2 = new JoystickButton(UtilStick, 2),
			button3 = new JoystickButton(UtilStick, 3),
			button4 = new JoystickButton(UtilStick, 4),
			button5 = new JoystickButton(UtilStick, 5),
			button6 = new JoystickButton(UtilStick, 6),
			button7 = new JoystickButton(UtilStick, 7),
			button8 = new JoystickButton(UtilStick, 8),
			button9 = new JoystickButton(UtilStick, 9),
			button10 = new JoystickButton(UtilStick, 10),
			button11 = new JoystickButton(UtilStick, 11),
			button12 = new JoystickButton(UtilStick, 12);
    
    public OI()
    {
	    // There are a few additional built in buttons you can use. Additionally,
	    // by subclassing Button you can create custom triggers and bind those to
	    // commands the same as any other Button.
	    
	    //// TRIGGERING COMMANDS WITH BUTTONS
	    // Once you have a button, it's trivial to bind it to a button in one of
	    // three ways:
	    
	    // Start the command when the button is pressed and let it run the command
	    // until it is finished as determined by it's isFinished method.
	    button3.whenPressed(new FeederOn());
	    
	    
	    // Run the command while the button is being held down and interrupt it once
	    // the button is released.
	    // button.whileHeld(new ExampleCommand());
	    
	    // Start the command when the button is released  and let it run the command
	    // until it is finished as determined by it's isFinished method.
	    // button.whenReleased(new ExampleCommand());
	}
}

