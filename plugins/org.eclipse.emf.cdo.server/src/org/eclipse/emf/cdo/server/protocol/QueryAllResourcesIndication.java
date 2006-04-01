package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.Protocol;
import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CdoResSignals;
import org.eclipse.emf.cdo.server.CdoResServerProtocol;
import org.eclipse.emf.cdo.server.Mapper;


public class QueryAllResourcesIndication extends AbstractIndicationWithResponse implements
    CdoResSignals
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
    Mapper mapper = ((CdoResServerProtocol) protocol).getMapper();
    mapper.transmitAllResources(channel);
  }
}
