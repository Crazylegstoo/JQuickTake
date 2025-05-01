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
import java.io.*;
import javax.swing.filechooser.FileSystemView;
/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class COMTestGUI extends JQuickTakePanel implements ActionListener, KeyListener, Runnable
{

  JFrame       ivParentFrame;
 
  JLabel      ivPortLabel;
  JComboBox<String>	ivPortChoice;
 
  JLabel      ivSpeedLabel;
  JComboBox<String>	ivSpeedChoice;
  String[]	ivSpeedList = {"57600","38400","19200","9600"};

  JButton     ivConnect, ivPortRefresh;

  JLabel	ivStatus, ivDateLabel, ivDate, ivTimeLabel, ivTime, ivNameLabel, ivName, ivModelLabel, ivModel;

  JLabel	ivTakenLabel, ivTaken, ivRemainLabel, ivRemain, ivBatteryLabel, ivBattery, ivFlashLabel, ivFlash, ivQualityLabel, ivQuality;
  
  ImageIcon	ivRefresh;
  
  JDialog	ivProgress;
  
  JProgressBar	ivProgBar;

  Camera	ivCamera;

  DebugLog ivDebugLog;
  
  LockEventMgr	ivLockEventMgr;
  LockEvent		ivLockEvent;


//
// Build the GUI.
//

  public COMTestGUI()
  {

// Establish global parms

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");

	ivCamera = (Camera)Environment.getValue("Camera");
	
	ivLockEventMgr = (LockEventMgr)Environment.getValue("LockEventMgr");

	ivRefresh = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("Port-Refresh.png"));
	
	return;

  }

