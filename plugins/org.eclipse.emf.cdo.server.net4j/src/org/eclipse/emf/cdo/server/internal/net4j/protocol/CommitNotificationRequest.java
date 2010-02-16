/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends CDOServerRequest
{
  private CDOCommitInfo commitInfo;

  public CommitNotificationRequest(CDOServerProtocol serverProtocol, CDOCommitInfo commitInfo)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
    this.commitInfo = commitInfo;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOCommitInfo(commitInfo);

    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Writing branchPoint: {0}", branchPoint); //$NON-NLS-1$
    // }
    //
    // out.writeCDOBranchPoint(branchPoint);
    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Writing {0} dirty IDs", dirtyIDs.size()); //$NON-NLS-1$
    // }
    //
    // out.writeCDOPackageUnits(packageUnits);
    //
    // out.writeInt(dirtyIDs == null ? 0 : dirtyIDs.size());
    // for (CDOIDAndVersion dirtyID : dirtyIDs)
    // {
    // if (TRACER.isEnabled())
    // {
    //        TRACER.format("Writing dirty ID: {0}", dirtyID); //$NON-NLS-1$
    // }
    //
    // out.writeCDOIDAndVersion(dirtyID);
    // }
    //
    // out.writeInt(deltas == null ? 0 : deltas.size());
    // for (CDORevisionDelta delta : deltas)
    // {
    // out.writeCDORevisionDelta(delta);
    // }
    //
    // out.writeInt(detachedObjects == null ? 0 : detachedObjects.size());
    // for (CDOID id : detachedObjects)
    // {
    // out.writeCDOID(id);
    // }
  }
}
