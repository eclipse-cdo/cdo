package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class ClientProtocolFactory extends org.eclipse.internal.net4j.ClientProtocolFactory
{
  public static final String TYPE = ProtocolConstants.PROTOCOL_NAME;

  public ClientProtocolFactory()
  {
    super(TYPE);
  }

  public ClientProtocol create(String description)
  {
    return new ClientProtocol();
  }

  public static ClientProtocol get(IManagedContainer container, String description)
  {
    return (ClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}