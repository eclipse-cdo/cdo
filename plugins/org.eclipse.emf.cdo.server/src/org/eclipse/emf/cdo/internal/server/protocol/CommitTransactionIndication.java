/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.StoreUtil;
import org.eclipse.emf.cdo.internal.server.View;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.protocol.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.internal.util.transaction.Transaction;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.transaction.ITransaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
public class CommitTransactionIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionIndication.class);

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CommitTransactionIndication.class);

  private CDOPackageImpl[] newPackages;

  private CDORevisionImpl[] newResources;

  private CDORevisionImpl[] newObjects;

  private CDORevisionImpl[] dirtyObjects;

  private Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();

  private long timeStamp;

  private PackageManager sessionPackageManager;

  private CDOPackageManager transactionPackageManager;

  private View view;

  public CommitTransactionIndication()
  {
    super(CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    timeStamp = System.currentTimeMillis();
    sessionPackageManager = getPackageManager();
    transactionPackageManager = new TransactionPackageManager();

    int viewID = in.readInt();
    view = getSession().getView(viewID);
    if (view == null || view.getViewType() != IView.Type.TRANSACTION)
    {
      throw new IllegalStateException("Illegal view: " + view);
    }

    IStore store = getStore();
    IStoreWriter storeWriter = store.getWriter(view);

    try
    {
      StoreUtil.setReader(storeWriter);
      newPackages = readNewPackages(in);
      newResources = readNewResources(in);
      newObjects = readNewObjects(in);
      dirtyObjects = readDirtyObjects(in);

      ITransaction<IStoreWriter> storeTransaction = new Transaction<IStoreWriter>(storeWriter);
      addPackages(storeTransaction, newPackages);
      addRevisions(storeTransaction, newResources);
      addRevisions(storeTransaction, newObjects);
      addRevisions(storeTransaction, dirtyObjects);
      storeTransaction.commit();
    }
    finally
    {
      storeWriter.release();
      StoreUtil.setReader(null);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(timeStamp);
    for (CDOPackageImpl newPackage : newPackages)
    {
      CDOIDRangeImpl.write(out, newPackage.getMetaIDRange());
    }

    writeIDMappings(out);
    if (dirtyObjects.length > 0)
    {
      getSessionManager().notifyInvalidation(timeStamp, dirtyObjects, getSession());
    }
  }

  private CDOPackageImpl[] readNewPackages(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new packages", size);
    }

    Repository repository = getRepository();
    CDOPackageImpl[] newPackages = new CDOPackageImpl[size];
    for (int i = 0; i < size; i++)
    {
      newPackages[i] = new CDOPackageImpl(transactionPackageManager, in);
      CDOIDRange oldRange = newPackages[i].getMetaIDRange();
      if (oldRange != null && oldRange.isTemporary())
      {
        CDOIDRange newRange = repository.getMetaIDRange(oldRange.getCount());
        newPackages[i].setMetaIDRange(newRange);

        long count = oldRange.getCount();
        for (long l = 0; l < count; l++)
        {
          CDOID oldID = oldRange.get(l);
          CDOID newID = newRange.get(l);

          if (TRACER.isEnabled())
          {
            TRACER.format("Mapping ID: {0} --> {1}", oldID, newID);
          }

          idMappings.put(oldID, newID);
        }
      }
    }

    return newPackages;
  }

  private CDORevisionImpl[] readNewResources(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new resources", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readNewObjects(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new objects", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readDirtyObjects(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} dirty objects", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readRevisions(ExtendedDataInputStream in, int size) throws IOException
  {
    CDORevisionImpl[] revisions = new CDORevisionImpl[size];
    for (int i = 0; i < size; i++)
    {
      revisions[i] = new CDORevisionImpl(in, transactionPackageManager);
      mapTemporaryID(revisions[i]);
    }

    return revisions;
  }

  private void addPackages(ITransaction<IStoreWriter> storeTransaction, CDOPackageImpl[] newPackages)
  {
    sessionPackageManager.addPackages(storeTransaction, newPackages);
  }

  private void addRevisions(ITransaction<IStoreWriter> storeTransaction, CDORevisionImpl[] revisions)
  {
    RevisionManager revisionManager = getRevisionManager();
    for (CDORevisionImpl revision : revisions)
    {
      revision.setCreated(timeStamp);
      revision.adjustReferences(idMappings);
      revisionManager.addRevision(storeTransaction, revision);
    }
  }

  private void mapTemporaryID(CDORevisionImpl revision)
  {
    CDOID oldID = revision.getID();
    if (oldID.isTemporary())
    {
      CDOID newID = getRepository().getNextCDOID();
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping ID: {0} --> {1}", oldID, newID);
      }

      idMappings.put(oldID, newID);
      revision.setID(newID);
    }
  }

  private void writeIDMappings(ExtendedDataOutputStream out) throws IOException
  {
    for (Entry<CDOID, CDOID> entry : idMappings.entrySet())
    {
      CDOID oldID = entry.getKey();
      if (!oldID.isMeta())
      {
        CDOID newID = entry.getValue();
        CDOIDImpl.write(out, oldID);
        CDOIDImpl.write(out, newID);
      }
    }

    CDOIDImpl.write(out, CDOID.NULL);
  }

  /**
   * @author Eike Stepper
   */
  private final class TransactionPackageManager implements CDOPackageManager
  {
    public TransactionPackageManager()
    {
    }

    public CDOPackageImpl lookupPackage(String uri)
    {
      for (CDOPackageImpl cdoPackage : newPackages)
      {
        if (ObjectUtil.equals(cdoPackage.getPackageURI(), uri))
        {
          return cdoPackage;
        }
      }

      return sessionPackageManager.lookupPackage(uri);
    }

    public CDOCorePackage getCDOCorePackage()
    {
      throw new UnsupportedOperationException();
    }

    public CDOResourcePackage getCDOResourcePackage()
    {
      throw new UnsupportedOperationException();
    }

    public int getPackageCount()
    {
      throw new UnsupportedOperationException();
    }

    public CDOPackage[] getPackages()
    {
      throw new UnsupportedOperationException();
    }

    public CDOPackage[] getElements()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isEmpty()
    {
      throw new UnsupportedOperationException();
    }

    public void addListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }

    public void removeListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }
  }
}
