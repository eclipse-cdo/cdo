/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j;

import org.eclipse.emf.internal.cdo.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  private CDOClientProtocol protocol;

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  public CDONet4jSessionImpl()
  {
    protocol = new CDOClientProtocol();
    protocol.setInfraStructure(this);
  }

  public CDOSessionProtocol getSessionProtocol()
  {
    return protocol;
  }

  /**
   * @since 2.0
   */
  public String getUserID()
  {
    IChannel channel = protocol.getChannel();
    return channel == null ? null : channel.getUserID();
  }

  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    EventUtil.addListener(protocol, protocolListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    EventUtil.removeListener(protocol, protocolListener);
    super.doDeactivate();
    protocol.close();
    protocol = null;
  }

  /**
   * @author Eike Stepper
   */
  protected class OptionsImpl extends org.eclipse.emf.internal.cdo.session.CDOSessionImpl.OptionsImpl implements
      org.eclipse.emf.cdo.net4j.CDOSession.Options
  {
    public OptionsImpl()
    {
    }

    public CDOClientProtocol getProtocol()
    {
      return protocol;
    }
  }
}
