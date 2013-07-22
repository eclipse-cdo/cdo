/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.helper;

/**
 * @author Eike Stepper
 */
public final class Progress
{
  private static final ProgressLog DEFAULT = new ProgressLog()
  {
    public boolean isCancelled()
    {
      return false;
    }

    public void addLine(String line)
    {
      System.out.println(line);
    }
  };

  private static ProgressLog log;

  public static ProgressLog log()
  {
    if (log == null)
    {
      return DEFAULT;
    }

    return log;
  }

  public static void set(ProgressLog log)
  {
    Progress.log = log;
  }
}
