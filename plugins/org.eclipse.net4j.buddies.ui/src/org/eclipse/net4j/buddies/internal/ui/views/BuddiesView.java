package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.SharedIcons;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

public class BuddiesView extends ContainerView implements IListener
{
  private ConnectAction connectAction = new ConnectAction();

  private DisconnectAction disconnectAction = new DisconnectAction();

  private StateAction availableAction = new StateAction("Available", State.AVAILABLE, SharedIcons.OBJ_BUDDY);

  private StateAction awayAction = new StateAction("Away", State.AWAY, SharedIcons.OBJ_BUDDY_AWAY);

  private StateAction doNotDisturbAction = new StateAction("Do Not Disturb", State.DO_NOT_DISTURB,
      SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB);

  private IBuddySession session;

  private boolean connecting;

  public BuddiesView()
  {
    if (isAutoConnect())
    {
      connect();
    }
  }

  protected Boolean isAutoConnect()
  {
    return OM.PREF_AUTO_CONNECT.getValue();
  }

  protected void connect()
  {
    new Thread("buddies-connector")
    {
      @Override
      public void run()
      {
        try
        {
          connecting = true;
          while (session == null && connecting)
          {
            String connectorDescription = OM.PREF_CONNECTOR_DESCRIPTION.getValue();
            IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, connectorDescription);
            if (connector == null)
            {
              throw new IllegalStateException("connector == null");
            }

            String userID = OM.PREF_USER_ID.getValue();
            String password = OM.PREF_PASSWORD.getValue();
            session = BuddiesUtil.openSession(connector, userID, password, 5000L);
            if (session != null)
            {
              if (connecting)
              {
                resetInput();
                connectAction.setEnabled(false);
                disconnectAction.setEnabled(true);
                availableAction.setEnabled(true);
                availableAction.setChecked(session.getSelf().getState() == IBuddy.State.AVAILABLE);
                awayAction.setEnabled(true);
                awayAction.setChecked(session.getSelf().getState() == IBuddy.State.AWAY);
                doNotDisturbAction.setEnabled(true);
                doNotDisturbAction.setChecked(session.getSelf().getState() == IBuddy.State.DO_NOT_DISTURB);
                session.addListener(BuddiesView.this);
              }
              else
              {
                session.close();
                session = null;
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

  protected void disconnect()
  {
    connecting = false;
    session.removeListener(this);
    session.close();
    session = null;
    resetInput();

    connectAction.setEnabled(true);
    disconnectAction.setEnabled(false);
    availableAction.setEnabled(false);
    availableAction.setChecked(false);
    awayAction.setEnabled(false);
    awayAction.setChecked(false);
    doNotDisturbAction.setEnabled(false);
    doNotDisturbAction.setChecked(false);
  }

  @Override
  public void dispose()
  {
    disconnect();
    super.dispose();
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
    else if (event instanceof IBuddyStateChangedEvent)
    {
      IBuddyStateChangedEvent e = (IBuddyStateChangedEvent)event;
      updateLabels(e.getBuddy());
    }
  }

  protected void closeView()
  {
    try
    {
      getSite().getShell().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            getSite().getPage().hideView(BuddiesView.this);
            BuddiesView.this.dispose();
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return session != null ? session : ContainerUtil.emptyContainer();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new BuddiesItemProvider();
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(connectAction);
    manager.add(disconnectAction);
    manager.add(new Separator());
    manager.add(availableAction);
    manager.add(awayAction);
    manager.add(doNotDisturbAction);
    super.fillLocalPullDown(manager);
  }

  /**
   * @author Eike Stepper
   */
  private final class ConnectAction extends SafeAction
  {
    private ConnectAction()
    {
      super("Connect", "Connect to buddies server");
    }

    @Override
    protected void safeRun() throws Exception
    {
      connect();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DisconnectAction extends SafeAction
  {
    private DisconnectAction()
    {
      super("Disonnect", "Disconnect from buddies server");
    }

    @Override
    protected void safeRun() throws Exception
    {
      disconnect();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StateAction extends SafeAction
  {
    private State state;

    private StateAction(String text, State state, String key)
    {
      super(text, Action.AS_RADIO_BUTTON);
      setToolTipText("Set own state to '" + text.toLowerCase() + "'");
      setImageDescriptor(SharedIcons.getDescriptor(key));
      this.state = state;
    }

    @Override
    protected void safeRun() throws Exception
    {
      if (session != null && isChecked())
      {
        Self self = (Self)session.getSelf();
        self.setState(state);
      }
    }
  }
}