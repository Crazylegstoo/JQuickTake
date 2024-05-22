/******************************************************************************
 *
 * QuickTake.java
 *
 * This is main app class that creates a tabbed interface with each tab
 * containing a group of camera functions, including:
 *
 * COMs - Connect to camera and display metadata from camera
 * Images - Save images from the camera as Apple QTK files
 * Control - 'Remote control' the camera and update various settings
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class JQuickTake extends WindowAdapter implements WindowListener, ChangeListener, LockListener
{

  JFrame       ivAppWin;
  Container    ivFrameContent;
  JTabbedPane  ivQTPane;
  JPanel       ivAppPanel;
  
  ImageIcon		ivIcon;

  COMTestGUI	ivCOMTest;
  ControlGUI	ivControl;
  ImageGUI		ivImage;
  
  HashMap<String, Object>      ivMap;
  int          ivLastTab;
  
  Camera		ivCamera;
  
  ImageRoll		ivImageRoll;
  
  LockEventMgr	ivLockEventMgr;
  
  DebugLog ivDebugLog;


//
// Build the GUI.
//

  public JQuickTake(boolean debug)
  {

//  Init environment

    ivMap = new HashMap<String, Object>(25);
    
    Environment.init(ivMap);
    
    Environment.setValue("Parent",this);

//  Initialize Debugging
 
    ivDebugLog = new DebugLog();
	Environment.setValue("DebugLog",ivDebugLog);
    Environment.setValue("DebugMode",debug);
	
	ivDebugLog.textOut(this,"DEBUG MODE: " + (boolean)Environment.getValue("DebugMode"));

// Create a Camera instance

    ivCamera = new Camera();
	
	Environment.setValue("Camera", ivCamera);

// Create an Image Roll instance (will manage images taken off camera)

    ivImageRoll = new ImageRoll();
	
	Environment.setValue("ImageRoll", ivImageRoll);

// Create an LockEventManager instance (will manage lock/unlock of UI tabs)

    ivLockEventMgr = new LockEventMgr();
	
	ivLockEventMgr.addListener(this);
	
	Environment.setValue("LockEventMgr", ivLockEventMgr);

//    Build the main UI

    ivAppWin = new JFrame("QuickTake Camera Manager");
	ivAppWin.setResizable(true);
	ivAppWin.pack();
    ivAppWin.addWindowListener(this);
    Environment.setValue("ParentFrame", ivAppWin);
	
	ivIcon			= new ImageIcon(ClassLoader.getSystemClassLoader().getResource("JQuickTake.jpg"));
	ivAppWin.setIconImage(ivIcon.getImage());

    ivAppPanel = new JPanel();
    ivAppPanel.setLayout(null);
    ivAppWin.getContentPane().add(ivAppPanel);
    ivAppPanel.setBounds(1,1,710,430);

    ivQTPane = new JTabbedPane();
    ivAppPanel.add(ivQTPane);
    ivQTPane.setBounds(1,1,700,400);

// Create GUI for connecting to the camera

    ivCOMTest  	= new COMTestGUI();
    new Thread(ivCOMTest).start();
    javax.swing.SwingUtilities.invokeLater(new Runnable()
	{
		public void run() 
		{
			ivCOMTest.createGUI();
        }
    });

// Create GUI for saving images off camera. Make it runnable for thread execution

    ivImage	 	= new ImageGUI();
    new Thread(ivImage).start();
    javax.swing.SwingUtilities.invokeLater(new Runnable()
	{
		public void run() 
		{
			ivImage.createGUI();
        }
    });

// Create GUI for 'remote cpontrol' of the camera. Make it runnable for thread execution
	
    ivControl   = new ControlGUI();
    new Thread(ivControl).start();
    javax.swing.SwingUtilities.invokeLater(new Runnable()
	{
		public void run() 
		{
			ivControl.createGUI();
        }
    });

// Add each GUI to a tab on the main window

    ivQTPane.addTab("Connect to Camera",null,ivCOMTest,"Connect to the camera");
    ivQTPane.setSelectedIndex(0);
    ivLastTab = 0;
    ivQTPane.setBackgroundAt(0,Color.green);
    ivQTPane.addTab("Save Pictures",null,ivImage,"Save pictures to computer");
    ivQTPane.addTab("Control Camera",null,ivControl,"Control and configure camera");

// Lock all tabs except Connect to ensure that no functions can be accessed until camera connection is established

	ivQTPane.setEnabledAt(1,false);
	ivQTPane.setEnabledAt(2,false);
 
    ivQTPane.addChangeListener(this);

//  Size and show the window

    ivAppWin.setBounds(50,100,710,430);
    ivAppWin.setResizable(false);
    ivAppWin.setVisible(true);

  }

//
// Catch window when it closes
//

  public void windowClosing(WindowEvent we)
  {
	ivCamera.closeCamera();
    System.exit(0);
  }

//
// Listen for Tabbed Pane change events
//
  public void stateChanged(ChangeEvent ce)
  {
    JQuickTakePanel  tvComp;
          
    ivQTPane.setBackgroundAt(ivLastTab,null);
    ivLastTab = ivQTPane.getSelectedIndex();
    tvComp = (JQuickTakePanel)ivQTPane.getComponentAt(ivLastTab);
    tvComp.refreshView();
    ivQTPane.setBackgroundAt(ivLastTab,Color.green);
	
  }

//
// Force repaint
//
  public void refresh()
  {
    ivAppWin.update(ivAppWin.getGraphics());
  }

//
// Return frame handle
//
  public JFrame getFrame()
  {
    return ivAppWin;
  }

//
// Listen for Lock events to determine which tabs to lock/unlock
//
  public void handleLockEvent(LockEvent e)
  {
	boolean	tvStatus;
	Object	tvOrigin;
	  
	tvStatus = e.getStatus();
	tvOrigin = e.getOrigin();

	if(tvOrigin instanceof COMTestGUI)
	{
		ivQTPane.setEnabledAt(1,tvStatus);
		ivQTPane.setEnabledAt(2,tvStatus);
	}
 
	if(tvOrigin instanceof ImageGUI)
	{
		ivQTPane.setEnabledAt(0,tvStatus);
		ivQTPane.setEnabledAt(2,tvStatus);
	}

	if(tvOrigin instanceof ControlGUI)
	{
		ivQTPane.setEnabledAt(0,tvStatus);
		ivQTPane.setEnabledAt(1,tvStatus);
	}
	return;
 }

//
// Run from the command line. There is an optional Debug parm that will cause messages to
// be written to the console for debugging purposes
//
  public static void main(String[] args)
  {
	boolean tvDebug;
	
	tvDebug = false;
	
	if (args.length > 0 && args[0].equals("debug"))
		tvDebug = true;

	JQuickTake client = new JQuickTake(tvDebug);
  }

}
