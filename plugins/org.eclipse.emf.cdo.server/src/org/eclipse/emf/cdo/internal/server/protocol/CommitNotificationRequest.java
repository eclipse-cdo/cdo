/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends Request
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CommitNotificationRequest.class);

  private long timeStamp;

  private CDOIDProvider provider;

  private List<CDOIDAndVersion> dirtyIDs;

  private List<CDORevisionDelta> deltas;

  public CommitNotificationRequest(IChannel channel, CDOIDProvider provider, long timeStamp,
      List<CDOIDAndVersion> dirtyIDs, List<CDORevisionDelta> deltas)
  {
    super(channel);
    this.provider = provider;
    this.timeStamp = timeStamp;
    this.dirtyIDs = dirtyIDs;
    this.deltas = deltas;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_INVALIDATION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing timeStamp: {0,date} {0,time}", timeStamp);
    }

    out.writeLong(timeStamp);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} dirty IDs", dirtyIDs.size());
    }

    out.writeInt(dirtyIDs == null ? 0 : dirtyIDs.size());
    for (CDOIDAndVersion dirtyID : dirtyIDs)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing dirty ID: {0}", dirtyID);
      }

      dirtyID.write(out, false);
    }

    out.writeInt(deltas == null ? 0 : deltas.size());
    for (CDORevisionDelta delta : deltas)
    {
      delta.write(out, provider);
    }
  }
}