//
// Create the GUI
//

  public void createGUI()
  {
	String[] tvCOMPorts;
	
    ivPortLabel = new JLabel("Serial Ports:");
    this.add(ivPortLabel);
    ivPortLabel.setBounds(10,10,100,25);

	tvCOMPorts = ivCamera.getCOMPorts();	// Get a list of available COM (Serial) ports for selection
	
	if(tvCOMPorts != null && tvCOMPorts.length > 0)
	{
		ivPortChoice = new JComboBox<String>(tvCOMPorts);
	} else 
	{
		ivPortChoice = new JComboBox<String>();
	}
    this.add(ivPortChoice);
	ivPortChoice.setToolTipText("Serlect the Serial port which the camera is plugged into");
    ivPortChoice.setBounds(10,40,175,25);
	ivPortChoice.addActionListener(this);

	ivPortRefresh = new JButton();
	ivPortRefresh.setBackground(Color.white);
    this.add(ivPortRefresh);
	ivPortRefresh.setIcon(ivRefresh);
	ivPortRefresh.setToolTipText("Refesh dropdown list of Serial ports");
    ivPortRefresh.setBounds(190,40,25,25);
    ivPortRefresh.addActionListener(this);
	
    ivSpeedLabel = new JLabel("Port Speed (bps):");
    this.add(ivSpeedLabel);
    ivSpeedLabel.setBounds(245,10,150,25);
   
    ivSpeedChoice = new JComboBox<String>(ivSpeedList);
    this.add(ivSpeedChoice);
	ivSpeedChoice.setToolTipText("(Optional) Select port speed for the selected Serial port");
    ivSpeedChoice.setBounds(245,40,100,25);
	ivSpeedChoice.addActionListener(this);
   
    ivConnect = new JButton("Connect");
    this.add(ivConnect);
	ivConnect.setToolTipText("Connect to the camera based on Serial port and port speed");
    ivConnect.setBounds(370,40,150,25);
    ivConnect.addActionListener(this);

	ivStatus = new JLabel();
    this.add(ivStatus);
    ivStatus.setBounds(10,80,500,25);

	ivNameLabel = new JLabel("Camera Name:");
    this.add(ivNameLabel);
    ivNameLabel.setBounds(10,115,110,25);

	ivName = new JLabel();
	ivName.setBackground(Color.WHITE);
	ivName.setOpaque(true);
    this.add(ivName);
    ivName.setBounds(110,115,210,25);

	ivModelLabel = new JLabel("Camera Model:");
    this.add(ivModelLabel);
    ivModelLabel.setBounds(410,115,100,25);

	ivModel = new JLabel();
	ivModel.setBackground(Color.WHITE);
	ivModel.setOpaque(true);
	ivModel.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivModel);
    ivModel.setBounds(520,115,65,25);

	ivTakenLabel = new JLabel("Pictures Taken:");
    this.add(ivTakenLabel);
    ivTakenLabel.setBounds(10,150,105,25);

	ivTaken = new JLabel();
 	ivTaken.setBackground(Color.WHITE);
	ivTaken.setOpaque(true);
	ivTaken.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivTaken);
    ivTaken.setBounds(115,150,25,25);

	ivRemainLabel = new JLabel("Pictures Remaining:");
    this.add(ivRemainLabel);
    ivRemainLabel.setBounds(185,150,125,25);

	ivRemain = new JLabel();
 	ivRemain.setBackground(Color.WHITE);
	ivRemain.setOpaque(true);
	ivRemain.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivRemain);
    ivRemain.setBounds(315,150,25,25);

	ivBatteryLabel = new JLabel("Battery Level:");
    this.add(ivBatteryLabel);
    ivBatteryLabel.setBounds(410,150,90,25);

	ivBattery = new JLabel();
 	ivBattery.setBackground(Color.WHITE);
	ivBattery.setOpaque(true);
	ivBattery.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivBattery);
    ivBattery.setBounds(500,150,45,25);

	ivFlashLabel = new JLabel("Flash Mode:");
    this.add(ivFlashLabel);
    ivFlashLabel.setBounds(10,185,80,25);

	ivFlash = new JLabel();
 	ivFlash.setBackground(Color.WHITE);
	ivFlash.setOpaque(true);
	ivFlash.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivFlash);
    ivFlash.setBounds(90,185,55,25);

	ivQualityLabel = new JLabel("Picture Quality:");
    this.add(ivQualityLabel);
    ivQualityLabel.setBounds(185,185,95,25);

	ivQuality = new JLabel();
 	ivQuality.setBackground(Color.WHITE);
	ivQuality.setOpaque(true);
	ivQuality.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivQuality);
    ivQuality.setBounds(285,185,70,25);

	ivDateLabel = new JLabel("Camera Date/Time:");
    this.add(ivDateLabel);
    ivDateLabel.setBounds(410,185,130,25);

	ivDate = new JLabel();
 	ivDate.setBackground(Color.WHITE);
	ivDate.setOpaque(true);
	ivDate.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivDate);
    ivDate.setBounds(540,185,65,25);

	ivTime = new JLabel();
 	ivTime.setBackground(Color.WHITE);
	ivTime.setOpaque(true);
	ivTime.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(ivTime);
    ivTime.setBounds(615,185,65,25);
	
	Environment.setValue("COMPort",(String)ivPortChoice.getSelectedItem());
	Environment.setValue("PortSpeed",(String)ivSpeedChoice.getSelectedItem());
	
	ivDebugLog.textOut("Init COMPort: " + (String)ivPortChoice.getSelectedItem());
	ivDebugLog.textOut("Init Speed: " + (String)ivSpeedChoice.getSelectedItem());	
	
	  return;
  }

