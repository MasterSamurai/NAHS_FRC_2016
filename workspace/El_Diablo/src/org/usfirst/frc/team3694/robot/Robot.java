/****************************************************\
 * Team 3694 NAHS Warbotz
 * FRC 2016 Robot Code
 * "El Diablo"
 * 
 * Version 1.1.0
 * 
 * Changes: 
 * -Fixed autonomous
 * -Tweaks
\***************************************************/

//Defines stuff
package org.usfirst.frc.team3694.robot;

//Imports
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.*;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

//ROBOT CODE FROM THIS POINT ON
public class Robot extends IterativeRobot {
	//SmartDashboard Objects and Variables
	public static SendableChooser chooser; //Destination Point
	public static SendableChooser chooser3; //Camera
	public static int status; //Camera Status
	public static int autoChooser; //Emergency variable :P
	public static int rawDist; //Int distance between points
	public static double dist; //Encoder distance between points
	
	//PWM Objects and Variables
	public static RobotDrive chassis; //Robot Chassis
	public static double motorPos; //Position of rollerTilt
	public static Victor leftDrive = new Victor(0); //Left Drive Motors (Both Front and Rear)---------------------------------PWM (0)
	public static Victor rightDrive = new Victor(1); //Right Drive Motors (Both Front and Rear)-------------------------------PWM (1)
	public static Victor roller = new Victor(2); //Roller Motor (Forwards and Backwards)--------------------------------------PWM (2)
	public static Victor rollerTilt = new Victor(3); //Roller Tilt (Forwards and Backwards)-----------------------------------PWM (3)
	
	//USB Objects and Variables
	public static Joystick driveStick = new Joystick(0); //Joystick used for Driving------------------------------------------USB (0)
	public static Joystick shootStick = new Joystick(1); //Joystick used for Shooting-----------------------------------------USB (1)
	
	//SPI Objects and Variables
	public static final double Kp = 0.03; //proportional scaling constant
	public static ADXL362 accel = new ADXL362(SPI.Port.kOnboardCS1, Accelerometer.Range.k16G); //Accelerometer----------------SPI (1)
	public static ADXRS450_Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); //Gyroscope-----------------------------------SPI (0)

	//DIO Objects and Variables
	public static Encoder leftEncoder = new Encoder(0, 1, true, Encoder.EncodingType.k4X); //Left Side Driver Encoder---------DIO (0,1)
	public static Encoder rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X); //Right Side Driver Encoder------DIO (2,3)
	public static Encoder rollerEncoder = new Encoder(5, 6, false, Encoder.EncodingType.k4X); //Roller Motor Encoder----------DIO (5,6)
	public static DigitalInput rollerUpSwitch = new DigitalInput(7); //Roller Upper Limit Switch------------------------------DIO (7)
	public static DigitalInput rollerDownSwitch = new DigitalInput(8); //Roller Lower Limit Switch----------------------------DIO (8)
	public static DigitalInput rollerBallSwitch = new DigitalInput(9); //Roller Ball Limit Switch-----------------------------DIO (9)
	public static final double distPerRev = 4.05 * Math.PI; //Distance per revolution
	public static final double distPerCount = distPerRev/249; //Distance per encoder count

//ROBOT INIZILIZATION (RUNS ONCE)
	public void robotInit() {
		if(status == 0){
    		CameraServer.getInstance().startAutomaticCapture("cam0");
    	}	
		//Table Creation for Destination Point
		chooser = new SendableChooser();
		chooser.initTable(NetworkTable.getTable("Autonomous"));
      	chooser.addDefault("Nothing", 0);
      	chooser.addObject("Onwards!" , 1); 
      	SmartDashboard.putData("Destination Point", chooser);
      	
        //Table selection for Camera Status
      	chooser3 = new SendableChooser();
      	chooser3.initTable(NetworkTable.getTable("RoboRio Camera"));
      	chooser3.addDefault("Enable", 0);
      	chooser3.addObject("Disable", 1); 
      	SmartDashboard.putData("RoboRio Camera", chooser3);
      	
      	//Initialize SmartDashboard Fields
      	//SmartDashboard.putString("Current Point", chooser2.getSelected().toString());
      	//SmartDashboard.putString("Destination Point", chooser.getSelected().toString());
      	SmartDashboard.putString("Roborio Camera Status:", chooser3.getSelected().toString());
      	SmartDashboard.putString("Error","");
      	dashVarUpdate(0 ,0, 0, 0, 0, 0, 0);
      	SmartDashboard.putString("Roller Direction", "---");
      	SmartDashboard.putString("Test Sequence", "---");
      	resetEncoders(); //reset encoders
      	
    	//Configure motors present in Chassis, configure safety
    	chassis = new RobotDrive(leftDrive, rightDrive);
    	gyro.calibrate();
    	chassis.setExpiration(0.1);
    }
