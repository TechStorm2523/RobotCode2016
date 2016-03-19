//package org.usfirst.frc.team2523.robot.subsystems;
//
//import org.usfirst.frc.team2523.robot.Robot;
//import org.usfirst.frc.team2523.robot.RobotMap;
//import org.usfirst.frc.team2523.robot.commands.TransferImagetoDS;
//
//import com.ni.vision.NIVision;
//import com.ni.vision.NIVision.DrawMode;
//import com.ni.vision.NIVision.Image;
//import com.ni.vision.NIVision.ShapeMode;
//
//import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.command.Subsystem;
//
///**
// * Class handling all robot camera functions
// */
//public class Camera extends Subsystem {
//	// target crosshair display constants
//	static final int TARGET_CROSSHAIR_SIZE = 20; // length from center
//	static final int TARGET_CROSSHAIR_WIDTH = 5;
//	static final int TARGET_CROSSHAIR_SPREAD = 5; // spread from center
//	// camera/image properties
//	public static final double IMAGE_WIDTH = 1280;
//	public static final double IMAGE_HEIGHT = 760;
//	public static final double FPS = 25;
//	public final static double CAMERA_FOV = 39.935; // VERTICAL (By measuring distance from a known size object that spans vertical FOV and using tan OR solving the equation in getRangeToBestTarget for FOV using other measurements from debug)
//	public final static double CAMERA_ELEVATION = 45; // degrees
//	
//    // define variables
//	public int session;
//    public Image frame;
//    
//
//    public Camera()
//    {
//    	// define frame as RGB with no border
//        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
//
//        // get session (the camera name (ex "cam0") can be found through the roborio web interface)
//        session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//        
//        // set up session
//        NIVision.IMAQdxConfigureGrab(session);
//    }    
//    
//	/**
//	 * Simply grabs the image from the camera and sends it to the DS without processing
//	 */
//	public void transferImagetoDS()
//	{
//		NIVision.IMAQdxGrab(session, frame, 1);
//		
//		// draw on any target indicators where needed
//		if (Robot.targetTracker.currentBestTarget != null || 
//				Robot.targetTracker.allTargets.length > 0)
//		{
//			// target indicator for best target,
//			drawTargetIndicator((int) Robot.targetTracker.currentBestTarget.centerX, 
//								(int) Robot.targetTracker.currentBestTarget.centerY,
//								frame);
//			
//			// bounding boxes for the rest
//			for (TargetReport target : Robot.targetTracker.allTargets)
//				drawBoundingBox(target, frame);
//		}
//		
//		CameraServer.getInstance().setImage(frame);
//	}
//	
//	/**
//	 * Draw a crosshair indicator at the given position
//	 */
//	public void drawTargetIndicator(int x, int y, Image frame)
//	{    
//		if (frame != null)
//		{
//	  		// draw four rectangles for crosshairs (shift so x,y is at center)
//	      	NIVision.Rect[] crosshairRects = new NIVision.Rect[4];
//	      	// right
//	  		crosshairRects[0] = new NIVision.Rect(y - TARGET_CROSSHAIR_WIDTH/2, x + TARGET_CROSSHAIR_SPREAD,
//	  											TARGET_CROSSHAIR_WIDTH, TARGET_CROSSHAIR_SIZE);
//	  		// left
//	  		crosshairRects[1] = new NIVision.Rect(y - TARGET_CROSSHAIR_WIDTH/2, x - TARGET_CROSSHAIR_SPREAD - TARGET_CROSSHAIR_SIZE,  
//	  											TARGET_CROSSHAIR_WIDTH, TARGET_CROSSHAIR_SIZE);
//	  		// top
//	  		crosshairRects[2] = new NIVision.Rect(y - TARGET_CROSSHAIR_SPREAD - TARGET_CROSSHAIR_SIZE, x - TARGET_CROSSHAIR_WIDTH/2,  
//	  										  	TARGET_CROSSHAIR_SIZE, TARGET_CROSSHAIR_WIDTH);
//	  		// bottom
//	  		crosshairRects[3] = new NIVision.Rect(y + TARGET_CROSSHAIR_SPREAD, x - TARGET_CROSSHAIR_WIDTH/2,
//	  											TARGET_CROSSHAIR_SIZE, TARGET_CROSSHAIR_WIDTH);
//	  
//	  		// add shapes to frame
//	  		for (NIVision.Rect crosshairRect : crosshairRects)
//	  			NIVision.imaqDrawShapeOnImage(frame, frame, crosshairRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 255.0f);
//		}
//	}
//	
//	public void drawBoundingBox(TargetReport target, Image frame)
//	{
//		NIVision.Rect boundingBox = new NIVision.Rect((int) (target.centerY - target.height/2.0), 
//													  (int) (target.centerX - target.width/2.0), 
//												   	  (int) target.height,
//												   	  (int) target.width);
//		
//		NIVision.imaqDrawShapeOnImage(frame, frame, boundingBox, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, 255.0f);
//	}
//    
//    public void initDefaultCommand() {
//        // Set the default command for a subsystem here.
//        setDefaultCommand(new TransferImagetoDS());
//    }
//}
//
