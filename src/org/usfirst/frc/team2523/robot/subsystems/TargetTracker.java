package org.usfirst.frc.team2523.robot.subsystems;

import java.io.IOException;
import java.util.ArrayList;

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
	public static final String CONTOUR_NET_TABLE = "GRIP/ContoursReport";
	public static final String OUTPUT_NET_TABLE = "GRIP/BestTargetReport";
	private static final String RASPBERRY_PI_LOCATION = "visionpi2523.local";
	public static final double TARGET_ACQUIRE_TIME = 0.5;
	// target geometry (this can probably be all you change to make this work next year, if the targets are rectangluar)
	private static final double IDEAL_ASPECT_RATIO = 20.0 / 14.0;
	private static final double IDEAL_AREA_RATIO =  88.0 / 280.0;
	private static final double TARGET_WIDTH = 20 / 12.0; // feet
	private static final double TARGET_HEIGHT = 14 / 12.0; // feet
	// elimination criteria
	private static final double MIN_REASONABLE_RANGE = 0; // SET BASED ON WHEN TARGET OUT OF CAMERA VIEW
	private static final double MAX_REASONABLE_RANGE = 15.5;
	private static final double CENTER_ZONE_SIZE = 100; // pixels, distance off center where target considering in middle
	private static final double GUESS_ACCURACY_TOLERANCE = 1; // feet
	
	// SINCE CAN'T USE CAMERA CLASS BCS CANT GET NETWORK IMAGE
	private static final double IMAGE_WIDTH = 640;
	private static final double IMAGE_HEIGHT = 480;
	private static final double FPS = 15;
	private final static double CAMERA_FOV = 39.935; // VERTICAL (By measuring distance from a known size object that spans vertical FOV and using tan OR solving the equation in getRangeToBestTarget for FOV using other measurements from debug)
	private final static double CAMERA_ELEVATION = 45;


	// Objects Used (Having these defined here instead of in Robot.java was an issue, but it might be
	// because they weren't static...???)
//	static? NetworkTable recievingTable;
//	static? NetworkTable sendingTable;
	
	// Variables
	public TargetReport currentBestTarget = null;
	private ArrayList<TargetReport> allTargets = null;
	public double currentRangeToBestTarget = 0;
	private boolean targetCloseToCenter = true;
	public boolean tracking = false;
	public double guessedRange = 0;

	public TargetTracker()
	{
//		recievingTable = NetworkTable.getTable(CONTOUR_NET_TABLE);
//		sendingTable = NetworkTable.getTable(OUTPUT_NET_TABLE);
		allTargets = new ArrayList<TargetReport>();
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
		return getRangeToTarget(currentBestTarget);
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
		getTargetReports(allTargets);
//		System.out.println("All Targets: " + allTargets);
		
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
			if (targetCloseToCenter && Math.abs(target.centerX - IMAGE_WIDTH / 2) > CENTER_ZONE_SIZE)
				continue;
			
			// add score for distance from throttle-based guess
			guessedRange = MAX_REASONABLE_RANGE*0.5*(-Robot.oi.UtilStick.getThrottle() + 1);
			if (Math.abs(guessedRange) > 0.01)
			{
				// give a bunch of weight (see TargetReport) based on closesness to guessedRange
				target.addScoreFromDistance(guessedRange, targetRange);
				
				// highly prioritize a target close to a guess (this basically overrides the above line...)
				if (Math.abs(guessedRange - targetRange) < GUESS_ACCURACY_TOLERANCE)
				{
					bestTarget = target;
					break;
				}
			}
			
			// if a target is better than the best, it's now the best
			if (target.getCumulativeScore() > bestScore)
			{
				bestTarget = target;
				bestScore = target.getCumulativeScore();
			}
		}
		allTargets.clear(); // we thought this might have been taking up too much memory, so we cleared it.
		
		// determine if in launcher range to target and set notification based on this
		currentRangeToBestTarget = getRangeToBestTarget();
		if (Robot.launcherWheels.inRange(currentRangeToBestTarget))
			Robot.launcherstatus.setInRange();
		else
			Robot.launcherstatus.setOutOfRange();

		// if we've found one, cache and return it
		// otherwise, return null (it will just default to the first value of bestTarget)
      // ALSO, report bestTarget to Driver station
		currentBestTarget = bestTarget;
