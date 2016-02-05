
package org.usfirst.frc.team2523.robot.subsystems;

import org.usfirst.frc.team2523.robot.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Feeder extends Subsystem {
	Relay feed = new Relay(RobotMap.feeder);
    
    public void initDefaultCommand() {
    }
    DigitalInput balldetector = new DigitalInput(RobotMap.ballDedectorLimSwitch);
    public boolean ballstate(){
    	return balldetector.get();
    }
	public void feed(){
    	feed.set(Relay.Value.kForward);
	}
	public void expel(){
    	feed.set(Relay.Value.kReverse);
	}	
	public void stop(){
    	feed.set(Relay.Value.kOff);
	}
}

    
