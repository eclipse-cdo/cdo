package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.Protocol;
import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CDOResSignals;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;


public class QueryAllResourcesIndication extends AbstractIndicationWithResponse implements
    CDOResSignals
{
  public QueryAllResourcesIndication()
  {
  }

  public short getSignalId()
  {
    return QUERY_ALL_RESOURCES;
  }

  public void indicate()
  {
  }

  public void respond()
  {
    Protocol protocol = getProtocol();
    Mapper mapper = ((ServerCDOResProtocol) protocol).getMapper();
    mapper.transmitAllResources(channel);
  }
}
