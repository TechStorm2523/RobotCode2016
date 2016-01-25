package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Using input from GRIP on network tables, analyze targets and find the best one
 */
public class TargetFinder extends Subsystem {	
	// CONSTANTS
	private final String CONTOUR_TABLE = "GRIP/ContoursReport";
	
	// Objects Used
	NetworkTable netTable;
	
	public TargetFinder()
	{
		netTable = NetworkTable.getTable(CONTOUR_TABLE);
	}
	
	
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
	private TargetReport getBestTarget(TargetReport[] allTargets)
	{
		
	}
	
	/**
	 * Retrieves the current TargetReports from GRIP via NetworkTables
	 * @return A list of TargetReports containing all fields in the TargetReports class
	 */
	private TargetReport[] getTargetReports()
	{
		// initialize null default value to pass if no connection
		double[] defaultValue = new double[0];
		
		// get relevant values
		double[] centerXs = netTable.getNumberArray("centerX", defaultValue);
		double[] centerYs = netTable.getNumberArray("centerY", defaultValue);
		double[] areas = netTable.getNumberArray("area", defaultValue);
		double[] width = netTable.getNumberArray("width", defaultValue);
		double[] height = netTable.getNumberArray("height", defaultValue);
		
		// for each given, create a new object
		TargetReport
	}

	
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

