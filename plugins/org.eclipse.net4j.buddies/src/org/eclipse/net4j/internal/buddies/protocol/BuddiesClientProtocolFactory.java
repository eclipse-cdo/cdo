package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.protocol.BuddiesProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.ClientProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class BuddiesClientProtocolFactory extends ClientProtocolFactory
{
  public static final String TYPE = BuddiesProtocolConstants.PROTOCOL_NAME;

  public BuddiesClientProtocolFactory()
  {
    super(TYPE);
  }

  public BuddiesClientProtocol create(String description)
  {
    return new BuddiesClientProtocol();
  }

  public static BuddiesClientProtocol get(IManagedContainer container, String description)
  {
    return (BuddiesClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}