package org.usfirst.frc.team2523.robot.subsystems;

/**
 * Basic class to contain information on a vision target
 * @author Robotics
 */
public class TargetReport {
	// constants
	private final double IDEAL_ASPECT_RATIO;
	private final double IDEAL_AREA_RATIO;
	// weights only matter relative to eachother 
	// (so if one is 1 and other is 2, that's twice as important)
	private static final double ASPECT_RATIO_WEIGHT = 2;
	private static final double AREA_RATIO_WEIGHT = 1; 
	private static final double ADDITIONAL_SCORE_WEIGHT = 100;
	
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
	public double additionalScore;
	
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
		this.additionalScore = 0;
	}
	
	/**
	 * Generates cumulative score for the current target.
	 */
	public double getCumulativeScore()
	{
		// if both scores are PERFECT, this will give a one
		// (remove the additional score from this equation if it doesn't exist)
		return (ASPECT_RATIO_WEIGHT*this.aspectRatioScore + AREA_RATIO_WEIGHT*this.areaRatioScore) / (ASPECT_RATIO_WEIGHT + AREA_RATIO_WEIGHT);// + ADDITIONAL_SCORE_WEIGHT*this.additionalScore) / 
//				(ASPECT_RATIO_WEIGHT + AREA_RATIO_WEIGHT + (this.additionalScore != 0 ? ADDITIONAL_SCORE_WEIGHT : 0) );
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
	 * Adds an additonal score to this TargetReport, based on the 
	 * distance between the given ideal value and the real value of this particular target.
	 * This will be factored into a call to getCumulativeScore().
	 * @param realValue The current, actual value
	 * @param idealValue The optimal value (would result in highest score)
	 */
	public void addScoreFromDistance(double realValue, double idealValue)
	{
		this.additionalScore = scoreFromDistance(realValue, idealValue);
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
