package org.usfirst.frc.team2523.robot.commands;

import org.usfirst.frc.team2523.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCommandBasic extends CommandGroup {

	public AutoCommandBasic() {
		// just drive straight for a bit to reach a defense
		addSequential(new DriveForTime(0.5, 0, 2));
		addSequential(new DriveForDistance(0.5,
				DriveTrain.DISTANCE_TO_DEFENSE_MIDDLE));
	}
}
