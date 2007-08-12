package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.ClientProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class CDOClientProtocolFactory extends ClientProtocolFactory<CDOClientProtocol>
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  public CDOClientProtocolFactory()
  {
    super(TYPE);
  }

  public CDOClientProtocol create(String description)
  {
    return new CDOClientProtocol();
  }

  public static CDOClientProtocol get(IManagedContainer container, String description)
  {
    return (CDOClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}