//
//package org.usfirst.frc.team2523.robot.subsystems;
//
//
//import org.usfirst.frc.team2523.robot.RobotMap;
//
//import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.command.Subsystem;
//
///**
// *
// */
//public class ArmPneumatics extends Subsystem {
//	DoubleSolenoid armPneumatics = new DoubleSolenoid(RobotMap.armSolenoid1, RobotMap.armSolenoid2);
//
//    public void initDefaultCommand() {
//        // Set the default command for a subsystem here.
//        //setDefaultCommand(new MySpecialCommand());
//    }
//
//	public void extend() {
//		armPneumatics.set(DoubleSolenoid.Value.kForward);
//	}
//	public void retract() {
//		armPneumatics.set(DoubleSolenoid.Value.kReverse);
//	}
//	public void off() {
//		armPneumatics.set(DoubleSolenoid.Value.kOff);
//	}
//}