package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.server.PluginRepositoryProvider;

/**
 * @author Eike Stepper
 */
public final class CDOPluginProtocolFactory extends CDOServerProtocolFactory
{
  public CDOPluginProtocolFactory()
  {
    super(PluginRepositoryProvider.INSTANCE);
  }
}