package org.usfirst.frc.team1294.robot.commands;

import org.usfirst.frc.team1294.robot.Robot;
import org.usfirst.frc.team1294.robot.subsystems.DriveBase;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DrivePid extends PIDCommand {

	private static final double PID_TOLERANCE = 1;
	private static final double PID_P = 0.15;
	private static final double PID_I = 0.02;
	private static final double PID_D = 0.09;
	
	protected DriveBase driveBase;
	protected double heading;
	protected double speed;
	private double desiredSpeed;
	private double distance;
	private double leftEncoderStart;
	private double rightEncoderStart;
	private boolean driveCurrentHeading = false;
	
	public DrivePid(double heading) {
		this(String.format("Turn to a heading of %f", heading), heading, 0, 0);
	}
	
	public DrivePid(double speed, double distance) {
		this(String.format("Drive the current heading at a speed of %f for a distance of %f.", speed, distance), -1, speed, distance);
		driveCurrentHeading = true;
	}
	
	public DrivePid(double heading, double speed, double distance) {
		this(String.format("Drive a heading of %f at a speed of %f for a distance of %f.)", heading, speed, distance), heading, speed, distance);
	}
	
	private DrivePid(String name, double heading, double speed, double distance) {
		// TODO: tune this pid
		super(name, PID_P, PID_I, PID_D);
		
		requires(Robot.driveBase);
		this.driveBase = Robot.driveBase;
		this.heading = heading;
		this.speed = speed;
		this.distance = distance;
		
		// since this is a closed loop command, and may be running without operator intervention, set a timeout
		this.setTimeout(15);
		
		LiveWindow.addActuator("DriveSystem", "DriveHeadingPid", this.getPIDController());
	}

	@Override
	protected void initialize() {
		// if heading was not provided, use current heading
		if (driveCurrentHeading) {
			this.setSetpoint(driveBase.getNormalizedAngle());
			System.out.println("setpoint to current heading " + driveBase.getNormalizedAngle());
		} else {
			this.setSetpoint(heading);
		}
		
		this.desiredSpeed = speed;
		
		this.getPIDController().setInputRange(0, 360);
		this.getPIDController().setOutputRange(-0.4, 0.4);
		this.getPIDController().setContinuous();
		//this.getPIDController().setPercentTolerance(PID_TOLERANCE);
		this.getPIDController().setAbsoluteTolerance(PID_TOLERANCE);
		
		this.driveBase.setTalonsToClosedLoopSpeed();
		
		this.leftEncoderStart = this.driveBase.getLeftPosition();
		this.rightEncoderStart = this.driveBase.getRightPosition();
	
		SmartDashboard.putData(this);
	}
	
	@Override
	protected double returnPIDInput() {
		return this.driveBase.getNormalizedAngle();
	}

	@Override
	protected void usePIDOutput(double output) {
		//System.out.println("output " + output);
		this.driveBase.arcadeDrive(desiredSpeed, output);
	}

	@Override
	protected void execute() {
		
	}

	@Override
	protected boolean isFinished() {
		if (isTimedOut()) {
			return true;
		}
		
		// return false if robot has not traveled far enough
		double distanceDriven = (
				Math.abs(driveBase.getLeftPosition() - leftEncoderStart) + 
				Math.abs(driveBase.getRightPosition() - rightEncoderStart)) / 2;

		System.out.println(distanceDriven + " " + distance);
		boolean hasDrivenFarEnough = false;
		
		if (distanceDriven < distance) {
			//return false;
		} else {
			this.desiredSpeed = 0; // TODO: do this slower
			hasDrivenFarEnough = true;
		}

		boolean isPointingRightDirection = false;
		// return false if robot is not pointing the correct direction
		if (Math.abs(driveBase.getNormalizedAngle() - this.heading) > PID_TOLERANCE) {
			//return false;
		} else {
			isPointingRightDirection = true;
		}
		
		
		return hasDrivenFarEnough && isPointingRightDirection;
	}

	@Override
	protected void end() {
		//this.driveBase.setTalonsToOpenLoop();
		//this.driveBase.arcadeDrive(0, 0);
	}

	@Override
	protected void interrupted() {
		end();
	}
}