//RESET ENCODERS
	public void resetEncoders(){
		leftEncoder.reset(); //Reset left encoder
    	rightEncoder.reset(); //Reset right encoder
    	rollerEncoder.reset();
	}
//UPDATE VARIABLES IN SMARTDASHBOARD
	public void dashVarUpdate(double a, double b, double c, double d, double e, double f, double g){
		SmartDashboard.putNumber("Gyro Angle", a);
      	SmartDashboard.putNumber("Gyro Angle Dial", b);
      	SmartDashboard.putNumber("Gyro Rate", c);
      	SmartDashboard.putNumber("Acceleration X-Axis", d);
      	SmartDashboard.putNumber("Acceleration Y-Axis", e);
      	SmartDashboard.putNumber("Acceleration Z-Axis", f);
      	SmartDashboard.putNumber("Roller Encoder", g);
	}
//ENCODER MOVE COUNTS
    public void move(double dist, double speed){
    	//double cntsNeeded = dist*distPerCount; //Counts needed for distance
    	//double rnddCountsNeeded = dist; //Rounded Counts Needed
    	//resetEncoders(); //reset encoders
    	if(leftEncoder.get() > dist || rightEncoder.get() > dist){
    		chassis.drive(0.0, 0.0);
    		//resetEncoders(); //reset encoders
    	}else{
    		leftDrive.set(-0.5);
    		rightDrive.set(-0.5);
    		if(leftEncoder.get() > rightEncoder.get()){
    			rightDrive.set(rightDrive.get() + 0.1);
    		}
    		if(rightEncoder.get() > leftEncoder.get()){
    			leftDrive.set(leftDrive.get() + 0.1);
    		}
    		if(leftEncoder.get() < rightEncoder.get()){
    			rightDrive.set(rightDrive.get() - 0.1);
    		}
    		if(rightEncoder.get() < leftEncoder.get()){
    			leftDrive.set(leftDrive.get() - 0.1);
    		}
    		chassis.drive(speed, (-(gyro.getAngle())*Kp)); //Drive until encoder counts met
    	}
    }
//CREATE ROLLER BUTTON
    public void rollerButton(int a, String b, double c){
    	if(shootStick.getRawButton(a)){
        	SmartDashboard.putString("Roller Direction", b);
        	roller.set(c);
        }
    }
//MOVE MANIPULATOR
    public void moveArm(Joystick stick){
    	motorPos = rollerTilt.getPosition(); // Store position
		// If Limit switches tripped, do not allow other direction.
		if (rollerUpSwitch.get() == true && shootStick.getAxis(Joystick.AxisType.kY) < 0) {
			rollerTilt.set(0);
		} else if (rollerDownSwitch.get() == true && shootStick.getAxis(Joystick.AxisType.kY) > 0) {
			rollerTilt.set(0);
		} else {
			rollerTilt.set(shootStick.getAxis(Joystick.AxisType.kY));
		}

		if (shootStick.getAxis(Joystick.AxisType.kY) == 0) {
			rollerTilt.setPosition(motorPos); // set rollerTilt to previous position	
		}
    }
//SEQUENCE CREATOR
    public void sequenceCreator(Victor v){
    	Timer.delay(0.5);
		v.set(0.25);
		Timer.delay(0.25);
		v.set(0.5);
		Timer.delay(0.25);
		v.set(0.75);
		Timer.delay(0.25);
		v.set(1.0);
		Timer.delay(0.25);
		
		v.set(0.75);
		Timer.delay(0.25);
		v.set(0.5);
		Timer.delay(0.25);
		v.set(0.25);
		Timer.delay(0.25);
		v.set(0.0);
		Timer.delay(0.25);
		
		v.set(-0.25);
		Timer.delay(0.25);
		v.set(-0.5);
		Timer.delay(0.25);
		v.set(-0.75);
		Timer.delay(0.25);
		v.set(-1.0);
		Timer.delay(0.25);
		
		v.set(-0.75);
		Timer.delay(0.25);
		v.set(-0.5);
		Timer.delay(0.25);
		v.set(-0.25);
		Timer.delay(0.25);
		v.set(0.0);
		Timer.delay(0.25);
    }
