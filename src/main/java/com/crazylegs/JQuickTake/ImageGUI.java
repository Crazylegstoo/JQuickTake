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

  JQuickTake     ivParent;
  Frame			ivParentFrame;
 
  JFileChooser	ivDirChooser;
  File			ivSaveDir;
  JTextField  	ivSaveDirText;
  
  JLabel				ivImageNumLabel, ivSaveDirLabel, ivProgressLabel;
  JComboBox<String>		ivImageNum;
  JCheckBox				ivSaveAll;
  
  JButton	ivSave, ivDirDialog;

  JProgressBar	ivProgress;

  DefaultListCellRenderer  ivListRenderer;

  ImageRoll	ivImageRoll;
  Camera	ivCamera;  
  DebugLog	ivDebugLog;

  public ImageGUI()
  {

    ivParent      = (JQuickTake)Environment.getValue("Parent");
    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

	ivImageRoll = (ImageRoll)Environment.getValue("ImageRoll");
	ivCamera = (Camera)Environment.getValue("Camera");

  }

//
// Build GUI
//

  public void createGUI()
  {

// Create combobox for selecting a specific image to save

    ivImageNumLabel = new JLabel("Select Image# to Save:");
    this.add(ivImageNumLabel);
    ivImageNumLabel.setBounds(10,30,140,25);

    ivImageNum = new JComboBox<String>();
	ivListRenderer = new DefaultListCellRenderer();
	ivListRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
	ivImageNum.setRenderer(ivListRenderer);	
    this.add(ivImageNum);
    ivImageNum.setBounds(160,30,75,25);

// Create a checkbox to denote that all images are to be saved

	ivSaveAll = new JCheckBox("Save All Images", false);
	this.add(ivSaveAll);
	ivSaveAll.setBounds(300,30,150,25);
	ivSaveAll.addActionListener(this);

// Create filechooser (and button) to specifty where images are to be saved

	ivSaveDirLabel = new JLabel("Save Image(s) to Directory...");
	this.add(ivSaveDirLabel);
	ivSaveDirLabel.setBounds(10,80,250,25);
	
	ivSaveDir = new File(System.getProperty("user.dir"));
	
	ivSaveDirText = new JTextField(ivSaveDir.getAbsolutePath());
	this.add(ivSaveDirText);
	ivSaveDirText.setBounds(10,110,400,25);

    ivDirDialog = new JButton("...");
    this.add(ivDirDialog);
    ivDirDialog.setBounds(410,110,25,25);
    ivDirDialog.addActionListener(this);

// Create button to start save process

    ivSave = new JButton("Save Image(s)");
    this.add(ivSave);
    ivSave.setBounds(10,230,125,25);
    ivSave.addActionListener(this);

// Create progressbar that will highlight image saving progress

	ivProgressLabel = new JLabel("Save Progress:");
	this.add(ivProgressLabel);
	ivProgressLabel.setBounds(10,170,95,25);
	
	ivProgress = new JProgressBar();
    ivProgress.setValue(0);
    ivProgress.setStringPainted(true);
    this.add(ivProgress);
	ivProgress.setBounds(105,170,250,25);

  }  
//
// Tab changed to this tab, so refresh any values coming from the camera
//

  public void refreshView()
  {

	ivSaveAll.setText("Save All " + ivCamera.getTaken()+ " Images");  // Refine checkbox label with # of images

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
		ivDirChooser.setDialogTitle("Select Directory for QuickTake Images");
		ivDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.add(ivDirChooser);
		tvDirSelect = ivDirChooser.showOpenDialog(this);

		if (tvDirSelect == JFileChooser.APPROVE_OPTION)
		{
			ivDebugLog.textOut(this,"Dir Chosen: " + ivDirChooser.getSelectedFile().getAbsolutePath());
			ivSaveDirText.setText(ivDirChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
  }

//
// Save a selected image or save all images - all on a thread.
//

	public void run()
	{
		String	tvDirSelect;
	  
		int tvImageNum;
        
// wait for the signal from the GUI

        while(true){
            try{synchronized(this){wait();}}
            catch (InterruptedException e){}
	
// Call ImageRoll accordingly to save selected or all image(s)

			ivParent.unlockTabs(this,false);
		
			tvDirSelect = ivSaveDirText.getText() + "\\";

			if(!ivSaveAll.isSelected())
			{
			
				tvImageNum = Integer.parseInt((String)ivImageNum.getSelectedItem());
				
				ivDebugLog.textOut(this,"Image selected: " + tvImageNum);
				
				ivImageRoll.saveImage(tvImageNum,tvDirSelect,ivProgress);
				
				ivImageNum.setEnabled(true);
						
			} else
			{
				ivImageRoll.saveAllImages(tvDirSelect, ivProgress);	
			}
			
			ivSave.setEnabled(true);
			ivSaveAll.setEnabled(true);

			ivParent.unlockTabs(this,true);
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
