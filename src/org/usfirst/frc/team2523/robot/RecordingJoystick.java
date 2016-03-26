package org.usfirst.frc.team2523.robot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;

/**
 * An alternative to the Joystick class which primarily serves as a wrapper to
 * the normal Joystick, but can also record and play back the states of a normal
 * joystick over time, and thus can be used to record autonomous commands using
 * driver input, and play them back in the same fashion in autonomous.
 * 
 * Output methods (i.e. rumble methods) are not supported as of yet.
 * 
 * @author Mckenna Cisler, Team 2523
 */
public class RecordingJoystick extends GenericHID {
	static final double STATE_STORE_FREQ = 20; // or n*20 to slow down... 20 is minimum // ms
	static final double AUTO_PERIOD_LENGTH = 15*10e3; // ms
	static final double MAX_PLAYBACK_DURATION = 10e6; // Arbitrarily large number // ms
	static final String PLAYBACK_FILE_EXTENSION = ".ser";
	
	// modes
	static final int MODE_NORMAL_OPERATION = 0;
	static final int MODE_RECORDING = 1;
	static final int MODE_PLAYBACK = 2;
	
	// Note: with the following maximums, 
	// too low values will result in lost data from recordings, while
	// too high values will result in excessive DS console logs.
	static final int MAX_JOYSTICK_AXES = FRCNetworkCommunicationsLibrary.kMaxJoystickAxes - 1; // just to remove errors becasue we call all these
	static final int MAX_JOYSTICK_POVS = FRCNetworkCommunicationsLibrary.kMaxJoystickPOVs;
	static final int MAX_JOYSTICK_BUTTONS = 14;

	/**
	 * Instead of re-implementing a whole joystick, we will use a traditional
	 * Joystick object to actually read values from the driver station.
	 */
	Joystick inputJoystick;

	/**
	 * A RecordingJoystick has three modes: 
	 *  Normal Operation: The joystick acts just as a normal, DS-connected joystick,
	 * 						providing information from the DS to the program through 
	 * 						this class and it's methods.
	 *  Recording: Same as normal operation, but the Joystick is now constantly
	 * 				logging its state, to be used later in Playback.
	 * 	Playback: The joystick does not interact with the DS, but rather
	 * 				configures itself internally to match a previous Recording
	 * 				on the same timescale as that Recording.
	 * 
	 * This variable denotes which of these modes is currently active.
	 * 
	 * The joystick always begins in Normal Operation, then transitions to either 
	 * Recording or Playback based on the method call.
	 */
	private int currentMode = MODE_NORMAL_OPERATION;
	
	/**
	 * Represents a momentary snapshot of the inputJoystick's state. An array of
	 * these will be used to store and retrieve the inputJoystick's changes over
	 * time.
	 */
	private static class JoystickState implements java.io.Serializable {
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		/**
		 * Serial Version Identifier
		 */
		private static final long serialVersionUID = -3780915990102519962L;
		// axes values
		public double xVal;
		public double yVal;
		public double zVal;
		public double twistVal;
		public double throttleVal;
		public double[] rawAxes;

		// button values
		public boolean triggerVal;
		public boolean topVal;
		public boolean[] rawButtons;
		
		// pov
		public int[] povVals;

		public JoystickState(double xVal, double yVal, double zVal,
				double twistVal, double throttleVal, double[] rawAxes,
				boolean triggerVal, boolean topVal, boolean[] rawButtons,
				int[] povVals)
		{
			this.xVal = xVal;
			this.yVal = yVal;
			this.zVal = zVal;
			this.twistVal = twistVal;
			this.throttleVal = throttleVal;
			this.rawAxes = rawAxes;
			this.triggerVal = triggerVal;
			this.topVal = topVal;
			this.rawButtons = rawButtons;
			this.povVals = povVals;
		}
		
		@Override
		public String toString() {
			return "JoystickState [xVal=" + xVal + ", yVal=" + yVal + ", zVal="
					+ zVal + ", twistVal=" + twistVal + ", throttleVal="
					+ throttleVal;// + ", rawAxes=" + Arrays.toString(rawAxes)
//					+ ", triggerVal=" + triggerVal + ", topVal=" + topVal
//					+ ", rawButtons=" + Arrays.toString(rawButtons)
//					+ ", povVals=" + Arrays.toString(povVals) + "]";
		}

	}
	
