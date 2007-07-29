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

import org.eclipse.net4j.util.om.monitor.MONITOR;

/**
 * @author Eike Stepper
 */
public abstract class MonitorTest
{
  private static final String[] CLASSES = { "A", "B", "C", "D", "E" };

  private static final String[] FIELDS = { "x", "y", "z" };

  /**
   * Supports {@link MONITOR progress monitoring}.
   */
  public static void readClasses()
  {
    int num = CLASSES.length;
    MONITOR.begin(2 * num, "Reading " + num + " classes");
    for (int i = 0; i < num; i++)
    {
      // Create class buffer
      MONITOR.worked(1, "Created class buffer for " + CLASSES[i]);

      // Read class
      MONITOR.fork(1, new Runnable()
      {
        public void run()
        {
          readFields();
        }
      }, "Read class " + CLASSES[i]);
    }
  }

  /**
   * Supports {@link MONITOR progress monitoring}.
   */
  public static void readFields()
  {
    int num = FIELDS.length;
    MONITOR.begin(num, "Reading " + num + " fields");
    for (int i = 0; i < num; i++)
    {
      // Read field
      MONITOR.worked(1, "Read field " + FIELDS[i]);
    }
  }
}
