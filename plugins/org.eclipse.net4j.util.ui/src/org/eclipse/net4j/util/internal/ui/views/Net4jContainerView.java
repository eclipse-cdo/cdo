package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginTransportContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

public class Net4jContainerView extends ContainerView
{
  public Net4jContainerView()
  {
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
  {
    return new Net4jItemProvider();
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }
}