	/**
	 * Joystick states are stored here over time to represent the joystick at
	 * all points in time.
	 * The ArrayList will be initialized at the size required for storage of 
	 * a complete autonomous period. (AUTO_PERIOD_LENGTH / STATE_STORE_FREQ)
	 */
	private ArrayList<JoystickState> joystickStates;
	
	/**
	 * The current state and total state progress, used only in Playback, are stored here.
	 * (Normal Operation and Recording states are drawn from DS interaction)
	 */
	private JoystickState currentState;
	private int currentStateIndex;
	
	/**
	 * Variables to handle progress through timed recordings.
	 * (Playbacks are based solely on state indexes)
	 */
	private double desiredDuration; // ms
	private double elapsedDuration; // ms
	
	/**
	 * The joystick states must be updated (either read from joystickStates
	 * or updated from live information) periodically.
	 * For the class to do this, it must assess the time since last store or read,
	 * and this is done below in the CRUCIAL updateState method.
	 * The following variables are used to keep track of the progress since this 
	 * method's last call.
	 */
	private double lastStateCheck = 0; // nanoseconds
	private double lastStateSave = 0; // nanoseconds
	
	
	/**
	 * The RecordingJoystick has the option to set a command from the
	 * Command-Based Programming robot program design scheme to be started
	 * once the Joystick playback has been completed. 
	 * Use the setCompletionCommand() method to do this.
	 * (Any commands assigned to buttons which are pressed during 
	 * recording will also be run)
	 */
	private Command completionCommand = null;
	
	/**
	 * Playback information is stored in playback files with absolute paths.
	 * The name is stored here in recording (before write)
	 * and playback (after read).
	 */
	private String playbackFile;
	
	// TODO: Implement playback indexes for default playback files?
	
	/**
	 * Construct an instance of a recording joystick. The joystick index is the
	 * usb port on the drivers station.
	 *
	 * @param port
	 *            The port on the driver station that the joystick is plugged
	 *            into.
	 */
	public RecordingJoystick(int port) {
		inputJoystick = new Joystick(port);
		joystickStates = new ArrayList<JoystickState>((int)(AUTO_PERIOD_LENGTH / STATE_STORE_FREQ));
	}
	
	/**
	 * Starts recording JoystickStates.
	 * Will run until stopRecording() is called (or for timed recordings, for a certain duration).
	 * @param playbackFileName A String (absolute) path to the name of the file to record to.
	 * Do not include a file extension, it will be added.
	 * (The RoboRio appears to only support absolute paths to files)
	 */
	public void startRecording(String playbackFileName)
	{
		startRecording(playbackFileName, MAX_PLAYBACK_DURATION);
	}
	
	/**
	 * Starts recording JoystickStates, but continues for a set duration.
	 * Once that duration has passed, automatically calls stopRecording().
	 * @param playbackFileName A String (absolute) path to the name of the file to record to.
	 * Do not include a file extension, it will be added.
	 * (The RoboRio appears to only support absolute paths to files)
	 * @param duration The duration to record for, in milliseconds.
	 */
	public void startRecording(String playbackFileName, double duration)
	{
		playbackFile = playbackFileName + PLAYBACK_FILE_EXTENSION;
		joystickStates = new ArrayList<JoystickState>((int)(AUTO_PERIOD_LENGTH / STATE_STORE_FREQ));
		desiredDuration = duration*1000; // convert to milliseconds
		elapsedDuration = 0;
		currentMode = MODE_RECORDING;
	}
	
	/**
	 * Stops the current recording.
	 * Necessary only when the parameterless startRecording() is called.
	 * @return true if playback file was written correctly, else false 
	 * if there was an error.
	 */
	public boolean stopRecording()
	{
		currentMode = MODE_NORMAL_OPERATION;
		return savePlaybackFile(playbackFile);
	}
	
