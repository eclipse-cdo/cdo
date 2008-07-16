/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.RevisionAdjuster;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends CDOClientRequest<CommitTransactionResult>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionRequest.class);

  private CDOTransactionImpl transaction;

  public CommitTransactionRequest(IChannel channel, final CDOTransactionImpl transaction)
  {
    super(channel);
    this.transaction = transaction;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    List<CDOPackage> newPackages = transaction.getNewPackages();
    Collection<CDOResource> newResources = transaction.getNewResources().values();
    Collection<CDOObject> newObjects = transaction.getNewObjects().values();
    Collection<CDORevisionDelta> dirtyObjects = transaction.getRevisionDeltas().values();

    out.writeInt(transaction.getViewID());
    out.writeInt(newPackages.size());
    out.writeInt(newResources.size() + newObjects.size());
    out.writeInt(dirtyObjects.size());

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new packages", newPackages.size());
    }

    for (CDOPackage newPackage : newPackages)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing package {0}", newPackage);
      }

      CDOModelUtil.writePackage(out, newPackage);
    }

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new objects", newResources.size() + newObjects.size());
    }

    writeRevisions(out, newResources);
    writeRevisions(out, newObjects);

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} dirty objects", dirtyObjects.size());
    }

    RevisionAdjuster revisionAdjuster = new RevisionAdjuster(transaction);
    for (CDORevisionDelta revisionDelta : dirtyObjects)
    {
      revisionDelta.write(out, transaction);
      CDOObject object = transaction.getDirtyObjects().get(revisionDelta.getID());
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
      revisionAdjuster.adjustRevision(revision, revisionDelta);
    }
  }

  @Override
  protected CommitTransactionResult confirming(ExtendedDataInputStream in) throws IOException
  {
    boolean success = in.readBoolean();
    if (!success)
    {
      String rollbackMessage = in.readString();
      OM.LOG.error(rollbackMessage);
      return new CommitTransactionResult(rollbackMessage);
    }

    long timeStamp = in.readLong();
    CommitTransactionResult result = new CommitTransactionResult(timeStamp);

    CDOSessionImpl session = transaction.getSession();
    List<CDOPackage> newPackages = transaction.getNewPackages();
    for (CDOPackage newPackage : newPackages)
    {
      if (newPackage.getParentURI() == null)
      {
        CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
        CDOIDMetaRange newRange = CDOIDUtil.readMetaRange(in);
        ((InternalCDOPackage)newPackage).setMetaIDRange(newRange);
        for (int i = 0; i < oldRange.size(); i++)
        {
          CDOIDTemp oldID = (CDOIDTemp)oldRange.get(i);
          CDOID newID = newRange.get(i);
          session.remapMetaInstance(oldID, newID);
          result.addIDMapping(oldID, newID);
        }
      }
    }

    for (;;)
    {
      CDOIDTemp oldID = (CDOIDTemp)CDOIDUtil.read(in, session);
      if (oldID.isNull())
      {
        break;
      }

      CDOID newID = CDOIDUtil.read(in, session);
      result.addIDMapping(oldID, newID);
    }

    return result;
  }

  @SuppressWarnings("unused")
  private void writeDirtyObjects(ExtendedDataOutputStream out) throws IOException
  {
    Collection<CDOObject> dirtyObjects = transaction.getDirtyObjects().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} dirty objects", dirtyObjects.size());
    }

    writeRevisions(out, dirtyObjects);
  }

  private void writeRevisions(ExtendedDataOutputStream out, Collection<?> objects) throws IOException
  {
    for (Iterator<?> it = objects.iterator(); it.hasNext();)
    {
      CDOObject object = (CDOObject)it.next();
      CDORevision revision = object.cdoRevision();
      revision.write(out, transaction, CDORevision.UNCHUNKED);
    }
  }
}
