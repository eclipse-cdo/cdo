/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOTimestampContext;
import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTimestampContextImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class SyncRevisionRequest extends CDOClientRequest<Collection<CDOTimestampContext>>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionRequest.class);

  private Map<CDOID, CDORevision> collectionRevisions;

  private CDOSessionImpl cdoSession;

  private int referenceChunk;

  public SyncRevisionRequest(IChannel channel, CDOSessionImpl cdoSession, Map<CDOID, CDORevision> cdoRevisions,
      int referenceChunk)
  {
    super(channel);
    collectionRevisions = cdoRevisions;
    this.referenceChunk = referenceChunk;
    this.cdoSession = cdoSession;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_SYNC;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Synchronization " + collectionRevisions.size() + " objects");
    }

    out.writeInt(referenceChunk);
    out.writeInt(collectionRevisions.size());
    for (CDORevision revision : collectionRevisions.values())
    {
      out.writeCDOID(revision.getID());
      out.writeInt(revision.getVersion());
    }
  }

  private CDOTimestampContext getMap(Map<Long, CDOTimestampContext> mapOfContext, long timestamp)
  {
    CDOTimestampContext result = mapOfContext.get(timestamp);
    if (result == null)
    {
      result = new CDOTimestampContextImpl(timestamp);
      mapOfContext.put(timestamp, result);
    }

    return result;
  }

  @Override
  protected Collection<CDOTimestampContext> confirming(CDODataInput in) throws IOException
  {
    CDORevisionManagerImpl revisionManager = getRevisionManager();
    TreeMap<Long, CDOTimestampContext> mapofContext = new TreeMap<Long, CDOTimestampContext>();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDORevision revision = in.readCDORevision();
      long revised = in.readLong();

      CDORevision oldRevision = collectionRevisions.get(revision.getID());
      if (oldRevision == null)
      {
        throw new IllegalStateException("Didn't expect to receive object with id '" + revision.getID() + "'");
      }

      Set<CDOIDAndVersion> dirtyObjects = getMap(mapofContext, revised).getDirtyObjects();
      dirtyObjects.add(CDOIDUtil.createIDAndVersion(oldRevision.getID(), oldRevision.getVersion()));
      revisionManager.addCachedRevision((InternalCDORevision)revision);
    }

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Synchronization received  " + size + " dirty objects");
    }

    size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      long revised = in.readLong();

      Collection<CDOID> detachedObjects = getMap(mapofContext, revised).getDetachedObjects();
      detachedObjects.add(id);
    }

    for (CDOTimestampContext timestampContext : mapofContext.values())
    {
      Set<CDOIDAndVersion> dirtyObjects = timestampContext.getDirtyObjects();
      Collection<CDOID> detachedObjects = timestampContext.getDetachedObjects();

      dirtyObjects = Collections.unmodifiableSet(dirtyObjects);
      detachedObjects = Collections.unmodifiableCollection(detachedObjects);

      ((CDOTimestampContextImpl)timestampContext).setDirtyObjects(dirtyObjects);
      ((CDOTimestampContextImpl)timestampContext).setDetachedObjects(detachedObjects);

      cdoSession.handleSyncResponse(timestampContext.getTimestamp(), dirtyObjects, detachedObjects);
    }

    return Collections.unmodifiableCollection(mapofContext.values());
  }
}
