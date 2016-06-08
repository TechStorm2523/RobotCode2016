
package org.usfirst.frc.team2523.robot;
import org.usfirst.frc.team2523.robot.subsystems.*;
import org.usfirst.frc.team2523.robot.commands.*;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static final DriveTrain drivetrain = new DriveTrain();
	public static final Winch winch = new Winch();
	public static final ArmPivot armpivot = new ArmPivot();
	public static final Feeder feeder = new Feeder();
	public static final LauncherWheels launcherWheels = new LauncherWheels();
	public static final TargetTracker targetTracker = new TargetTracker();
//	public static Camera camera = new Camera();
	public static final LauncherPneumatics launcherPneumatics = new LauncherPneumatics();
	public static final Dashboard dashboard = new Dashboard();
	public static final LauncherStatus launcherstatus = new LauncherStatus();
	public static final ArmSolenoids armsolenoids = new ArmSolenoids();
	
	// MUST be after subsystems
	public static OI oi = new OI();
	
    Command autonomousCommand;
    SendableChooser autoChooser;
    public static SendableChooser playbackChooser;
    public static SendableChooser recordingChooser;
    
    public static NetworkTable targetRecievingTable;
    public static NetworkTable targetSendingTable;

    public Robot()
    {
    	targetRecievingTable = NetworkTable.getTable(TargetTracker.CONTOUR_NET_TABLE);
    	targetSendingTable = NetworkTable.getTable(TargetTracker.OUTPUT_NET_TABLE);
    }
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {  
    	// auto chooser
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Drive to Defense", new AutoCommandBasic());
        autoChooser.addObject("Do Nothing", null);
        autoChooser.addObject("Basic (Drive-Over) Defense", new AutoCommandBasicDefense());
//        autoChooser.addObject("Cheval de Frise (Tippy) Defense", new AutoCommandChevaldeFrise());
//        autoChooser.addObject("Drawbridge Defense", new AutoCommandDrawbridge());
//        autoChooser.addObject("Portcullis (Gate) Defense", new AutoCommandPortcullis());
//      autoChooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto Program", autoChooser);
        
        // to playback auto chooser
        playbackChooser = new SendableChooser();
        
        // to record auto chooser
        recordingChooser = new SendableChooser();
        
        playbackChooser.addDefault("Don't Play Back", null);
        recordingChooser.addDefault("Don't Record", null);
        
        playbackChooser.addObject("Cheval de Frise (Tippy) Defense", "Cheval_Defense");
        recordingChooser.addObject("Cheval de Frise (Tippy) Defense", "Cheval_Defense");
        
        playbackChooser.addObject("Drawbridge Defense", "Drawbridge_Defense");
        recordingChooser.addObject("Drawbridge Defense", "Drawbridge_Defense");
        
        playbackChooser.addObject("Swing Door Defense", "Swing_Door_Defense");
        recordingChooser.addObject("Swing Door Defense", "Swing_Door_Defense");
        
        playbackChooser.addObject("Experimental Recording", "Experimental_Record");
        recordingChooser.addObject("Experimental Recording", "Experimental_Record");
        
        SmartDashboard.putData("Joystick Playback Program", playbackChooser);
        SmartDashboard.putData("Joystick Recording Program", recordingChooser);
        
//    	targetTracker.init(); OLD VISION
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){
    	armpivot.armLimitOverride = false;
    	winch.lowerWinchLimitOverride = false;
    	winch.winchLimitOverride = false;
    	launcherPneumatics.lower();
    	armsolenoids.extend();
    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		allPeriodic();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {    	      
		String joystickRecording = (String) playbackChooser.getSelected();
		autonomousCommand = (Command) autoChooser.getSelected();
		//armsolenoids.extend();	
		// We prioritize a normal auto command
		if (autonomousCommand != null)
		{			
	    	// schedule the chosen autonomous command
	        autonomousCommand.start();
		}
		else if (joystickRecording != null)
		{
//			System.out.println("RECORDING CHOSEN: "  + joystickRecording);
			oi.DriveStick.startPlayback(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_drive");
			oi.UtilStick.startPlayback(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_util");
		}
		
//		targetTracker.startTracking(); - not necessary because we do this when the RPi starts up now
		
		// prep for driving and winching
		launcherPneumatics.raise();
		armsolenoids.extend();
    	winch.releaseBrake();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {    	
        Scheduler.getInstance().run();       
        allPeriodic();
    }

    public void teleopInit() {
    	// release winch brake and raise launcher
    	winch.releaseBrake();
    	launcherPneumatics.raise();
    	armsolenoids.extend();
    	
    	// make sure that arm is satisfied with where it is
    	armpivot.currentTargetAngle = armpivot.getArmAngle();
    	
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
    	oi.DriveStick.stopPlayback();
    	oi.UtilStick.stopPlayback();
        if (autonomousCommand != null) autonomousCommand.cancel();
        
//        if (!targetTracker.tracking)
//        	targetTracker.startTracking(); - not necessary because we do this when the RPi starts up now
        
        // check if we should start recording joysticks
		String joystickRecording = (String) recordingChooser.getSelected();
        if (joystickRecording != null)
        {        	
        	// record for length of auto
        	oi.DriveStick.startRecording(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_drive", 15); // recordLen
        	oi.UtilStick.startRecording(RobotMap.JOYSTICK_RECORDINGS_SAVE_LOCATION + joystickRecording + "_util", 15);
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {    
        Scheduler.getInstance().run();
        allPeriodic();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    /**
     * This function (implemented by mckenna) is run periodically during ALL modes
     * (except test mode)
     */
    private void allPeriodic()
    {
		armpivot.updateArmProperties();
		
        // update joystick recording
        oi.DriveStick.updateState();
        oi.UtilStick.updateState();
//        System.out.println(Timer.getMatchTime());
    }
}
