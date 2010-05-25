/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.session.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOTimeStampContextImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public abstract class AbstractSyncRevisionsRequest extends CDOClientRequest<Collection<CDOTimeStampContext>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, AbstractSyncRevisionsRequest.class);

  protected Map<CDOID, CDOIDAndVersion> idAndVersions;

  protected int referenceChunk;

  public AbstractSyncRevisionsRequest(CDOClientProtocol protocol, short signalID,
      Map<CDOID, CDOIDAndVersion> idAndVersions, int referenceChunk)
  {
    super(protocol, signalID);
    this.idAndVersions = idAndVersions;
    this.referenceChunk = referenceChunk;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Synchronization " + idAndVersions.size() + " objects"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    out.writeInt(referenceChunk);
    out.writeInt(idAndVersions.size());
    for (CDOIDAndVersion idAndVersion : idAndVersions.values())
    {
      out.writeCDOIDAndVersion(idAndVersion);
    }
  }

  @Override
  protected Collection<CDOTimeStampContext> confirming(CDODataInput in) throws IOException
  {
    CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)getSession().getRevisionManager();
    TreeMap<Long, CDOTimeStampContext> mapofContext = new TreeMap<Long, CDOTimeStampContext>();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDORevision revision = in.readCDORevision();
      long revised = in.readLong();

      CDOIDAndVersion idAndVersion = idAndVersions.get(revision.getID());
      if (idAndVersion == null)
      {
        throw new IllegalStateException(MessageFormat.format(
            Messages.getString("SyncRevisionsRequest.2"), revision.getID())); //$NON-NLS-1$
      }

      Set<CDOIDAndVersion> dirtyObjects = getMap(mapofContext, revised).getDirtyObjects();
      dirtyObjects.add(CDOIDUtil.createIDAndVersion(idAndVersion.getID(), idAndVersion.getVersion()));
      revisionManager.addCachedRevision((InternalCDORevision)revision);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Synchronization received  " + size + " dirty objects"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      long revised = in.readLong();

      Collection<CDOID> detachedObjects = getMap(mapofContext, revised).getDetachedObjects();
      detachedObjects.add(id);
    }

    for (CDOTimeStampContext timestampContext : mapofContext.values())
    {
      Set<CDOIDAndVersion> dirtyObjects = timestampContext.getDirtyObjects();
      Collection<CDOID> detachedObjects = timestampContext.getDetachedObjects();

      dirtyObjects = Collections.unmodifiableSet(dirtyObjects);
      detachedObjects = Collections.unmodifiableCollection(detachedObjects);

      ((CDOTimeStampContextImpl)timestampContext).setDirtyObjects(dirtyObjects);
      ((CDOTimeStampContextImpl)timestampContext).setDetachedObjects(detachedObjects);
    }

    return Collections.unmodifiableCollection(mapofContext.values());
  }

  private CDOTimeStampContext getMap(Map<Long, CDOTimeStampContext> mapOfContext, long timestamp)
  {
    CDOTimeStampContext result = mapOfContext.get(timestamp);
    if (result == null)
    {
      result = new CDOTimeStampContextImpl(timestamp);
      mapOfContext.put(timestamp, result);
    }

    return result;
  }
}
