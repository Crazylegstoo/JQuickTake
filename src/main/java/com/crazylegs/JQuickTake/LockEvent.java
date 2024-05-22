/**
 * 
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 *
 * LockEvent class encapsulates an event that other classes can throw to indicate they are doing some operation
 * that requires certain UI functions to be locked. Practically speaking, the main JQuickTake class will be looking for these
 * events to lock/unlock various tabs on the UI. Example: When the 'Save Image' tab is actively saving an image to disk, it will throw this
 * event to ensure that the Connect and Control tabs are locked. The purpose here is to prevent the User from driving any other camera operations
 * While Save is attemtping to read images from the camera.
 *
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
