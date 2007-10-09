package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class ServerProtocolFactory extends org.eclipse.internal.net4j.ServerProtocolFactory
{
  public static final String TYPE = ProtocolConstants.PROTOCOL_NAME;

  public ServerProtocolFactory()
  {
    super(TYPE);
  }

  public ServerProtocol create(String description)
  {
    return new ServerProtocol();
  }

  public static ServerProtocol get(IManagedContainer container, String description)
  {
    return (ServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}