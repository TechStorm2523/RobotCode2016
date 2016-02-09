package org.usfirst.frc.team2523.robot.subsystems;

import java.util.Comparator;
import java.util.Vector;

import org.usfirst.frc.team2523.robot.Robot;
import org.usfirst.frc.team2523.robot.Robot.ParticleReport;
import org.usfirst.frc.team2523.robot.Robot.Scores;
import org.usfirst.frc.team2523.robot.commands.IdentifyBestTarget;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Analyzes targets and finds the best one. Based on the 2015 Vision Color Sample,
 * and https://wpilib.screenstepslive.com/s/3120/m/8731/l/91395-c-java-code
 * Also defines methods which can be used to display target information on
 * SmartDashboard and provide functionality to subsystems using the camera.
 */
public class TargetTracker extends Subsystem {	
	// CONSTANTS
	// target geometry
	private final double IDEAL_ASPECT_RATIO = 20.0 / 14.0;
	private final double IDEAL_AREA_RATIO =  88.0 / 280.0;
	private final double TARGET_WIDTH = 20 / 12.0;
	private final double TARGET_HEIGHT = 14 / 12.0;
	// camera/image properties
	private final double IMAGE_WIDTH = 640;
	private final double IMAGE_HEIGHT = 480;
	private final double CAMERA_FOV = 52; // 52 or 60 // TODO: NEEDS ADJUSTING // VERTICAL FOV
	private final double CAMERA_ELEVATION = 68; // degrees
	// threshold values
	NIVision.Range HUE_RANGE = new NIVision.Range(24, 49);	//Default hue range for target
	NIVision.Range SAT_RANGE = new NIVision.Range(67, 255);	//Default saturation range for target
	NIVision.Range VAL_RANGE = new NIVision.Range(49, 255);	//Default value range for target
	// general scoring
	private final double AREA_MINIMUM = 0.5; // as percentage of total area
	private final double AREA_MAXIMUM = 100.0;
	private final double MIN_SCORE = 75;
	NIVision.ParticleFilterCriteria2 areaFilterCritera[] = new NIVision.ParticleFilterCriteria2[1];
	NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
	
	// Exterior reference variables
	private ParticleReport currentBestTarget = null;
	public ParticleReport[] allTargets = null;
	public double currentRangeToBestTarget = 0;
	
	//Constants
//	double AREA_MINIMUM = 0.5; //Default Area minimum for particle as a percentage of total image area
//	double LONG_RATIO = 2.22; //Tote long side = 26.9 / Tote height = 12.1 = 2.22
//	double SHORT_RATIO = 1.4; //Tote short side = 16.9 / Tote height = 12.1 = 1.4
//	double SCORE_MIN = 75.0;  //Minimum score to be considered a tot
		

	
	//A structure to hold measurements of a particle
	public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>{
		double PercentAreaToImageArea;
		double Area;
		double ConvexHullArea;
		double BoundingRectLeft;
		double BoundingRectTop;
		double BoundingRectRight;
		double BoundingRectBottom;
		double AspectRatioScore;
		double AreaRatioScore;
		
		public int compareTo(ParticleReport r)
		{
			return (int)(r.Area - this.Area);
		}
		
		public int compare(ParticleReport r1, ParticleReport r2)
		{
			return (int)(r1.Area - r2.Area);
		}
	};

	//Structure to represent the scores for the various tests used for target identification
	public class Scores {
		double aspectRatioScore;
		double areaRatioScore;
	};

	//Images
	Image frame;
	Image binaryFrame;
	int imaqError;
	
	public void TargetReport()
	{
		// create images
		frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		areaFilterCritera[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM, AREA_MAXIMUM, 0, 0);
	}
	
	public ParticleReport getBestTarget()
	{
		// get image
		//NIVision.IMAQdxGrab(session, frame, 1);
		
		// threshold based on HSV
		NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSV, HUE_RANGE, SAT_RANGE, VAL_RANGE);
		
		// send masked image to dashboard
		CameraServer.getInstance().setImage(binaryFrame);
		
		// filter out small particles
		areaFilterCritera[0].lower = (float)AREA_MINIMUM;
		areaFilterCritera[0].upper = (float)AREA_MAXIMUM;
		imaqError = NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, areaFilterCritera, filterOptions, null);
		
		// get number of particles after filter
		int numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
		
		if (numParticles > 0)
		{		
			//Measure particles and sort by particle size
			Vector<ParticleReport> particles = new Vector<ParticleReport>();
			for(int particleIndex = 0; particleIndex < numParticles; particleIndex++)
			{
				// create a new particle report, as defined above, and populate
				// it with the relevant information about the particle
				ParticleReport par = new ParticleReport();
				par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
				par.Area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
				par.BoundingRectTop = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
				par.BoundingRectLeft = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
				par.BoundingRectBottom = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
				par.BoundingRectRight = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
				// score each target using arbituary functions I made (to see which are most like the target)
				par.AspectRatioScore = calculateAspectRatioScore(par);
				par.AreaRatioScore = calculateAreaRatioScore(par);
				particles.add(par);
			}
			particles.sort(null);
			
			// then, find the best target based on its score
			ParticleReport bestTarget = null;
			double bestScore = 0;
			
			for (ParticleReport particle : particles)
			{
				if (getCumulativeScore(particle) > bestScore)
				{
					bestTarget = particle;
					bestScore = getCumulativeScore(particle);
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
		else
		{
			currentBestTarget = null;
			return null;
		}
	}

	/**
	 * Calculates a 0-1 aspect ratio score of the given target,
	 * based off the difference between it's ratio and the ideal one.	
	 */
	private double calculateAspectRatioScore(ParticleReport report) {
		double height = report.BoundingRectRight - report.BoundingRectLeft;
		double width = report.BoundingRectBottom - report.BoundingRectTop;
		
		// ensure no error
		if (height != 0)
			return scoreFromDistance(width / height, IDEAL_ASPECT_RATIO);
		else
			return 0;
	}
	
	/**
	 * Generates cumulative score for the current target.
	 * Must have setAdditionalScores run beforehand
	 */
	private double getCumulativeScore(ParticleReport report)
	{
		// if both scores are PERFECT, this will give a one
		return (report.AspectRatioScore + report.AreaRatioScore) / 2.0;
	}

	/**
	 * Calculates a 0-1 area score of the given target,
	 * based off the difference between the ideal ratio 
	 * of the bounding box's area to the internal area AND the actual.
	 */
	private double calculateAreaRatioScore(ParticleReport report) {
		double boundingBoxArea = (report.BoundingRectBottom - report.BoundingRectTop) * 
								(report.BoundingRectRight - report.BoundingRectLeft);
		
		// prevent error when there is zero area
		if (boundingBoxArea != 0)
			return scoreFromDistance(report.Area / boundingBoxArea, IDEAL_AREA_RATIO);
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
	 * Finds the target's horizontal distance to the target in feet (or whatever measure TARGET_WIDTH is in)
	 * @return The distance, or 0 if no target is found. Most accurate if head on to target
	 */
	public double getRangeToBestTarget()
	{
		if (currentBestTarget != null)
		{
			// chose to use height because most consistent across view angles
			// d = TargetHeightFeet*FOVHeightPixel / (2*TargetHeightPixel*tan(FOV/2) ) (HYPOTENUSE)
			return TARGET_HEIGHT*IMAGE_HEIGHT / (2*currentBestTarget.height*Math.tan(CAMERA_FOV/2))
					*Math.cos(Math.toDegrees(CAMERA_ELEVATION)); // convert to horizontal
		}
		else
			return 0;
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new IdentifyBestTarget());
    }
}

