//package org.usfirst.frc.team2523.robot.subsystems;
//
//import java.io.IOException;
//
//import org.usfirst.frc.team2523.robot.Robot;
//import org.usfirst.frc.team2523.robot.commands.IdentifyBestTarget;
//
//import edu.wpi.first.wpilibj.command.Subsystem;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
//
///**
// * Using input from GRIP on network tables, analyzes targets and finds the best one.
// * Also defines methods which can be used to display target information on
// * SmartDashboard and provide functionality to subsystems using the camera.
// */
//public class TargetTracker extends Subsystem {	
//	// CONSTANTS
//	private static final String CONTOUR_NET_TABLE = "GRIP/ContoursReport";
//	private static final String RASPBERRY_PI_LOCATION = "raspberrypi.local";
//	// target geometry
//	private static final double IDEAL_ASPECT_RATIO = 20.0 / 14.0;
//	private static final double IDEAL_AREA_RATIO =  88.0 / 280.0;
//	private static final double TARGET_WIDTH = 20 / 12.0;
//	private static final double TARGET_HEIGHT = 14 / 12.0;
//
//	// Objects Used
//	NetworkTable netTable;
//	
//	// Variables
//	public TargetReport currentBestTarget = null;
//	public TargetReport[] allTargets = null;
//	public double currentRangeToBestTarget = 0;
//	
//	public TargetTracker()
//	{
//		netTable = NetworkTable.getTable(CONTOUR_NET_TABLE);
//	}
//	
//	/**
//	 * Finds the distance of the current target from the center of the camera view
//	 * @return A two part array [x, y] representing the x and y distances 
//	 * normalized to be [+/-1, +/-1] at edges of image
//	 * (+x is to the right, +y is up)
//	 */
//	public double[] getTargetDistanceFromCenter()
//	{
//		// watch for no valid target, in which case give no displacement
//		double[] distance = new double[2];
//		if (currentBestTarget != null)
//		{
//			// x increases to right, but y increased downwards, so invert y
//			distance[0] =  (currentBestTarget.centerX - Camera.IMAGE_WIDTH/2.0 ) / (Camera.IMAGE_WIDTH/2);
//			distance[1] = -(currentBestTarget.centerY - Camera.IMAGE_HEIGHT/2.0) / (Camera.IMAGE_HEIGHT/2);
//		}
//		return distance;
//	}
//	
//	/**
//	 * Finds the target's horizontal distance to the target in feet (or whatever measure TARGET_WIDTH is in)
//	 * @return The distance, or 0 if no target is found. Most accurate if head on to target
//	 * Based on https://wpilib.screenstepslive.com/s/3120/m/8731/l/90361-identifying-and-processing-the-targets
//	 * and example code in 2015 Vision Retro Sample (they do it slightly differently with variables, but its the same)
//	 */
//	public double getRangeToBestTarget()
//	{
//		if (currentBestTarget != null)
//		{
//			// chose to use height because most consistent across view angles
//			// d = TargetHeightFeet*FOVHeightPixel / (2*TargetHeightPixel*tan(FOV/2) ) (HYPOTENUSE)
//			return TARGET_HEIGHT*Camera.IMAGE_HEIGHT / (2*currentBestTarget.height*Math.tan(Math.toRadians(Camera.CAMERA_FOV/2)))
//					*Math.cos(Math.toRadians(Camera.CAMERA_ELEVATION)); // convert to horizontal
//		}
//		else
//			return 0;
//	}
//	
//	/**
//	 * Generates complete scores for all found targets,
//	 * then deduces the best. Sets this class's best target
//	 * reference to the one found
//	 * @return The best target's TargetReport
//	 */
//	public TargetReport retrieveBestTarget()
//	{
//		// get targets...
//		allTargets = getTargetReports();
//		
//		// and find the best one
//		TargetReport bestTarget = null;
//		double bestScore = 0;
//		for (TargetReport target : allTargets)
//		{
//			if (target.getCumulativeScore() > bestScore)
//			{
//				bestTarget = target;
//				bestScore = target.getCumulativeScore();
//			}	
//		}
//		
//		// determine if in launcher range to target
//		currentRangeToBestTarget = getRangeToBestTarget();
//		if (Robot.launcherWheels.inRange(currentRangeToBestTarget))
//			Robot.launcherstatus.setInRange();
//		else
//			Robot.launcherstatus.setOutOfRange();
//		
//		// if we've found one, cache and return it
//		// otherwise, return null (it will just default to the first value of bestTarget)
//		currentBestTarget = bestTarget;
//		return bestTarget;
//	}
//	
//	/**
//	 * Retrieves the current TargetReports from GRIP via NetworkTables
//	 * @return A list of TargetReports containing all fields in the TargetReports class
//	 */
//	private TargetReport[] getTargetReports()
//	{
//		// initialize null default value to pass if no connection
//		double[] defaultValue = new double[0];
//		
//		// get relevant values
//		double[] centerXs = netTable.getNumberArray("centerX", defaultValue);
//		double[] centerYs = netTable.getNumberArray("centerY", defaultValue);
//		double[] areas = netTable.getNumberArray("area", defaultValue);
//		double[] widths = netTable.getNumberArray("width", defaultValue);
//		double[] heights = netTable.getNumberArray("height", defaultValue);
//		double[] solidities = netTable.getNumberArray("height", defaultValue);
//		
//		// for each given, create a new object
//		TargetReport[] reports = new TargetReport[centerXs.length];
//		for (int i = 0; i < reports.length; i++)
//		{
//			// it will auto-initialize further scores
//			reports[i] = new TargetReport(centerXs[i],
//										  centerYs[i],
//										  areas[i], 
//										  widths[i],
//										  heights[i],
//										  solidities[i],
//										  IDEAL_ASPECT_RATIO,
//										  IDEAL_AREA_RATIO);
//		}
//		
//		return reports;
//	}
//
//	/**
//	 * Starts vision tracking on Pi via SSH network command
//	 */
//	public void startTracking()
//	{	
//		ProcessBuilder starterProcess = new ProcessBuilder("ssh", 
//										"pi@" + RASPBERRY_PI_LOCATION,
//										" -c \"/home/pi/vision/start_vision.sh " + 
//										Camera.FPS + " " + 
//										Camera.IMAGE_WIDTH + 'x' + Camera.IMAGE_HEIGHT + "\"");
//		
//		try 
//		{
//			starterProcess.start();
//		} 
//		catch (IOException e) 
//		{
//			System.out.println("Could not start process to commence Raspberry Pi GRIP program: ");
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Stops vision tracking on Pi via SSH network command
//	 */
//	public void stopTracking()
//	{
//		ProcessBuilder enderProcess = new ProcessBuilder("ssh", 
//									  "pi@" + RASPBERRY_PI_LOCATION,
//									  " -c \"/home/pi/vision/stop_vision.sh\"");
//		
//		try 
//		{
//			enderProcess.start();
//		} 
//		catch (IOException e) 
//		{
//			System.out.println("Could not start process to termintate Raspberry Pi GRIP program: ");
//			e.printStackTrace();
//		}
//	}
//	
//    public void initDefaultCommand() {
//        // Set the default command for a subsystem here.
//        setDefaultCommand(new IdentifyBestTarget());
//    }
//}
//
