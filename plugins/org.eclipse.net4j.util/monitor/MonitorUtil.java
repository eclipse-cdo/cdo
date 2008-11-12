/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.internal.util.om.monitor.LegacyMonitor;
import org.eclipse.net4j.internal.util.om.monitor.MON;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public final class MonitorUtil
{
  private MonitorUtil()
  {
  }

  public static boolean isCanceled()
  {
    return MON.isCanceled();
  }

  public static OMMonitor begin()
  {
    return MON.begin(OMMonitor.UNKNOWN, null);
  }

  public static OMMonitor begin(int totalWork)
  {
    return MON.begin(totalWork, null);
  }

  public static OMMonitor begin(String task)
  {
    return MON.begin(OMMonitor.UNKNOWN, task);
  }

  public static OMMonitor begin(int totalWork, String task)
  {
    return MON.begin(totalWork, task);
  }

  static void handleTrace(final OMMonitorHandler messageHandler, String msg, int level, boolean isTask)
  {
    if (messageHandler != null)
    {
      try
      {
        if (isTask)
        {
          messageHandler.handleTask(msg, level);
        }
        else
        {
          messageHandler.handleMessage(msg, level);
        }
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Eclipse
  {
    public static void startMonitoring(IProgressMonitor progressMonitor, final OMMonitorHandler messageHandler)
    {
      MON.startMonitoring(new EclipseMonitor(progressMonitor)
      {
        @Override
        protected void trace(String msg, int level, boolean isTask)
        {
          super.trace(msg, level, isTask);
          handleTrace(messageHandler, msg, level, isTask);
        }
      });
    }

    public static void startMonitoring(IProgressMonitor progressMonitor)
    {
      startMonitoring(progressMonitor, null);
    }

    public static void stopMonitoring()
    {
      MON.stopMonitoring();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Legacy
  {
    public static void startMonitoring(final OMMonitorHandler messageHandler)
    {
      MON.startMonitoring(new LegacyMonitor()
      {
        @Override
        protected void trace(String msg, int level, boolean isTask)
        {
          super.trace(msg, level, isTask);
          handleTrace(messageHandler, msg, level, isTask);
        }
      });
    }

    public static void startMonitoring()
    {
      startMonitoring(null);
    }

    public static void stopMonitoring()
    {
      MON.stopMonitoring();
    }
  }
}
