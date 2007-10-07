package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IToolBarManager;

public class BuddiesView extends ContainerView
{
  public BuddiesView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
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