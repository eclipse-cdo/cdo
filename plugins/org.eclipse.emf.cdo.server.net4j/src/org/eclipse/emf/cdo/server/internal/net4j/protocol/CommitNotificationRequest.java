/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends CDOServerRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitNotificationRequest.class);

  private CDOBranchPoint branchPoint;

  private final CDOPackageUnit[] packageUnits;

  private List<CDOIDAndVersion> dirtyIDs;

  private List<CDORevisionDelta> deltas;

  private List<CDOID> detachedObjects;

  public CommitNotificationRequest(CDOServerProtocol serverProtocol, CDOBranchPoint branchPoint,
      CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects,
      List<CDORevisionDelta> deltas)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
    this.branchPoint = branchPoint;
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
      TRACER.format("Writing branchPoint: {0}", branchPoint); //$NON-NLS-1$
    }

    out.writeCDOBranchPoint(branchPoint);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} dirty IDs", dirtyIDs.size()); //$NON-NLS-1$
    }

    out.writeCDOPackageUnits(packageUnits);

    out.writeInt(dirtyIDs == null ? 0 : dirtyIDs.size());
    for (CDOIDAndVersion dirtyID : dirtyIDs)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing dirty ID: {0}", dirtyID); //$NON-NLS-1$
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
