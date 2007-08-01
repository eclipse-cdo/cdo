/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;

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
    super(channel, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
    this.transaction = transaction;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    writeNewPackages(out);
    writeNewResources(out);
    writeNewObjects(out);
    writeDirtyObjects(out);
  }

  private void writeNewPackages(ExtendedDataOutputStream out) throws IOException
  {
    List<CDOPackageImpl> newPackages = transaction.getNewPackages();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new packages", newPackages.size());
    }

    out.writeInt(newPackages.size());
    for (CDOPackageImpl newPackage : newPackages)
    {
      newPackage.write(out);
    }
  }

  private void writeNewResources(ExtendedDataOutputStream out) throws IOException
  {
    Collection<CDOResourceImpl> newResources = transaction.getNewResources().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new resources", newResources.size());
    }

    writeRevisions(out, newResources);
  }

  private void writeNewObjects(ExtendedDataOutputStream out) throws IOException
  {
    Collection<InternalCDOObject> newObjects = transaction.getNewObjects().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new objects", newObjects.size());
    }

    writeRevisions(out, newObjects);
  }

  private void writeDirtyObjects(ExtendedDataOutputStream out) throws IOException
  {
    Collection<InternalCDOObject> dirtyObjects = transaction.getDirtyObjects().values();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} dirty objects", dirtyObjects.size());
    }

    writeRevisions(out, dirtyObjects);
  }

  private void writeRevisions(ExtendedDataOutputStream out, Collection objects) throws IOException
  {
    out.writeInt(objects.size());
    for (Iterator it = objects.iterator(); it.hasNext();)
    {
      InternalCDOObject object = (InternalCDOObject)it.next();
      CDORevisionImpl revision = (CDORevisionImpl)object.cdoRevision();
      revision.write(out, transaction);
    }
  }

  @Override
  protected CommitTransactionResult confirming(ExtendedDataInputStream in) throws IOException
  {
    long timeStamp = in.readLong();
    CommitTransactionResult result = new CommitTransactionResult(timeStamp);

    List<CDOPackageImpl> newPackages = transaction.getNewPackages();
    for (CDOPackageImpl newPackage : newPackages)
    {
      CDOIDRange oldRange = newPackage.getMetaIDRange();
      CDOIDRange newRange = CDOIDRangeImpl.read(in);
      newPackage.setMetaIDRange(newRange);
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
}
