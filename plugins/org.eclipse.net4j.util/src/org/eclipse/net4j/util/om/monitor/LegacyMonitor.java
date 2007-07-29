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

import org.eclipse.net4j.internal.util.om.monitor.Monitor;

/**
 * @author Eike Stepper
 */
public final class LegacyMonitor extends Monitor
{
  private LegacyMonitor(LegacyMonitor parent, int workFromParent)
  {
    super(parent, workFromParent);
  }

  private LegacyMonitor()
  {
    super(null, 0);
  }

  @Override
  public LegacyMonitor subMonitor(int workFromParent)
  {
    return new LegacyMonitor(this, workFromParent);
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
