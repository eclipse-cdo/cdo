package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;

public class ConnectorsView extends ContainerView
{
  private Action addConnectorAction = new SafeAction("Add Connector", "Add a connector", getAddImageDescriptor())
  {
    @Override
    protected void doRun() throws Exception
    {
      Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", "127.0.0.1:2036");
    }
  };

  public ConnectorsView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IConnector;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(addConnectorAction);
    super.fillLocalToolBar(manager);
  }
}