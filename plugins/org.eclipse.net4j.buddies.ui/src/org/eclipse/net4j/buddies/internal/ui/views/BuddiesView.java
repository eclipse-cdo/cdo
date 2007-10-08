package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IMenuManager;

public class BuddiesView extends ContainerView implements IListener
{
  private ConnectAction connectAction = new ConnectAction();

  private DisconnectAction disconnectAction = new DisconnectAction();

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
                session.addListener(BuddiesView.this);
                connectAction.setEnabled(false);
                disconnectAction.setEnabled(true);
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
    session.removeListener(this);
    session.close();
    session = null;
    connecting = false;
    connectAction.setEnabled(true);
    disconnectAction.setEnabled(false);
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
          if (isAutoConnect())
          {
            connect();
          }
        }
      }
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
    return session;
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
    if (session == null && !connecting)
    {
    }
    else
    {
    }

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
}