/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Eike Stepper
 */
public class ServerSession extends Lifecycle implements ISession, IListener
{
  private IChannel channel;

  private IBuddy self;

  public ServerSession(IChannel channel, IBuddy self)
  {
    this.channel = channel;
    this.self = self;
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public IBuddy getSelf()
  {
    return self;
  }

  public void close()
  {
    channel.close();
    deactivate();
  }

  /**
   * @see PlatformObject#getAdapter(Class)
   */
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter)
  {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == channel)
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
    channel.addListener(this);
    self.getAccount().touch();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    self.getAccount().touch();
    channel.removeListener(this);
    super.doDeactivate();
  }
}
