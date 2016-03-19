package org.usfirst.frc.team2523.robot.subsystems;

/**
 * Basic class to contain information on a vision target
 * @author Robotics
 */
public class TargetReport {
	// constants
	private final double IDEAL_ASPECT_RATIO;
	private final double IDEAL_AREA_RATIO;
	
	// target characteristics
	public double centerX;
	public double centerY;
	public double area;
	public double width;
	public double height;
	public double solidity;
	
	// possible additional target scoring values
	public double aspectRatioScore;
	public double areaRatioScore;
	
	/**
	 * Basic Constructor
	 * @param centerX
	 * @param centerY
	 * @param area
	 * @param width
	 * @param height
	 */
	TargetReport(double centerX, double centerY, double area, double width, double height,
				double solidity, double idealAspectRatio, double idealAreaRatio)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.area = area;
		this.width = width;
		this.height = height;
		this.solidity = solidity;
		this.IDEAL_ASPECT_RATIO = idealAspectRatio;
		this.IDEAL_AREA_RATIO = idealAreaRatio;
		
		// run further calculations upon initialization
		this.aspectRatioScore = calculateAspectRatioScore();
		this.areaRatioScore = calculateAreaRatioScore();
	}
	
	/**
	 * Generates cumulative score for the current target.
	 * Must have setAdditionalScores run beforehand
	 */
	public double getCumulativeScore()
	{
		// if both scores are PERFECT, this will give a one
		return (this.aspectRatioScore + this.areaRatioScore) / 2.0;
	}
	
	/**
	 * Calculates a 0-1 area score of the given target,
	 * based off the difference between the ideal ratio 
	 * of the bounding box's area to the internal area AND the actual.
	 */
	private double calculateAreaRatioScore() {
		double boundingBoxArea = this.width * this.height;
		
		// prevent error when there is zero area
		if (boundingBoxArea != 0)
			return scoreFromDistance(this.area / boundingBoxArea, IDEAL_AREA_RATIO);
		else
			return 0;
	}
	
	/**
	 * Calculates a 0-1 aspect ratio score of the given target,
	 * based off the difference between it's ratio and the ideal one.	
	 */
	private double calculateAspectRatioScore() {
		// ensure no error
		if (this.height != 0)
			//
			return scoreFromDistance(this.width / this.height, IDEAL_ASPECT_RATIO);
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
		// Create a "pyramid function", inverting an absolute value function and 
		// shifting so a 0 in the difference between one and the ratio of the values
		// results in a 1 on the score
		if (idealValue != 0)
			return Math.max(0, Math.min(1 - Math.abs(1 - realValue/idealValue), 1));
		else
			return 0;
	}	
}
