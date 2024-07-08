/******************************************************************************
 *
 * ImageGUI.java
 *
 * Enable user to select the image(s) that will be read from the Camera and
 * written to Computer as an Apple QTK image file 
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class ImageGUI extends JQuickTakePanel implements ActionListener, KeyListener, Runnable
{

  Frame			ivParentFrame;
 
  JFileChooser	ivDirChooser;
  File			ivSaveDir;
  JTextField  	ivSaveDirText, ivPrefixText;
  
  JLabel				ivImageNumLabel, ivSaveDirLabel, ivProgressLabel, ivPrefixLabel, ivPrefixExample;
  JComboBox<String>		ivImageNum;
  JCheckBox				ivSaveAll;
  
  JButton	ivSave, ivDirDialog;

  JProgressBar	ivProgress;

  DefaultListCellRenderer  ivListRenderer;
  
  Properties		ivProps;
  String			ivPropsSavePath;

  ImageRoll	ivImageRoll;
  Camera	ivCamera;  
  DebugLog	ivDebugLog;
  
  LockEventMgr	ivLockEventMgr;
  LockEvent		ivLockEvent;
  
  public ImageGUI()
  {

    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

	ivImageRoll = (ImageRoll)Environment.getValue("ImageRoll");
	
	ivCamera = (Camera)Environment.getValue("Camera");
	
	ivLockEventMgr = (LockEventMgr)Environment.getValue("LockEventMgr");
	
	ivProps = (Properties)Environment.getValue("ConfigProps");
	
	ivPropsSavePath = ivProps.getProperty("save.path");

  }

//
// Build GUI
//

  public void createGUI()
  {

// Create combobox for selecting a specific image to save

    ivImageNumLabel = new JLabel("Select Picture# to Save:");
    this.add(ivImageNumLabel);
    ivImageNumLabel.setBounds(10,30,150,25);

    ivImageNum = new JComboBox<String>();
	ivListRenderer = new DefaultListCellRenderer();
	ivListRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
	ivImageNum.setRenderer(ivListRenderer);	
    this.add(ivImageNum);
    ivImageNum.setBounds(170,30,75,25);

// Create a checkbox to denote that all images are to be saved

	ivSaveAll = new JCheckBox("Save All Pictures", false);
	this.add(ivSaveAll);
	ivSaveAll.setBounds(310,30,150,25);
	ivSaveAll.addActionListener(this);

// Create filechooser (and button) to specifty where images are to be saved

	ivSaveDirLabel = new JLabel("Save Picture(s) to Folder...");
	this.add(ivSaveDirLabel);
	ivSaveDirLabel.setBounds(10,80,250,25);
	
	if(ivPropsSavePath == null)
	{
		ivSaveDir = new File(System.getProperty("user.home"));
	} else
	{
		ivSaveDir = new File(ivPropsSavePath);
	}
	
	ivSaveDirText = new JTextField(ivSaveDir.getAbsolutePath());
	this.add(ivSaveDirText);
	ivSaveDirText.setBounds(10,110,400,25);

    ivDirDialog = new JButton("Browse");
    this.add(ivDirDialog);
    ivDirDialog.setBounds(410,110,100,24);
    ivDirDialog.addActionListener(this);

// Create field to specifty a filename prefix for images

	ivPrefixLabel = new JLabel("Picture Filename Prefix (optional)...");
	this.add(ivPrefixLabel);
	ivPrefixLabel.setBounds(10,150,250,25);
	
	ivPrefixText = new JTextField();
	this.add(ivPrefixText);
	ivPrefixText.setBounds(10,180,300,25);

	ivPrefixExample = new JLabel("(Naming Format: prefix-IMAGEnn.qtk)");
	this.add(ivPrefixExample);
	ivPrefixExample.setBounds(320,180,375,25);

// Create button to start save process

    ivSave = new JButton("Save Picture(s)");
    this.add(ivSave);
    ivSave.setBounds(10,300,125,25);
    ivSave.addActionListener(this);

// Create progressbar that will highlight image saving progress

	ivProgressLabel = new JLabel("Save Progress:");
	this.add(ivProgressLabel);
	ivProgressLabel.setBounds(10,240,95,25);
	
	ivProgress = new JProgressBar();
    ivProgress.setValue(0);
    ivProgress.setStringPainted(true);
    this.add(ivProgress);
	ivProgress.setBounds(105,240,400,25);

  }  
//
// Tab changed to this tab, so refresh any values coming from the camera
//

  public void refreshView()
  {

	ivSaveAll.setText("Save All " + ivCamera.getTaken()+ " Pictures");  // Refine checkbox label with # of images

	ivImageNum.removeAllItems();
	
	if(Integer.parseInt(ivCamera.getTaken()) > 0)    // Update combobox with the number of images available 
	{
		ivSaveAll.setEnabled(true);
		ivSave.setEnabled(true);
		
		for (int i = 1; i <= Integer.parseInt(ivCamera.getTaken()); ++i) 
		{
		  ivImageNum.addItem(Integer.toString(i));
		}		} else
	{
		ivSaveAll.setEnabled(false);
		ivSave.setEnabled(false);
	}
	  
	return;
  }

//
// Catch various button events
//

  public void actionPerformed(ActionEvent ae)
  {
	int		tvDirSelect;

// Save button - read image(s) from camera and save to QTK file. This is done on a thread so that 
// a progress bar can animate on the main thread
		
    if(ae.getSource() == ivSave)
    {
		ivSave.setEnabled(false);
		ivSaveAll.setEnabled(false);
		ivImageNum.setEnabled(false);
		synchronized(this){notifyAll();}		
   }

// Save All checkbox - enable/disable Image Number dropdown depending on whether checkbox is selected
   
    if(ae.getSource() == ivSaveAll)
    {

		ivDebugLog.textOut(this,"CheckBox state" + ivSaveAll.isSelected());

		if(ivSaveAll.isSelected())
		{
			ivImageNum.setEnabled(false);
		} else
		{
			ivImageNum.setEnabled(true);
		}
	}

// Save Directory button - pop-up file chooser dialog to select directory for saving QTK files
			
    if(ae.getSource() == ivDirDialog)
    {
	
		ivDirChooser = new JFileChooser(ivSaveDir);
		ivDirChooser.setDialogTitle("Select Folder for QuickTake Pictures");
		ivDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.add(ivDirChooser);
		tvDirSelect = ivDirChooser.showOpenDialog(this);

		if (tvDirSelect == JFileChooser.APPROVE_OPTION)
		{
			ivDebugLog.textOut(this,"Folder Chosen: " + ivDirChooser.getSelectedFile().getAbsolutePath());
			ivSaveDir = ivDirChooser.getSelectedFile();
			ivSaveDirText.setText(ivSaveDir.getAbsolutePath());
		}
	}
	
  }

//
// Save a selected image or save all images - all on a thread.
//

	public void run()
	{
		String	tvDirSelect, tvPrefixSelect;
	  
		int tvImageNum;
        
// wait for the signal from the GUI

        while(true){
            try{synchronized(this){wait();}}
            catch (InterruptedException e){}
	
// Call ImageRoll accordingly to save selected or all image(s)

			ivLockEvent = new LockEvent(this,false);            // Lock other UI tabs while picture is being saved
			ivLockEventMgr.notifyListeners(ivLockEvent);
		
			tvDirSelect = ivSaveDirText.getText();
			tvPrefixSelect = ivPrefixText.getText();

			if(!ivSaveAll.isSelected())
			{
			
				tvImageNum = Integer.parseInt((String)ivImageNum.getSelectedItem());
				
				ivDebugLog.textOut(this,"Image selected: " + tvImageNum);
				
				ivImageRoll.saveImage(tvImageNum,tvDirSelect,tvPrefixSelect,ivProgress);
				
				ivImageNum.setEnabled(true);
						
			} else
			{
				ivImageRoll.saveAllImages(tvDirSelect, tvPrefixSelect, ivProgress);	
			}
			
			ivSave.setEnabled(true);
			ivSaveAll.setEnabled(true);
			
			ivProps.setProperty("save.path", tvDirSelect);

			ivLockEvent = new LockEvent(this,true);            // Unlock other UI tabs once picture is saved
			ivLockEventMgr.notifyListeners(ivLockEvent);
		}
			
   }

//
// Catch key events
//
  
  public void keyPressed(KeyEvent ke)
  {
    if(ke.getKeyCode() == KeyEvent.VK_ENTER)
    {
//      this.addName();
    }
  }
  
  public void keyTyped(KeyEvent ke)
  {
  }
  
  public void keyReleased(KeyEvent ke)
  {
  }
  

}
