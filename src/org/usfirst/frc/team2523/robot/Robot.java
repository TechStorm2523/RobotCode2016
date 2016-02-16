
package org.usfirst.frc.team2523.robot;

import java.io.IOException;

import org.usfirst.frc.team2523.robot.subsystems.*;
import org.usfirst.frc.team2523.robot.commands.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
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
	public static TargetTracker targetTracker = new TargetTracker();
	public static final LauncherPneumatics launcherPneumatics = new LauncherPneumatics();
	public static final Dashboard dashboard = new Dashboard();
	public static final LauncherStatus launcherstatus = new LauncherStatus();

	// MUST be after subsystems
	public static OI oi = new OI();
	
	// nettables
	public static NetworkTable netTable;
	
    Command autonomousCommand;
    SendableChooser autoChooser;

    public Robot()
    {
    	netTable = NetworkTable.getTable(RobotMap.CONTOUR_NET_TABLE);
    }
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Drive to Defense", new AutoCommandBasic());
        autoChooser.addObject("Do Nothing", new AutoCommandNOTHING());
        autoChooser.addObject("Basic (Drive-Over) Defense", new AutoCommandBasicDefense());
        autoChooser.addDefault("Cheval de Frise (Tippy) Defense", new AutoCommandChevaldeFrise());
        autoChooser.addDefault("Drawbridge Defense", new AutoCommandDrawbridge());
        autoChooser.addDefault("Portcullis (Gate) Defense", new AutoCommandPortcullis());
//      autoChooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", autoChooser);
        
        targetTracker.init();
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){
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
        autonomousCommand = (Command) autoChooser.getSelected();
        
        // SHOULDNT BE NEEDED
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new MyAutoCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} */
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {    	
        Scheduler.getInstance().run();
        allPeriodic();
    }

    public void teleopInit() {
    	// ensure that arm extends
    	winch.setDistance(winch.MAX_ARM_EXTENSION);
    	
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();

        NIVision.IMAQdxStartAcquisition(Robot.targetTracker.session);
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
    }
}
