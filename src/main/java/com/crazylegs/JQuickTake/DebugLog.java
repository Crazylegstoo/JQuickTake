/******************************************************************************
 *
 * DebugLog.java
 *
 * When commandline 'debug' parm is present, this class allows messages to
 * be written to the console for debugging.
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import java.util.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

public class DebugLog
{
  HexFormat ivPound;

  public void DebugLog()
  {
	return;
  }

// Write out a descriptive message followed by the hex values for a specified byte array

  public void hexOut(Object caller,String desc, byte[] buffer)
  {
	String tvOut;

    if((boolean)Environment.getValue("DebugMode"))
	{
		ivPound = HexFormat.ofDelimiter(", ").withPrefix("#");
		tvOut = ivPound.formatHex(buffer);
//	System.out.println(caller.getClass().getName() + ">>"+ caller.getClass().getEnclosingMethod().getName() + ": " + desc + ": Length: " + buffer.length + " >> " + tvOut);
		System.out.println(caller.getClass().getName() + ": " + desc + ": Length: " + buffer.length + " >> " + tvOut);
	}

	return;
  }

// Write out a descriptive message

  public void textOut(Object caller,String desc)
  {

    if((boolean)Environment.getValue("DebugMode"))
		System.out.println(caller.getClass().getName() + ": " + desc);

	return;
  }

}