	/**
	 * Begins playback of the given playback file.
	 * @param playbackFileName The absolute path to the playback file
	 * (without extension).
	 * (The RoboRio appears to only support absolute paths to files)
	 * @return true if playback file was read correctly, else false 
	 * if there was an error.
	 */
	public boolean startPlayback(String playbackFileName)
	{	
		// setup and load playback file
		playbackFile = playbackFileName + PLAYBACK_FILE_EXTENSION;
		boolean loadSuccess = loadPlaybackFile(playbackFile);
		
		if (loadSuccess)
		{
			// setup first state
			currentStateIndex = 0;
			currentState = joystickStates.get(0);
			currentMode = MODE_PLAYBACK;
		}
		
		return loadSuccess;
	}
	
	/**
	 * Stops the current Playback, and runs any completionCommand.
	 * No need to call normally, as playback will be stopped once finished.
	 */
	public void stopPlayback()
	{
		if (completionCommand != null)
			completionCommand.start();
		
		currentMode = MODE_NORMAL_OPERATION;
	}
	
	/**
	 * Sets the Command-Based Programming command to be started once playback has finished.
	 * Can be called at any time between when startRecording() and stopRecording() are called.
	 * TODO: In the future, it may be better to store the completionCommand with a particular
	 * playback file, OUTSIDE of the array of JoystickStates.
	 * @param command The command to be started.
	 */
	public void setCompletionCommand(Command command)
	{
		this.completionCommand = command;
	}

	/**
	 * Must be called during Recording and Playback at the highest 
	 * frequency available such that the call frequency is the SAME
	 * or a POWER OF TWO FRACTION MULTIPLE (1/2, 1/4, 1/8, etc.)
	 * of STATE_STORE_FREQ, to avoid odd "beat" effects and the 
	 * resulting variations in update intervals.
	 * (The default STATE_STORE_FREQ is optimized for use in autonomousPeriodic()
	 * and teleopPeriodic() methods, which operate at approximately 50hz - 
	 * so it is either 50hz, 25hz, 12.5hz, etc.)
	 */
	public void updateState()
	{
		// convert to nano seconds to measure delta since last
		if (System.nanoTime() - lastStateSave >= STATE_STORE_FREQ*10e5) // apparently the correct conversion is 10e5
		{
			if (currentMode == MODE_PLAYBACK)
			{
				System.out.println("Recording Joystick: PLAYING BACK, state index = " + currentStateIndex);
				currentStateIndex++;
				
				// handle checks for finishing, in addition to setting new state
				if (currentStateIndex >= joystickStates.size())
				{
					stopPlayback();
					return;
				}
				else
					currentState = joystickStates.get(currentStateIndex);
			}
			else if (currentMode == MODE_RECORDING)
			{
				System.out.println("Recording Joystick: RECORDING, progress = " + elapsedDuration + " / " + desiredDuration);
				
				// handle checks for finishing, in addition to logging new state
				if (elapsedDuration >= desiredDuration)
				{
					stopRecording();
					return;
				}
				else
					saveState();
			}
			lastStateSave = System.nanoTime();
		}
		
		// log bad update frequency 
		// (if check frequency is less than our desired update frequency)
		if (System.nanoTime() - lastStateCheck > STATE_STORE_FREQ*10e5) // apparently the correct conversion is 10e5
		{
//			System.out.println("RecordingJoystick's state is not updated frequently enough (" + 
//						(System.nanoTime() - lastStateCheck) + "ns vs desired " + STATE_STORE_FREQ*10e6 + "ns)");
		}
		
		elapsedDuration += (System.nanoTime() - lastStateCheck) / 10e5; // apparently the correct conversion is 10e5
		lastStateCheck = System.nanoTime();
	}
	
	/**
	 * Saves the current state of the joystick by adding an entry to
	 * joystickStates.
	 * Note: this should not be called while playing back, or information 
	 * will be repeated.
	 */
	private void saveState()
	{
		// get raw lists of values
		double[] rawAxes = new double[MAX_JOYSTICK_AXES];
		for (int i = 0; i < rawAxes.length; i++)
			rawAxes[i] = getRawAxis(i);
		
		boolean[] rawButtons = new boolean[MAX_JOYSTICK_BUTTONS];
		for (int i = 0; i < rawButtons.length; i++)
			rawButtons[i] = getRawButton(i);
		
		int[] povVals = new int[MAX_JOYSTICK_POVS];
		for (int i = 0; i < rawAxes.length; i++)
			rawAxes[i] = getPOV(i);
		
		JoystickState currentState = new JoystickState(
									getX(),
									getY(),
									getZ(),
									getTwist(),
									getThrottle(),
									rawAxes,
									getTrigger(),
									getTop(),
									rawButtons,
									povVals);
		
		joystickStates.add(currentState);
	}
	
