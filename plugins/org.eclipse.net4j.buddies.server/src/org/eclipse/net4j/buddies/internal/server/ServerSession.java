/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.server.protocol.BuddiesServerProtocol;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * @author Eike Stepper
 */
public class ServerSession extends Lifecycle implements ISession, IListener
{
  private BuddiesServerProtocol protocol;

  private IBuddy self;

  /**
   * @since 2.0
   */
  public ServerSession(BuddiesServerProtocol protocol, IBuddy self)
  {
    this.protocol = protocol;
    this.self = self;
  }

  /**
   * @since 2.0
   */
  @Override
  public BuddiesServerProtocol getProtocol()
  {
    return protocol;
  }

  @Override
  public IBuddy getSelf()
  {
    return self;
  }

  @Override
  public void close()
  {
    protocol.close();
    protocol = null;
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == protocol)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          deactivate();
        }
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    protocol.addListener(this);
    self.getAccount().touch();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    self.getAccount().touch();
    protocol.removeListener(this);
    super.doDeactivate();
  }
}
