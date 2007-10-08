package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.protocol.ProtocolConstants;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class BuddiesServerProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = ProtocolConstants.PROTOCOL_NAME;

  public BuddiesServerProtocolFactory()
  {
    super(TYPE);
  }

  public BuddiesServerProtocol create(String description)
  {
    return new BuddiesServerProtocol();
  }

  public static BuddiesServerProtocol get(IManagedContainer container, String description)
  {
    return (BuddiesServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}