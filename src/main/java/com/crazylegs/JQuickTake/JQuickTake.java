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
import java.net.*;
import java.nio.file.*;
/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class JQuickTake extends WindowAdapter implements WindowListener, ChangeListener, LockListener, ActionListener
{

  JFrame       ivAppWin;
  JTabbedPane  ivQTPane;
  JPanel       ivAppPanel;
  
  JMenuBar		ivMenuBar;
  JMenu			ivFileMenu, ivLoggingMenu, ivHelpMenu;
  JMenuItem		ivFileExit, ivLoggingDebug, ivHelpAbout;
  
  ImageIcon		ivIcon;

  COMTestGUI	ivCOMTest;
  ControlGUI	ivControl;
  ImageGUI		ivImage;
  
  HashMap<String, Object>      ivMap;
  int          ivLastTab;
  
  Camera		ivCamera;
  
  ImageRoll		ivImageRoll;
  
  LockEventMgr	ivLockEventMgr;
  
  File			ivFile;
  URL			ivResource;
  String		ivPropPath, ivFileSeparator;
  InputStream	ivInput;
  OutputStream	ivOutput;
  Properties	ivProps;
  
  DebugLog ivDebugLog;
	
  JDialog		ivDBDialog;
  JLabel		ivDBLabel;

  JCheckBox		ivDebugChoice, ivDebugVerbose;
  JLabel		ivDebugDirLabel;
  File			ivDebugDir;
  JFileChooser	ivDebugDirChooser;
  JTextField  	ivDebugDirText;
  JButton		ivDebugDirDialog, ivDBDialogDone;


//
// Build the GUI.
//

  public JQuickTake()
  {

//  Init environment

    ivMap = new HashMap<String, Object>(25);
    
    Environment.init(ivMap);
    
    Environment.setValue("Parent",this);
		
// Load values from config.properties

	ivFileSeparator = System.getProperty("file.separator");

	this.loadProperties();

//  Initialize Debugging
 
    ivDebugLog = new DebugLog();
	Environment.setValue("DebugLog",ivDebugLog);
	ivDebugLog.initLoggingDirectory();
	ivDebugLog.setMode(false);
	ivDebugLog.setVerbose(false);

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
    ivAppWin.setBounds(1,1,800,550);
	ivAppWin.pack();
    ivAppWin.addWindowListener(this);
    Environment.setValue("ParentFrame", ivAppWin);
	
	ivIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("JQuickTake.jpg"));
	ivAppWin.setIconImage(ivIcon.getImage());

    ivAppPanel = new JPanel();
    ivAppPanel.setLayout(null);
    ivAppWin.getContentPane().add(ivAppPanel);
    ivAppPanel.setBounds(1,1,800,550);

    ivQTPane = new JTabbedPane();
    ivAppPanel.add(ivQTPane);
    ivQTPane.setBounds(1,1,700,450);
	
// Create Menu Bar
	
	ivMenuBar = new JMenuBar();
	
	ivFileMenu = new JMenu("File");
	ivFileExit = new JMenuItem("Exit");
	ivFileExit.addActionListener(this);
	ivFileMenu.add(ivFileExit);
	
	ivLoggingMenu = new JMenu("Logging");
	ivLoggingDebug = new JMenuItem("Configure Debug Logging");
	ivLoggingDebug.addActionListener(this);
	ivLoggingMenu.add(ivLoggingDebug);
	
	ivHelpMenu = new JMenu("Help");
	ivHelpAbout = new JMenuItem("About JQuickTake");
	ivHelpAbout.addActionListener(this);
	ivHelpMenu.add(ivHelpAbout);
	
	ivMenuBar.add(ivFileMenu);
	ivMenuBar.add(ivLoggingMenu);
	ivMenuBar.add(ivHelpMenu);
	
	ivAppWin.setJMenuBar(ivMenuBar);

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

// Create GUI for 'remote control' of the camera. Make it runnable for thread execution
	
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

    ivAppWin.setBounds(50,100,710,460);
    ivAppWin.setResizable(false);
    ivAppWin.setVisible(true);

  }

