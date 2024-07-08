/******************************************************************************
 *
 * Image.java
 *
 * This class holds all the data for a single image and is also able to
 * write it's image to an Apple QTK image file.
 *
 *
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import java.util.*;
import java.nio.*;
import java.io.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class Image
{

	int		ivImageNum;
	int		ivImageSize, ivImageWidth, ivImageHeight;
	int		ivHour, ivMinute, ivSecond, ivYear, ivMonth, ivDay;
	boolean	ivFlashUsed;
	String	ivQualityMode,ivDisplayDate, ivDisplayTime, ivCameraModel, ivSaveName;
	byte[]	ivImageHeader,ivImageData,ivThumbData;
    String	ivFileSeparator;
  
	DebugLog	ivDebugLog;
	
	byte[]		ivQT1XXHeader = {(byte)0x71,(byte)0x6B,(byte)0x74,(byte)0x6E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
								(byte)0x00,(byte)0x01};

//
// Create the initial image with the header info, image#, and camera Model
//

  public Image(int imageNum, byte[] imageHeader, String model)
  { 
	
	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

	this.setImageNum(imageNum);
	
	this.setCameraModel(model);
	
	this.parseHeader(imageHeader);
	
	ivSaveName = "IMAGE" + String.format("%02d",this.getImageNum());

	ivFileSeparator = System.getProperty("file.separator");


	return;
  }

//
// Parse out all the interesting elements from the image header
//

  public void parseHeader(byte[] header)
  {
	ByteBuffer	tvBuffer;
	byte[]		tvSubArray;

// Parse image size in bytes from header 

	tvSubArray = new byte[4];
	tvSubArray[0] = (byte)0x00;		
	System.arraycopy(header,5,tvSubArray,1,3);	
	tvBuffer = ByteBuffer.wrap(tvSubArray); 
	ivImageSize = tvBuffer.getInt();
	
	 ivDebugLog.textOut(this,"Image Size: " + ivImageSize);

// Parse image width in pixels from header 

	tvSubArray = new byte[4];
	tvSubArray[0] = (byte)0x00;		
	tvSubArray[1] = (byte)0x00;		
	System.arraycopy(header,8,tvSubArray,2,2);	
	tvBuffer = ByteBuffer.wrap(tvSubArray); 
	ivImageWidth = tvBuffer.getInt();
	
	 ivDebugLog.textOut(this,"Image Width: " + ivImageWidth);

// Parse image height in pixels from header 

	tvSubArray = new byte[4];
	tvSubArray[0] = (byte)0x00;		
	tvSubArray[1] = (byte)0x00;		
	System.arraycopy(header,10,tvSubArray,2,2);	
	tvBuffer = ByteBuffer.wrap(tvSubArray); 
	ivImageHeight = tvBuffer.getInt();
	
	 ivDebugLog.textOut(this,"Image Width: " + ivImageHeight);

// Parse Timestamp

	ivMonth = header[13];
	ivDay = header[14];
	ivYear = header[15];
	ivHour = header[16];
	ivMinute = header[17];
	ivSecond = header[18];

	ivDisplayDate = String.format("%02d",ivMonth) + "/" + String.format("%02d",ivDay) + "/" + String.format("%02d",ivYear); 

	ivDisplayTime = String.format("%02d",ivHour) + ":" + String.format("%02d",ivMinute) + ":" + String.format("%02d",ivSecond); 
	
	ivDebugLog.textOut(this, "Image Date (dd/mm/yy): " + ivDisplayDate + "   Time: " + ivDisplayTime);
	
// Parse Flash mode

	ivFlashUsed = false;

	if(header[19] == 1)
		ivFlashUsed = true;

	ivDebugLog.textOut(this,"Flash mode: " + ivFlashUsed);
		
// Parse Quality mode
	
	switch(header[24])
	{
		case 32:
			ivQualityMode = "Standard";
			break;
		case 16:
			ivQualityMode = "High";
			break;
		default:
			ivQualityMode = "Unknown";
			break;
	}

	ivDebugLog.textOut(this,"Quality mode: " + ivQualityMode);

	ivImageHeader = new byte[header.length];
	System.arraycopy(header,0,ivImageHeader,0,header.length);	
	
	ivDebugLog.hexOut(this,"Saved Header Hex: ",ivImageHeader);
	
	ivDebugLog.textOut(this,"Saved Header RAW: " + ivImageHeader);
	  
	  return;
  }  
  
//
// Check whether the full file path name already exists for the image
//

  public String checkFileName(String filepath, String prefix)
  {
	  File				tvFile;
	  String			tvName;
	  String			tvStatus;
	  
	  if (prefix.trim().length() > 0)
	  {
		tvName = filepath + ivFileSeparator + prefix + "-" + ivSaveName + ".qtk";  // Set full filename
	  } else
	  {
		tvName = filepath + ivFileSeparator + ivSaveName + ".qtk";  // Set full filename
	  }  
	  

		  tvFile = new File(tvName);
		  
		  if(tvFile.exists())   // Does file already exist?
		  {
				tvStatus = tvName;
		  } else
		  {
				tvStatus = "OK";
		  }
	  
	  return tvStatus;
  }		  
  
//
// Given a file path, write the image to local storage as a QTK file. The filename will be IMAGExx.QTK where xx is the image#
//

  public String saveImageToFile(String filepath, String prefix)
  {
	  File				tvFile;
	  FileOutputStream	tvWriter;
	  String			tvName;
	  String			tvStatus;
	  
	  if (prefix.trim().length() > 0)
	  {
		tvName = filepath + ivFileSeparator + prefix + "-" + ivSaveName + ".qtk";  // Set full filename
	  } else
	  {
		tvName = filepath + ivFileSeparator + ivSaveName + ".qtk";  // Set full filename
	  }  
	  
	  try
	  {
		  tvFile = new File(tvName);
		  
		  ivDebugLog.textOut(this,"Save to file: " + tvName);
		  
		  if(tvFile.exists())   // If file exists, delete the current file
		  {
				tvFile.delete();
		  }
		  
		tvFile.createNewFile();
		  
		tvWriter = new FileOutputStream(tvName);

		tvWriter.write(this.createFileHeader());    // Write the header info
		tvWriter.write(ivImageData);   // Now write the actual image info
		  
		tvWriter.close();
		  
		tvStatus = "OK";
		  
	  } catch (Exception e) 
		{ 
			tvStatus = e.getMessage() + "     "; 
		} 
	  
	  return tvStatus;
  }		  

//
// Create a string of data that is the file header. It will contain some metadata about the image. The first 4 bytes reflect the
// Model of the camera. This is key because QT100 and QT150/200 use different compression methods for image data. Utilities such as dcraw
// use the Model info to determine how to process the image data.
//

  public byte[] createFileHeader()
  {
	  byte[]		tvFileHeader;
	  
	  tvFileHeader = new byte[736];	  
	  
	  System.arraycopy(ivQT1XXHeader,0,tvFileHeader,0,ivQT1XXHeader.length);

// Setup unique elements for QT10xx Standard Quality, QT1xx High Quality, and QT150/200 header
	
	switch(this.getCameraModel())
	{
		case "QT100":					// Write 'qktk'
			tvFileHeader[0] = 0x71;
			tvFileHeader[1] = 0x6B;
			tvFileHeader[2] = 0x74;
			tvFileHeader[3] = 0x6B;
	
			switch(ivImageHeader[24])   // Set image quality element
			{
				case 32:
					tvFileHeader[7] = 0x04;  // Standard
					break;
				case 16:
					tvFileHeader[7] = 0x08;  // High
					break;
				default:
					tvFileHeader[7] = 0x04;  // standard
					break;
			}
			break;
			
		case "QT150":				// Write 'qktn'
			tvFileHeader[0] = 0x71;
			tvFileHeader[1] = 0x6B;
			tvFileHeader[2] = 0x74;
			tvFileHeader[3] = 0x6E;
			tvFileHeader[7] = 0x04;
			break;
			
		default:
			break;
	}
	  
// Setup image size

	  tvFileHeader[9] = ivImageHeader[5];
	  tvFileHeader[10] = ivImageHeader[6];
	  tvFileHeader[11] = ivImageHeader[7];

// Setup image height
	  
  	  tvFileHeader[544] = ivImageHeader[10];
  	  tvFileHeader[545] = ivImageHeader[11];

// Setup image width

  	  tvFileHeader[546] = ivImageHeader[8];
  	  tvFileHeader[547] = ivImageHeader[9];

// Setup flashe mode
	  
  	  tvFileHeader[13] = ivImageHeader[19];
	  
	  System.arraycopy(ivImageHeader,4,tvFileHeader,14,60);
	
	  return tvFileHeader;
  }

//
// Various Set and Get below
//

  public void setImageNum(int num)
  {
	  ivImageNum = num;
	  
	  return;
  }
  
  public int getImageNum()
  {
	  return ivImageNum;
  }

  public void setCameraModel(String model)
  {
	  ivCameraModel = model;
	  
	  return;
  }
  
  public String getCameraModel()
  {
	  return ivCameraModel;
  }
  
  public int getImageSize()
  {
	  return ivImageSize;
  }
   
  public byte[] getImageSizeHex()
  {
	byte[]	tvBytes;
	
	tvBytes = new byte[3];
	tvBytes[0]=ivImageHeader[5];
	tvBytes[1]=ivImageHeader[6];
	tvBytes[2]=ivImageHeader[7];
	
	  return tvBytes;
  }
 
  public void setImageData(byte[] image)
  {
	  ivImageData = new byte[image.length];
	  System.arraycopy(image,0,ivImageData,0,image.length);
	  
//	  ivDebugLog.hexOut(this,"Image :",ivImageData);
	  
	  return;
  }
  
  public void setThumbData(byte[] thumb)
  {
	  ivThumbData = new byte[thumb.length];
	  System.arraycopy(thumb,0,ivThumbData,0,thumb.length);
	  
//	  ivDebugLog.hexOut(this,"Image :",ivThumbData);
	  
	  return;
  }
  
  public String getSaveName()
  {
	  return ivSaveName;
  }	
  
  public byte[] getHeader()
  {
	  return ivImageHeader;
  }	

}
