
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
	public final double POTENTIOMETER_DEGREE_LIMIT = 270;
	public final double POTENTIOMETER_START_DEGREE = 30;
	private static final double MAX_ANGLE = 80;
	// variables
	public double currentSpeed;
	public double pastPotentiometerAngle = 0;
	public double lastPotentiometerRateRead = 0;

	Talon arm1 = new Talon(RobotMap.lifter1);
	Talon arm2 = new Talon(RobotMap.lifter2);
	AnalogPotentiometer armPotentiometer = new AnalogPotentiometer(RobotMap.armPoten1, 
																   POTENTIOMETER_DEGREE_LIMIT,
																   POTENTIOMETER_START_DEGREE);
	
	public void setArmByJoystick()
	{
		double commandedSpeed = OI.UtilStick.getY();
		commandedSpeed = Robot.winch.getLimitedArmSpeed(commandedSpeed);
		set(commandedSpeed);
	}

	public void set(double speed)
	{
		if (speed > 0 && getArmAngle() > MAX_ANGLE && 
				(RobotMap.MATCH_LENGTH - Timer.getMatchTime() > 20))
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
	
	public void updateArmRate()
	{
		lastPotentiometerRateRead = System.nanoTime();
		pastPotentiometerAngle = getArmAngle();
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

