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

	Image[]		ivImageRoll;
	
	Camera		ivCamera;

	DebugLog	ivDebugLog;


  public ImageRoll()
  { 
	
	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");
	ivCamera = (Camera)Environment.getValue("Camera");
	
	ivImageRoll = new Image[40];   // Allocate enough space for up to 40 images (supports capacity for all QuickTake models)

	return;
  }

//
// Take a request to save and image and pass it along to the appropriate image.
//

  public void saveImage(int imageNum, String path, JProgressBar progress)
  {
	int 			tvRoll;
	String			tvStatus;

	
	progress.setValue(0);    // Prepare the specified progress bar that will show 'save progress'
	progress.setString("initializing...");

	tvRoll = imageNum - 1;

// If the image is not currently stored in the roll, it needs to be read from the camera. This will
// cause an image object to be created and stored in the roll.
	  
	if(ivImageRoll[tvRoll] == null)
	{
		ivImageRoll[tvRoll] = ivCamera.createImage(imageNum);
		progress.setString("Processing... " + ivImageRoll[tvRoll].getSaveName());		
		ivCamera.transferImageData(ivImageRoll[tvRoll],progress);
	}

// With the image object created, tell the image to save itself to the computer. Then update progress bar.
	
	tvStatus = ivImageRoll[tvRoll].saveImageToFile(path);
	progress.setValue(100);
	if(tvStatus == "OK")
	{
		progress.setString("Save Completed... " + ivImageRoll[tvRoll].getSaveName());	
	} else
	{
		progress.setString(tvStatus);	
	}


	return;
  }  

  public void saveAllImages(String path, JProgressBar progress)
  {
	int 			tvRoll, i, j;
	LockEventMgr	tvLockEventMgr;
	String			tvStatus;
	
	tvLockEventMgr = (LockEventMgr)Environment.getValue("LockEventMgr");
	
	progress.setValue(0);    // Prepare the specified progress bar that will show 'save progress'
	progress.setString("initializing...");

// Loop through every image to have each one save itself. If an image is not in the roll, it needs to
// be read from the camera - causing an image object to be created and stored in the roll

	j = Integer.parseInt(ivCamera.getTaken());
		
	for (i = 1; i <= j; ++i) 
	{
		tvRoll = i - 1;
	  
		if(ivImageRoll[tvRoll] == null)  // See if image is in the roll and proceed accordingly
		{
			ivImageRoll[tvRoll] = ivCamera.createImage(i);
			progress.setString("Processing... " + ivImageRoll[tvRoll].getSaveName());		
			ivCamera.transferImageData(ivImageRoll[tvRoll], progress);
		}

// With the image object created, tell the image to save itself to the computer. Then update progress bar.

		tvStatus = ivImageRoll[tvRoll].saveImageToFile(path);
		progress.setValue(100);
		if(tvStatus == "OK")
		{
			progress.setString(i + " of " + j + " Saves Completed... " + ivImageRoll[tvRoll].getSaveName());
		} else
		{
			progress.setString(tvStatus);
			i = j+1;
		}

		try {
			Thread.sleep(500);
		} catch (Exception e) { e.printStackTrace(); }
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
