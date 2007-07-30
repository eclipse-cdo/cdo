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

import org.eclipse.net4j.util.om.monitor.MonitorUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMSubMonitor;

/**
 * @author Eike Stepper
 */
public class LegacyTest
{
  private static final String[] CLASSES = { "Pair", "Triple" };

  private static final String[][] FIELDS = { { "x", "y" }, { "x", "y", "z" } };

  public static void main(String[] args)
  {
    MonitorUtil.Legacy.startMonitoring();
    readClasses();
    MonitorUtil.Legacy.stopMonitoring();
  }

  /**
   * Supports {@link MonitorUtil progress monitoring}.
   */
  public static void readClasses()
  {
    int num = CLASSES.length;
    OMMonitor monitor = MonitorUtil.begin(2 * num, "Reading " + num + " classes");
    for (int c = 0; c < num; c++)
    {
      // Create class buffer
      monitor.worked(1, "Created class buffer for " + CLASSES[c]);

      // Read class
      OMSubMonitor subMonitor = monitor.fork();
      try
      {
        readFields(c);
      }
      finally
      {
        subMonitor.join("Read class " + CLASSES[c]);
      }
    }
  }

  /**
   * Supports {@link MonitorUtil progress monitoring}.
   * 
   * @param i2
   */
  public static void readFields(int c)
  {
    int num = FIELDS[c].length;
    OMMonitor monitor = MonitorUtil.begin(num, "Reading " + num + " fields");
    for (int f = 0; f < num; f++)
    {
      // Read field
      monitor.worked(1, "Read field " + FIELDS[c][f]);
    }
  }
}
