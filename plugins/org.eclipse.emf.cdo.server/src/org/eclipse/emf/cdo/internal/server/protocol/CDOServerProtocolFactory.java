package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class CDOServerProtocolFactory extends ServerProtocolFactory<CDOServerProtocol>
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  public CDOServerProtocolFactory()
  {
    super(TYPE);
  }

  public CDOServerProtocol create(String description)
  {
    return new CDOServerProtocol();
  }

  public static CDOServerProtocol get(IManagedContainer container, String description)
  {
    return (CDOServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}