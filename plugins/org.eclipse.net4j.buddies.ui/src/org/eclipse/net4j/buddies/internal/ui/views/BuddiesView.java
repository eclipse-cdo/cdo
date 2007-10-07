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
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IToolBarManager;

public class BuddiesView extends ContainerView implements IListener
{
  private IBuddySession session;

  public BuddiesView()
  {
    String connectorDescription = OM.PREF_CONNECTOR_DESCRIPTION.getValue();
    IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, connectorDescription);
    if (connector == null)
    {
      throw new IllegalStateException("connector == null");
    }

    String userID = OM.PREF_USER_ID.getValue();
    String password = OM.PREF_PASSWORD.getValue();
    session = BuddiesUtil.openSession(connector, userID, password);
    if (session == null)
    {
      throw new IllegalStateException("session == null");
    }

    session.addListener(this);
  }

  @Override
  public void dispose()
  {
    session.removeListener(this);
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
          closeView();
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
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    // manager.add(addConnectorAction);
    super.fillLocalToolBar(manager);
  }
}