//
// Catch various Menu choices
//

  public void actionPerformed(ActionEvent ae)
  {

	int		tvDirSelect;
	
//  Exit the app

    if(ae.getSource() == ivFileExit)
    {
	  this.gracefulExit();
	}
	
//  About the app

    if(ae.getSource() == ivHelpAbout)
    {		
		JOptionPane.showMessageDialog(ivAppWin,
					"JQuickTake v1.4 - November 2025 \n\n" +
					"Developed by Kevin Godin and licensed for use under GPL-3.0 \n\n" +
					"Source and Docs at github.com/Crazylegstoo/JQuickTake \n\n" +
					"Feedback/questions can be sent to jquicktake@gmail.com \n\n",
					"About JQuickTake",
					JOptionPane.INFORMATION_MESSAGE
	

);	
	}
	
//  Configure Debug logging

    if(ae.getSource() == ivLoggingDebug)
    {
		ivDBDialog = new JDialog(ivAppWin,"Configure Debug Logging");
		ivDBDialog.setLayout(null);
		ivDBDialog.setSize(600,200);
		ivDBDialog.setLocation(200,200);

// Create a checkbox to denote Debug Mode

		ivDebugChoice = new JCheckBox("Write Debug Log?", ivDebugLog.getMode());
		ivDBDialog.add(ivDebugChoice);
		ivDebugChoice.setBounds(10,10,135,25);
		ivDebugChoice.setToolTipText("Check to log debugging info to a file");
		ivDebugChoice.addActionListener(this);

// Create filechooser (and button) to specifty where Debug Log is to be saved

		ivDebugDirLabel = new JLabel("Location for log files:");
		ivDBDialog.add(ivDebugDirLabel);
		ivDebugDirLabel.setBounds(10,40,135,25);
	
		ivDebugDirText = new JTextField(ivDebugLog.getDirectory());
		ivDBDialog.add(ivDebugDirText);
		ivDebugDirText.setToolTipText("Folder in which debug log will be saved");
		ivDebugDirText.setBounds(160,40,275,25);

		ivDebugDirDialog = new JButton("Browse");
		ivDBDialog.add(ivDebugDirDialog);
		ivDebugDirDialog.setToolTipText("Browse file system for a folder");
		ivDebugDirDialog.setBounds(460,40,100,24);
		ivDebugDirDialog.addActionListener(this);

// Create a checkbox to denote Verbose Debug Mode

		ivDebugVerbose = new JCheckBox("Verbose Logging? Caution: Will log all received camera data in excess of 200 bytes!", ivDebugLog.getVerbose());
		ivDBDialog.add(ivDebugVerbose);
		ivDebugVerbose.setBounds(10,70,500,25);
		ivDebugVerbose.setToolTipText("Log additional data");
		ivDebugVerbose.addActionListener(this);

// Create a Configure button to dismiss the dialog

		ivDBDialogDone = new JButton("Configure");
		ivDBDialog.add(ivDBDialogDone);
		ivDBDialogDone.setBounds(250,125,100,25);
		ivDBDialogDone.addActionListener(this);

		if(ivDebugLog.getMode())
		{
			ivDebugDirText.setEnabled(true);
			ivDebugDirDialog.setEnabled(true);
			ivDebugDirLabel.setEnabled(true);
			ivDebugVerbose.setEnabled(true);
		} else
		{
			ivDebugDirText.setEnabled(false);
			ivDebugDirDialog.setEnabled(false);
			ivDebugDirLabel.setEnabled(false);
			ivDebugVerbose.setEnabled(false);
		}

		ivDBDialog.setVisible(true);
	}

	
//  Process Debug checkbox

    if(ae.getSource() == ivDebugChoice)
    {
		if(ivDebugChoice.isSelected())
		{
			ivDebugDirText.setEnabled(true);
			ivDebugDirDialog.setEnabled(true);
			ivDebugDirLabel.setEnabled(true);
			ivDebugVerbose.setEnabled(true);
		} else
		{
			ivDebugDirText.setEnabled(false);
			ivDebugDirDialog.setEnabled(false);
			ivDebugDirLabel.setEnabled(false);
			ivDebugVerbose.setEnabled(false);
		}

	}

// Log Directory Directory button - pop-up file chooser dialog to select directory for saving Debug Log files
			
    if(ae.getSource() == ivDebugDirDialog)
    {
	
		ivDebugDir = new File(ivDebugLog.getDirectory());
		ivDebugDirChooser = new JFileChooser(ivDebugDir);
		ivDebugDirChooser.setDialogTitle("Select Folder for Debug Log File");
		ivDebugDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ivDBDialog.add(ivDebugDirChooser);
		tvDirSelect = ivDebugDirChooser.showOpenDialog(ivDBDialog);

		if (tvDirSelect == JFileChooser.APPROVE_OPTION)
		{
			ivDebugDir = ivDebugDirChooser.getSelectedFile();
			ivDebugDirText.setText(ivDebugDir.getAbsolutePath());
		}
	}

// Debug Dialog Done button pressed
			
    if(ae.getSource() == ivDBDialogDone)
    {
		if(ivDebugChoice.isSelected())
		{
			ivDebugLog.setPath(ivDebugDirText.getText());		
			ivDebugLog.setMode(true);;
		} else
		{
			ivDebugLog.setMode(false);;
		}
		
		if(ivDebugVerbose.isSelected())
		{
			ivDebugLog.setVerbose(true);;
		} else
		{
			ivDebugLog.setVerbose(false);;
		}
		
		ivDBDialog.dispose();
	}

	  
  }

//
// Catch window when it closes
//

  public void windowClosing(WindowEvent we)
  {
	  this.gracefulExit();
  }

// 
// Perform graceful exit
//

  public void gracefulExit()
  {
	ivCamera.closeCamera();  
	
	this.writeProperties();
	
	ivDebugLog.closeLog();

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

// Load config.properties
  
  public void loadProperties()
  {
	
	try
	{

		ivPropPath = System.getProperty("user.home") + ivFileSeparator + "JQuickTake.properties";

		ivFile = new File(ivPropPath);
		
		if(!ivFile.exists())
			ivFile.createNewFile();

		ivInput = new FileInputStream(ivFile);

        ivProps = new Properties();

		ivProps.load(ivInput);
		
		Environment.setValue("ConfigProps",ivProps);	
		
	}  catch (Exception e) 
		{ 
			e.printStackTrace();
		}
	return;
  }

// Write config.properties
    
  public void writeProperties()
  {
	try
	{

		ivOutput = new FileOutputStream(ivFile);

        // save properties to project root folder
        ivProps.store(ivOutput, null);

		ivOutput.close();
		
	}  catch (Exception e) 
		{ 
            e.printStackTrace();
		}
	return;
  }
//
// Run from the command line. There is an optional Debug parm that will cause messages to
// be written to the console for debugging purposes
//
  public static void main(String[] args)
  {
	
	try
	{
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } 
    catch (Exception e)
	{
		e.printStackTrace();
    }
	
	JQuickTake client = new JQuickTake();
  }

}
