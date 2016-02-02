
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.ArmPivotComm;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
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
	private final double POTENTIOMETER_DEGREE_LIMIT = 270;
	private final double POTENTIOMETER_START_DEGREE = 30;
	public final double MAX_IN_MATCH_ANGLE = 80;
	public final double ARM_PID_STOP_TOLERANCE = 3; // roughly leads to arm PID positioning precision
	
	// variables
	public double currentSpeed;
	public double currentMaxAngle = MAX_IN_MATCH_ANGLE;
	public double pastPotentiometerAngle = 0;
	public double lastPotentiometerRateRead = 0;

	Talon arm1 = new Talon(RobotMap.lifter1);
	Talon arm2 = new Talon(RobotMap.lifter2);
	AnalogPotentiometer armPotentiometer = new AnalogPotentiometer(RobotMap.armPoten1, 
																   POTENTIOMETER_DEGREE_LIMIT,
																   POTENTIOMETER_START_DEGREE);
	public PIDControl armPID = new PIDControl(PID_KP, PID_KI, 0); // No PD
	
	public void setArmByJoystick()
	{
		double commandedSpeed = OI.UtilStick.getY();
		commandedSpeed = Robot.winch.getLimitedArmSpeed(commandedSpeed);
		set(commandedSpeed);
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
		return armPotentiometer.get();
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
			currentMaxAngle = POTENTIOMETER_DEGREE_LIMIT;
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

