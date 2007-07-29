/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.om.monitor.Monitor;
import org.eclipse.net4j.internal.util.om.monitor.RootMonitor;
import org.eclipse.net4j.internal.util.om.monitor.SubMonitor;

/**
 * @author Eike Stepper
 */
public final class MONITOR
{
  public static final int UNKNOWN = -1;

  private static final ThreadLocal<Monitor> CURRENT = new ThreadLocal();

  private MONITOR()
  {
  }

  public static void begin()
  {
    begin(UNKNOWN, null);
  }

  public static void begin(int totalWork)
  {
    begin(totalWork, null);
  }

  public static void begin(String task)
  {
    begin(UNKNOWN, task);
  }

  public static void begin(int totalWork, String task)
  {
    Monitor monitor = CURRENT.get();
    if (monitor != null)
    {
      monitor.begin(totalWork, task, 0);
    }
  }

  public static void worked()
  {
    worked(1, null);
  }

  public static void worked(int work)
  {
    worked(work, null);
  }

  public static void worked(String msg)
  {
    worked(1, msg);
  }

  public static void worked(int work, String msg)
  {
    Monitor monitor = CURRENT.get();
    if (monitor != null)
    {
      monitor.worked(work, msg, 0);
    }
  }

  public static void fork(Runnable runnable)
  {
    fork(1, runnable, null);
  }

  public static void fork(int workFromParent, Runnable runnable)
  {
    fork(workFromParent, runnable, null);
  }

  public static void fork(Runnable runnable, String msg)
  {
    fork(1, runnable, msg);
  }

  public static void fork(int workFromParent, Runnable runnable, String msg)
  {
    Monitor monitor = CURRENT.get();
    if (monitor == null)
    {
      runnable.run();
      return;
    }

    SubMonitor subMonitor = monitor.newSubMonitor(workFromParent);
    CURRENT.set(subMonitor);

    try
    {
      runnable.run();
    }
    finally
    {
      Monitor current = CURRENT.get();
      if (current != subMonitor)
      {
        throw new IllegalStateException("Illegal monitor nesting");
      }

      CURRENT.set(subMonitor.getParent());
    }

    if (msg != null)
    {
      subMonitor.message(msg, 0);
    }
  }

  public static void message(String msg)
  {
    Monitor monitor = CURRENT.get();
    if (monitor != null)
    {
      monitor.message(msg, 0);
    }
  }

  static void startMonitoring(RootMonitor rootMonitor)
  {
    Monitor monitor = CURRENT.get();
    if (monitor != null)
    {
      throw new IllegalStateException("Monitoring has already been started");
    }

    CURRENT.set(rootMonitor);
  }

  static void stopMonitoring()
  {
    Monitor monitor = CURRENT.get();
    if (monitor == null)
    {
      throw new IllegalStateException("Monitoring has not been started");
    }

    if (!(monitor instanceof RootMonitor))
    {
      throw new IllegalStateException("Illegal monitor nesting");
    }

    CURRENT.set(null);
  }
}
