/******************************************************************************
 *
 * ImageRoll.java
 *
 * This class holds onto all the images that have been pulled off the camera. The 
 * idea is akin to the 'camera roll' on a smartphone. It's job is to work with the GUIs
 * and the camera to perform image processing. Currently, it directs the saving of
 * a single image, the saving of all images, and the deletion of all images. 
 *
 * In the future, this class will likely be extended to support image display.
 *
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import java.awt.*;
import java.util.*;
import java.nio.*;
import java.io.*;
import javax.swing.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class ImageRoll
{

	Frame		ivParentFrame;

	Image[]		ivImageRoll;
	
	Camera		ivCamera;

	DebugLog	ivDebugLog;


  public ImageRoll()
  { 
	
	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");
	ivCamera = (Camera)Environment.getValue("Camera");

    ivParentFrame = (JFrame)Environment.getValue("ParentFrame");
	
	ivImageRoll = new Image[40];   // Allocate enough space for up to 40 images (supports capacity for all QuickTake models)

	return;
  }

//
// Take a request to save and image and pass it along to the appropriate image.
//

  public void saveImage(int imageNum, String path, String prefix, JProgressBar progress)
  {
	int 			tvRoll;
	String			tvStatus;
	boolean			tvWrite;
	int				tvOverWriteInput;
	Object[]		tvOptions = {"Overwrite Existing File", "Cancel Save"};

	
	progress.setValue(0);    // Prepare the specified progress bar that will show 'save progress'
	progress.setString("initializing...");

	tvRoll = imageNum - 1;
	
	tvWrite = true;

// If the image is not currently stored in the roll, it needs to be read from the camera. This will
// cause an image object to be created and stored in the roll.
	  
	if(ivImageRoll[tvRoll] == null)
	{
		ivImageRoll[tvRoll] = ivCamera.createImage(imageNum);
		progress.setString("Reading from camera... " + ivImageRoll[tvRoll].getSaveName());		
		ivCamera.transferImageData(ivImageRoll[tvRoll],progress);
	}

// Verify whether filename already exists. If it does, prompt User for permission to Overwrite or Not

	tvStatus = ivImageRoll[tvRoll].checkFileName(path, prefix);
	
	if(!tvStatus.equals("OK"))     // File already exists, so prompt User
	{
		tvOverWriteInput = JOptionPane.showOptionDialog(ivParentFrame,
									"Confirm overwrite of " + tvStatus,
									"File Already Exists!",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									null,  //No custom icon
									tvOptions,
									tvOptions[1]);
										
		tvWrite = false;

		if(tvOverWriteInput == 0) 		// If user confirms overwrite, set the boolean
			tvWrite = true;		

	}
	
// With the image object created, tell the image to save itself to the computer. Then update progress bar.

	if(tvWrite)
	{
		tvStatus = ivImageRoll[tvRoll].saveImageToFile(path, prefix);
		progress.setValue(100);
	
		if(tvStatus == "OK")
		{
			progress.setString("Save Completed... " + ivImageRoll[tvRoll].getSaveName());	
		} else
		{
			progress.setString(tvStatus);	// Some issue was encountered - e.g. Write Access denied for selected save location - so bail out
		}
	} else
	{
		progress.setString("Save Cancelled... " + ivImageRoll[tvRoll].getSaveName());	
	}


	return;
  }  

  public void saveAllImages(String path, String prefix, JProgressBar progress)
  {
	int 			tvRoll, i, j;
	LockEventMgr	tvLockEventMgr;
	String			tvStatus;
	boolean			tvWriteCurrent, tvWriteFuture;
	int				tvOverWriteInput;
	Object[]		tvOptions = {"Overwrite Current File", "Overwrite All Duplicate Files", "Cancel Save"};
	
	tvLockEventMgr = (LockEventMgr)Environment.getValue("LockEventMgr");
	
	progress.setValue(0);    // Prepare the specified progress bar that will show 'save progress'
	progress.setString("initializing...");
	
	tvWriteFuture = false;

// Loop through every image to have each one save itself. If an image is not in the roll, it needs to
// be read from the camera - causing an image object to be created and stored in the roll

	j = Integer.parseInt(ivCamera.getTaken());
		
	for (i = 1; i <= j; ++i) 
	{
		tvRoll = i - 1;
	
		tvWriteCurrent = true;
	  
		if(ivImageRoll[tvRoll] == null)  // See if image is in the roll and proceed accordingly
		{
			ivImageRoll[tvRoll] = ivCamera.createImage(i);
			progress.setString("Reading from camera... " + ivImageRoll[tvRoll].getSaveName());		
			ivCamera.transferImageData(ivImageRoll[tvRoll], progress);
		}

// // Verify whether filename already exists. If it does, prompt User for permission to Overwrite current, all future Duplicates, or Cancel out

		if(!tvWriteFuture)  // Only check for a duplicate filename is User has not specified to Overwrite everything
		{
			tvStatus = ivImageRoll[tvRoll].checkFileName(path, prefix);
		
			if(!tvStatus.equals("OK"))  // File already exists, so prompt User
			{
				tvOverWriteInput = JOptionPane.showOptionDialog(ivParentFrame,
											"Confirm overwrite of " + tvStatus,
											"File Already Exists!",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE,
											null,  //No custom icon
											tvOptions,
											tvOptions[2]);
												
				tvWriteCurrent = false;

				if(tvOverWriteInput == 0)				// If user confirms Current overwrite, set the boolean
					tvWriteCurrent = true;		

				if(tvOverWriteInput == 1)				// If user confirms Future overwrites, set the booleans
				{
					tvWriteCurrent = true;	
					tvWriteFuture = true;
				}
					
			}
		}

// With the image object created, tell the image to save itself to the computer. Then update progress bar.

		if(tvWriteCurrent)
		{
			tvStatus = ivImageRoll[tvRoll].saveImageToFile(path,prefix);
			progress.setValue(100);
			if(tvStatus == "OK")
			{
				progress.setString(i + " of " + j + " Saves Completed... " + ivImageRoll[tvRoll].getSaveName());
			} else
			{
				progress.setString(tvStatus);   // Some issue was encountered - e.g. Write Access denied for selected save location - so bail out
				i = j+1;
			}

			try {
				Thread.sleep(500);
			} catch (Exception e) { e.printStackTrace(); }
		} else
		{
			progress.setString("Save Cancelled... " + ivImageRoll[tvRoll].getSaveName());
			i = j + 1;
		}
	}
	
	return;
  }  

//
// Delete all images from the camera and the roll
//

  public void deleteImages()
  {

	ivCamera.deleteImages();
	
	Arrays.fill(ivImageRoll,null);
	  
	return;
  }

}
