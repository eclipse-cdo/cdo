/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class SyncRevisionsIndication extends AbstractSyncRevisionsIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionsIndication.class);

  public SyncRevisionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SYNC_REVISIONS);
  }

  public SyncRevisionsIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    IStoreAccessor reader = StoreThreadLocal.getAccessor();
    if (TRACER.isEnabled())
    {
      TRACER.format("Refreshing store accessor: " + reader); //$NON-NLS-1$
    }

    reader.refreshRevisions();
    super.indicating(in);
  }

  @Override
  protected void process(CDOID id, int version)
  {
    if (version > 0)
    {
      udpateObjectList(id, version);
    }
  }
}
