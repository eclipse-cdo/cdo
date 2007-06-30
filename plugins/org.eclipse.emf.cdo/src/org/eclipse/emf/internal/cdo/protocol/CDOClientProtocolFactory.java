package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.internal.net4j.ClientProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class CDOClientProtocolFactory extends ClientProtocolFactory
{
  public CDOClientProtocolFactory()
  {
    super(CDOProtocolConstants.PROTOCOL_NAME);
  }

  public Object create(String description)
  {
    return new CDOClientProtocol();
  }
}