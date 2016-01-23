package org.usfirst.frc.team1294.robot.subsystems;

import edu.wpi.first.wpilibj.CANTalon;

import org.usfirst.frc.team1294.robot.RobotMap;
import org.usfirst.frc.team1294.robot.commands.TankDriveWithJoystick;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * An example subsystem.
 */
public class DriveSystem extends Subsystem {
	public CANTalon leftFrontTalon;
	public CANTalon rightFrontTalon;
	public CANTalon leftBackTalon;
	public CANTalon rightBackTalon;
	public RobotDrive drive;
	private AnalogGyro gyro;
	
    public DriveSystem() {
    	//encoders are on talon one and four
        // Set left feedback device and talon numbers
    	leftFrontTalon = new CANTalon(RobotMap.leftFrontTalon);
    	//leftFrontTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	leftBackTalon = new CANTalon(RobotMap.leftBackTalon);
    	// Set right feedback device and talon number
    	rightFrontTalon = new CANTalon(RobotMap.rightFrontTalon);
    	rightBackTalon = new CANTalon(RobotMap.rightBackTalon);
    	//rightBackTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	leftFrontTalon.setInverted(true);
    	leftBackTalon.reverseOutput(true);
    	rightBackTalon.setInverted(true);
    	//set left back talon to slave mode
    	leftBackTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
    	leftBackTalon.set(leftFrontTalon.getDeviceID());
    	//set right front talon to slave mode
    	rightFrontTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
    	rightFrontTalon.set(rightBackTalon.getDeviceID());
    	//set control mode for encoder talons
    	
    	drive = new RobotDrive(leftFrontTalon, rightBackTalon);
    	gyro = new AnalogGyro(0);
    	
    	LiveWindow.addSensor("Drive Sensor", "Gyro", gyro);
    	LiveWindow.addActuator("Drive", "Left Front", leftFrontTalon);
    	LiveWindow.addActuator("Drive", "Left Rear", leftBackTalon);
    	LiveWindow.addActuator("Drive", "Right Front", rightFrontTalon);
    	LiveWindow.addActuator("Drive", "Right Rear", rightBackTalon);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new TankDriveWithJoystick());
    }
    
    public void tankDrive(Joystick left, Joystick right) {
		drive.tankDrive(left, right);
	}
    public void arcadeDrive(double y, double d){
    	drive.arcadeDrive(y, d);
    }
    public void setToEncoders(){
    	rightBackTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	leftFrontTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	rightBackTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
    	leftFrontTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
    	drive.setMaxOutput(2048);
    }
	
	public void stop() {
		drive.drive(0,0);
	}
	public AnalogGyro getGyro(){
		return gyro;
	}

	public void tankDrive(double d, double e) {
		// TODO Auto-generated method stub
		drive.tankDrive(d, e);
	}

	public void resetGyro() {
		// TODO Auto-generated method stub
		gyro.reset();
	}
}
