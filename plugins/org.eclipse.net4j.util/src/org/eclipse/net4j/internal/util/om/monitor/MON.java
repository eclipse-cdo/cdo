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
package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.IllegalMonitorNestingException;
import org.eclipse.net4j.util.om.monitor.MonitorNotBegunException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 */
public final class MON
{
  public static final int UNKNOWN = -1;

  private static final ThreadLocal<Monitor> CURRENT = new ThreadLocal();

  private static final NullMonitor NULL_MONITOR = new NullMonitor();

  private MON()
  {
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
    Monitor current = CURRENT.get();
    if (current == null)
    {
      throw new IllegalMonitorNestingException("Monitoring has not been started");
    }

    if (current.getParent() != null)
    {
      throw new IllegalMonitorNestingException("Illegal monitor nesting");
    }

    current.done();
    CURRENT.set(null);
  }

  public static OMMonitor begin(int totalWork, String task)
  {
    Monitor current = CURRENT.get();
    if (current == null)
    {
      return NULL_MONITOR;
    }

    if (current.hasBegun())
    {
      throw new IllegalStateException("Monitor has already begun");
    }

    current.begin(totalWork, task);
    return current;
  }

  static void checkMonitor(Monitor monitor)
  {
    Monitor current = CURRENT.get();
    if (current != monitor)
    {
      throw new IllegalMonitorNestingException("Illegal monitor nesting\nCurrent:\n" + current.dump() + "Used:\n"
          + monitor.dump());
    }

    if (!current.hasBegun())
    {
      throw new MonitorNotBegunException("Monitor has not begun");
    }

    monitor.checkCanceled();
  }

  static void setMonitor(Monitor monitor)
  {
    CURRENT.set(monitor);
  }
}
