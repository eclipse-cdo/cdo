package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public final class ClientProtocolFactoryRegistry extends
    HashMapRegistry<String, ProtocolFactory>
{
  @Override
  protected ProtocolFactory register(String protocolName, ProtocolFactory factory)
  {
    if (factory.isForClients())
    {
      return super.register(protocolName, factory);
    }
    else
    {
      throw new IllegalArgumentException("Protocol factory is not for clients: " + factory);
    }
  }
}