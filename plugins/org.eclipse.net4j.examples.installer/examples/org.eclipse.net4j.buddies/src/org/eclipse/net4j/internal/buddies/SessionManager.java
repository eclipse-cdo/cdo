/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.ISessionManager;
import org.eclipse.net4j.buddies.ISessionManagerEvent;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.buddies.bundle.OM;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.text.MessageFormat;

public class SessionManager extends Lifecycle implements ISessionManager, IListener
{
  public static final SessionManager INSTANCE = new SessionManager();

  private IBuddySession session;

  private State state = State.DISCONNECTED;

  private boolean connecting;

  private boolean flashing;

  private SessionManager()
  {
  }

  @Override
  public IBuddySession getSession()
  {
    return session;
  }

  @Override
  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    if (this.state != state)
    {
      IEvent event = new SessionManagerEvent(this.state, state, session);
      this.state = state;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(event, listeners);
      }
    }
  }

  @Override
  public boolean isFlashing()
  {
    return flashing;
  }

  @Override
  public boolean isConnecting()
  {
    return state == ISessionManager.State.CONNECTING;
  }

  public String getConnectorDescription()
  {
    return OM.PREF_CONNECTOR_DESCRIPTION.getValue();
  }

  public String getUserID()
  {
    return OM.PREF_USER_ID.getValue();
  }

  public String getPassword()
  {
    return OM.PREF_PASSWORD.getValue();
  }

  public Boolean isAutoConnect()
  {
    return OM.PREF_AUTO_CONNECT.getValue();
  }

  @Override
  public void connect()
  {
    new Thread("buddies-connector") //$NON-NLS-1$
    {
      @Override
      public void run()
      {
        try
        {
          String userID = getUserID();
          String password = getPassword();

          setState(ISessionManager.State.CONNECTING);
          connecting = true;
          while (session == null && connecting)
          {
            IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, getConnectorDescription());
            if (connector == null)
            {
              throw new IllegalStateException("connector == null"); //$NON-NLS-1$
            }

            session = BuddiesUtil.openSession(connector, userID, password, 5000L);
            if (session != null)
            {
              if (connecting)
              {
                session.addListener(SessionManager.this);
                setState(ISessionManager.State.CONNECTED);
              }
              else
              {
                session.close();
                session = null;
                setState(ISessionManager.State.DISCONNECTED);
              }
            }
          }
        }
        finally
        {
          connecting = false;
        }
      }
    }.start();
  }

  @Override
  public void disconnect()
  {
    connecting = false;
    if (session != null)
    {
      session.removeListener(this);
      session.close();
      session = null;
    }

    setState(ISessionManager.State.DISCONNECTED);
  }

  @Override
  public void flashMe()
  {
    if (session != null && !flashing)
    {
      final Self self = (Self)session.getSelf();
      final IBuddy.State original = self.getState();
      new Thread("buddies-flasher") //$NON-NLS-1$
      {
        @Override
        public void run()
        {
          flashing = true;
          IBuddy.State state = original == IBuddy.State.AVAILABLE ? IBuddy.State.LONESOME : IBuddy.State.AVAILABLE;
          for (int i = 0; i < 15; i++)
          {
            self.setState(state);
            ConcurrencyUtil.sleep(200);
            state = state == IBuddy.State.AVAILABLE ? IBuddy.State.LONESOME : IBuddy.State.AVAILABLE;
          }

          self.setState(original);
          flashing = false;
        }
      }.start();
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == session)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          disconnect();
          if (isAutoConnect())
          {
            connect();
          }
        }
      }
      else if (event instanceof IContainerEvent<?>)
      {
        @SuppressWarnings("unchecked")
        IContainerEvent<IBuddy> e = (IContainerEvent<IBuddy>)event;
        if (e.getDeltaKind() == IContainerDelta.Kind.ADDED)
        {
          e.getDeltaElement().addListener(this);
        }
        else if (e.getDeltaKind() == IContainerDelta.Kind.REMOVED)
        {
          e.getDeltaElement().removeListener(this);
        }
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (isAutoConnect())
    {
      connect();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    disconnect();
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class SessionManagerEvent extends Event implements ISessionManagerEvent
  {
    private static final long serialVersionUID = 1L;

    private State oldState;

    private State newState;

    private IBuddySession session;

    public SessionManagerEvent(State oldState, State newState, IBuddySession session)
    {
      super(SessionManager.this);
      this.oldState = oldState;
      this.newState = newState;
      this.session = session;
    }

    @Override
    public State getOldState()
    {
      return oldState;
    }

    @Override
    public State getNewState()
    {
      return newState;
    }

    @Override
    public IBuddySession getSession()
    {
      return session;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("SessionManagerEvent[source={0}, oldState={1}, newState={2}, session={3}]", //$NON-NLS-1$
          getSource(), getOldState(), getNewState(), getSession());
    }
  }
}
