
/**
 * Environment keeps track of the current environment settings for
 * the system.
 *
 * @written:  March 2001
 * @author :  Kevin Godin
 */
 
package com.crazylegs.JQuickTake;

import java.util.*;

public class Environment
{
/*==============================================================*/
/*                     Class Constants                          */
/*--------------------------------------------------------------*/

/*==============================================================*/
/*                    Class Variables                           */
/*--------------------------------------------------------------*/
   private static HashMap<String, Object>  ivMap;

/*==============================================================*/
/*                     Constructors                             */
/*--------------------------------------------------------------*/
/**
  */

  public Environment(){}

/*==============================================================*/
/*                     Class Methods                            */
/*--------------------------------------------------------------*/
/** This method will set the current applet context.
 *
 */

   public static void init(HashMap<String, Object> map)
   {
     ivMap   = map;
   }

/*==============================================================*/
/*                     Class Methods                            */
/*--------------------------------------------------------------*/
/** This method will set the current applet context.
 *
 */
   public static void   setValue(String tag, Object value)
   {
     ivMap.put(tag, value);
   }

/*--------------------------------------------------------------*/
/** This method will get the current applet context.
 *
 */
   public static Object getValue(String tag)
   {
     return ivMap.get(tag);
   }

/*==============================================================*/
/*                     End of Listing                           */
/*--------------------------------------------------------------*/

}