//
// Refresh GUI values
//

  public void refreshView()
  {
	ivName.setText(ivCamera.getName());
	
	ivModel.setText(ivCamera.getCameraModel());
	
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
	
	ivModel.setText("");
	
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
	
	String[]	tvCOMPorts;
	int			tvCount, tvDirSelect;
	
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

// Button has been pushed to refresh Serial Ports dropdown

    if(ae.getSource() == ivPortRefresh)
    {
		tvCOMPorts = ivCamera.getCOMPorts();	// Get a list of available COM (Serial) ports for selection
		
		ivPortChoice.removeAllItems();
			
		if(tvCOMPorts != null && tvCOMPorts.length > 0)
		{
			for (tvCount = 0; tvCount < tvCOMPorts.length; tvCount++)
			{
				ivPortChoice.addItem(tvCOMPorts[tvCount]);
			}
		}
	}

// Button has been pushed to connect to camera

    if(ae.getSource() == ivConnect)
    {
		ivProgress = new JDialog(ivParentFrame, "Connect to Camera");
		ivProgress.setBounds(200,275,500,75);
		ivProgress.setResizable(false);
		ivProgress.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		ivProgBar = new JProgressBar(0,100);
		ivProgBar.setIndeterminate(true);
		ivProgBar.setStringPainted(true);
		ivProgBar.setString("Attempting to connect to QuickTake Camera on " + (String)ivPortChoice.getSelectedItem());
		ivProgBar.setBounds(50,25,450,25);
		ivProgress.add(ivProgBar);
		
		synchronized(this){notifyAll();}
				
		ivProgress.setModal(true);
		ivProgress.setVisible(true);
    }
  
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

		ivLockEvent = new LockEvent(this,false);   // Lock other UI tabs while connect is attempted
		ivLockEventMgr.notifyListeners(ivLockEvent);
		
		if(ivCamera.getOpenStatus())
			ivCamera.closeCamera();

		ivCamera.openCamera();
				
		if(ivCamera.getOpenStatus())
		{
			this.testCamera();
			
		} else
		{

	// If unsuccessful in opening a comms session with the camera, display a message to the user about resolving the issue

			ivLockEvent = new LockEvent(this,false);             // Lock other UI tabs while connect is attempted
			ivLockEventMgr.notifyListeners(ivLockEvent);

			ivStatus.setForeground(Color.RED.darker());
			ivStatus.setText("Cannot detect a QuickTake camera to connect.");
			
			this.resetView();
			
			ivProgress.dispose();  // Kill the Progress pop-up
			
			JOptionPane.showMessageDialog(this,
						"Possible issues to confirm: \n \n01. Check that camera is plugged into your PC. \n \n" + 
						" 02. Check that camera is turned on. \n \n" +
						" 03. Select the correct Serial port for camera communications. \n \n" +
						" 04. Verify that Serial port is not in use by another application. \n \n",
						"Cannot connect to QuickTake camera on " + tvPortChoice,
						JOptionPane.ERROR_MESSAGE);	

		}
	} else
	{

// If there is no COM port chosen, it means the dropdown was not populated. This, in turn, means there are no
// COM ports defined on the user's computer
				
		this.resetView();

		ivStatus.setForeground(Color.RED.darker());
		ivStatus.setText("No Serial ports detected on this computer.");
				
		ivProgress.dispose();  // Kill the Progress pop-up
				
		JOptionPane.showMessageDialog(this,
					"No Serial ports detected on this computer",
					"Cannot connect with QuickTake camera on " + tvPortChoice,
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
		ivStatus.setForeground(Color.GREEN.darker());
		ivStatus.setText("Successful connection to QuickTake camera on " + (String)ivPortChoice.getSelectedItem() + " at " + (String)ivSpeedChoice.getSelectedItem() + " bps");
		
		this.refreshView();
				
		ivProgress.dispose();  // Kill the Progress pop-up

		ivLockEvent = new LockEvent(this,true);           // Unlock other UI tabs 
		ivLockEventMgr.notifyListeners(ivLockEvent);
		
	} else 
	{

		this.resetView();
	
		ivStatus.setForeground(Color.RED.darker());
		ivStatus.setText("Cannot ping QuickTake camera.");
				
		ivProgress.dispose();  // Kill the Progress pop-up
	
		JOptionPane.showMessageDialog(this,
					"Possible issues to confirm: \n \n01. Check that camera is plugged into your PC. \n \n" + 
					" 02. Check that camera is turned on. \n \n" +
					" 03. Select the correct COM port for camera communications. \n \n" +
					" 04. Verify that COM port is not in use by another application. \n \n",
					"Cannot communicate with QuickTake camera on " + (String)ivPortChoice.getSelectedItem(),
					JOptionPane.ERROR_MESSAGE);

		ivLockEvent = new LockEvent(this,false);           // Lock other UI tabs since connect falied
		ivLockEventMgr.notifyListeners(ivLockEvent);
		
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
