package org.usfirst.frc.team2523.robot.subsystems;

/**
 * Basic class to contain information on a vision target
 * @author Robotics
 */
public class TargetReport {
	// target characteristics
	public double centerX;
	public double centerY;
	public double area;
	public double width;
	public double height;
	
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
	TargetReport(double centerX, double centerY, double area, double width, double height)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.area = area;
		this.width = width;
		this.height = height;
	}
	
	public void setAdditionalScores(double aspectRatioScore, double areaRatioScore)
	{
		this.aspectRatioScore = aspectRatioScore;
		this.areaRatioScore = areaRatioScore;
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
}
