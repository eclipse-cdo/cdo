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
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BuddiesView extends ContainerView implements IListener
{
  private ConnectAction connectAction = new ConnectAction();

  private DisconnectAction disconnectAction = new DisconnectAction();

  private FlashAction flashAction = new FlashAction();

  private StateAction availableAction = new StateAction("Available", State.AVAILABLE, SharedIcons.OBJ_BUDDY);

  private StateAction lonesomeAction = new StateAction("Lonesome", State.LONESOME, SharedIcons.OBJ_BUDDY_LONESOME);

  private StateAction awayAction = new StateAction("Away", State.AWAY, SharedIcons.OBJ_BUDDY_AWAY);

  private StateAction doNotDisturbAction = new StateAction("Do Not Disturb", State.DO_NOT_DISTURB,
      SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB);

  private IBuddySession session;

  private boolean connecting;

  private boolean flashing;

  private static BuddiesView INSTANCE;

  public BuddiesView()
  {
    if (isAutoConnect())
    {
      connect();
    }
  }

  public static synchronized BuddiesView getINSTANCE()
  {
    return INSTANCE;
  }

  public IBuddySession getSession()
  {
    return session;
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

            boolean connected = connector.waitForConnection(5000L);
            if (connected)
            {
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
                  flashAction.setEnabled(true);
                  updateState();
                  session.addListener(BuddiesView.this);
                  session.getSelf().addListener(BuddiesView.this);
                }
                else
                {
                  session.close();
                  session = null;
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
          connecting = false;
        }
      }
    }.start();
  }

  protected void disconnect()
  {
    connecting = false;
    session.getSelf().removeListener(BuddiesView.this);
    session.removeListener(this);
    session.close();
    session = null;
    resetInput();

    connectAction.setEnabled(true);
    disconnectAction.setEnabled(false);
    flashAction.setEnabled(false);
    availableAction.setEnabled(false);
    availableAction.setChecked(false);
    lonesomeAction.setEnabled(false);
    lonesomeAction.setChecked(false);
    awayAction.setEnabled(false);
    awayAction.setChecked(false);
    doNotDisturbAction.setEnabled(false);
    doNotDisturbAction.setChecked(false);
  }

  @Override
  public synchronized void dispose()
  {
    INSTANCE = null;
    disconnect();
    super.dispose();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Control control = super.createUI(parent);
    updateState();
    INSTANCE = this;
    return control;
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
      if (session != null && event.getSource() == session.getSelf())
      {
        updateState();
      }
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
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(availableAction);
    manager.add(lonesomeAction);
    manager.add(awayAction);
    manager.add(doNotDisturbAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(connectAction);
    manager.add(disconnectAction);
    manager.add(new Separator());
    manager.add(flashAction);
    super.fillLocalPullDown(manager);
  }

  protected void updateState()
  {
    updateState(availableAction, IBuddy.State.AVAILABLE);
    updateState(lonesomeAction, IBuddy.State.LONESOME);
    updateState(awayAction, IBuddy.State.AWAY);
    updateState(doNotDisturbAction, IBuddy.State.DO_NOT_DISTURB);
  }

  protected void updateState(StateAction action, State state)
  {
    action.setEnabled(session != null);
    action.setChecked(session != null && session.getSelf().getState() == state);
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

  /**
   * @author Eike Stepper
   */
  private final class FlashAction extends SafeAction
  {
    private FlashAction()
    {
      super("Flash Me", "Flash Me");
    }

    @Override
    protected void safeRun() throws Exception
    {
      if (session != null && !flashing)
      {
        final Self self = (Self)session.getSelf();
        final State original = self.getState();
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
  }
}