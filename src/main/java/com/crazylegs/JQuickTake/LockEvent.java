/**
 * 
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/

package com.crazylegs.JQuickTake;


public class LockEvent
{
	Object	ivOrigin;
	boolean	ivStatus;
	
	public LockEvent(Object origin, boolean status)
	{
		ivOrigin = origin;
		ivStatus = status;
		
		return;
	}
	
	public Object getOrigin()
	{
		return ivOrigin;
	}
	
	public boolean getStatus()
	{
		return ivStatus;
	}
}
