/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.cdo.core.CDOResSignals;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import java.util.List;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class ResourcesChangedRequest extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      ResourcesChangedRequest.class);

  private List<ResourceChangeInfo> infos;

  public ResourcesChangedRequest(Channel channel, List<ResourceChangeInfo> infos)
  {
    super(channel);
    this.infos = infos;
  }

  @Override
  protected short getSignalID()
  {
    return CDOResSignals.RESOURCES_CHANGED;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Transmitting " + infos.size() + " resource changes");
    }

    for (ResourceChangeInfo info : infos)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "Transmitting changeKind=" + info.getChangeKind() + ", rid="
            + info.getRID() + ". path=" + info.getPath());
      }

      info.transmit(out);
    }

    out.writeByte(ResourceChangeInfo.NONE);
  }
}