//		sendBestTargetReport();
//    	System.out.println(currentBestTarget);
		return bestTarget;
	}
	
	/**
	 * Retrieves the current TargetReports from GRIP via NetworkTables
	 * @return A list of TargetReports containing all fields in the TargetReports class
	 */
	private void getTargetReports(ArrayList<TargetReport> reports)
	{
		// initialize null default value to pass if no connection
		double[] defaultValue = new double[0];
		
		// get relevant values
		double[] centerXs = Robot.targetRecievingTable.getNumberArray("centerX", defaultValue);
		double[] centerYs = Robot.targetRecievingTable.getNumberArray("centerY", defaultValue);
		double[] areas = Robot.targetRecievingTable.getNumberArray("area", defaultValue);
		double[] widths = Robot.targetRecievingTable.getNumberArray("width", defaultValue);
		double[] heights = Robot.targetRecievingTable.getNumberArray("height", defaultValue);
		double[] solidities = Robot.targetRecievingTable.getNumberArray("solidity", defaultValue);
		
		// for each given, create a new object
		//ArrayList<TargetReport> reports = new ArrayList<TargetReport>();
		int i = 0;
		while (	i < centerXs.length && 
				i < centerYs.length && 
				i < areas.length && 
				i < widths.length && 
				i < heights.length && 
				i < solidities.length)
		{
			// it will auto-initialize further scores
			reports.add(new TargetReport(centerXs[i],
									  centerYs[i],
									  areas[i], 
									  widths[i],
									  heights[i],
									  solidities[i],
									  IDEAL_ASPECT_RATIO,
									  IDEAL_AREA_RATIO));
			i++;
		}
	}

    /**
     * Sends the currentBestTarget via NetworkTables in a format emulating
     * GRIP's reports, so can be used with the SmartDashboard extension.
     */
    private void sendBestTargetReport()
    {
        // define arrays (just of the bestTarget and a vertical bar so length 2)
        double[] centerXs = new double[2];
        double[] centerYs = new double[2];
        //double[] areas = new double[2];
        double[] widths =  new double[2];
        double[] heights = new double[2];
        
        // send currentBestTarget if it exists
        if (currentBestTarget != null)
        {
	        // set props of bestTarget
	        centerXs[0] = currentBestTarget.centerX;
	        centerYs[0] = currentBestTarget.centerY;
	        //areas[0]
	        widths[0] = currentBestTarget.width;
	        heights[0] = currentBestTarget.height;
        }
        
        // create artificial target to display center bar
        centerXs[1] = IMAGE_WIDTH/2;
        centerYs[1] = IMAGE_HEIGHT/2;
        widths[1] = 10;
        heights[1] = IMAGE_HEIGHT;
        
        // send info
        Robot.targetSendingTable.putNumberArray("centerX", centerXs);
        Robot.targetSendingTable.putNumberArray("centerY", centerYs);
        //Robot.targetSendingTable.putNumberArray("area", areas);
        Robot.targetSendingTable.putNumberArray("width", widths);
        Robot.targetSendingTable.putNumberArray("height", heights);
    }

    /*
     * THE FOLLOWING DON"T WORK... I THINK
     * IT WAS REALLY HARD TO TRY TO DEBUG IT, so I just ran the commands 
     * when the rasberry pi started up
     * 
     */
    
    
	/**
	 * Starts vision tracking on Pi via SSH network command
	 */
	public void startTracking()
	{	
		ProcessBuilder starterProcess = new ProcessBuilder("ssh", 
										"pi@" + RASPBERRY_PI_LOCATION,
										"\"sudo /home/pi/code/stop_vision.sh && /home/pi/code/start_vision.sh " + 
										FPS + " " + 
										IMAGE_WIDTH + "x" + IMAGE_HEIGHT + "\"");
		
		try 
		{
			starterProcess.start();
			tracking = true;
			System.out.println("Started vision tracking on Pi");// + startedProcess + " (Error:" + startedProcess.getErrorStream() + ")");
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
									  "\"/home/pi/code/stop_vision.sh\"");
		
		try 
		{
			enderProcess.start();
			tracking = false;
			System.out.println("Stopped vision tracking on Pi");
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

