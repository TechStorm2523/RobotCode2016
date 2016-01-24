package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Using input from GRIP on network tables, analyze targets and find the best one
 */
public class TargetFinder extends Subsystem {	
	
	
	/** TODO: WHICH ONE?
	 * Generates complete scores for all found targets
	 */
	private void ScoreAllTargets()
	// OR
	/**
	 * Generates complete scores for all found targets,
	 * then deduces the best.
	 * @return The best target's TargetReport
	 */
	private TargetReport getBestTarget()
	
	/**
	 * Retrieves the current TargetReports from GRIP via NetworkTables
	 * @return A list of TargetReports containing all fields in the TargetReports class
	 */
	private TargetReport[] getTargetReports()
	{
		// TODO: retrieve from network tables
	}

	
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

