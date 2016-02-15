// THIS IS DEPRECATED
//package org.usfirst.frc.team2523.robot.subsystems;
//
//import org.usfirst.frc.team2523.robot.RobotMap;
//import org.usfirst.frc.team2523.robot.commands.DrawTargetsOnCamera;
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
// * @deprecated
// */
//public class Camera extends Subsystem {
//	// CONSTANTS
//	final int TARGET_CROSSHAIR_SIZE = 30;
//	final int TARGET_CROSSHAIR_WIDTH = 5;
//	final int TARGET_CROSSHAIR_SPREAD = 5;
//	
//    // define variables
//	public int session;
//    public Image frame;
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
//    /**
//     * Draw a crosshair indicator at the given position
//     */
//    public void drawTargetIndicator(int x, int y)
//    {
//    	 // grab image and frame
//        NIVision.IMAQdxGrab(session, frame, 1);
//        
//    	// draw four rectangles for crosshairs
//        NIVision.Rect[] crosshairRects = new NIVision.Rect[4];
//    	crosshairRects[0] = new NIVision.Rect(x + TARGET_CROSSHAIR_SPREAD, y, 
//    										  TARGET_CROSSHAIR_SIZE, TARGET_CROSSHAIR_WIDTH);
//    	crosshairRects[1] = new NIVision.Rect(x - TARGET_CROSSHAIR_SPREAD, y, 
//				  							  TARGET_CROSSHAIR_SIZE, TARGET_CROSSHAIR_WIDTH);
//    	crosshairRects[2] = new NIVision.Rect(x, y + TARGET_CROSSHAIR_SPREAD, 
//    										  TARGET_CROSSHAIR_WIDTH, TARGET_CROSSHAIR_SIZE);
//    	crosshairRects[3] = new NIVision.Rect(x, y + TARGET_CROSSHAIR_SPREAD, 
//				  							  TARGET_CROSSHAIR_WIDTH, TARGET_CROSSHAIR_SIZE);
//    
//        // add shapes to frame
//    	for (NIVision.Rect crosshairRect : crosshairRects)
//    		NIVision.imaqDrawShapeOnImage(frame, frame, crosshairRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 0.0f);
//    	
//        // set the image
//        CameraServer.getInstance().setImage(frame);
//    }
//    
//    public void initDefaultCommand() {
//        // Set the default command for a subsystem here.
//        setDefaultCommand(new DrawTargetsOnCamera());
//    }
//}
//
