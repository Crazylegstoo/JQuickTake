/******************************************************************************
 *
 * JQTProperties.java
 *
 * Read/Write values to external config.properties to be used for 
 * subsequent executions of the QT app
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class JQTProperties
{
	File			ivFile;
	URL				ivResource;
	String			ivSavePath;
	String			ivStatus;
	InputStream		ivInput;
	OutputStream	ivOutput;
	Properties		ivProps;

  public void JQTProperties()
  {
	try
	{
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String appConfigPath = rootPath + "config.properties";

//		ivResource = ClassLoader.getSystemClassLoader().getResource("config.properties");

//		ivFile = Paths.get(ivResource.toURI()).toFile();

//		ivInput = new FileInputStream(ivFile);

        ivProps = new Properties();
		
		ivProps.load(new FileInputStream(appConfigPath));


//		ivProps.load(ivInput);
		
		System.out.println("JQT ivProps: " + ivProps);
			  
	}  catch (Exception e) 
		{ 
            e.printStackTrace();
		}
	return;
  }

// Read the 'Save File Path' property

  public String getSavePath()
  {
	try
	{
		ivSavePath = ivProps.getProperty("save.path");

	}  catch (Exception e) 
		{ 
            e.printStackTrace();
		}
		
	return ivSavePath;
  }

// Update the 'Save File Path' property

  public void setSavePath(String newPath)
  {
        try {
		
            // set the properties value
            ivProps.setProperty("save.path", newPath);

        } catch (Exception e) 
		{
            e.printStackTrace();
        }
		
	return;
  }

// Write properties back to the filesystem

  public void storeProps()
  {
        try {

            // save properties to project root folder
            ivProps.store(ivOutput, null);

        } catch (Exception e) 
		{
            e.printStackTrace();
        }
		
	return;
  }

}
