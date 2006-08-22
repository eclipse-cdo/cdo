/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;


public class QueryExtentIndication extends AbstractIndicationWithResponse
{
  private int cid;

  private boolean exactMatch;

  private int rid;

  public short getSignalId()
  {
    return CDOProtocol.QUERY_EXTENT;
  }

  public void indicate()
  {
    cid = receiveInt();
    exactMatch = receiveBoolean();
    rid = receiveInt();
  }

  public void respond()
  {
    Mapper mapper = ((ServerCDOProtocol) getProtocol()).getMapper();
    mapper.transmitExtent(getChannel(), cid, exactMatch, rid);
  }
}
