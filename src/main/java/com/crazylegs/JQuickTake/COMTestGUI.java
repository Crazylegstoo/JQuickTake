/******************************************************************************
 *
 * COMTestGUI.java
 *
 * Connect to the camera on a specified COM port at a specified speed. Once 
 * connected, this class will display metadata pulled from the camera, such 
 * as pictures taken, pictures remaining, flash mode, quality mode, etc.
 *
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class COMTestGUI extends JQuickTakePanel implements ActionListener, KeyListener, Runnable
{

  JQuickTake     ivParent;
  JFrame       ivParentFrame;
 
  JLabel      ivPortLabel;
  JComboBox<String>	ivPortChoice;
 
  JLabel      ivSpeedLabel;
  JComboBox<String>	ivSpeedChoice;
  String[]	ivSpeedList = {"57600","38400","19200","9600"};

  JButton     ivConnect;

  JLabel	ivStatus, ivDateLabel, ivDate, ivTimeLabel, ivTime, ivNameLabel, ivName;

  JLabel	ivTakenLabel, ivTaken, ivRemainLabel, ivRemain, ivBatteryLabel, ivBattery, ivFlashLabel, ivFlash, ivQualityLabel, ivQuality;
  
  JDialog	ivProgress;
  
  JProgressBar	ivProgBar;

  Camera	ivCamera;

  DebugLog ivDebugLog;


//
// Build the GUI.
//

  public COMTestGUI()
  {

// Establish global parms

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

    ivParent      = (JQuickTake)Environment.getValue("Parent");
    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");

	ivCamera = (Camera)Environment.getValue("Camera");

	return;

  }

//
// Create the GUI
//

  public void createGUI()
  {
	String[] tvCOMPorts;
	
    ivPortLabel = new JLabel("COM Ports:");
    this.add(ivPortLabel);
    ivPortLabel.setBounds(10,10,100,25);

	tvCOMPorts = ivCamera.getCOMPorts();	// Get a list of available COM (serial) ports for selection
	
	ivDebugLog.textOut(this,"tvComports: " + tvCOMPorts);
	if(tvCOMPorts != null && tvCOMPorts.length > 0)
	{
		ivPortChoice = new JComboBox<String>(tvCOMPorts);
	} else 
	{
		ivPortChoice = new JComboBox<String>();
	}
    this.add(ivPortChoice);
    ivPortChoice.setBounds(10,40,100,25);
	ivPortChoice.addActionListener(this);

    ivSpeedLabel = new JLabel("Port Speed (bps):");
    this.add(ivSpeedLabel);
    ivSpeedLabel.setBounds(145,10,100,25);
   
    ivSpeedChoice = new JComboBox<String>(ivSpeedList);
    this.add(ivSpeedChoice);
    ivSpeedChoice.setBounds(145,40,100,25);
	ivSpeedChoice.addActionListener(this);
   
    ivConnect = new JButton("Connect");
    this.add(ivConnect);
    ivConnect.setBounds(270,40,150,25);
    ivConnect.addActionListener(this);

	ivStatus = new JLabel();
	ivStatus.setForeground(Color.RED);
    this.add(ivStatus);
    ivStatus.setBounds(10,80,500,25);

	ivNameLabel = new JLabel("Camera Name:");
    this.add(ivNameLabel);
    ivNameLabel.setBounds(10,115,90,25);

	ivName = new JLabel();
	ivName.setBackground(Color.WHITE);
	ivName.setOpaque(true);
    this.add(ivName);
    ivName.setBounds(100,115,210,25);

	ivDateLabel = new JLabel("Camera Date/Time:");
    this.add(ivDateLabel);
    ivDateLabel.setBounds(355,115,120,25);

	ivDate = new JLabel();
 	ivDate.setBackground(Color.WHITE);
	ivDate.setOpaque(true);
    this.add(ivDate);
    ivDate.setBounds(475,115,50,25);

	ivTime = new JLabel();
 	ivTime.setBackground(Color.WHITE);
	ivTime.setOpaque(true);
    this.add(ivTime);
    ivTime.setBounds(535,115,50,25);

	ivTakenLabel = new JLabel("Pictures Taken:");
    this.add(ivTakenLabel);
    ivTakenLabel.setBounds(10,150,105,25);

	ivTaken = new JLabel();
 	ivTaken.setBackground(Color.WHITE);
	ivTaken.setOpaque(true);
    this.add(ivTaken);
    ivTaken.setBounds(110,150,25,25);

	ivRemainLabel = new JLabel("Pictures Remaining:");
    this.add(ivRemainLabel);
    ivRemainLabel.setBounds(180,150,125,25);

	ivRemain = new JLabel();
 	ivRemain.setBackground(Color.WHITE);
	ivRemain.setOpaque(true);
    this.add(ivRemain);
    ivRemain.setBounds(305,150,25,25);

	ivBatteryLabel = new JLabel("Battery Level:");
    this.add(ivBatteryLabel);
    ivBatteryLabel.setBounds(355,150,90,25);

	ivBattery = new JLabel();
 	ivBattery.setBackground(Color.WHITE);
	ivBattery.setOpaque(true);
    this.add(ivBattery);
    ivBattery.setBounds(445,150,35,25);

	ivFlashLabel = new JLabel("Flash Mode:");
    this.add(ivFlashLabel);
    ivFlashLabel.setBounds(10,185,80,25);

	ivFlash = new JLabel();
 	ivFlash.setBackground(Color.WHITE);
	ivFlash.setOpaque(true);
    this.add(ivFlash);
    ivFlash.setBounds(85,185,55,25);

	ivQualityLabel = new JLabel("Picture Quality:");
    this.add(ivQualityLabel);
    ivQualityLabel.setBounds(180,185,95,25);

	ivQuality = new JLabel();
 	ivQuality.setBackground(Color.WHITE);
	ivQuality.setOpaque(true);
    this.add(ivQuality);
    ivQuality.setBounds(275,185,100,25);

	Environment.setValue("COMPort",(String)ivPortChoice.getSelectedItem());
	Environment.setValue("PortSpeed",(String)ivSpeedChoice.getSelectedItem());
	
	ivDebugLog.textOut(this,"Init COMPort: " + (String)ivPortChoice.getSelectedItem());
	ivDebugLog.textOut(this,"Init Speed: " + (String)ivSpeedChoice.getSelectedItem());	
	
	  return;
  }

//
// Refresh GUI values
//

  public void refreshView()
  {
	ivName.setText(ivCamera.getName());
	
	ivDate.setText(ivCamera.getDisplayDate());
	ivTime.setText(ivCamera.getDisplayTime());
	
	ivTaken.setText(ivCamera.getTaken());
	ivRemain.setText(ivCamera.getRemaining());

	ivBattery.setText(ivCamera.getBatteryLevel());
	
	ivFlash.setText(ivCamera.getFlash());
	ivQuality.setText(ivCamera.getQuality());
	
	return;
  }

//
// Blank GUI values
//

  public void resetView()
  {
	ivName.setText("");
	
	ivDate.setText("");
	ivTime.setText("");
	
	ivTaken.setText("");
	ivRemain.setText("");

	ivBattery.setText("");
	
	ivFlash.setText("");
	ivQuality.setText("");
	
	return;
  }

//
// Catch various button events
//

  public void actionPerformed(ActionEvent ae)
  {
	String tvPortChoice, tvSpeedChoice;
	
//  Save selected COM Port

    if(ae.getSource() == ivPortChoice)

    {
		tvPortChoice = (String)ivPortChoice.getSelectedItem();
		Environment.setValue("COMPort",(String)tvPortChoice);
	}

//  Save selected Port Speed

    if(ae.getSource() == ivSpeedChoice)
    {
		tvSpeedChoice = (String)ivSpeedChoice.getSelectedItem();
		Environment.setValue("PortSpeed",(String)tvSpeedChoice);
	}

// Button has been pushed to connect to camera

    if(ae.getSource() == ivConnect)
    {
		ivProgress = new JDialog(ivParentFrame, "Connect to Camera");
		ivProgress.setBounds(200,275,400,75);
		ivProgress.setResizable(false);
		ivProgress.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		ivProgBar = new JProgressBar(0,100);
		ivProgBar.setIndeterminate(true);
		ivProgBar.setStringPainted(true);
		ivProgBar.setString("Attempting to connect to QuickTake Camera on " + (String)ivPortChoice.getSelectedItem());
		ivProgBar.setBounds(50,25,300,25);
		ivProgress.add(ivProgBar);
		
		synchronized(this){notifyAll();}
				
		ivProgress.setModal(true);
		ivProgress.setVisible(true);
    }
   	
  ivDebugLog.textOut(this,"Final COMPort: " + (String)ivPortChoice.getSelectedItem());
  ivDebugLog.textOut(this,"Final Speed: " + (String)ivSpeedChoice.getSelectedItem());
  
  return;
  
  }

//
// Save a selected image or save all images - all on a thread.
//

	public void run()
	{
       
// wait for the signal from the GUI

        while(true){
            try{synchronized(this){wait();}}
            catch (InterruptedException e){}
	
// Connect to the camera
		
			this.connectCamera();

		}
			
   }

//
// Connect to Camera
//
  private void connectCamera()
  {
	String tvPortChoice, tvSpeedChoice;

	tvPortChoice = (String)ivPortChoice.getSelectedItem();		
	tvSpeedChoice = (String)ivSpeedChoice.getSelectedItem();

// If the user has selected a COM port, save it (and the selected Speed) in Environment. Then flash a message
// to let the user know there will be an attempt to establish comms with the camera
	
	if(tvPortChoice != null)
	{
		ivParent.unlockTabs(this,false);
		
		if(ivCamera.getOpenStatus())
			ivCamera.closeCamera();

		ivCamera.openCamera();
				
		if(ivCamera.getOpenStatus())
		{
			this.testCamera();
			
		} else
		{

	// If unsuccessful in opening a comms session with the camera, display a message to the user about resolving the issue

			ivDebugLog.textOut(this,"Camera will not open");
			
			ivParent.unlockTabs(this,false);

			ivStatus.setText("Cannot connect to camera.");
			
			this.resetView();
			
			ivProgress.dispose();  // Kill the Progress pop-up
			
			JOptionPane.showMessageDialog(this,
						"Possible issues to confirm: \n \n01. Check that camera is plugged into your PC. \n \n" + 
						" 02. Check that camera is turned on. \n \n" +
						" 03. Select the correct COM port for camera communications. \n \n" +
						" 04. Verify that COM port is not in use by another application. \n \n",
						"Cannot connect to QuickTake Camera on " + tvPortChoice,
						JOptionPane.ERROR_MESSAGE);	

		}
	} else
	{

// If there is no COM port chosen, it means the dropdown was not populated. This, in turn, means there are no
// COM ports defined on the user's computer
				
		this.resetView();

		ivStatus.setText("No COM ports on this computer.");
				
		ivProgress.dispose();  // Kill the Progress pop-up
				
		JOptionPane.showMessageDialog(this,
					"No COM (Serial) Ports detected on this computer",
					"Cannot connect with QuickTake Camera on " + tvPortChoice,
					JOptionPane.ERROR_MESSAGE);	
	}	  
	
	return;
  }

//
// Test the camera connection by issuing a Ping to the camera. If successsful, display camera metadata and unlock the GUI
//

  private void testCamera()
  {
		
	if(ivCamera.pingCamera())
	{
		ivStatus.setText("Successful connection to QuickTake Camera on " + (String)ivPortChoice.getSelectedItem() + " at " + (String)ivSpeedChoice.getSelectedItem() + " bps");
		
		this.refreshView();
				
		ivProgress.dispose();  // Kill the Progress pop-up
		
		ivParent.unlockTabs(this,true);
		
	} else 
	{
		ivDebugLog.textOut(this,"Cannot ping camera");
				
		this.resetView();
	
		ivStatus.setText("Cannot ping camera.");
				
		ivProgress.dispose();  // Kill the Progress pop-up
	
		JOptionPane.showMessageDialog(this,
					"Possible issues to confirm: \n \n01. Check that camera is plugged into your PC. \n \n" + 
					" 02. Check that camera is turned on. \n \n" +
					" 03. Select the correct COM port for camera communications. \n \n" +
					" 04. Verify that COM port is not in use by another application. \n \n",
					"Cannot communicate with QuickTake Camera on " + (String)ivPortChoice.getSelectedItem(),
					JOptionPane.ERROR_MESSAGE);
		
		ivParent.unlockTabs(this,false);
		
		ivCamera.closeCamera();
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
	
	return;
  }
  
  public void keyTyped(KeyEvent ke)
  {
	  return;
  }
  
  public void keyReleased(KeyEvent ke)
  {
	  return;
  }
  

}