	/**
	 * Inherited methods; wrap those of Joystick in Normal Operation and Recording, but
	 * simply playback states in Playback mode.
	 */
	
	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getX(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public double getX(Hand hand) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.xVal;
		else
			return inputJoystick.getX();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getY(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public double getY(Hand hand) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.yVal;
		else
			return inputJoystick.getY();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getZ(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public double getZ(Hand hand) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.zVal;
		else
			return inputJoystick.getZ();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getTwist()
	 */
	@Override
	public double getTwist() {
		if (currentMode == MODE_PLAYBACK)
			return currentState.twistVal;
		else
			return inputJoystick.getTwist();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getThrottle()
	 */
	@Override
	public double getThrottle() {
		if (currentMode == MODE_PLAYBACK)
			return currentState.throttleVal;
		else
			return inputJoystick.getThrottle();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getRawAxis(int)
	 */
	@Override
	public double getRawAxis(int which) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.rawAxes[which];
		else
			return inputJoystick.getRawAxis(which);
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getTrigger(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public boolean getTrigger(Hand hand) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.triggerVal;
		else
			return inputJoystick.getTrigger();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getTop(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public boolean getTop(Hand hand) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.topVal;
		else
			return inputJoystick.getTop();
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getBumper(edu.wpi.first.wpilibj.GenericHID.Hand)
	 */
	@Override
	public boolean getBumper(Hand hand) {
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getRawButton(int)
	 */
	@Override
	public boolean getRawButton(int button) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.rawButtons[button];
		else
			return inputJoystick.getRawButton(button);
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.GenericHID#getPOV(int)
	 */
	@Override
	public int getPOV(int pov) {
		if (currentMode == MODE_PLAYBACK)
			return currentState.povVals[pov];
		else
			return inputJoystick.getPOV(pov);
	}

	// TODO: Include excess Joystick functions that may be used 
	
	/**
	 * Saves the JoystickStates to a playback file.
	 * @return true if playback file was read correctly, else false 
	 * if there was an error.
	 * Adapted from examples at http://www.tutorialspoint.com/java/java_serialization.htm
	 */
	private boolean savePlaybackFile(String playbackFileName)
	{
		try 
		{
			if (playbackFileName != null)
			{
				FileOutputStream fileOut = new FileOutputStream(playbackFileName);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				
				// write the entire joystickStates array
				out.writeObject(joystickStates);
				
				out.close();
				fileOut.close();
				
				System.out.println("A RecordingJoystick playback file was saved at " + playbackFileName);
				return true;
			}
			else
				return false;
		} 
		catch (IOException e) 
		{
			System.out.println("The RecordingJoystick playback file " + playbackFileName + " could not be saved: " + e);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Reads a set of JoystickStates from a playback file into that variable.
	 * @return true if playback file was read correctly, else false 
	 * if there was an error.
	 * Adapted from examples at http://www.tutorialspoint.com/java/java_serialization.htm
	 */
	private boolean loadPlaybackFile(String playbackFileName)
	{
		try
		{
			if (playbackFileName != null)
			{
				FileInputStream fileIn = new FileInputStream(playbackFileName);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				
				// read in the data and assign it to the joystickStates array
				joystickStates = (ArrayList<JoystickState>) in.readObject();
				
				in.close();
				fileIn.close();
				
				System.out.println("A RecordingJoystick playback file was loaded from " + playbackFileName);
				System.out.println(joystickStates);
				
				return true;
			}
			else
				return false;
		} 
		catch (IOException e) 
		{
			System.out.println("The RecordingJoystick playback file " + playbackFileName + " could not be openned: " + e);
			return false;
		} 
		catch (ClassNotFoundException e) 
		{
			System.out.println("The RecordingJoystick playback file " + playbackFileName + " could not be imported: " + e);
			return false;
		}
	}
}
