package org.eclipse.net4j.transport;

import org.eclipse.net4j.transport.Connector.Type;
import org.eclipse.net4j.util.registry.IRegistryElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface ProtocolFactory extends IRegistryElement<String>
{
  public static final Set<Type> FOR_CLIENTS = Collections.singleton(Type.CLIENT);

  public static final Set<Type> FOR_SERVERS = Collections.singleton(Type.SERVER);

  public static final Set<Type> SYMMETRIC = Collections.unmodifiableSet(new HashSet(Arrays
      .asList(new Type[] { Type.CLIENT, Type.SERVER })));

  public Set<Type> getConnectorTypes();

  public boolean isForClients();

  public boolean isForServers();

  public boolean isSymmetric();

  public Protocol createProtocol(Channel channel);
}