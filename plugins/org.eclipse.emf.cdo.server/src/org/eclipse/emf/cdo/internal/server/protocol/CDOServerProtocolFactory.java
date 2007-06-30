package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.internal.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class CDOServerProtocolFactory extends ServerProtocolFactory
{

  public CDOServerProtocolFactory()
  {
    super(CDOProtocolConstants.PROTOCOL_NAME);
  }

  public Object create(String description)
  {
    return new CDOServerProtocol();
  }
}