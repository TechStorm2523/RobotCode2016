package org.usfirst.frc.team2523.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Using input from GRIP on network tables, analyze targets and find the best one
 */
public class TargetFinder extends Subsystem {	
	// CONSTANTS
	private final String CONTOUR_TABLE = "GRIP/ContoursReport";
	// determined by geometry of target
	private final double IDEAL_ASPECT_RATIO = 20.0 / 14.0;
	private final double IDEAL_AREA_RATIO =  88.0 / 280.0;
	
	// Objects Used
	NetworkTable netTable;
	
	public TargetFinder()
	{
		netTable = NetworkTable.getTable(CONTOUR_TABLE);
	}
	
	/**
	 * Generates complete scores for all found targets,
	 * then deduces the best.
	 * @return The best target's TargetReport
	 */
	public TargetReport getBestTarget()
	{
		// get targets...
		TargetReport[] allTargets = getTargetReports();
		
		// score them...
		allTargets = scoreAllTargets(allTargets);
		
		// and find the best one
		TargetReport bestTarget = null;
		double bestScore = 0;
		for (TargetReport target : allTargets)
		{
			if (target.getCumulativeScore() > bestScore)
			{
				bestTarget = target;
				bestScore = target.getCumulativeScore();
			}
		}
		
		// if we've found one, return it
		// otherwise, return null
		return bestTarget;
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
		double[] widths = netTable.getNumberArray("width", defaultValue);
		double[] heights = netTable.getNumberArray("height", defaultValue);
		
		// for each given, create a new object
		TargetReport[] reports = new TargetReport[centerXs.length];
		for (int i = 0; i < reports.length; i++)
		{
			reports[i] = new TargetReport(centerXs[i],
										  centerYs[i],
										  areas[i],
										  widths[i],
										  heights[i]);
		}
		
		return reports;
	}
	
	/**
	 * Generates complete scores for all found targets
	 */
	private TargetReport[] scoreAllTargets(TargetReport[] allTargets)
	{
		for (TargetReport target : allTargets)
		{
			double aspectRatio = calculateAspectRatioScore(target);
			double areaRatio = calculateAreaScore(target);			
			
			target.setAdditionalScores(aspectRatio, areaRatio);
		}
		
		return allTargets;
	}
	
	/**
	 * Calculates a 0-1 area score of the given target,
	 * based off the difference between the ideal ratio 
	 * of the bounding box's area to the internal area AND the actual.
	 */
	private double calculateAreaScore(TargetReport target) {
		double boundingBoxArea = target.width * target.height;
		
		// prevent error when there is zero area
		if (boundingBoxArea != 0)
			return scoreFromDistance(target.area / boundingBoxArea, IDEAL_AREA_RATIO);
		else
			return 0;
	}
	
	/**
	 * Calculates a 0-1 aspect ratio score of the given target,
	 * based off the difference between it's ratio and the ideal one.	
	 */
	private double calculateAspectRatioScore(TargetReport target) {
		// ensure no error
		if (target.height != 0)
			//
			return scoreFromDistance(target.width / target.height, IDEAL_ASPECT_RATIO);
		else
			return 0;
	}
	
	/**
	 * Calculates a score representing the distance of a value off the ideal value.
	 * Uses a peicewise "pyramid" function which is highest (1) at one but falls off to 0
	 * as the ratio approaches 0 or 2.
	 * @param realValue The current, actual value
	 * @param idealValue The optimal value (would result in highest score)
	 * @return A 0-1 value representing the "closeness" of the realValue to the idealValue
	 */
	private double scoreFromDistance(double realValue, double idealValue)
	{
		// Create a "pyramid function", inverting an absoulute value function and 
		// shifting so a 0 in the difference between one and the ratio of the values
		// results in a 1 on the score
		if (idealValue != 0)
			return Math.max(0, Math.min(1 - Math.abs(1 - realValue/idealValue), 1));
		else
			return 0;
	}	
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

