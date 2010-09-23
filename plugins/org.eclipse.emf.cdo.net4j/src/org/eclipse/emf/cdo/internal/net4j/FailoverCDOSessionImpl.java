/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.CDOSessionFailoverEvent;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class FailoverCDOSessionImpl extends CDONet4jSessionImpl
{
  public FailoverCDOSessionImpl(FailoverCDOSessionConfigurationImpl configuration)
  {
    super(configuration);
  }

  @Override
  public FailoverCDOSessionConfigurationImpl getConfiguration()
  {
    return (FailoverCDOSessionConfigurationImpl)super.getConfiguration();
  }

  @Override
  protected void sessionProtocolDeactivated()
  {
    fireFailoverEvent(CDOSessionFailoverEvent.Type.STARTED);

    unhookSessionProtocol();
    List<AfterFailoverRunnable> runnables = getConfiguration().failover(FailoverCDOSessionImpl.this);
    CDOSessionProtocol sessionProtocol = hookSessionProtocol();

    for (AfterFailoverRunnable runnable : runnables)
    {
      runnable.run(sessionProtocol);
    }

    fireFailoverEvent(CDOSessionFailoverEvent.Type.FINISHED);
  }

  private void fireFailoverEvent(final CDOSessionFailoverEvent.Type type)
  {
    fireEvent(new CDOSessionFailoverEvent()
    {
      public CDOSession getSource()
      {
        return FailoverCDOSessionImpl.this;
      }

      public Type getType()
      {
        return type;
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public static interface AfterFailoverRunnable
  {
    public void run(CDOSessionProtocol sessionProtocol);
  }
}
