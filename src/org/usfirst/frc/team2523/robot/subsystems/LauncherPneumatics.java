
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Controls the phneumatics which raise/lower the launcher
 */
public class LauncherPneumatics extends Subsystem {
	DoubleSolenoid launcherPneumatics = new DoubleSolenoid(RobotMap.launcherSolenoid1, RobotMap.launcherSolenoid2);

	public void rase() {
		launcherPneumatics.set(DoubleSolenoid.Value.kForward);
	}
	public void lower() {
		launcherPneumatics.set(DoubleSolenoid.Value.kReverse);
	}
	public void off() {
		launcherPneumatics.set(DoubleSolenoid.Value.kOff);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}



