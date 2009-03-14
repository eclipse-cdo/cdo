/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends CDOServerRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitNotificationRequest.class);

  private long timeStamp;

  private final CDOPackageUnit[] packageUnits;

  private List<CDOIDAndVersion> dirtyIDs;

  private List<CDORevisionDelta> deltas;

  private List<CDOID> detachedObjects;

  public CommitNotificationRequest(IChannel channel, long timeStamp, CDOPackageUnit[] packageUnits,
      List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects, List<CDORevisionDelta> deltas)
  {
    super(channel, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
    this.timeStamp = timeStamp;
    this.packageUnits = packageUnits;
    this.dirtyIDs = dirtyIDs;
    this.deltas = deltas;
    this.detachedObjects = detachedObjects;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing timeStamp: {0,date} {0,time}", timeStamp);
    }

    out.writeLong(timeStamp);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} dirty IDs", dirtyIDs.size());
    }

    out.writeCDOPackageUnits(packageUnits);

    out.writeInt(dirtyIDs == null ? 0 : dirtyIDs.size());
    for (CDOIDAndVersion dirtyID : dirtyIDs)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing dirty ID: {0}", dirtyID);
      }

      out.writeCDOIDAndVersion(dirtyID);
    }

    out.writeInt(deltas == null ? 0 : deltas.size());
    for (CDORevisionDelta delta : deltas)
    {
      out.writeCDORevisionDelta(delta);
    }

    out.writeInt(detachedObjects == null ? 0 : detachedObjects.size());
    for (CDOID id : detachedObjects)
    {
      out.writeCDOID(id);
    }
  }
}
