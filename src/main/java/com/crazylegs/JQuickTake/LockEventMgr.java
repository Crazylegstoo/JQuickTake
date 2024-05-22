/******************************************************************************
 *
 * LockEventMgr.java
 *
 ******************************************************************************/

//package xxx;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 *
 * LockEventMgr alls other classes to register as listeners for LockEvent events. Other classes may then create LockEvent events and then 
 * ask LockEventMgr to pass those events to any registered listener. See LockEvent for details on its purpose. 
 *
 * **/

package com.crazylegs.JQuickTake;

import java.util.*;

public class LockEventMgr
{

  Vector  ivListener;
        
  public LockEventMgr()
  {
    ivListener = new Vector();
  }
  
//
// Add Listeners to the Manager
//
  public void addListener(LockListener listener)
  {
    ivListener.addElement(listener);
  }
  
//
// Notify Listeners that there has been a change
//
  public void notifyListeners(LockEvent event)
  {
    LockListener	ivTarget;
    
    for(Enumeration e = ivListener.elements(); e.hasMoreElements();)
    {
      ivTarget = (LockListener)e.nextElement();
      ivTarget.handleLockEvent(event);
    }
         
  }

}
