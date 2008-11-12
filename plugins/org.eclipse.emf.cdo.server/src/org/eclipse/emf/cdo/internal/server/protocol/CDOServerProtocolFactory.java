package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  private IRepositoryProvider repositoryProvider;

  public CDOServerProtocolFactory(IRepositoryProvider repositoryProvider)
  {
    super(TYPE);
    this.repositoryProvider = repositoryProvider;
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  public CDOServerProtocol create(String description)
  {
    return new CDOServerProtocol(repositoryProvider);
  }

  public static CDOServerProtocol get(IManagedContainer container, String description)
  {
    return (CDOServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
