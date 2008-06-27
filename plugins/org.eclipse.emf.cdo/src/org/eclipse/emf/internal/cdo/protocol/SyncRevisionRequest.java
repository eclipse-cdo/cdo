/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *                   https://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class SyncRevisionRequest extends CDOClientRequest<Set<CDOIDAndVersion>>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionRequest.class);

  private Map<CDOID, CDORevision>  collectionRevisions;

  private CDOSessionImpl cdoSession;

  private int referenceChunk;

  public SyncRevisionRequest(IChannel channel, CDOSessionImpl cdoSession, Map<CDOID, CDORevision>  cdoRevisions,
      int referenceChunk)
  {
    super(channel);
    this.collectionRevisions = cdoRevisions;
    this.referenceChunk = referenceChunk;
    this.cdoSession = cdoSession;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_SYNC;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled()) PROTOCOL.trace("Synchronization " + collectionRevisions.size() + " objects");

    out.writeInt(referenceChunk);
    out.writeInt(collectionRevisions.size());

    for (CDORevision revision : collectionRevisions.values())
    {
      CDOIDUtil.write(out, revision.getID());

      if (revision != null)
      {
        out.writeInt(revision.getVersion());
      }
      else
        out.writeInt(-1);

    }

  }

  @Override
  protected Set<CDOIDAndVersion> confirming(ExtendedDataInputStream in) throws IOException
  {
    Set<CDOIDAndVersion> listofDirtyObjects = new HashSet<CDOIDAndVersion>();

    int size = in.readInt();

    for (int i = 0; i < size; i++)
    {
      CDORevisionImpl revision = new CDORevisionImpl(in, getSession().getRevisionManager(), getSession()
          .getPackageManager());
      
      CDORevision oldRevision = collectionRevisions.get(revision.getID());
      
      if (oldRevision == null)
        throw new IllegalStateException();
      
      listofDirtyObjects.add(CDOIDUtil.createIDAndVersion(oldRevision.getID(), oldRevision.getVersion()));
      
      getSession().getRevisionManager().addRevision(revision);
    }

    if (PROTOCOL.isEnabled()) PROTOCOL.trace("Synchronization received  " + size + " dirty objects");

    cdoSession.notifySync(listofDirtyObjects);

    return listofDirtyObjects;
  }
}
