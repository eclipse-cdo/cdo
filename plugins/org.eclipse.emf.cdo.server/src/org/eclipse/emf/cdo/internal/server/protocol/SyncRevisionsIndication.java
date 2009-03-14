/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class SyncRevisionsIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionsIndication.class);

  private List<Pair<InternalCDORevision, Long>> dirtyObjects = new ArrayList<Pair<InternalCDORevision, Long>>();

  private List<Pair<CDOID, Long>> detachedObjects = new ArrayList<Pair<CDOID, Long>>();

  private int referenceChunk;

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
      TRACER.format("Refreshing store accessor: " + reader);
    }

    reader.refreshRevisions();
    referenceChunk = in.readInt();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      int version = in.readInt();
      if (version > 0)
      {
        try
        {
          InternalCDORevision revision = getRepository().getRevisionManager().getRevision(id, referenceChunk);
          if (revision == null)
          {
            detachedObjects.add(new Pair<CDOID, Long>(id, getTimestamp(id, version)));
          }
          else if (revision.getVersion() != version)
          {
            dirtyObjects.add(new Pair<InternalCDORevision, Long>(revision, getTimestamp(id, version)));
          }
        }
        catch (IllegalArgumentException revisionIsNullException)
        {
          detachedObjects.add(new Pair<CDOID, Long>(id, getTimestamp(id, version)));
        }
      }
    }
  }

  private long getTimestamp(CDOID id, int version)
  {
    CDORevision revision = getRepository().getRevisionManager().getRevisionByVersion(id, 0, version, false);
    if (revision != null)
    {
      return revision.getRevised() + 1;
    }

    return CDORevision.UNSPECIFIED_DATE;
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Sync found " + dirtyObjects.size() + " dirty objects");
    }

    out.writeInt(dirtyObjects.size());
    for (Pair<InternalCDORevision, Long> revisionAndOldRevised : dirtyObjects)
    {
      out.writeCDORevision(revisionAndOldRevised.getElement1(), referenceChunk);
      out.writeLong(revisionAndOldRevised.getElement2());
    }

    out.writeInt(detachedObjects.size());
    for (Pair<CDOID, Long> idAndRevised : detachedObjects)
    {
      out.writeCDOID(idAndRevised.getElement1());
      out.writeLong(idAndRevised.getElement2());
    }
  }
}
