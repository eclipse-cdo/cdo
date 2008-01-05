/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.RevisionAdjuster;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

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
    out.writeInt(transaction.getViewID());
    writeNewPackages(out);
    writeNewResources(out);
    writeNewObjects(out);
    writeRevisionDeltas(out);
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

    List<CDOPackage> newPackages = transaction.getNewPackages();
    for (CDOPackage newPackage : newPackages)
    {
      CDOIDRange oldRange = newPackage.getMetaIDRange();
      CDOIDRange newRange = CDOIDRangeImpl.read(in);
      ((CDOPackageImpl)newPackage).setMetaIDRange(newRange);
      for (long i = 0; i < oldRange.getCount(); i++)
      {
        CDOID oldID = oldRange.get(i);
        CDOID newID = newRange.get(i);
        transaction.getSession().remapMetaInstance(oldID, newID);
        result.addIDMapping(oldID, newID);
      }
    }

    for (;;)
    {
      CDOID oldID = CDOIDImpl.read(in);
      if (oldID.isNull())
      {
        break;
      }

      CDOID newID = CDOIDImpl.read(in);
      result.addIDMapping(oldID, newID);
    }

    return result;
  }

  private void writeNewPackages(ExtendedDataOutputStream out) throws IOException
  {
    List<CDOPackage> newPackages = transaction.getNewPackages();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new packages", newPackages.size());
    }

    out.writeInt(newPackages.size());
    for (CDOPackage newPackage : newPackages)
    {
      ((CDOPackageImpl)newPackage).write(out);
    }
  }

  private void writeNewResources(ExtendedDataOutputStream out) throws IOException
  {
    Collection<CDOResource> newResources = transaction.getNewResources().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new resources", newResources.size());
    }

    writeRevisions(out, newResources);
  }

  private void writeNewObjects(ExtendedDataOutputStream out) throws IOException
  {
    Collection<CDOObject> newObjects = transaction.getNewObjects().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new objects", newObjects.size());
    }

    writeRevisions(out, newObjects);
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

  private void writeRevisionDeltas(ExtendedDataOutputStream out) throws IOException
  {
    Collection<CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} revision deltas", revisionDeltas.size());
    }

    RevisionAdjuster revisionAdjuster = new RevisionAdjuster(transaction);
    out.writeInt(revisionDeltas.size());
    for (CDORevisionDelta revisionDelta : revisionDeltas)
    {
      ((CDORevisionDeltaImpl)revisionDelta).write(out, transaction);
      // CDORevisionImpl revision = transaction.getRevision(revisionDelta.getID());
      CDOObject object = transaction.getDirtyObjects().get(revisionDelta.getID());
      CDORevisionImpl revision = (CDORevisionImpl)object.cdoRevision();
      revisionAdjuster.adjustRevision(revision, revisionDelta);
    }
  }

  private void writeRevisions(ExtendedDataOutputStream out, Collection<?> objects) throws IOException
  {
    out.writeInt(objects.size());
    for (Iterator<?> it = objects.iterator(); it.hasNext();)
    {
      InternalCDOObject object = (InternalCDOObject)it.next();
      CDORevisionImpl revision = (CDORevisionImpl)object.cdoRevision();
      revision.write(out, transaction, CDORevision.UNCHUNKED);
    }
  }
}
