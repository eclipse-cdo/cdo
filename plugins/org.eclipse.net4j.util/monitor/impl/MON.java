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
package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.monitor.IllegalMonitorNestingException;
import org.eclipse.net4j.util.om.monitor.MonitorAlreadyBegunException;
import org.eclipse.net4j.util.om.monitor.MonitorException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 */
public final class MON
{
  public static final int UNKNOWN = -1;

  private static final ThreadLocal<Monitor> CURRENT = new ThreadLocal<Monitor>();

  private MON()
  {
  }

  public static boolean isCanceled()
  {
    Monitor current = CURRENT.get();
    if (current == null)
    {
      return false;
    }

    return current.isCanceled();
  }

  public static void setCanceled(boolean canceled)
  {
    Monitor current = CURRENT.get();
    if (current != null)
    {
      current.setCanceled(canceled);
    }
  }

  public static void startMonitoring(Monitor rootMonitor)
  {
    Monitor current = CURRENT.get();
    if (current != null)
    {
      throw new IllegalMonitorNestingException("Monitoring has already been started");
    }

    CURRENT.set(rootMonitor);
  }

  public static void stopMonitoring()
  {
    try
    {
      Monitor current = CURRENT.get();
      if (current == null)
      {
        OM.LOG.warn("Monitoring has not been started");
      }
      else
      {
        if (current.getParent() != null)
        {
          OM.LOG.warn("Illegal monitor nesting");
        }

        current.done();
      }
    }
    finally
    {
      CURRENT.set(null);
    }
  }

  public static OMMonitor begin(int totalWork, String task)
  {
    Monitor current = CURRENT.get();
    if (current == null)
    {
      return NullMonitor.INSTANCE;
    }

    if (current.hasBegun())
    {
      throw new MonitorAlreadyBegunException("Monitor has already begun");
    }

    current.begin(totalWork, task);
    return current;
  }

  static void checkMonitor(Monitor monitor) throws MonitorException
  {
    Monitor current = CURRENT.get();
    if (current != monitor)
    {
      throw new IllegalMonitorNestingException("Illegal monitor nesting\n" + // 
          "Current monitor stack:\n" + current.dump() + //
          "Used monitor stack:\n" + monitor.dump());
    }

    monitor.checkCanceled();
  }

  static void setMonitor(Monitor monitor)
  {
    CURRENT.set(monitor);
  }
}
