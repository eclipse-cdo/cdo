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
public class InvalidationNotificationRequest extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      InvalidationNotificationRequest.class);

  private Collection<Long> changedObjectIds;

  public InvalidationNotificationRequest(Channel channel, Collection<Long> changedObjectIds)
  {
    super(channel);
    this.changedObjectIds = changedObjectIds;
  }

  @Override
  protected short getSignalID()
  {
    return CDOSignals.INVALIDATION_NOTIFICATION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    int size = changedObjectIds.size();
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Transmitting " + size + " invalidations");
    }

    out.writeInt(size);
    for (Long oid : changedObjectIds)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "Transmitting oid " + oid);
      }

      out.writeLong(oid);
    }
  }
}
