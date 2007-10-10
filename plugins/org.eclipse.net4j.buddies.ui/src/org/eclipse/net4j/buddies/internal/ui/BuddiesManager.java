package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.buddies.ui.IBuddiesManagerStateChangedEvent;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

public class BuddiesManager extends Lifecycle implements IBuddiesManager, IListener
{
  public static final BuddiesManager INSTANCE = new BuddiesManager();

  private IBuddySession session;

  private State state;

  private boolean flashing;

  private BuddiesManager()
  {
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    if (this.state != state)
    {
      IEvent event = new StateChangedEvent(this.state, state, session);
      this.state = state;
      fireEvent(event);
    }
  }

  public boolean isConnecting()
  {
    return state == IBuddiesManager.State.CONNECTING;
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

  public void connect()
  {
    new Thread("buddies-connector")
    {
      @Override
      public void run()
      {
        try
        {
          setState(IBuddiesManager.State.CONNECTING);
          while (session == null && isConnecting())
          {
            IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, getConnectorDescription());
            if (connector == null)
            {
              throw new IllegalStateException("connector == null");
            }

            boolean connected = connector.waitForConnection(5000L);
            if (connected)
            {
              session = BuddiesUtil.openSession(connector, getUserID(), getPassword(), 5000L);
              if (session != null)
              {
                if (isConnecting())
                {
                  session.addListener(BuddiesManager.this);
                  setState(IBuddiesManager.State.CONNECTED);
                }
                else
                {
                  session.close();
                  session = null;
                  setState(IBuddiesManager.State.DISCONNECTED);
                }
              }
            }
            else
            {
              LifecycleUtil.deactivate(connector);
            }
          }
        }
        finally
        {
          setState(IBuddiesManager.State.DISCONNECTED);
        }
      }
    }.start();
  }

  public void disconnect()
  {
    if (session != null)
    {
      session.removeListener(this);
      session.close();
      session = null;
    }

    setState(IBuddiesManager.State.DISCONNECTED);
  }

  public void flashMe()
  {
    if (session != null && !flashing)
    {
      final Self self = (Self)session.getSelf();
      final IBuddy.State original = self.getState();
      new Thread("buddies-flasher")
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
      else if (event instanceof IContainerEvent)
      {
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
  private final class StateChangedEvent extends Event implements IBuddiesManagerStateChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private State oldState;

    private State newState;

    private IBuddySession session;

    public StateChangedEvent(State oldState, State newState, IBuddySession session)
    {
      super(BuddiesManager.this);
      this.oldState = oldState;
      this.newState = newState;
      this.session = session;
    }

    public State getOldState()
    {
      return oldState;
    }

    public State getNewState()
    {
      return newState;
    }

    public IBuddySession getSession()
    {
      return session;
    }
  }
}