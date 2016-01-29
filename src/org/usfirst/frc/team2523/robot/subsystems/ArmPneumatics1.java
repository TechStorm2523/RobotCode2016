
package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPneumatics1 extends Subsystem {
	DoubleSolenoid armpneumatics1 = new DoubleSolenoid(1, 2);
	
	

	

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}



