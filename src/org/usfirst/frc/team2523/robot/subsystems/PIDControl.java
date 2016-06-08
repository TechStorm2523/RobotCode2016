
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Jack,
 * !!!!!BADLY DESIGNED - make a better one!!!! (Could be done in 1/3 of the lines of code... so much repetition!)
 * 
 * PIDControl class for creating PID control systems. 
 * Class stores past error and cumulative integral error, and therefore a new instance should be used for each system. 
 * @author Robotics
 */
@SuppressWarnings("unused")
public class PIDControl extends Subsystem {
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	// create gain variables for PID
	private double KP;
	private double KI;
	private double KD;
	
	// create max and min output constants, as well as integral limit
	private double MIN_OUTPUT;
	private double MAX_OUTPUT;
	private double INTEGRAL_LIMIT;
	
	// create integral error cumulative counter
	private double IntegralError;
	
	// create previous error used for delta_error
	private double PreviousError;
	
	/**
	 * Based off document at http://www.chiefdelphi.com/media/papers/download/1829
	 * @param KP Gain for proportional control parameter. Float between 0.0 and 1.0
	 * @param KI Gain for integral control parameter. Float between 0.0 and 1.0
	 * @param KD Gain for derivative control parameter. Float between 0.0 and 1.0
	 * @param MIN_OUTPUT Minimum output for PID functions.
	 * @param MAX_OUTPUT Maximum output for PID functions.
	 * @param INTEGRAL_LIMIT Maximum integral parameter value for PID functions.
	 */
	public PIDControl(double KP, double KI, double KD, double MIN_OUTPUT, double MAX_OUTPUT, double INTEGRAL_LIMIT) 
	{
		// globalize inputs
		this.KP = KP;
		this.KI = KI;
		this.KD = KD;
		this.MIN_OUTPUT = MIN_OUTPUT;
		this.MAX_OUTPUT = MAX_OUTPUT;
		this.INTEGRAL_LIMIT = INTEGRAL_LIMIT;
	}
	
	/**
	 * Based off document at http://www.chiefdelphi.com/media/papers/download/1829
	 * @param KP Gain for proportional control parameter. Float between 0.0 and 1.0
	 * @param KI Gain for integral control parameter. Float between 0.0 and 1.0
	 * @param KD Gain for derivative control parameter. Float between 0.0 and 1.0
	 * @param MIN_OUTPUT Minimum output for PID functions.
	 * @param MAX_OUTPUT Maximum output for PID functions.
	 */
	public PIDControl(double KP, double KI, double KD, double MIN_OUTPUT, double MAX_OUTPUT)
	{
		this(KP, KI, KD, MIN_OUTPUT, MAX_OUTPUT, 10);
	}
	
	/**
	 * Based off document at http://www.chiefdelphi.com/media/papers/download/1829
	 * @param KP Gain for proportional control parameter. Float between 0.0 and 1.0
	 * @param KI Gain for integral control parameter. Float between 0.0 and 1.0
	 * @param KD Gain for derivative control parameter. Float between 0.0 and 1.0
	 */
	public PIDControl(double KP, double KI, double KD)
	{
		this(KP, KI, KD, -1, 1, 10);
	}
	
	/**
	 * Function to calculate motor output based on PID Control (Proportional, Integral, and Derivative)
	 * @param target Target value of actuator, gyro, etc.
	 * @param current Current value of actuator, gyro, etc.
	 * @return Returns the output based on PID Control
	 */
	public double getPIDoutput(double target, double current) 
	{
		// initialize variables
		double error;
		double deltaError;
		double p;
		double i;
		double d;
		double output;
		
		// calculate errors based off difference of target and current values and previous error
		error = current - target;
		IntegralError += error;
		deltaError = error - PreviousError;
		
		// define proportional parameter
		p = error * this.KP;
		
		// define integral parameter
		i = IntegralError * this.KI;
				
		// define derivative parameter
		d = deltaError * this.KD;
		
		// limit integral parameter
		i = limitIntegral(i);
		
		// find output
		output = p + i + d;
		
		// limit output
		output = limitOutput(output);
		
		// update previous error
		PreviousError = error;
		
		return output;
	}
	
