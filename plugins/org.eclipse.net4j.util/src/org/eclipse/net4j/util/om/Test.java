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

/**
 * @author Eike Stepper
 */
public class Test
{
  public static void main(String[] args)
  {
    LegacyUtil.startMonitoring();
    readClasses();
    LegacyUtil.stopMonitoring();
  }

  /**
   * Supports {@link PROGRESS progress monitoring}.
   */
  private static void readClasses()
  {
    int num = 3;
    PROGRESS.begin(2 * num, "Reading {0}", "class", "classes");
    for (int i = 0; i < num; i++)
    {
      // Create class buffer
      PROGRESS.worked(1, "Created class buffer {0}");

      // Read class
      PROGRESS.fork(1, new Runnable()
      {
        public void run()
        {
          readFields();
        }
      }, "Read {0}", "class", "classes");
    }
  }

  /**
   * Supports {@link PROGRESS progress monitoring}.
   */
  private static void readFields()
  {
    int num = 5;
    PROGRESS.begin(num, "Reading {0}", "field", "fields");
    for (int i = 0; i < num; i++)
    {
      // Read field
      PROGRESS.worked(1, "Read {0}", "field", "fields");
    }
  }
}
