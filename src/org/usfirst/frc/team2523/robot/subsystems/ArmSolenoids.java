
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Controls the pneumatics which contract/extend the arm
 */
public class ArmSolenoids extends Subsystem {
	DoubleSolenoid ArmSolenoids = new DoubleSolenoid(RobotMap.armsolenoid1, RobotMap.armsolenoid2);

	public void contract() {
		ArmSolenoids.set(DoubleSolenoid.Value.kForward);
	}
	public void extend() {
		ArmSolenoids.set(DoubleSolenoid.Value.kReverse);
	}
	
	public boolean getState()
	{
		return ArmSolenoids.get() != DoubleSolenoid.Value.kReverse;
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}



