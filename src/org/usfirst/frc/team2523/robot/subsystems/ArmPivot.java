
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.OI;
import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.RobotMap;
import org.usfirst.frc.team2523.robot.commands.ArmPivotComm;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPivot extends Subsystem {
	// constants
	public final double ENCODER_DEGREES_PER_PULSE = 0; // TODO: SET!!!!!!!
	
	// variables
	public double currentSpeed;

	Talon arm1 = new Talon(RobotMap.lifter1);
	Talon arm2 = new Talon(RobotMap.lifter2);
	Encoder armEncoder = new Encoder(RobotMap.armEncoderPort1, RobotMap.armEncoderPort2, 
									false, Encoder.EncodingType.k4X);
	
	public ArmPivot()
	{
		// Initialize encoder scaling
		armEncoder.setDistancePerPulse(ENCODER_DEGREES_PER_PULSE);
	}
	
	public void setArmByJoystick()
	{
		double commandedSpeed = OI.UtilStick.getY();
		commandedSpeed = Robot.winch.limitArmSpeed(commandedSpeed);
		set(commandedSpeed);
	}

	public void set(double speed)
	{
		arm1.set(speed);
		arm2.set(-speed);
		this.currentSpeed = speed;
	}
	
	public double getArmAngle()
	{
		return armEncoder.getDistance();
	}
	
	public double getArmRate()
	{
		return armEncoder.getRate();
	}
	
	
	public void resetArmEncoder()
	{
		armEncoder.reset();
		Timer.delay(1); // wait for complete reset
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
    	arm2.set(2);
	}
	 */

}
