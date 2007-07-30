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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.monitor.IMessageHandler;

/**
 * @author Eike Stepper
 */
public final class LegacyMonitor extends Monitor
{
  private IMessageHandler messageHandler;

  public LegacyMonitor(IMessageHandler messageHandler)
  {
    super(null, 0);
    this.messageHandler = messageHandler;
  }

  private LegacyMonitor(LegacyMonitor parent, int workFromParent)
  {
    super(parent, workFromParent);
  }

  @Override
  public LegacyMonitor subMonitor(int workFromParent)
  {
    return new LegacyMonitor(this, workFromParent);
  }

  @Override
  protected void message(String msg, int level)
  {
    if (getParent() != null)
    {
      super.message(msg, level);
    }
    else
    {
      if (messageHandler != null)
      {
        try
        {
          messageHandler.handleMessage(msg, level);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
