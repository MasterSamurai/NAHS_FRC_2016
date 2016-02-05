//Defines stuff
package org.usfirst.frc.team3694.robot;

//Import stuff here
import java.awt.event.KeyEvent;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;
//ROBOT CODE FROM THIS POINT ON

public class Robot extends SampleRobot {
	//Camera and Object-Tracking Objects and Variables
	public static USBCamera camera0;
	
	//SmartDashboard Objects and Variables
	public static int number;
	public static KeyEvent event;
	
	//Drive and Chassis Objects and Variables
	public static RobotDrive chassis;
	public static final Victor frontDrive = new Victor(0);
	public static final Victor rearDrive = new Victor(1);
	public static final Victor roller = new Victor(3);
	public static final Victor rollerTilt = new Victor(4);
	
	//Joystick Objects and Variables
	public static final Joystick driveStick = new Joystick(0);
	public static final Joystick shootStick = new Joystick(1);
	public static double shootY;

//ROBOT INIZILIZATION
    public void robotInit() {
        //Stream and Adjust Image
        CameraServer.getInstance().startAutomaticCapture("cam0");
        camera0.setFPS(10);
       	
    	//Invert Chassis Motors so that they roll in the right direction
    	frontDrive.setInverted(true);
    	rearDrive.setInverted(true);
    	
    	//Configure motors present in Chassis, configure safety
    	chassis = new RobotDrive(frontDrive, rearDrive);
    	chassis.setExpiration(0.1);
    }
//Detect number input from Driver's Station
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            number = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 3;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 4;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 5;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 6;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 7;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 8;
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            number = 9;
        }
       
    }
  //WHEN DISABLED    
    public void disabled(){
    	keyTyped(event);
        //Initialize SmartDashboard Autonomous Defense Chooser with Options
    	if(number == 0){
    		SmartDashboard.putString("Nothing (0)", "nothing");
    	}else{
    		SmartDashboard.putString("Nothing 0", "nothing");
    	}
    	if(number == 1){
        	SmartDashboard.putString("Lowbar (1)", "lowbar");
    	}else{
        	SmartDashboard.putString("Lowbar 1", "lowbar");
    	}
    	if(number == 2){
        	SmartDashboard.putString("Portcullis (2)" ,"portcullis");
    	}else{
        	SmartDashboard.putString("Portcullis 2" ,"portcullis");
    	}
    	if(number == 3){
        	SmartDashboard.putString("Cheval de Frise (3)" ,"cheval");
    	}else{
        	SmartDashboard.putString("Cheval de Frise 3" ,"cheval");
    	}
    	if(number == 4){
        	SmartDashboard.putString("Moat (4)","moat");
    	}else{
        	SmartDashboard.putString("Moat 4","moat");
    	}
    	if(number == 5){
        	SmartDashboard.putString("Ramparts (5)" ,"ramparts");
    	}else{
        	SmartDashboard.putString("Ramparts 5" ,"ramparts");
    	}
    	if(number == 6){
    		SmartDashboard.putString("Drawbridge (6)" ,"drawbridge");
    	}else{
    		SmartDashboard.putString("Drawbridge 6" ,"drawbridge");
    	}
    	if(number == 7){
    		SmartDashboard.putString("Sally Port (7)" ,"sallyport");
    	}else{
    		SmartDashboard.putString("Sally Port 7" ,"sallyport");
    	}
    	if(number == 8){
        	SmartDashboard.putString("Rock Wall (8)" ,"rockwall");
    	}else{
        	SmartDashboard.putString("Rock Wall 8" ,"rockwall");
    	}
    	if(number == 9){
        	SmartDashboard.putString("Rough Terrain (9)" ,"roughterrain");
    	}else{
        	SmartDashboard.putString("Rough Terrain 9" ,"roughterrain");
    	}
    }
//AUTONOMOUS
    public void autonomous() { 	
    	//Start of loops to figure out which defense selected
    	if(number == 1){
    		//Lowbar autonomous
    		chassis.setSafetyEnabled(false);
    		
    		//Drive forward for two seconds at half speed, the stop
    	    chassis.drive(0.5, 0.0);	
    	    Timer.delay(2.0);
    	    chassis.drive(0.0, 0.0);		
    	}else if(number == 2){
    		//Portcullis autonomous
    	}else if(number == 3){
    		//Cheval de Frise autonomous
    	}else if(number == 4){
    		//Moat autonomous
    	}else if(number == 5){
    		//Rampart autonomous
    	}else if(number == 6){
    		//Drawbridge autonomous
    	}else if(number == 7){
    		//Sallyport autonomous
    	}else if(number == 8){
    		//Rock Wall autonomous
    	}else if(number == 9){
    		//Rough Terrain autonomous
    	}else{
    		//If none of the above are the selected defense, then there is an error
    		SmartDashboard.putString("Error", "No defenses match");
    	}
    }
    		
//TELEOPERATED
    public void operatorControl() {
    	//Enable Chassis Safety
    	chassis.setSafetyEnabled(true);
        
    	//While in Teleoperated mode.
        while (isOperatorControl() && isEnabled()) {
            //Slight delay required
        	Timer.delay(0.005);
            
        	//Drive chassis using Arcade Drive (One Joystick)
            chassis.arcadeDrive(driveStick);
            
            //Set the roller's tilt to be equal to the Shooting Joystick's Y
            shootY = shootStick.getAxis(Joystick.AxisType.kY);
            rollerTilt.set(shootY);
        }
    }
}
//END BRACKET, ROBOT CODE ENDS