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

import org.eclipse.net4j.internal.util.om.monitor.LegacySubMonitor;
import org.eclipse.net4j.internal.util.om.monitor.RootMonitor;
import org.eclipse.net4j.internal.util.om.monitor.SubMonitor;

/**
 * @author Eike Stepper
 */
public final class LegacyMonitor extends RootMonitor
{
  private LegacyMonitor()
  {
  }

  @Override
  public SubMonitor newSubMonitor(int workFromParent)
  {
    return new LegacySubMonitor(this, workFromParent);
  }

  @Override
  public void message(String msg, int level)
  {
    for (int i = 0; i < level; i++)
    {
      System.out.print("  ");
    }

    System.out.println(msg);
  }

  public static void startMonitoring()
  {
    MONITOR.startMonitoring(new LegacyMonitor());
  }

  public static void stopMonitoring()
  {
    MONITOR.stopMonitoring();
  }
}
