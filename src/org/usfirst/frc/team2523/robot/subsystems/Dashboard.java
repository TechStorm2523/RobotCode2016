
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.commands.SetDashboard;


import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Dashboard extends Subsystem {
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new SetDashboard());
    }
}