	/**
	 * Function to calculate motor output based on PI Control (Proportional and Integral)
	 * @param target Target value of actuator, gyro, etc.
	 * @param current Current value of actuator, gyro, etc.
	 * @return Returns the output based on PI Control
	 */
	public double getPIoutput(double target, double current) 
	{
		// initialize variables
		double error;
		double p;
		double i;
		double output;
		
		// calculate errors based off difference of target and current values and previous error
		error = current - target;
		IntegralError += error;
		
		// define proportional parameter
		p = error * this.KP;
		
		// define integral parameter
		i = IntegralError * this.KI;
		
		// limit integral parameter
		i = limitIntegral(i);
		
		// find output
		output = p + i;
		
		// limit output
		output = limitOutput(output);
		
		return output;
	}
	
	/**
	 * Function to calculate motor output based on PD Control (Proportional and Derivative)
	 * @param target Target value of actuator, gyro, etc.
	 * @param current Current value of actuator, gyro, etc.
	 * @return Returns the output based on PD Control
	 */
	public double getPDoutput(double target, double current) 
	{
		// initialize variables
		double error;
		double deltaError;
		double p;
		double d;
		double output;
		
		// calculate errors based off difference of target and current values and previous error
		error = current - target;
		deltaError = error - PreviousError;
		
		// define proportional parameter
		p = error * this.KP;
				
		// define derivative parameter
		d = deltaError * this.KD;
		
		// find output
		output = p + d;
		
		// limit output
		output = limitOutput(output);
		
		// update previous error
		PreviousError = error;
		
		return output;
	}
	
	/**
	 * Function to calculate motor output based on P Control (Proportional)
	 * @param target Target value of actuator, gyro, etc.
	 * @param current Current value of actuator, gyro, etc.
	 * @return Returns the output based on P Control
	 */
	public double getPoutput(double target, double current) 
	{
		// initialize variables
		double error;
		double output;
		
		// calculate errors based off difference of target and current values
		error = current - target;
			
		// define output in terms of proportional parameter
    	output = error * this.KP;
		
		// limit output
		output = limitOutput(output);
		
		return output;
	}
	
	/**
	 * Limits the given output to within max and min output
	 * @param rawOutput Output that may not be limited
	 * @return Returns new output
	 */
	private double limitOutput(double rawOutput)
	{
		// return the max or min if it is greater or less than either
		if (rawOutput > this.MAX_OUTPUT) return this.MAX_OUTPUT;
		else if (rawOutput < this.MIN_OUTPUT) return this.MIN_OUTPUT;
		else return rawOutput;
	}
	
	/**
	 * Limits the given integral to within defined max and min output
	 * @param rawIntegral Integral value that may not be limited
	 * @return Returns new integral value
	 */
	private double limitIntegral(double rawIntegral)
	{
		if (rawIntegral > this.INTEGRAL_LIMIT) return this.INTEGRAL_LIMIT;
		else if (rawIntegral < this.INTEGRAL_LIMIT * -1) return this.INTEGRAL_LIMIT * -1;
		else return rawIntegral;
	}
	
	/**
	 * Sets the maximum and minimum speeds to output from the PID control
	 * @param min Minimum value
	 * @param max Maximum value
	 */
	public void setMinMax(double min, double max)
	{
		// set values
		this.MIN_OUTPUT = min;
		this.MAX_OUTPUT = max;
	}
	
	/**
	 * Sets the maximum and minimum speeds to output from the PID control, by one value
	 * @param minMax Absolute value of min/max value
	 */
	public void setMinMax(double minMax)
	{
		// set values
		this.MIN_OUTPUT = minMax;
		this.MAX_OUTPUT = minMax;
	}
	
	/**
	 * Sets integral value to zero to allow uninhibited movement
	 */
	public void resetIntegral()
	{
		this.IntegralError = 0;
	}
	
	public void initDefaultCommand() {
	    // Set the default command for a subsystem here.
	}
}

