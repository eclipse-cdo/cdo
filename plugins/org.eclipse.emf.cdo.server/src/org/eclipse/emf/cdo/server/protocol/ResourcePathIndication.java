/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CdoProtocol;
import org.eclipse.emf.cdo.server.CdoServerProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ResourceManager;


public class ResourcePathIndication extends AbstractIndicationWithResponse
{
  private String path;

  public short getSignalId()
  {
    return CdoProtocol.RESOURCE_PATH;
  }

  public void indicate()
  {
    path = receiveString();
    if (isDebugEnabled()) debug("Requested path " + path);
  }

  public void respond()
  {
    Mapper mapper = ((CdoServerProtocol) getProtocol()).getMapper();
    ResourceManager resourceManager = mapper.getResourceManager();
    ResourceInfo info = resourceManager.getResourceInfo(path, mapper);

    if (info == null)
    {
      int rid = mapper.getNextRid();
      resourceManager.registerResourceInfo(path, rid, 1);

      if (isDebugEnabled()) debug("No resource with path " + path);
      if (isDebugEnabled()) debug("Reserving rid " + rid);
      transmitInt(-rid);
    }
    else
    {
      if (isDebugEnabled()) debug("Responding rid " + info.getRid());
      transmitInt(info.getRid());
    }
  }
}
