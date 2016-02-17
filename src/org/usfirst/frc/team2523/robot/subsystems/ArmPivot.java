
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.ArmPivotComm;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPivot extends Subsystem {
	
	// constants
	private final double PID_KP = 0.05;
	private final double PID_KI = 0.1; 
	public final double ARM_STARTING_ANGLE = 0; // degrees, positive for down off horizontal
	private final double POTENTIOMETER_ANGLE_PER_VOLTS = 13500;
	private final double POTENTIOMETER_MAX_ANGLE = 270;
	private final double POTENTIOMETER_START_DEGREE = 13297.5 + 30; // first term is to zero at zero point, second is arm angle off potentiometer start
	public final double MAX_IN_MATCH_ANGLE = 160;
	public final double ARM_PID_STOP_TOLERANCE = 2; // degrees, roughly leads to arm PID positioning precision
	public final double MAX_JOYSTICK_SPEED = 0.5;
	public final double JOYSTICK_DEADZONE = 0.02; // normalized units
	
	// variables
	public double currentSpeed;
	public double currentMaxAngle = MAX_IN_MATCH_ANGLE;
	public double pastPotentiometerAngle = 0;
	public double lastPotentiometerRateRead = 0;

	CANTalon arm1 = new CANTalon(RobotMap.lifter1);
	CANTalon arm2 = new CANTalon(RobotMap.lifter2);
	public AnalogPotentiometer armPotentiometer = new AnalogPotentiometer(RobotMap.armPoten1, POTENTIOMETER_ANGLE_PER_VOLTS);
	public PIDControl armPID = new PIDControl(PID_KP, PID_KI, 0); // No PD
	
	public ArmPivot()
	{
		setBrake(true);
	}
	
	public void setArmByJoystick()
	{
		// we may need to limit the commanded speed to remain within the winch speed limits
		double commandedSpeed = MAX_JOYSTICK_SPEED*Robot.oi.UtilStick.getY();
		double realSpeed = commandedSpeed;//Robot.winch.getLimitedArmSpeed(commandedSpeed);
		
		// apply deadzone
		if (Math.abs(realSpeed) < JOYSTICK_DEADZONE)
			realSpeed = 0;
		
		set(realSpeed); // JACK - consider using armPID and setTargetAngle for this so the arm stays up (also see whether the motors are braking...)
	}

	public void set(double speed)
	{
		if (speed > 0 && getArmAngle() > currentMaxAngle)
		{
			arm1.set(0);
			arm2.set(0);
			this.currentSpeed = 0;
		} else {
			arm1.set(speed);
			arm2.set(-speed);
			this.currentSpeed = speed;
		}
	}
	
	public void setTargetAngle(double angle)
	{
		set(armPID.getPIoutput(angle, getArmAngle()));
	}
	
	public double getArmAngle()
	{
		return POTENTIOMETER_MAX_ANGLE - (armPotentiometer.get() - POTENTIOMETER_START_DEGREE);
	}
	
	/**
	 * @return Rate with units in degrees per second
	 */
	public double getArmRate()
	{
		return ( pastPotentiometerAngle - getArmAngle() ) / 
			   ((System.nanoTime() - lastPotentiometerRateRead)*10e6);
	}
	
	public void updateArmProperties()
	{
		lastPotentiometerRateRead = System.nanoTime();
		pastPotentiometerAngle = getArmAngle();
		
		// set max angle based on match time
		if (RobotMap.MATCH_LENGTH - Timer.getMatchTime() > 20)
			currentMaxAngle = MAX_IN_MATCH_ANGLE;
		else
			currentMaxAngle = 10e6; // infinite
	}
	
	/**
	 * Sets the motor to brake or coast when no power
	 * @param brake Brakes if this is true, coasts if this is false
	 */
	public void setBrake(boolean brake)
	{
		arm1.enableBrakeMode(brake);
		arm2.enableBrakeMode(brake);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		setDefaultCommand(new ArmPivotComm());
	}
	/* public static void up(){

    	arm1.set(1);
    	arm2.set(-1);
	}
	public void stoparm(){
    	arm1.set(0);
    	arm2.set(0);
	}

	public void down(){
    	arm1.set(-1);
    	arm2.set(1);
	}
	 */

}

