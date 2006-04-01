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


import org.eclipse.net4j.core.impl.AbstractRequest;

import org.eclipse.emf.cdo.core.CdoResSignals;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;

import java.util.List;


public class ResourcesChangedRequest extends AbstractRequest
{
  private List<ResourceChangeInfo> infos;

  public ResourcesChangedRequest(List<ResourceChangeInfo> infos)
  {
    this.infos = infos;
  }

  public short getSignalId()
  {
    return CdoResSignals.RESOURCES_CHANGED;
  }

  public void request()
  {
    for (ResourceChangeInfo info : infos)
    {
      info.transmit(getChannel());
    }

    transmitByte(ResourceChangeInfo.NONE);
  }
}
