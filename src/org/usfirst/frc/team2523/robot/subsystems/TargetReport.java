package org.usfirst.frc.team2523.robot.subsystems;

/**
 * Basic class to contain information on a vision target
 * @author Robotics
 */
public class TargetReport {
	// target characteristics
	public int centerX;
	public int centerY;
	public int area;
	public int width;
	public int height;
	
	// possible additional target scoring values
	public int aspectRatioScore;
	
	/**
	 * Basic Constructor
	 * @param centerX
	 * @param centerY
	 * @param area
	 * @param width
	 * @param height
	 */
	TargetReport(int centerX, int centerY, int area, int width, int height)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.area = area;
		this.width = width;
		this.height = height;
	}
}
