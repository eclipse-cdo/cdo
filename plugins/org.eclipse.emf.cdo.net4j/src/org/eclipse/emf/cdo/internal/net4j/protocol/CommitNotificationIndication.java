/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitNotificationIndication extends CDOClientIndication
{
  public CommitNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalCDOSession session = getSession();
    CDOCommitInfo commitInfo = in.readCDOCommitInfo(session.getCommitInfoManager());
    session.handleCommitNotification(commitInfo);

    // CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Read branchpoint: {0}", branchPoint); //$NON-NLS-1$
    // }
    //
    // CDOPackageUnit[] packageUnits = in.readCDOPackageUnits(null);
    // InternalCDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    // for (int i = 0; i < packageUnits.length; i++)
    // {
    // packageRegistry.putPackageUnit((InternalCDOPackageUnit)packageUnits[i]);
    // }
    //
    // int size = in.readInt();
    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Reading {0} dirty IDs", size); //$NON-NLS-1$
    // }
    //
    // InternalCDOSession session = getSession();
    // Set<CDOIDAndVersion> dirtyOIDandVersions = new HashSet<CDOIDAndVersion>();
    // for (int i = 0; i < size; i++)
    // {
    // CDOIDAndVersion dirtyOIDandVersion = in.readCDOIDAndVersion();
    // if (TRACER.isEnabled())
    // {
    //        TRACER.format("Read dirty ID: {0}", dirtyOIDandVersion); //$NON-NLS-1$
    // }
    //
    // dirtyOIDandVersions.add(dirtyOIDandVersion);
    // }
    //
    // size = in.readInt();
    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Reading {0} Deltas", size); //$NON-NLS-1$
    // }
    //
    // List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
    // for (int i = 0; i < size; i++)
    // {
    // CDORevisionDelta revisionDelta = in.readCDORevisionDelta();
    // deltas.add(revisionDelta);
    // }
    //
    // size = in.readInt();
    // if (TRACER.isEnabled())
    // {
    //      TRACER.format("Reading {0} Detach Objects", size); //$NON-NLS-1$
    // }
    //
    // List<CDOID> detachedObjects = new ArrayList<CDOID>();
    // for (int i = 0; i < size; i++)
    // {
    // detachedObjects.add(in.readCDOID());
    // }
  }
}
