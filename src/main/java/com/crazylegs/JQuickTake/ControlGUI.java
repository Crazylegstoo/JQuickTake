/******************************************************************************
 *
 * ControlGUI.java
 *
 * Enable user to control/configure Camera. Notably, the GUI provides a 
 * functional mock-up of the Camera's physical interface.
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class ControlGUI extends JQuickTakePanel implements ActionListener, KeyListener, Runnable
{

  Frame       	ivParentFrame;
 
  Camera		ivCamera;
  
  JButton		ivFlash, ivQuality, ivDelete, ivTimer, ivTakeImage, ivSetDate, ivSetName;
  
  ImageIcon		ivFlashAuto, ivFlashOn, ivFlashOff, ivQualityHigh, ivQualityStandard, ivDeleteAll, ivTimerClock, ivTimerBlinking, ivTimerPending, ivBattery;
  ImageIcon		ivTimer5, ivTimer4, ivTimer3, ivTimer2, ivTimer1;
 
  JPanel		ivDisplay;
  
  JLabel		ivTakenCount, ivRemainCount, ivDateLabel, ivBatteryLevel, ivBatteryPic;
  
  JTextField	ivName;
  
  JDialog		ivProgress;
  
  JProgressBar	ivProgBar;
  
  DebugLog		ivDebugLog;
  
  boolean		ivTimerBlink, ivTakePicture, ivDeletePictures;
  
  ImageRoll		ivImageRoll;
  
  LockEventMgr	ivLockEventMgr;
  LockEvent		ivLockEvent;
  

  public ControlGUI()
  {

    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

	ivImageRoll = (ImageRoll)Environment.getValue("ImageRoll");
	ivCamera = (Camera)Environment.getValue("Camera");
	ivLockEventMgr = (LockEventMgr)Environment.getValue("LockEventMgr");
	
  }

//
// Build the GUI.
//
  
  public void createGUI()
  {

// Setup icons that (sort of) match the camera's LCD interface

	ivFlashAuto			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Flash-Auto.jpg"));
	ivFlashOn			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Flash-On.jpg"));
	ivFlashOff			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Flash-Off.jpg"));
	ivQualityStandard	= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Quality-Standard.jpg"));
	ivQualityHigh		= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Quality-High.jpg"));
	ivDeleteAll			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Delete-All.jpg"));
	ivTimerClock		= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-On.jpg"));
	ivTimerBlinking		= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-Blink.gif"));
	ivTimerPending		= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-Pending.jpg"));
	ivTimer5			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-5.jpg"));
	ivTimer4			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-4.jpg"));
	ivTimer3			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-3.jpg"));
	ivTimer2			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-2.jpg"));
	ivTimer1			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Timer-1.jpg"));
	ivBattery			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Battery.jpg"));

// Setup the camera control panel

	ivDisplay = new JPanel();
	ivDisplay.setLayout(null);
	this.add(ivDisplay);
	ivDisplay.setBounds(260,30,180,225);
	ivDisplay.setBackground(Color.white);

// Add Flash control

	ivFlash = new JButton();
	ivDisplay.add(ivFlash);
	ivFlash.setBackground(Color.white);
	ivFlash.setBounds(0,0,40,40);
    ivFlash.addActionListener(this);	

// Add Image Quality control

	ivQuality = new JButton();
	ivDisplay.add(ivQuality);
	ivQuality.setBackground(Color.white);
	ivQuality.setBounds(140,0,40,40);
    ivQuality.addActionListener(this);	

// Add Image Delete All control

	ivDelete = new JButton();
	ivDisplay.add(ivDelete);
	ivDelete.setBackground(Color.white);
	ivDelete.setBounds(0,160,40,40);
	ivDelete.setIcon(ivDeleteAll);
    ivDelete.addActionListener(this);	
	ivDeletePictures = false;

// Add Image Timer control

	ivTimer = new JButton();
	ivDisplay.add(ivTimer);
	ivTimer.setBackground(Color.white);
	ivTimer.setBounds(140,160,40,40);
	ivTimer.setIcon(ivTimerClock);
    ivTimer.addActionListener(this);	
	ivTimerBlink = false;

// Add button to take a picture

	ivTakeImage = new JButton("Take a Picture");
	ivDisplay.add(ivTakeImage);
	ivTakeImage.setBounds(0,200,180,25);
    ivTakeImage.addActionListener(this);	
	ivTakePicture = false;

// Add label for pics taken

	ivTakenCount = new JLabel("00",SwingConstants.CENTER);
	ivDisplay.add(ivTakenCount);
	ivTakenCount.setBounds(50,50,80,100);
	ivTakenCount.setFont(new Font("Dialog", Font.PLAIN, 70));

// Add label for pics remaining

	ivRemainCount = new JLabel("00",SwingConstants.CENTER);
	ivDisplay.add(ivRemainCount);
	ivRemainCount.setBounds(100,5,30,30);
	ivRemainCount.setFont(new Font("Dialog", Font.PLAIN, 20));

// Add label/button for camera date
	
	ivSetDate = new JButton("Set Camera to Computer Date/Time");
	this.add(ivSetDate);
	ivSetDate.setBounds(10,280,250,25);
    ivSetDate.addActionListener(this);	

	ivDateLabel = new JLabel("Camera Date/Time is");
	this.add(ivDateLabel);
	ivDateLabel.setBounds(280,280,350,25);

// Add label/button for camera name
	
	ivSetName = new JButton("Update Camera Name");
	this.add(ivSetName);
	ivSetName.setBounds(10,325,250,25);
    ivSetName.addActionListener(this);	

	ivName = new JTextField("Camera name is here");
	this.add(ivName);
	ivName.setBounds(280,325,250,25);
	
// Add icon/label for battery indicator
	
	ivBatteryPic = new JLabel(ivBattery);
	ivDisplay.add(ivBatteryPic);
	ivBatteryPic.setBounds(50,140,50,25);	

	ivBatteryLevel = new JLabel("0%");
	ivDisplay.add(ivBatteryLevel);
	ivBatteryLevel.setBounds(100,140,25,25);


  }
//
// Tab changed to this tab, so update any data that comes from the Camera
//

  public void refreshView()
  {

	switch(ivCamera.getFlash())    // Set Flash icon accordingly
	{
		case "Auto":
			ivFlash.setIcon(ivFlashAuto);
			break;
		case "Disabled":
			ivFlash.setIcon(ivFlashOff);
			break;
		case "Forced":
			ivFlash.setIcon(ivFlashOn);
			break;
		default:
			break;
	}

	if(!ivCamera.getQualityHigh())      // Set Quality icon accordingly
	{
		ivQuality.setIcon(ivQualityStandard);
	} else
	{
		ivQuality.setIcon(ivQualityHigh);
	}

    ivTakenCount.setText(ivCamera.getTaken());     // Update pictures taken

    ivRemainCount.setText(ivCamera.getRemaining());   // Update pictures remaining

	ivDateLabel.setText("Camera MM/DD/YY and HH:MM:SS is " + ivCamera.getDisplayDate() + "  " + ivCamera.getDisplayTime());   // Display camera date/time

	ivName.setText(ivCamera.getName());   // Display camera name

	ivBatteryLevel.setText(ivCamera.getBatteryLevel());    // Display battery level

	
	return;
  }

//
// Catch various button events
//

  public void actionPerformed(ActionEvent ae)
  {
	  
// Flash button - toggle it accordingly and update Camera

    if(ae.getSource() == ivFlash)
    {
		this.updateFlash();
	}
   
// Quality button - toggle it accordingly and update Camera

    if(ae.getSource() == ivQuality)
    {
		this.updateQuality();
	}
 
// Timer button - toggle to blinking or not blinking accordingly (blimking effect is an animated GIG)

    if(ae.getSource() == ivTimer)
    {
		if(ivTimerBlink)
		{
			ivTimerBlink = false;
			ivTimer.setIcon(ivTimerClock);
		} else
		{
			ivTimerBlink = true;
			ivTimer.setIcon(ivTimerBlinking);
		}
	}
  
// Take a Picture button - If Timer is blinking, there will be a 5-second timer before picture is taken. 
// But if timer is not blinking, take the picture immediately. In both cases, the Camera will be ultimately be told
// to take the picture.

    if(ae.getSource() == ivTakeImage)
    {
		this.prepareForPicture();
	}
  
// Delete Images - pop-up a warning, then tell Camera to delete all images it has. Note that Camera
// only has 'delete all' capability - no ability to delete a specific image

    if(ae.getSource() == ivDelete)
    {
		this.prepareToDelete();
	}
  
// Set date/time button - grab the System date/time and tell Camera to update itself

    if(ae.getSource() == ivSetDate)
    {
		this.updateDateTime();
	}
  
// Update Name button - read the new name from the GUI and tell Camera to update itself

    if(ae.getSource() == ivSetName)
    {
		this.updateName();
	}
 
  }

//
// Prepare to take a pciture
//

	public void prepareForPicture()
	{
		if(Integer.parseInt(ivCamera.getRemaining()) > 0)    // If no memory space left on camera, let user know - otherwise take a picture
		{

//			ivParent.unlockTabs(this,false);
			ivLockEvent = new LockEvent(this,false);            // Lock other UI tabs while picture is being taken
			ivLockEventMgr.notifyListeners(ivLockEvent);
		
			this.createProgress("Take a Picture", "Processing Picture...");   // Start up Progress pop-up
		
			if(ivTimerBlink)		// Fire up thread to take picture while modal Progress pop-up is displayed
			{
				synchronized(this){notifyAll();}
			
				ivProgress.setModal(true);
				ivProgress.setVisible(true);
				
				ivTimer.setIcon(ivTimerClock);
				
			} else
			{		
				ivTakePicture = true;
				synchronized(this){notifyAll();}
			
				ivProgress.setModal(true);
				ivProgress.setVisible(true);
			}
		} else
		{
			JOptionPane.showMessageDialog(ivDisplay,
					"Camera is full, so no more pictures can be taken!",
					"Take A Picture - Error",
					JOptionPane.ERROR_MESSAGE);	
		}
		
		return;
	}

//
// Prepare to delete all pictures
//

	public void prepareToDelete()
	{
		int			tvDeleteInput;

		if(Integer.parseInt(ivCamera.getTaken()) > 0)     // If no pictures on camera, then let user know - otherwise proceed to delete
		{
			tvDeleteInput = JOptionPane.showConfirmDialog(ivDisplay,
										"All pictures on the camera will be erased if you proceed!",
										"Delete All - Confirmation",
										JOptionPane.OK_CANCEL_OPTION);
										
			if(tvDeleteInput == JOptionPane.OK_OPTION)    // If user conforms delete, then delete pictures on a thread while Progress pop-up displayed
			{
		
//				ivParent.unlockTabs(this,false);
				ivLockEvent = new LockEvent(this,false);         // Lock other UI tabs while pictures are being deleted
				ivLockEventMgr.notifyListeners(ivLockEvent);
				
				this.createProgress("Erase All Pictures", "Pictures are being erased...");
			
				ivDeletePictures = true;
				synchronized(this){notifyAll();}
				
				ivProgress.setModal(true);
				ivProgress.setVisible(true);			}
		} else
		{
				JOptionPane.showMessageDialog(ivDisplay,
						"There are currently no pictures to erase from the camera.",
						"Delete All - Error",
						JOptionPane.ERROR_MESSAGE);
		}
		
		return;
	}

//
// Save a selected image or save all images
//

	public void run()
	{
        
// wait for the signal from the GUI

       while(true)
	   {
			try{synchronized(this){wait();}}
            catch (InterruptedException e){}

// If Timer has been selected,m display a 5-second count-down and then take a picture

			while(ivTimerBlink)
			{
				ivTimer.setIcon(ivTimer5);
				try {
					Thread.sleep(1000);
				} catch (Exception e) { e.printStackTrace(); } 	

				ivTimer.setIcon(ivTimer4);
				try {
					Thread.sleep(1000);
				} catch (Exception e) { e.printStackTrace(); } 	
				
				ivTimer.setIcon(ivTimer3);
				try {
					Thread.sleep(1000);
				} catch (Exception e) { e.printStackTrace(); } 	
				
				ivTimer.setIcon(ivTimer2);
				try {
					Thread.sleep(1000);
				} catch (Exception e) { e.printStackTrace(); } 	
				
				ivTimer.setIcon(ivTimer1);
				try {
					Thread.sleep(1000);
				} catch (Exception e) { e.printStackTrace(); } 	
				
				ivTimer.setIcon(ivTimerPending);
				
				this.takePicture();	
				
				ivProgress.dispose();  // Kill the Progress pop-up

				ivLockEvent = new LockEvent(this,true);            // Unlock other UI tabs once picture is taken
				ivLockEventMgr.notifyListeners(ivLockEvent);
				
				ivTimerBlink = false;
			}

// Take a picture immediately

			while(ivTakePicture)
			{
				this.takePicture();	
				
				ivProgress.dispose();  // Kill the Progress pop-up

//				ivParent.unlockTabs(this,true);
				ivLockEvent = new LockEvent(this,true);            // Unlock other UI tabs once picture is taken
				ivLockEventMgr.notifyListeners(ivLockEvent);	
				
				ivTakePicture = false;
			}

// Delete all pictures
			
			while(ivDeletePictures)
			{

				this.deletePictures();	
				
				ivProgress.dispose();  // Kill the Progress pop-up

//				ivParent.unlockTabs(this,true);
				ivLockEvent = new LockEvent(this,true);            // Unlock other UI tabs once pictures are deleted
				ivLockEventMgr.notifyListeners(ivLockEvent);
				
				ivDeletePictures = false;
			}
			
			
		}
	
   }

//
// Tell the Camera to take a picture, and then refresh GUI pictures taken/remaining
//

	public void takePicture()
	{
		ivCamera.takePicture();
				
		ivTakenCount.setText(ivCamera.getTaken());

		ivRemainCount.setText(ivCamera.getRemaining());    
	
		ivBatteryLevel.setText(ivCamera.getBatteryLevel());
			
		try {
			Thread.sleep(2000);
		} catch (Exception e) { e.printStackTrace(); } 

		
		return;
	}

//
// Tell the Camera to delete all pictures, and then refresh GUI pictures taken/remaining
//

	public void deletePictures()
	{
		ivDebugLog.textOut(this,"ABOUT TO DELETE IMAGES");
		
		ivImageRoll.deleteImages();
		
		ivTakenCount.setText(ivCamera.getTaken());

		ivRemainCount.setText(ivCamera.getRemaining());

		ivBatteryLevel.setText(ivCamera.getBatteryLevel());	
		
		return;
	}


//
// Create 'progress' dialog that simply presents a message while a progress bar animates
//

	public void createProgress(String title, String message)
	{

		ivProgress = new JDialog(ivParentFrame, title);
		ivProgress.setBounds(200,275,400,75);
		ivProgress.setResizable(false);
		ivProgress.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		ivProgBar = new JProgressBar(0,100);
		ivProgBar.setIndeterminate(true);
		ivProgBar.setStringPainted(true);
		ivProgBar.setString(message);
		ivProgBar.setBounds(50,25,300,25);
		ivProgress.add(ivProgBar);

		return;
	}

//
// Update Flash setting
//

	public void updateFlash()
	{
		int		tvFlashMode;
		
		if(ivFlash.getIcon() == ivFlashAuto)
		{
			ivFlash.setIcon(ivFlashOn);
			tvFlashMode = 2;
		} else
		{
			if(ivFlash.getIcon() == ivFlashOn)
			{
				ivFlash.setIcon(ivFlashOff);
				tvFlashMode = 1;
			} else
			{
				ivFlash.setIcon(ivFlashAuto);
				tvFlashMode = 0;
			}
		}
		
		ivCamera.updateFlashMode(tvFlashMode);

		ivBatteryLevel.setText(ivCamera.getBatteryLevel());
		
		return;
	}

//
// Update Quality setting
//

	public void updateQuality()
	{
		boolean				tvQualityHigh;

		tvQualityHigh = false;
		
		if(ivQuality.getIcon() == ivQualityHigh)
		{
			ivQuality.setIcon(ivQualityStandard);
		} else
		{
			ivQuality.setIcon(ivQualityHigh);
			tvQualityHigh = true;
		}
 		
		ivCamera.updateQualityMode(tvQualityHigh);
   
		ivRemainCount.setText(ivCamera.getRemaining());		

		ivBatteryLevel.setText(ivCamera.getBatteryLevel());
		
		return;
	}

//
// Update Date/Time setting
//

	public void updateDateTime()
	{
		byte[]				tvDateTimeArr;
		LocalDateTime		tvDateTime;

		tvDateTime = LocalDateTime.now();
		
		tvDateTimeArr = new byte[6];
		
		tvDateTimeArr[0] = (byte)tvDateTime.getMonthValue();
		tvDateTimeArr[1] = (byte)tvDateTime.getDayOfMonth();
		tvDateTimeArr[2] = (byte)(tvDateTime.getYear() % 100);
		tvDateTimeArr[3] = (byte)tvDateTime.getHour();
		tvDateTimeArr[4] = (byte)tvDateTime.getMinute();
		tvDateTimeArr[5] = (byte)tvDateTime.getSecond();

		ivDebugLog.hexOut(this,"LOCAL DATETIME ",tvDateTimeArr);
		
		ivCamera.updateDateTime(tvDateTimeArr);

		ivBatteryLevel.setText(ivCamera.getBatteryLevel());   // Refresh battery level displayed

		ivDateLabel.setText("Camera MM/DD/YY and HH:MM:SS is  " + ivCamera.getDisplayDate() + "  " + ivCamera.getDisplayTime());  // Refresh GUI with new date/time
		
		JOptionPane.showMessageDialog(ivDisplay,
							"Camera Date/Time have been set to Computer Date/Time",
							"Set Camera Date and Time",
							JOptionPane.INFORMATION_MESSAGE);
							
		return;
	}

//
// Update Name setting
//

	public void updateName()
	{
		byte[]		tvNameBytes;
		String 		tvName;

		tvName = ivName.getText().trim();
		
		tvNameBytes = tvName.getBytes();
		
		ivDebugLog.hexOut(this,"Name text area: ", tvNameBytes);
		
		ivDebugLog.textOut(this,"Name text area: " + tvName);
		
		if(tvNameBytes.length > 32)
		{
		
			JOptionPane.showMessageDialog(ivDisplay,
									"New " + tvNameBytes.length + " chanacter name exceeds 32 character maximum.",
									"Camera Name Too Long",
									JOptionPane.ERROR_MESSAGE);
									
		} else
		{

			ivCamera.updateCameraName(tvNameBytes);

			ivBatteryLevel.setText(ivCamera.getBatteryLevel());  // Refresh battery level displayed
		
			JOptionPane.showMessageDialog(ivDisplay,
									"Camera Name has been updated!",
									"Update Camera Name",
									JOptionPane.INFORMATION_MESSAGE);
		}
		
		return;
	}

//
// Catch key events
//
  
  public void keyPressed(KeyEvent ke)
  {
    if(ke.getKeyCode() == KeyEvent.VK_ENTER)
    {
    }
  }
  
  public void keyTyped(KeyEvent ke)
  {
  }
  
  public void keyReleased(KeyEvent ke)
  {
  }
  

}
