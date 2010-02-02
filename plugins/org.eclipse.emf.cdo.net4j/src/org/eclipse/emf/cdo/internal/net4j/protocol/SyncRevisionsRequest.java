/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.transaction.CDORefreshContext;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class SyncRevisionsRequest extends AbstractSyncRevisionsRequest
{
  public SyncRevisionsRequest(CDOClientProtocol protocol, Map<CDOID, CDOIDAndVersion> idAndVersions, int referenceChunk)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_SYNC_REVISIONS, idAndVersions, referenceChunk);
  }

  public SyncRevisionsRequest(CDOClientProtocol protocol, short signalID, Map<CDOID, CDOIDAndVersion> idAndVersions,
      int referenceChunk)
  {
    super(protocol, signalID, idAndVersions, referenceChunk);
  }

  @Override
  protected Collection<CDORefreshContext> confirming(CDODataInput in) throws IOException
  {
    Collection<CDORefreshContext> refreshContexts = super.confirming(in);
    Collection<CDOPackageUnit> emptyNewPackageUnits = Collections.emptyList();
    for (CDORefreshContext refreshContext : refreshContexts)
    {
      getSession().handleSyncResponse(refreshContext.getBranchPoint(), emptyNewPackageUnits,
          refreshContext.getDirtyObjects(), refreshContext.getDetachedObjects());
    }

    return refreshContexts;
  }
}
