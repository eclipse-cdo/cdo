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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.om.progress.Monitor;
import org.eclipse.net4j.internal.util.om.progress.RootMonitor;
import org.eclipse.net4j.internal.util.om.progress.SubMonitor;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class PROGRESS
{
  public static final int UNKNOWN = -1;

  private static final ThreadLocal<Monitor> MONITOR = new ThreadLocal();

  private PROGRESS()
  {
  }

  public static void begin(int totalWork, String taskPattern, String... singularAndPlural)
  {
    Monitor monitor = getMonitor();
    String task = null;
    if (taskPattern != null)
    {
      task = format(totalWork, taskPattern, singularAndPlural);
    }

    monitor.begin(totalWork, task);
  }

  public static void worked(int work, String successPattern, String... singularAndPlural)
  {
    Monitor monitor = getMonitor();
    String success = null;
    if (successPattern != null)
    {
      success = format(work, successPattern, singularAndPlural);
    }

    monitor.worked(work, success);
  }

  public static void fork(int workFromParent, Runnable runnable, String successPattern, String... singularAndPlural)
  {
    Monitor monitor = getMonitor();
    SubMonitor subMonitor = new SubMonitor(monitor, workFromParent);
    MONITOR.set(subMonitor);

    try
    {
      runnable.run();
      if (successPattern != null)
      {
        String success = format(workFromParent, successPattern, singularAndPlural);
        subMonitor.onSuccess(success);
      }
    }
    finally
    {
      Monitor current = MONITOR.get();
      if (current != subMonitor)
      {
        throw new IllegalStateException("Illegal monitor nesting");
      }

      MONITOR.set(subMonitor.getParent());
    }
  }

  static void startMonitoring(RootMonitor rootMonitor)
  {
    Monitor monitor = MONITOR.get();
    if (monitor != null)
    {
      throw new IllegalStateException("Monitoring has already been started");
    }

    MONITOR.set(rootMonitor);
  }

  static void stopMonitoring()
  {
    Monitor monitor = MONITOR.get();
    if (monitor == null)
    {
      throw new IllegalStateException("Monitoring has not been started");
    }

    if (!(monitor instanceof RootMonitor))
    {
      throw new IllegalStateException("Illegal monitor nesting");
    }

    MONITOR.set(null);
  }

  private static String format(int totalWork, String pattern, String... singularAndPlural)
  {
    String task;
    if (singularAndPlural != null && singularAndPlural.length != 0)
    {
      if (singularAndPlural.length != 2)
      {
        throw new IllegalArgumentException("Give exactly singular and plural form after the pattern");
      }

      String singular = singularAndPlural[0];
      String plural = singularAndPlural[1];
      String arg = String.valueOf(totalWork) + " " + (totalWork == 1 ? singular : plural);
      task = MessageFormat.format(pattern, arg);
    }
    else
    {
      task = MessageFormat.format(pattern, totalWork);
    }
    return task;
  }

  private static Monitor getMonitor()
  {
    Monitor monitor = MONITOR.get();
    if (monitor == null)
    {
      throw new IllegalStateException("No monitor available");
    }

    return monitor;
  }
}
