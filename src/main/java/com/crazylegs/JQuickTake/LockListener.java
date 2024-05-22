
/**
 * 
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 *
 * LockEventListener is an interface to be supported by any class that wants to receive LockEvent events. Where JQuickTake
 * is concerned, it is the only class that currently would implement this interface. See LockEvent for details on its purpose. 
 *
 **/

package com.crazylegs.JQuickTake;


public interface LockListener
{

  public void handleLockEvent(LockEvent e);

}
