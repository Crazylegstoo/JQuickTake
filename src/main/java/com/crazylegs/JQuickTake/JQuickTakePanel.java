/******************************************************************************
 *
 * JQuickTakePanel.java
 *
 ******************************************************************************/

package com.crazylegs.JQuickTake;

import com.crazylegs.JQuickTake.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Kevin Godin
 * @version $Revision: 1.0 $
 **/
public class JQuickTakePanel extends JPanel
{

  JLabel   ivMessage;
        
  public JQuickTakePanel()
  {
    this.setLayout(null);
    this.setBounds(1,1,600,400);
    this.setBorder(new EtchedBorder());

    ivMessage = new JLabel();
    ivMessage.setBounds(10,350,400,25);
    this.add(ivMessage);
  }
        
  public void refreshView()
  {
  }
    
  public void setMessage(String msg)
  {
    ivMessage.setText(msg);
  }
  
}
