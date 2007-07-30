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

  /**
   * @author Eike Stepper
   */
  public static final class Eclipse
  {
    public static void startMonitoring(IProgressMonitor progressMonitor)
    {
      MON.startMonitoring(new EclipseMonitor(progressMonitor));
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
    public static void startMonitoring(IMessageHandler messageHandler)
    {
      MON.startMonitoring(new LegacyMonitor(messageHandler));
    }

    public static void startMonitoring()
    {
      MON.startMonitoring(new LegacyMonitor(null));
    }

    public static void stopMonitoring()
    {
      MON.stopMonitoring();
    }
  }
}
