/******************************************************************************
 *
 * Camera.java
 *
 * This class embodies the camera, itself, and performs all actual camera 
 * interacts - i.e. connect, take a picture, read an image, etc. This is 
 * where all the hex-based commands are sent to the camera and all the hex-based
 * responses are read and translated.
 *
 * Note that this class leverages a 3rd-party framework - jSerialComm - to provide
 * all the low-level functions to use the Computer's COM port.
 *
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import com.fazecast.jSerialComm.*;
import javax.swing.*;
import java.util.Arrays;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class Camera
{

  DebugLog ivDebugLog;

  int ivSpeed;
  int ivDataBits;
  int ivStopBits;
  int ivParity;
  
  boolean	ivOpenStatus;

  SerialPort[] ivPorts;
  String[] ivCOMPorts;
  int i;

  SerialPort ivCameraPort;
  byte[] ivReadBuffer;
  int ivBufferLength;

  int    ivDateDD, ivDateMM, ivDateYY, ivTimeHH, ivTimeMM, ivTimeSS;
  
  String ivCameraModel, ivBatteryLevel, ivPicsTaken, ivPicsRemaining, ivName, ivFlash, ivQuality;
  
  boolean	ivQualityBool;
  
// Various byte arrays are defined to write commands to the camera. In some instances, the byte array is modified during runtime to reflect choices
// the user has made though the GUI

  byte[] ivOpenCmd = {(byte)0x5A,(byte)0xA5,(byte)0x55,(byte)0x05,(byte)0x00,(byte)0x00,(byte)0x25,(byte)0x80,(byte)0x00,(byte)0x80,(byte)0x02,(byte)0x00,(byte)0x80};

  byte[] ivPortSpeedCmd = {(byte)0x16,(byte)0x2A,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x00,(byte)0x03,(byte)0x03,(byte)0x08,(byte)0x04,(byte)0x00};

  byte[] ivPingCmd = {(byte)0x16,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

  byte[] ivCompACKCmd = {(byte)0x06};

  byte[] ivMetadataCmd = {(byte)0x16,(byte)0x28,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x80,(byte)0x00};
  
  byte[] ivCameraACKResp = {(byte)0x00};
  
  byte[] ivGetImageHeaderCmd = {(byte)0x16,(byte)0x28,(byte)0x00,(byte)0x21,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x00};

  byte[] ivGetImageImageCmd = {(byte)0x16,(byte)0x28,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

  byte[] ivGetImageThumbCmd = {(byte)0x16,(byte)0x28,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x09,(byte)0x60,(byte)0x00};

  byte[] ivUpdateFlashCmd = {(byte)0x16,(byte)0x2A,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x07,(byte)0x01,(byte)0x00};

  byte[] ivUpdateQualityCmd = {(byte)0x16,(byte)0x2A,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x06,(byte)0x02,(byte)0x00,(byte)0x00};

  byte[] ivUpdateNameCmd = {(byte)0x16,(byte)0x2A,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x22,(byte)0x00,(byte)0x02,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20};

  byte[] ivTakePictureCmd = {(byte)0x16,(byte)0x1B,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

  byte[] ivDeleteImagesCmd = {(byte)0x16,(byte)0x29,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

  byte[] ivUpdateDateTimeCmd = {(byte)0x16,(byte)0x2A,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x01,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};



  public Camera()
  {

	ivDebugLog = (DebugLog)Environment.getValue("DebugLog");

	this.setOpenStatus(false);

	return;
  }

//
// Return a list of all available COM ports on the computer
//

  public String[] getCOMPorts()
  {
	SerialPort[] ivPorts = SerialPort.getCommPorts();

	ivDebugLog.textOut(this,"Ports Length " + ivPorts.length);

    if (ivPorts != null && ivPorts.length > 0)
	{
		i = 0;
		ivCOMPorts = new String[ivPorts.length];
		for (SerialPort port : ivPorts)
		{
			ivCOMPorts[i] = port.getSystemPortName();
		}
	}

	return ivCOMPorts;
  }

//
// Open a connection to the camera
//

  public void openCamera()
  {

	boolean tvCOMOpen;
	
	int		tvPortSpeed;

	tvCOMOpen = false;

// If the camera has a previous open session, close it and start a new session

	if(this.getOpenStatus())
		this.closeCamera();

// Create a COM port instance according to user-selected COM port#

	SerialPort ivCameraPort = SerialPort.getCommPort((String)Environment.getValue("COMPort"));

	Environment.setValue("CameraPort",ivCameraPort);

	ivDebugLog.textOut(this,"New Port: " + ivCameraPort);

	ivDebugLog.textOut(this,"New SelectCOMPort " + (String)Environment.getValue("COMPort"));

	int QTbaud = ivCameraPort.getBaudRate();
	int QTdata = ivCameraPort.getNumDataBits();
	int QTstop = ivCameraPort.getNumStopBits();
	int QTparity = ivCameraPort.getParity();	
	ivDebugLog.textOut(this,(String)Environment.getValue("COMPort") + " Baud " + QTbaud + "  DataBits " + QTdata + "  StopBits " + QTstop + "  Parity " + QTparity);

// Initially, open COM port at 9600 baud, 8 bits, 1 stop bit, no parity

	ivCameraPort.setComPortParameters(9600,8,1,SerialPort.NO_PARITY);
	
	tvCOMOpen = ivCameraPort.openPort();
	
	ivDebugLog.textOut(this,"COM Port Open is " + tvCOMOpen);

// Clear DTR on COM port, which is the trigger that causes the camera to 'wake up' for communications

	ivCameraPort.clearDTR();

	ivDebugLog.textOut(this,"DTR Cleared");

// Sleep to give the camera time to respond and then attempt to read 7 bytes that camera will send when worken up. If no bytes are
// available to read at this point, throw an exception and do nothing else. This is likely because
// the camera is not switched on or an invalid COM port was selected by the user.

	this.setOpenStatus(false);
	
	try {
		Thread.sleep(100);
	} catch (Exception e) { e.printStackTrace(); } 

	if (ivCameraPort.bytesAvailable() < 1)
	{
		ivCameraPort.closePort();
	} else
	{
		ivDebugLog.textOut(this,"Camera Open has bytes to read: " + ivCameraPort.bytesAvailable());

		this.readCamera(0,7,true);

		ivDebugLog.hexOut(this,"Post DTR bytes: ",ivReadBuffer);


// Parse out the Camera model from the camera response. If byte 4 is 0xC8, it's a QT150, otherwise it's a QT100. This is important to know
// when saving/decoding images read from the camera since different compression algorithms are used for different camera models.
//
// For the future.... QT200 would be detected a different way. Basically, if byte 1 is not 0xA5 and the camera is pingable, it is QT200. 
// See https://github.com/colinleroy/a2tools/blob/master/src/quicktake/qt200-serial.c - specifically qt200_wakeup function for details

		switch(ivReadBuffer[3]) 
		{
			case (byte)0xC8:
				this.setCameraModel("QT150");
				break;
			default:
				this.setCameraModel("QT100");
				break;
		}

// Reply to the camera to indicate it's time to open a session. Adjust bytes 6 and 7 to reflect the user-requested port speed.
// Byte 12 is set to the appropriate checksum. Note the default Open cmd is for 9600 baud

		ivDebugLog.textOut(this,"ENVIRONMENT PORT SPEED: " + (String)Environment.getValue("PortSpeed"));

		tvPortSpeed = Integer.parseInt((String)Environment.getValue("PortSpeed"));

		ivDebugLog.textOut(this,"OpenCamera Select Port Speed " + tvPortSpeed);

		switch(tvPortSpeed) 
		{
			case 9600:
				ivOpenCmd[6] = (byte)0x25;
				ivOpenCmd[7] = (byte)0x80;
				ivOpenCmd[12] = (byte)0x80;
				break;
			case 19200:
				ivOpenCmd[6] = (byte)0x4B;
				ivOpenCmd[7] = (byte)0x00;
				ivOpenCmd[12] = (byte)0x26;
				break;
			case 38400:
				ivOpenCmd[6] = (byte)0x96;
				ivOpenCmd[7] = (byte)0x00;
				ivOpenCmd[12] = (byte)0x71;
				break;
			case 57600:
				ivOpenCmd[6] = (byte)0xE1;
				ivOpenCmd[7] = (byte)0x00;
				ivOpenCmd[12] = (byte)0xBC;
				break;
			default:
				break;
		}

		this.writeCamera(ivOpenCmd);

		ivDebugLog.textOut(this,"Wrote " + ivBufferLength + "  > " + ivOpenCmd.length);

// Wait a short time and then read 10 throwaway bytes from the camera. Note that bytes 3 and 4 should
// match the Open cmd bytes 6 and 7.

		this.readCamera(50,10,true);
		
		ivDebugLog.hexOut(this,"Read again " + ivBufferLength + " bytes: ",ivReadBuffer);

// Computer must now set the COM port to EVEN parity and then sleep for 1 second before 
// finalizing the port speed adjustment

		ivCameraPort.setParity(SerialPort.EVEN_PARITY);

		try {
			Thread.sleep(1000);
		} catch (Exception e) { e.printStackTrace(); } 
			
		this.setPortSpeed(tvPortSpeed);   // Set the port speed

		this.acquireMetaData();   // Read camera metadata

		this.setOpenStatus(true);
		
	}
		
	ivDebugLog.textOut(this,"OPEN ivCameraPort: " + ivCameraPort);

	return;
  }

//
// Close the comms session with the camera
//

  public void closeCamera()
  {

	if(this.getOpenStatus())
	{
		ivCameraPort = (SerialPort)Environment.getValue("CameraPort");

		ivCameraPort.closePort();
		ivDebugLog.textOut(this,"Port Closed");
	
		this.setOpenStatus(false);
	}

	return;
  }

//
// Set the camera port spoeed to the user-selected value
//

  public void setPortSpeed(int portSpeed)
  {
	int tvBytesRead;

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");

	ivDebugLog.textOut(this,"PORT ivCameraPort: " + ivCameraPort);
	ivDebugLog.textOut(this,"SetPortSpeed Select Port Speed " + portSpeed);

// modify the port speed command according to user-selected port speed

	switch(portSpeed) 
	{
		case 9600:
			ivPortSpeedCmd[13] = (byte)0x08;
			break;
		case 19200:
			ivPortSpeedCmd[13] = (byte)0x10;
			break;
		case 38400:
			ivPortSpeedCmd[13] = (byte)0x20;
			break;
		case 57600:
			ivPortSpeedCmd[13] = (byte)0x30;
			break;
		default:
			ivPortSpeedCmd[13] = (byte)0x08;
			break;
	}

	ivDebugLog.hexOut(this,"Port Speed Byte: ",ivPortSpeedCmd);		
		
	this.pingCamera();		// Ping the camera to make sure it's there and ready

	this.writeCamera(ivPortSpeedCmd);

	ivDebugLog.textOut(this,"Wrote " + ivBufferLength + "  > " + ivPortSpeedCmd.length);

// Camera should send a 1 byte ACK, to which the computer will respond with a 1 byte ACK

	this.readCamera(50,1,true);

	ivDebugLog.textOut(this,"CAM ACK BYTES READ " + ivBufferLength);

	ivDebugLog.hexOut(this,"CAM ACK HERE IS WHAT YOU WROTE > ",ivCompACKCmd);

	this.writeCamera(ivCompACKCmd);

	ivDebugLog.textOut(this,"COMP ACK Wrote " + ivBufferLength + "  > " + ivCompACKCmd.length);

// Sleep then toggle the COM port to the selected speed so that camera and computer are using the same speed

	try {
		Thread.sleep(150);
	} catch (Exception e) { e.printStackTrace(); } 
	
	if(!ivCameraPort.setBaudRate(portSpeed))
		ivDebugLog.textOut(this,"BAUD NOT SET");

// The camera will respond with 1024 bytes of 0xAA that needs to read and then thrown away

	this.readCamera(50,1024,false);

	ivDebugLog.hexOut(this,"Read again " + ivBufferLength + " bytes: ",ivReadBuffer);

// ACK to camera that data received, then read camera's ACK

	ivDebugLog.hexOut(this,"HERE IS WHAT YOU WROTE > ",ivCompACKCmd);

	this.writeCamera(ivCompACKCmd);

	ivDebugLog.textOut(this,"Wrote " + ivBufferLength + "  > " + ivCompACKCmd.length);

	this.readCamera(50,1,true);

	ivDebugLog.textOut(this,"BYTES READ " + ivBufferLength);

	try {
		Thread.sleep(100);
	} catch (Exception e) { e.printStackTrace(); } 

	return;
  }


//
// Ping The Camera
//

  public boolean pingCamera()
  {

	boolean tvPing;

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	
	tvPing = false;

// Send the ping command to the camera

	ivDebugLog.hexOut(this,"HERE IS WHAT YOU WROTE AGAIN > ",ivPingCmd);
	ivDebugLog.textOut(this,"PING ivCameraPort: " + ivCameraPort);

	this.writeCamera(ivPingCmd);

	ivDebugLog.textOut(this,"Wrote " + ivBufferLength + "  > " + ivPingCmd.length);

// Now wait for the camera to ACK the ping

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Read last" + ivBufferLength + " bytes: ",ivReadBuffer);
	
	if(ivReadBuffer[0] == ivCameraACKResp[0])
		tvPing = true;

	return tvPing;
  }


//
// Get Camera metadata and set corressponding instance vars
//

  public void acquireMetaData()
  {

	int tvDay, tvMonth, tvYear,tvLevel,tvTaken,tvRemain,tvHour,tvMinute,tvSecond;
	byte[] tvNameArray;
	String tvName;
	

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	
	ivDebugLog.textOut(this,"ABOUT TO AQUIRE");

// Send the metadata command to the camera and then read the 1-byte camera ACK response
		
//	this.pingCamera();		

	this.writeCamera(ivMetadataCmd);

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Camera Info " + ivBufferLength + " bytes: ",ivReadBuffer);		

// Acknowledge to camera that metadata was received

	this.writeCamera(ivCompACKCmd);

// Read 128 of camera response data

	this.readCamera(50,128,true);

	ivDebugLog.hexOut(this,"Camera Info Hex " + ivReadBuffer.length + " bytes: ",ivReadBuffer);
	
	ivDebugLog.textOut(this,"Camera info RAW: " + ivReadBuffer);

// Parse out the Data and Time from metadata

	tvMonth = ivReadBuffer[16];
	tvDay = ivReadBuffer[17];
	tvYear = ivReadBuffer[18];
	tvHour = ivReadBuffer[19];
	tvMinute = ivReadBuffer[20];
	tvSecond = ivReadBuffer[21];

// Parse the Battery Level reading

	tvLevel = ivReadBuffer[2];

// Parse the number of pictures Taken, and the number of pictures Remaining to be taken (at current Picture Quality)

	tvTaken = ivReadBuffer[4];
	
	tvRemain = ivReadBuffer[6];

// Determine the current Flash setting 		

	switch(ivReadBuffer[22]) 
	{
		case 0:
			this.setFlash("Auto");
			break;
		case 1:
			this.setFlash("Disabled");
			break;
		case 2:
			this.setFlash("Forced");
			break;
		default:
			this.setFlash("Unknown");
			break;
	}

// Determine the current Picture Quality setting
		
	switch(ivReadBuffer[27])
	{
		case 32:
			this.setQuality("Standard 320x240");
			this.setQualityHigh(false);
			break;
		case 16:
			this.setQuality("High 640x480");
			this.setQualityHigh(true);
			break;
		default:
			this.setQuality("Unknown");
			this.setQualityHigh(false);
			break;
	}

	ivDebugLog.textOut(this, "Camera Date (dd/mm/yy): " + tvDay + "/" + tvMonth + "/" + tvYear + "   Time: " + tvHour + ":" + tvMinute);
	ivDebugLog.textOut(this,"Battery Level " + tvLevel + "%   Pics Taken:" + tvTaken + "  Remaining:" + tvRemain);
	ivDebugLog.textOut(this,"QUALITY HIGH? " + this.getQualityHigh());

// Set Class instance vars that can later be accessed by other classes

	this.setDate(tvMonth,tvDay,tvYear);
	this.setTime(tvHour,tvMinute,tvSecond);
	this.setBatteryLevel(tvLevel);
	this.setTaken(tvTaken);
	this.setRemaining(tvRemain);

// Parse the Camera Name and set the Class instance var 
		
	tvNameArray = new byte[32];
	System.arraycopy(ivReadBuffer, 47, tvNameArray, 0, 32);

	tvName = new String(tvNameArray);
	this.setName(tvName);

	ivDebugLog.textOut(this,"Camera Name: " + tvName);
	
	return;

  }	

//
// Read a specific image's header info and then return an image object 
//
  
  public Image createImage(int imageNum)
  {

	Image tvImage;

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	
	ivDebugLog.textOut(this,"ABOUT TO GET PHOTO HEADER");

// First get the image header. Modify the GetImageHeader command to contain the specific image# to read. 
// Then send the command to the camera and then read the 1-byte camera ACK response
//
// The header will contain details about the image, including size, width, height, resolution, date/time, etc.
		
//	this.pingCamera();		

	ivGetImageHeaderCmd[6] = (byte)imageNum;

	this.writeCamera(ivGetImageHeaderCmd);
	
	this.readCamera(50,1,true);
	
// Acknowledge to camera that ACK was received

	this.writeCamera(ivCompACKCmd);
	
// Read the actual header data

	this.readCamera(50,64,true);
	
// create a new Image object

	tvImage = new Image(imageNum, ivReadBuffer, ivCameraModel);
	
// Now get the image thumbnail from the camera and give it to the image object. We will not read the actual image from the camera right away
// to avoid memory overhead. The image will only be read in the event the user asks to save/display the image
	
	this.transferThumbData(tvImage);
	
	return tvImage;
	
  }

//
// For a specific image, ask the camera for the thumbnail data and store it in the image object. A thumbnail is a 80x60 pixel version of the image
//

  private void transferThumbData(Image image)
  {
	
	boolean	tvThumbComplete;
	
	byte[]	tvThumbData;
	
	int		tvBytesRead,tvBufferToRead,tvThumbSize;	

// First modify the Get thumbnail command with the image number, then send the command to the camera

	ivGetImageThumbCmd[6] = (byte)image.getImageNum();

//	this.pingCamera();
	
	this.writeCamera(ivGetImageThumbCmd);
	
	this.readCamera(50,1,true);
	
// Acknowledge to camera that ACK was received

	this.writeCamera(ivCompACKCmd);
		
// Read the actual thumbnail data. It will be 2400 bytes of data, read to 512-byte chunks

	tvThumbComplete = false;
	
	tvThumbSize = 2400;   // standard thumbnail image size for qt100/150
	
	tvThumbData = new byte[tvThumbSize];
	
	tvBytesRead = 0;
	
	tvBufferToRead = 512;
	
	while (!tvThumbComplete)
	{
		ivDebugLog.textOut(this,"tranferThumb ABOUT TO READ");
		this.readCamera(50,tvBufferToRead,true);
		
		System.arraycopy(ivReadBuffer,0,tvThumbData,tvBytesRead,ivBufferLength);
		
		tvBytesRead = tvBytesRead + ivReadBuffer.length;	
		
		ivDebugLog.textOut(this,"transferThumb UPDATED BYTES READ: " + tvBytesRead);

		if(ivBufferLength < 512)
		{
			tvThumbComplete = true;
		}else
		{
			this.writeCamera(ivCompACKCmd);
					
			if((tvThumbSize - tvBytesRead) < 512)
				tvBufferToRead = tvThumbSize - tvBytesRead;

			ivDebugLog.textOut(this,"tranferThumb ACK SENT: " );

		}

	}
	
	image.setThumbData(tvThumbData);
	
	return;
	
  }

//
// For a specific image, ask the camera for the image data and store it in the image object. Since this can take a bit of time,
// a specified progress bar will be update during the process.
//

  public void transferImageData(Image image, JProgressBar progress)
  {
	
	boolean	tvImageComplete;
	
	byte[]	tvImageData, tvImageSizeBytes;
	
	int		tvBytesRead,tvBufferToRead,tvImageSize;	
	
	float	tvProgressStep;

// Now go get the actual image data. First set the image number and image size bytes in the Cmd to reflect the
// Header values for those elements

	ivGetImageImageCmd[6] = (byte)image.getImageNum();

	tvImageSize = image.getImageSize();
	tvImageSizeBytes = image.getImageSizeHex();
	ivGetImageImageCmd[7] = tvImageSizeBytes[0];
	ivGetImageImageCmd[8] = tvImageSizeBytes[1];
	ivGetImageImageCmd[9] = tvImageSizeBytes[2];
		
//	this.pingCamera();		
	
	this.writeCamera(ivGetImageImageCmd);
	
	this.readCamera(50,1,true);
	
// Acknowledge to camera that ACK was received

	this.writeCamera(ivCompACKCmd);
		
// Read the actual image data according the image size found the in the header. The data will be read in 512-byte chunks, and after each chunk 
// is read, a progress bar will be updated

	tvImageComplete = false;
	
	tvImageSize = image.getImageSize();
	
	tvImageData = new byte[tvImageSize];
	
	tvProgressStep = 0;
	
	progress.setValue(0);
	
	tvBytesRead = 0;
	
	tvBufferToRead = 512;
	
	while (!tvImageComplete)
	{

		this.readCamera(50,tvBufferToRead,true);
		
		System.arraycopy(ivReadBuffer,0,tvImageData,tvBytesRead,ivBufferLength);
		
//		ivDebugLog.textOut(this,"tranferImage BYTES READ: " + ivReadBuffer.length + "    tvBytesRead: " + tvBytesRead);
//		ivDebugLog.hexOut(this,"tranferImage buffer: ", ivReadBuffer);
//		ivDebugLog.hexOut(this,"tranferImage accum : ", tvImageData);
		
		tvBytesRead = tvBytesRead + ivReadBuffer.length;

		tvProgressStep = ((float)tvBytesRead/(float)tvImageSize)*100;  // Calc how much of the image has been read so far
		
//		ivDebugLog.textOut(this,"PROGRESS: BytesRead " + tvBytesRead + "     ImageSize " + tvImageSize + "     Progress " + tvProgressStep);

		progress.setValue(Math.round(tvProgressStep));   // Update progress bar
		
//		ivDebugLog.textOut(this,"tranferImage UPDATED BYTES READ: " + tvBytesRead);

		if(ivBufferLength < 512)
		{
			progress.setValue(100);
			tvImageComplete = true;
		}else
		{
			this.writeCamera(ivCompACKCmd);
					
			if((tvImageSize - tvBytesRead) < 512)
				tvBufferToRead = tvImageSize - tvBytesRead;

//			ivDebugLog.textOut(this,"tranferImage ACK SENT: " );

		}

	}
	
//	ivDebugLog.hexOut(this,"Read Image Data: ",tvImageData);
	
	image.setImageData(tvImageData);
	
	return;
	
  }

//
// Read some data from the camera. The input parms drive this process as follows:
//
// waitTime - How many ms to wait for the camera to pass some data
//
// bufferLength - How many bytes to read from the camera
//
// accum - Boolean to denote whether or not the bytes read should be 'saved' into a buffer (class instance var) for processing by other method.
//         This is useful since sometimes bytes read from the camera are throw-away.
//

  private void readCamera(int waitTime, int bufferLength, boolean accum)
  {
	int		tvBytesRead;
	byte[]	tvAccumBuffer = {0x00};

	tvBytesRead = 0;

//		ivDebugLog.textOut(this,"readCamera  waitTime: " + waitTime);
//		ivDebugLog.textOut(this,"readCamera  bufferLength: " + bufferLength);
//		ivDebugLog.textOut(this,"readCamera  accum: " + accum);
	
	if(accum)
		tvAccumBuffer = new byte[bufferLength];

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");

	while (tvBytesRead < bufferLength)   // Read from camera until the expecte number of bytes are read
	{
		try {
			while (ivCameraPort.bytesAvailable() == 0)
				Thread.sleep(waitTime);
		} catch (Exception e) { e.printStackTrace(); } 

//		ivDebugLog.textOut(this,"readCamera bytes to read: " + ivCameraPort.bytesAvailable());
		
		ivReadBuffer = new byte[ivCameraPort.bytesAvailable()];
		ivBufferLength = ivCameraPort.readBytes(ivReadBuffer, ivReadBuffer.length);	

		if(accum)
			System.arraycopy(ivReadBuffer,0,tvAccumBuffer,tvBytesRead,ivBufferLength);
				
//		ivDebugLog.textOut(this,"readCamera BYTES READ: " + ivReadBuffer.length + "    tvBytesRead: " + tvBytesRead);
//		ivDebugLog.hexOut(this,"readCamera buffer: ", ivReadBuffer);
//		ivDebugLog.hexOut(this,"readCamera accum : ", tvAccumBuffer);
		
		tvBytesRead = tvBytesRead + ivReadBuffer.length;		
			
    }
  
	if(accum)   // If read bytes are to be saved, move them to the appropriate instance variable
	{
		ivReadBuffer = new byte[bufferLength];
		System.arraycopy(tvAccumBuffer,0,ivReadBuffer,0,bufferLength);
		ivBufferLength = ivReadBuffer.length;
	}
		
	return;
  }

//
// Write a string of bytes to the camera - basically a byte string with a camera command
//

  private void writeCamera(byte[] command)
  {
		ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
		
		ivBufferLength = ivCameraPort.writeBytes(command,command.length);
		
		return;
  }

//
// Modify Flash Mode command with new flash mode
//

  public void updateFlashMode(int mode)
  {

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	  
// Update the Set Flash Cmd accordingly	

	switch(mode) 
	{
		case 0:
			ivUpdateFlashCmd[13] = (byte)0x00;
			break;
		case 1:
			ivUpdateFlashCmd[13] = (byte)0x01;
			break;
		case 2:
			ivUpdateFlashCmd[13] = (byte)0x02;
			break;
		default:
			break;
	}

	this.writeCamera(ivUpdateFlashCmd);

	ivDebugLog.textOut(this,"Update Flash " + ivBufferLength + "  > " + ivUpdateFlashCmd.length);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Update Flash Read last" + ivBufferLength + " bytes: ",ivReadBuffer);
	
//	if(ivReadBuffer[0] == ivCameraACKResp[0])
//	tvSuccess = true;
	
	this.acquireMetaData();

	return;
  }

//
// Modify Camera Name command with new camera name
//

  public void updateCameraName(byte[] name)
  {
	int		tvLimit;
	byte[]	tvName;

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	  
// Update the Set Quality Cmd accordingly	

	tvName = new byte[32];
	Arrays.fill(tvName, (byte)0x20);
	
	tvLimit = 32;
	
	if(name.length < 32)
		tvLimit = name.length;
		
	System.arraycopy(name, 0, tvName, 0, tvLimit);

	System.arraycopy(tvName, 0, ivUpdateNameCmd, 13, 32);

	this.writeCamera(ivUpdateNameCmd);

	ivDebugLog.textOut(this,"Update Name " + ivBufferLength + "  > " + ivUpdateNameCmd.length);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Update Name last" + ivBufferLength + " bytes: ",ivReadBuffer);
	
//	if(ivReadBuffer[0] == ivCameraACKResp[0])
//	tvSuccess = true;
	
	this.acquireMetaData();

	return;
  }

//
// Modify Date Time command with new date/time
//

  public void updateDateTime(byte[] dateTime)
  {

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	  
// Update camera dateTime cmd with new dtae and time values

	ivUpdateDateTimeCmd[13] = dateTime[0];
	ivUpdateDateTimeCmd[14] = dateTime[1];
	ivUpdateDateTimeCmd[15] = dateTime[2];
	ivUpdateDateTimeCmd[16] = dateTime[3];
	ivUpdateDateTimeCmd[17] = dateTime[4];
	ivUpdateDateTimeCmd[18] = dateTime[5];

	this.writeCamera(ivUpdateDateTimeCmd);

	ivDebugLog.textOut(this,"Update DateTime " + ivBufferLength + "  > " + ivUpdateNameCmd.length);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Update Date Time last" + ivBufferLength + " bytes: ",ivReadBuffer);
	
	this.acquireMetaData();

	return;
  }

//
// tell camera to take a picture
//

  public void takePicture()
  {
	  
	int	tvInt;

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");

	this.writeCamera(ivTakePictureCmd);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
				
	tvInt = Integer.parseInt(this.getTaken());
	this.setTaken(tvInt+1);
				
	tvInt = Integer.parseInt(this.getRemaining());
	this.setRemaining(tvInt-1);

	return;
  }

//
// Modify Quality Mode command with new quality mode
//

  public void updateQualityMode(boolean mode)
  {

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");
	  
// Update the Set Quality Cmd accordingly	

	if(mode)
	{
		ivUpdateQualityCmd[13] = (byte)0x10;
	} else
	{
		ivUpdateQualityCmd[13] = (byte)0x20;
	}

	this.writeCamera(ivUpdateQualityCmd);

	ivDebugLog.textOut(this,"Update Quality " + ivBufferLength + "  > " + ivUpdateQualityCmd.length);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
	
	ivDebugLog.hexOut(this,"Update Quality Read last" + ivBufferLength + " bytes: ",ivReadBuffer);
	
//	if(ivReadBuffer[0] == ivCameraACKResp[0])
//	tvSuccess = true;
	
	this.acquireMetaData();

	return;
  }

//
// Tell camera to delete all its images
//

  public void deleteImages()
  {

	ivCameraPort = (SerialPort)Environment.getValue("CameraPort");

	this.writeCamera(ivDeleteImagesCmd);

// Now wait for the camera to ACK

	this.readCamera(50,1,true);
				
	this.acquireMetaData();

	return;
  }


//
// Below are various Set and Get for interesting variables
//

  private void setOpenStatus(boolean open)
  {
	  ivOpenStatus = open;
	  
	  return;
  }
  
  public boolean getOpenStatus()
  {
	  return ivOpenStatus;
  } 

  private void setCameraModel(String model)
  {
	  ivCameraModel = model;
	  
	  ivDebugLog.textOut(this,"Model is " + ivCameraModel);
	  
	  return;
  }
  
  public String getCameraModelodel()
  {
	  return ivCameraModel;
  } 
  
  public void setDate(int month,int day,int year)
  { 
	ivDateMM = month;
	ivDateDD = day; 
	ivDateYY = year;

	return;
  }

  public String getDisplayDate()
  {
	String tvDate;
	
	tvDate = String.format("%02d",ivDateMM) + "/" + String.format("%02d",ivDateDD) + "/" + String.format("%02d",ivDateYY); 
	
	return tvDate;
  }

  public void setTime(int hour,int minute, int second)
  {
	ivTimeHH = hour;
	ivTimeMM = minute;
	ivTimeSS = second;	

	return;
  }

  public String getDisplayTime()
  {
	String tvTime;
	  
	tvTime = String.format("%02d",ivTimeHH) + ":" + String.format("%02d",ivTimeMM) + ":" + String.format("%02d",ivTimeSS); 
	
	return tvTime;
  }

  public void setBatteryLevel(int level)
  {
	ivBatteryLevel = level + "%"; 

	return;
  }

  public String getBatteryLevel()
  {
	return ivBatteryLevel;
  }

  public String getPortSpeed()
  {
	String tvPortSpeed;
	
	tvPortSpeed = Integer.toString(ivCameraPort.getBaudRate());
	
	return tvPortSpeed;
  }

  public void setTaken(int taken)
  {
	ivPicsTaken = Integer.toString(taken); 

	return;
  }

  public String getTaken()
  {
	return ivPicsTaken;
  }

  public void setRemaining(int remain)
  {
	ivPicsRemaining = Integer.toString(remain); 

	return;
  }

  public String getRemaining()
  {
	return ivPicsRemaining;
  }

  public void setName(String name)
  {
	ivName = name; 

	return;
  }

  public String getName()
  {
	return ivName;
  }

  public void setFlash(String mode)
  {
	ivFlash = mode; 

	return;
  }

  public String getFlash()
  {
	return ivFlash;
  }

  public void setQuality(String quality)
  {
	ivQuality = quality; 

	return;
  }

  public String getQuality()
  {
	return ivQuality;
  }

  public void setQualityHigh(boolean quality)
  {
	ivQualityBool = quality; 

	return;
  }

  public boolean getQualityHigh()
  {
	return ivQualityBool;
  }

}
