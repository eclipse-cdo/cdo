/***************************************************************************
 * Copyright (c) 2004-2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.thread;


import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ThreadInterruptedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public final class DeadlockDetector
{
  public static boolean DETECTION = false;

  public static final boolean COMPLETE_TRACE = true;

  protected static final HashMap locks = new HashMap();

  public static void preLock()
  {
    if (DETECTION)
    {
      Object old = locks.put(Thread.currentThread(), identifySource());
      if (old != null) throw new ImplementationError("Don't nest deadlock detection!");
    }
  }

  public static void postLock()
  {
    if (DETECTION) locks.remove(Thread.currentThread());
  }

  public static void sleep(long millis) throws ThreadInterruptedException
  {
    preLock();

    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      throw new ThreadInterruptedException(ex);
    }
    finally
    {
      postLock();
    }
  }

  public static void wait(Object object) throws ThreadInterruptedException
  {
    preLock();

    try
    {
      object.wait();
    }
    catch (InterruptedException ex)
    {
      throw new ThreadInterruptedException(ex);
    }
    finally
    {
      postLock();
    }
  }

  public static void dump()
  {
    System.out.println();
    System.out.println("Deadlock Detection Dump");
    System.out.println("=====================================================================");

    Map.Entry[] array = (Map.Entry[]) locks.entrySet().toArray(new Map.Entry[locks.size()]);
    for (int i = 0; i < array.length; i++)
    {
      Entry entry = array[i];
      Thread key = (Thread) entry.getKey();
      String val = (String) entry.getValue();
      System.out.println("Lock in " + key + "\n" + val);
    }

    System.out.println("=====================================================================");
    System.out.println();
    //    locks.clear();
  }

  public static String identifySource()
  {
    class SourceIdentificationException extends Exception
    {
      private static final long serialVersionUID = 1L;
    }

    try
    {
      throw new SourceIdentificationException();
    }
    catch (SourceIdentificationException ex)
    {
      String ignore = DeadlockDetector.class.getName();
      StackTraceElement[] frames = ex.getStackTrace();

      for (int i = 0; i < frames.length; i++)
      {
        if (!frames[i].getClassName().equals(ignore))
        {
          if (!COMPLETE_TRACE) return frames[i].toString();

          StringBuffer result = new StringBuffer();
          for (int j = i; j < frames.length; j++)
          {
            result.append("\tat " + frames[j].toString() + "\n");
          }
          return result.toString();
        }
      }

      throw new ImplementationError("identifySource() must not be called from inside the class "
          + ignore);
    }
  }
}
