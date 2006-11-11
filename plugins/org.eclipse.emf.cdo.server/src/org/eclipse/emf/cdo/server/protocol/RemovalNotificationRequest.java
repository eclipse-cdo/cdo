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

import org.eclipse.emf.cdo.core.CDOSignals;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import java.util.Collection;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class RemovalNotificationRequest extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      RemovalNotificationRequest.class);

  private Collection<Integer> rids;

  public RemovalNotificationRequest(Channel channel, Collection<Integer> rids)
  {
    super(channel);
    this.rids = rids;
  }

  @Override
  protected short getSignalID()
  {
    return CDOSignals.REMOVAL_NOTIFICATION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    int size = rids.size();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Transmitting " + size + " removals");
    }

    out.writeInt(size);
    for (Integer rid : rids)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Transmitting rid " + rid);
      }

      out.writeInt(rid);
    }
  }
}
