package org.usfirst.frc.team2523.robot.subsystems;

import java.io.IOException;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.commands.IdentifyBestTarget;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Using input from GRIP on network tables, analyzes targets and finds the best one.
 * Also defines methods which can be used to display target information on
 * SmartDashboard and provide functionality to subsystems using the camera.
 */
public class TargetTracker extends Subsystem {	
	// CONSTANTS
	private static final String CONTOUR_NET_TABLE = "GRIP/ContoursReport";
	private static final String RASPBERRY_PI_LOCATION = "visionpi2523.local";
	public static final double TARGET_ACQUIRE_TIME = 1;
	// target geometry
	private static final double IDEAL_ASPECT_RATIO = 20.0 / 14.0;
	private static final double IDEAL_AREA_RATIO =  88.0 / 280.0;
	private static final double TARGET_WIDTH = 20 / 12.0; // feet
	private static final double TARGET_HEIGHT = 14 / 12.0; // feet
	// elimination criteria
	private static final double MIN_REASONABLE_RANGE = 0; // SET BASED ON WHEN TARGET OUT OF CAMERA VIEW
	private static final double MAX_REASONABLE_RANGE = 15.5;
	private static final double CENTER_ZONE_SIZE = 50; // pixels, distance off center where target considering in middle
	
	// SINCE CAN'T USE CAMERA CLASS BCS CANT GET NETWORK IMAGE
	private static final double IMAGE_WIDTH = 1280;
	private static final double IMAGE_HEIGHT = 760;
	private static final double FPS = 25;
	private final static double CAMERA_FOV = 39.935; // VERTICAL (By measuring distance from a known size object that spans vertical FOV and using tan OR solving the equation in getRangeToBestTarget for FOV using other measurements from debug)
	private final static double CAMERA_ELEVATION = 45;

	// Objects Used
	NetworkTable netTable;
	
	// Variables
	public TargetReport currentBestTarget = null;
	private TargetReport[] allTargets = null;
	public double currentRangeToBestTarget = 0;
	private boolean targetCloseToCenter = false;

	// POTENTIAL BUG ISSUE
	public TargetTracker()
	{
		netTable = NetworkTable.getTable(CONTOUR_NET_TABLE);
	}
	
	/**
	 * Finds the distance of the current target from the center of the camera view
	 * @return A two part array [x, y] representing the x and y distances 
	 * normalized to be [+/-1, +/-1] at edges of image
	 * (+x is to the right, +y is up)
	 */
	public double[] getTargetDistanceFromCenter()
	{
		// watch for no valid target, in which case give no displacement
		double[] distance = new double[2];
		if (currentBestTarget != null)
		{
			// x increases to right, but y increased downwards, so invert y
			distance[0] =  (currentBestTarget.centerX - IMAGE_WIDTH/2.0 ) / (IMAGE_WIDTH/2);
			distance[1] = -(currentBestTarget.centerY - IMAGE_HEIGHT/2.0) / (IMAGE_HEIGHT/2);
		}
		return distance;
	}
	
	/**
	 * Finds the distance to the current best target.
	 * @return The distance, or 0 if no target is found. Most accurate if head on to target
	 */
	public double getRangeToBestTarget()
	{
		getRangeToTarget(currentBestTarget);
	}
	
	/**
	 * Finds the horizontal distance to the target in feet (or whatever measure TARGET_HEIGHT is in)
	 * @return The distance, or 0 if no target is found. Most accurate if head on to target
	 * Based on https://wpilib.screenstepslive.com/s/3120/m/8731/l/90361-identifying-and-processing-the-targets
	 * and example code in 2015 Vision Retro Sample (they do it slightly differently with variables, but its the same)
	 */
	private double getRangeToTarget(TargetReport target)
	{
		if (target != null)
		{
			// chose to use height because most consistent across view angles
			// d = TargetHeightFeet*FOVHeightPixel / (2*TargetHeightPixel*tan(FOV/2) ) (HYPOTENUSE)
			return TARGET_HEIGHT*IMAGE_HEIGHT / (2*target.height*Math.tan(Math.toRadians(CAMERA_FOV/2)))
					*Math.cos(Math.toRadians(CAMERA_ELEVATION)); // convert to horizontal
		}
		else
			return 0;
	}
	
	/**
	 * Generates complete scores for all found targets,
	 * then deduces the best. Sets this class's best target
	 * reference to the one found
	 * @return The best target's TargetReport
	 */
	public TargetReport retrieveBestTarget()
	{
		// get targets...
		allTargets = getTargetReports();
		
		// and find the best one
		TargetReport bestTarget = null;
		double bestScore = 0;
		for (TargetReport target : allTargets)
		{
			// Eliminate targets based on range
			double targetRange = getRangeToTarget(target);
			if (targetRange > MAX_REASONABLE_RANGE || targetRange < MIN_REASONABLE_RANGE)
				continue;
			
			// When DRIVERS are lining up, eliminate based on the assumption that the target is aligned.
			if (targetCloseToCenter && Math.abs(target.centerX - IMAGE_WIDTH / 2) < CENTER_ZONE_SIZE)
				continue;
			
			if (target.getCumulativeScore() > bestScore)
			{
				bestTarget = target;
				bestScore = target.getCumulativeScore();
			}	
		}
		
		// determine if in launcher range to target
		currentRangeToBestTarget = getRangeToBestTarget();
		if (Robot.launcherWheels.inRange(currentRangeToBestTarget))
			Robot.launcherstatus.setInRange();
		else
			Robot.launcherstatus.setOutOfRange();
		
		// if we've found one, cache and return it
		// otherwise, return null (it will just default to the first value of bestTarget)
		currentBestTarget = bestTarget;
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
		double[] solidities = netTable.getNumberArray("height", defaultValue);
		
		// for each given, create a new object
		TargetReport[] reports = new TargetReport[centerXs.length];
		for (int i = 0; i < reports.length; i++)
		{
			// it will auto-initialize further scores
			reports[i] = new TargetReport(centerXs[i],
										  centerYs[i],
										  areas[i], 
										  widths[i],
										  heights[i],
										  solidities[i],
										  IDEAL_ASPECT_RATIO,
										  IDEAL_AREA_RATIO);
		}
		
		return reports;
	}

	/**
	 * Starts vision tracking on Pi via SSH network command
	 */
	public void startTracking()
	{	
		ProcessBuilder starterProcess = new ProcessBuilder("ssh", 
										"pi@" + RASPBERRY_PI_LOCATION,
										" -c \"/home/pi/code/start_vision.sh " + 
										FPS + " " + 
										IMAGE_WIDTH + "x" + IMAGE_HEIGHT + "\"");
		
		try 
		{
			starterProcess.start();
		} 
		catch (IOException e) 
		{
			System.out.println("Could not start process to commence Raspberry Pi GRIP program: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops vision tracking on Pi via SSH network command
	 */
	public void stopTracking()
	{
		ProcessBuilder enderProcess = new ProcessBuilder("ssh", 
									  "pi@" + RASPBERRY_PI_LOCATION,
									  " -c \"/home/pi/code/stop_vision.sh\"");
		
		try 
		{
			enderProcess.start();
		} 
		catch (IOException e) 
		{
			System.out.println("Could not start process to termintate Raspberry Pi GRIP program: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param targetCloseToCenter If true, indicates that the vision tracking can 
	 * eliminate targets out to the sides
	 */
	public void setTargetCloseToCenter(boolean targetCloseToCenter) 
	{
		this.targetCloseToCenter = targetCloseToCenter;
	}	
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new IdentifyBestTarget());
    }
}