//AUTONOMOUS INITIATION (RUNS ONCE)
    public void autonomousInit() {
    	Timer.delay(0.005); //Slight delay required
    	chassis.setSafetyEnabled(false);
    	gyro.reset(); //Reset Gyro
    	resetEncoders(); //reset encoders
    	
    	//Get options we selected before autonomous
    	autoChooser = Integer.parseInt(chooser.getSelected().toString());
    	//Camera Status
  
    	 if(isEnabled()){
    	    	if(autoChooser == 0){
    	    		SmartDashboard.putString("Error", "You chose Nothing as one of the points"); //Display error
    	    	}else{
    	    		move(10, -0.6);
    	    	}
    	    }
    }
//AUTONOMOUS PERIODIC (CALLED EVERY FIELD CYCLE)  
    public void autonomousPeriodic(){
    	dashVarUpdate(gyro.getAngle(), gyro.getAngle(), gyro.getRate(), accel.getX(), accel.getY(), accel.getZ(), 0); //Update SmartDashboard Values
    //Autonomous Move
   
    }
//TELEOPERATED INITIATION (RUNS ONCE)
    public void teleopInit() {
    	chassis.setSafetyEnabled(false); //Enable Safety
    	resetEncoders(); //reset encoders
    	gyro.reset();
    }
//TELEOPERATED PERIODIC (CALLED EVERY FIELD CYCLE)
    public void teleopPeriodic(){
    		Timer.delay(0.001); //Slight delay required
        	dashVarUpdate(gyro.getAngle(), gyro.getAngle(), gyro.getRate(), accel.getX(), accel.getY(), accel.getZ(), rollerEncoder.get()); //Update SmartDashboard Values
         	chassis.arcadeDrive(driveStick); //Drive chassis using Arcade Drive (One Joystick)
            moveArm(shootStick); //Move manipulator arm
            
            //Roller Button Functions
            rollerButton(3, "Stopped", 0.0); //Stop Roller-----------------------ShootStick (3)
            if (rollerBallSwitch.get() == true) {
    			rollerButton(4, "Cannot Go Backwards", 0.0);//Can't go Backwards
    			roller.set(0.0);
    		} else {
    			rollerButton(4, "Backwards", -0.75);//Roll Roller Backwards----ShootStick (4)
    		} 
            rollerButton(5, "Forwards", 0.75); //Roll Roller Forwards----------ShootStick (5)
     
            if(driveStick.getRawButton(2)){ //Manually reset Gyro--------------DriveStick (2)
            	gyro.reset();
            }
     }
//TEST SEQUENCE (RUNS ONCE)
    public void testInit(){
    	if(isEnabled()){
    		SmartDashboard.putString("Test Sequence", "Initializing.");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Initializing..");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Initializing...");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "<!> Preparing to Start Test Sequence <!>");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "<!> Please keep your distance. The test will start in 10 seconds. <!>");
    		Timer.delay(10.0);
    		SmartDashboard.putString("Test Sequence", "Now testing Left Drive");
    		
    		
    		//Left Drive Sequence (0 to 1.0, then back to 0. Then 0 to -1.0, then back to 0.
    		sequenceCreator(leftDrive);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Right Drive");
    		
    		//Right Drive Sequence (0 to 1.0, then back to 0. Then 0 to -1.0, then back to 0.
    		sequenceCreator(rightDrive);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Roller");
    		
    		//Roller Drive Sequence (0 to 1.0, then back to 0. Then 0 to -1.0, then back to 0.
    		sequenceCreator(roller);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Manipulator Arm");
    		
    		//Press Roller Up Switch
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Roller Up Switch");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Please press the Roller Up Switch");
    		while(rollerUpSwitch.get() == false){
    			Timer.delay(0.5);
    			SmartDashboard.putString("Test Sequence", "Please press the Roller Up Switch");
    		}
    		
    		//Press Roller Down Switch
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Roller Down Switch");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Please press the Roller Down Switch");
    		while(rollerDownSwitch.get() == false){
    			Timer.delay(0.5);
    			SmartDashboard.putString("Test Sequence", "Please press the Roller Down Switch");
    		}
    		
    		//Press Roller Ball Switch
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Success! Now testing Roller Ball Switch");
    		Timer.delay(0.5);
    		SmartDashboard.putString("Test Sequence", "Please press the Roller Ball Switch");
    		while(rollerBallSwitch.get() == false){
    			Timer.delay(0.5);
    			SmartDashboard.putString("Test Sequence", "Please press the Roller Ball Switch");
    		}
    		SmartDashboard.putString("Test Sequence", "<!> You have successfuly completed the Test Sequence <!>");
     	}
    } 
}