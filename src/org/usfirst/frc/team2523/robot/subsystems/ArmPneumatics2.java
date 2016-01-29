
package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmPneumatics2 extends Subsystem {
	DoubleSolenoid armpneumatics2 = new DoubleSolenoid(3, 4);
	
	

	

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}



