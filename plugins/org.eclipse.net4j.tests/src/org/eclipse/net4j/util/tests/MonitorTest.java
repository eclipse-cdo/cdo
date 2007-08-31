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
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.om.monitor.IllegalMonitorNestingException;
import org.eclipse.net4j.util.om.monitor.MonitorAlreadyBegunException;
import org.eclipse.net4j.util.om.monitor.MonitorUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMSubMonitor;

/**
 * @author Eike Stepper
 */
public class MonitorTest extends AbstractOMTest
{
  private static final String[] CLASSES = { "Pair", "Triple" };

  private static final String[][] FIELDS = { { "x", "y" }, { "x", "y", "z" } };

  public void testJoined() throws Exception
  {
    MonitorUtil.Legacy.startMonitoring();
    try
    {
      readClasses(CLASSES, FIELDS, true, true);
    }
    finally
    {
      MonitorUtil.Legacy.stopMonitoring();
    }
  }

  public void testJoinedNotStarted() throws Exception
  {
    readClasses(CLASSES, FIELDS, true, true);
  }

  public void testJoinedStopNotStarted() throws Exception
  {
    readClasses(CLASSES, FIELDS, true, true);
    MonitorUtil.Legacy.stopMonitoring();
  }

  public void testUnjoined() throws Exception
  {
    MonitorUtil.Legacy.startMonitoring();
    try
    {
      readClasses(CLASSES, FIELDS, true, false);
      fail("IllegalMonitorNestingException expected");
    }
    catch (IllegalMonitorNestingException ex)
    {
    }
    finally
    {
      MonitorUtil.Legacy.stopMonitoring();
    }
  }

  public void testUnjoinedNotStarted() throws Exception
  {
    readClasses(CLASSES, FIELDS, true, false);
  }

  public void testNotForked() throws Exception
  {
    MonitorUtil.Legacy.startMonitoring();
    try
    {
      readClasses(CLASSES, FIELDS, false, false);
      fail("MonitorAlreadyBegunException expected");
    }
    catch (MonitorAlreadyBegunException ex)
    {
    }
    finally
    {
      MonitorUtil.Legacy.stopMonitoring();
    }
  }

  /**
   * Supports {@link MonitorUtil progress monitoring}.
   */
  private static void readClasses(String[] classes, String[][] fields, boolean fork, boolean join)
  {
    int num = classes.length;
    OMMonitor monitor = MonitorUtil.begin(2 * num, "Reading " + num + " classes");
    for (int i = 0; i < num; i++)
    {
      // Create class buffer
      monitor.worked(1, "Created class buffer for " + classes[i]);

      // Read class
      OMSubMonitor subMonitor = fork ? monitor.fork() : null;
      try
      {
        readFields(fields[i], fork, join);
      }
      finally
      {
        if (join)
        {
          subMonitor.join("Read class " + classes[i]);
        }
      }
    }
  }

  /**
   * Supports {@link MonitorUtil progress monitoring}.
   */
  private static void readFields(String[] fields, boolean fork, boolean join)
  {
    int num = fields.length;
    OMMonitor monitor = MonitorUtil.begin(2 * num, "Reading " + num + " fields");
    for (int i = 0; i < num; i++)
    {
      // Read field
      monitor.worked(1, "Read field " + fields[i]);

      OMSubMonitor subMonitor = fork ? monitor.fork() : null;
      try
      {
        readSetting();
      }
      finally
      {
        if (join)
        {
          subMonitor.join();
        }
      }
    }
  }

  /**
   * Supports {@link MonitorUtil progress monitoring}.
   */
  private static void readSetting()
  {
    OMMonitor monitor = MonitorUtil.begin(1, "Reading setting");
    // Read setting
    monitor.worked("Read setting");
  }
}
