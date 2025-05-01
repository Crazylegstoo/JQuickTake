/******************************************************************************
 *
 * DebugLog.java
 *
 * When 'debug' parm is present, this class allows messages to
 * be written to the console for debugging.
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.io.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class DebugLog
{
  HexFormat 	ivPound;
  int			ivLineNum;
  String		ivPath, ivDirectory, ivLoggingSavePath;
  File			ivFile;
  FileWriter	ivFileWriter;
  PrintWriter	ivPrintWriter;
  
  boolean		ivDebugMode, ivDebugVerbose;
  
  Properties		ivProps;
  
  public void DebugLog()
  {

	this.setLineNum(0);  
	
	ivPath = " ";
	
	return;
  }

  public void initLoggingDirectory()
  {
	
	ivProps = (Properties)Environment.getValue("ConfigProps");
	
	ivLoggingSavePath = ivProps.getProperty("logging.path");
	
	if(ivLoggingSavePath == null)
	{
		ivFile = new File(System.getProperty("user.home"));
	} else
	{
		ivFile = new File(ivLoggingSavePath);
	}
	
	this.setDirectory(ivFile.getAbsolutePath());

    return;
  }

// Write out a descriptive message followed by the hex values for a specified byte array

  public void hexOut(String desc, byte[] buffer)
  {
	String 				tvOut, tvLogOut, tvMsg;
	
	byte[]				tvTrim;
	
	StackTraceElement	tvElement;
	
    if(ivDebugMode)
	{

		tvElement = this.getCaller();
		
		ivPound = HexFormat.ofDelimiter(", ").withPrefix("#");

		if(!ivDebugVerbose && buffer.length > 200)
		{
			tvTrim = new byte[200];
			System.arraycopy(buffer,0,tvTrim,0,200);
			tvOut = ivPound.formatHex(tvTrim);
			tvMsg = "...<NON-VERBOSE LOGGING>";
		} else {
			tvOut = ivPound.formatHex(buffer);
			tvMsg = "";
		}
		
		tvLogOut = this.getLineNum() + ": " + this.timeStamp() + " " + tvElement.getClassName().substring(25) + "." + tvElement.getMethodName() + "(" + tvElement.getLineNumber() + "): " + desc + " - Length: " + buffer.length + " >> " + tvOut + tvMsg + "\n";	
		
		try
		{
	  
			ivPrintWriter.print(tvLogOut);
			ivPrintWriter.flush();
	  
		} catch(Exception e)
			{
				System.out.println("DebugLog hexOut exception: " + e);
			}
			
	}

	return;
  }

// Write out a descriptive message

  public void textOut(String desc)
  {
	String				tvLogOut, tvOut;  
	StackTraceElement	tvElement;

    if(ivDebugMode)
	{
		tvElement = this.getCaller();

		if(!ivDebugVerbose && desc.length() > 200)
		{
			tvOut = desc.substring(0,199) + "...<NON-VERBOSE LOGGING>";
		} else {
			tvOut = desc;
		}
		
		tvLogOut = this.getLineNum() + ": " + this.timeStamp() + " " + tvElement.getClassName().substring(25) + "." + tvElement.getMethodName() + "(" + tvElement.getLineNumber() + "): " + tvOut + "\n";
		
		try
		{
	  
			ivPrintWriter.print(tvLogOut);
			ivPrintWriter.flush();
	  
		} catch(Exception e)
			{
				System.out.println("DebugLog textOut exception: " + e);
			}
	}

	return;
  }

// Write out a descriptive message

  public void stackOut(Exception ex)
  {

    if(ivDebugMode)
	{
		try
		{
	  
			ex.printStackTrace(ivPrintWriter);
			ivPrintWriter.flush();
	  
		} catch(Exception e)
			{
				System.out.println("DebugLog stackOut exception: " + e);
			}
	}

	return;
  }
  
// Get StackTrace containing caller info

  private StackTraceElement getCaller()
  {
	StackTraceElement	tvElement;
	
	tvElement = Thread.currentThread().getStackTrace()[3];

	return tvElement;
  }

// Format current time

  private String timeStamp()
  {
	LocalTime			tvTime;
	DateTimeFormatter	tvFormat;
	String				tvFormatTime;
	
	tvTime = LocalTime.now();

    tvFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    tvFormatTime = tvTime.format(tvFormat);

	return tvFormatTime;
  }

// Get Debug Mode

  public boolean getMode()
  {
	  return ivDebugMode;
  }

// Set Debug Mode

  public void setMode(boolean debugMode)
  {
	  ivDebugMode = debugMode;
	  return;
  }

// Get verbose Mode

  public boolean getVerbose()
  {
	  return ivDebugVerbose;
  }

// Set Verbose Mode

  public void setVerbose(boolean verbose)
  {
	  ivDebugVerbose = verbose;
	  return;
  }

// Get log file directory

  public String getDirectory()
  {
	  return ivDirectory;
  }

// Set log file directory

  public void setDirectory(String directory)
  {
	  ivDirectory = directory;
			
	  ivProps.setProperty("logging.path", ivDirectory);

	  return;
  }

// Set log file path

  public void setPath(String path)
  {
	LocalDateTime		tvDateTime;
	DateTimeFormatter	tvFormat;
	String				tvFormatDateTime, tvFileSeparator;
	
	this.setDirectory(path);

	tvDateTime = LocalDateTime.now();

    tvFormat = DateTimeFormatter.ofPattern("MMM dd yyyy HH-mm-ss");

    tvFormatDateTime = tvDateTime.format(tvFormat);
	
	tvFileSeparator = System.getProperty("file.separator");

    ivPath = path + tvFileSeparator + "JQuickTake Log " + tvFormatDateTime + ".log";

	  
	try
	{
	  
	  if(ivPrintWriter != null)
		  ivPrintWriter.close();
	  
	  ivFileWriter = new FileWriter(ivPath);
	  ivPrintWriter = new PrintWriter(ivFileWriter);
	  
	} catch(Exception e)
		{
		  System.out.println("DebugLog setPath exception: " + e);
		}
	  
	  return;
  }

// close log file

  public void closeLog()
  {

    if(ivDebugMode)
	{
		try
		{
	  
			ivPrintWriter.flush();
		
			ivPrintWriter.close();
	  
		} catch(Exception e)
			{
			System.out.println("DebugLog closeLog exception: " + e);
			}
	}
		
	  return;
  }
  
// Set Line#

  private void setLineNum(int init)
  {
	  ivLineNum = init;
	  return;
  }
  
// Get Line#

  private int getLineNum()
  {
	  ivLineNum++;
	  return ivLineNum;
  }
  

}
