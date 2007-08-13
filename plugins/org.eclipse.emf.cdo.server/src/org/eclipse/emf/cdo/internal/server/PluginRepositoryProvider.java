package org.eclipse.emf.cdo.internal.server;

import org.eclipse.net4j.util.container.IPluginContainer;

/**
 * @author Eike Stepper
 */
public final class PluginRepositoryProvider extends ContainerRepositoryProvider
{
  public static final PluginRepositoryProvider INSTANCE = new PluginRepositoryProvider();

  private PluginRepositoryProvider()
  {
    super(IPluginContainer.INSTANCE);
  }
}