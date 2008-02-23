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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.View;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.protocol.id.CDOIDUtil;
import org.eclipse.emf.cdo.protocol.model.CDOModelUtil;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.protocol.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.internal.util.transaction.Transaction;
import org.eclipse.net4j.util.ImplementationError;
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
public class CommitTransactionIndicationOLD extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionIndication.class);

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CommitTransactionIndication.class);

  private CDOPackage[] newPackages;

  private InternalCDORevision[] newResources;

  private InternalCDORevision[] newObjects;

  private CDORevisionDeltaImpl[] dirtyObjects;

  private CDOID[] dirtyIDs;

  private Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();

  private long timeStamp;

  private PackageManager sessionPackageManager;

  private CDOPackageManager transactionPackageManager;

  private View view;

  private String rollbackMessage;

  public CommitTransactionIndicationOLD()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    timeStamp = System.currentTimeMillis();
    sessionPackageManager = getPackageManager();
    transactionPackageManager = new TransactionPackageManager();

    int viewID = in.readInt();
    view = (View)getSession().getView(viewID);
    if (view == null || view.getViewType() != IView.Type.TRANSACTION)
    {
      throw new IllegalStateException("Illegal view: " + view);
    }

    IStore store = getStore();
    IStoreWriter storeWriter = store.getWriter(view);

    try
    {
      StoreUtil.setReader(storeWriter);
      newPackages = readNewPackages(in, storeWriter);
      newResources = readNewResources(in, storeWriter);
      newObjects = readNewObjects(in, storeWriter);
      dirtyObjects = readDirtyObjects(in, storeWriter);
      ITransaction<IStoreWriter> storeTransaction = new Transaction<IStoreWriter>(storeWriter, false);

      try
      {
        addPackages(storeTransaction, newPackages);
        addRevisions(storeTransaction, newResources);
        addRevisions(storeTransaction, newObjects);
        writeRevisions(storeTransaction, dirtyObjects);
        // addRevisions(storeTransaction, dirtyObjects);

        storeWriter.commit();
        storeTransaction.commit();
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
        rollbackMessage = ex.getLocalizedMessage();
        storeWriter.rollback();
        storeTransaction.rollback();
      }
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
    if (rollbackMessage != null)
    {
      out.writeBoolean(false);
      out.writeString(rollbackMessage);
    }
    else
    {
      out.writeBoolean(true);
      out.writeLong(timeStamp);
      for (CDOPackage newPackage : newPackages)
      {
        CDOIDUtil.writeMetaRange(out, newPackage.getMetaIDRange());
      }

      writeIDMappings(out);
      if (dirtyIDs.length > 0)
      {
        // getSessionManager().notifyInvalidation(timeStamp, dirtyIDs, getSession());
      }
    }
  }

  private CDOPackage[] readNewPackages(ExtendedDataInputStream in, IStoreWriter storeWriter) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new packages", size);
    }

    CDOPackage[] newPackages = new CDOPackage[size];
    for (int i = 0; i < size; i++)
    {
      newPackages[i] = CDOModelUtil.readPackage(transactionPackageManager, in);
      mapMetaRange(newPackages[i]);
    }

    return newPackages;
  }

  private void mapMetaRange(CDOPackage newPackage)
  {
    Repository repository = getRepository();
    CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
    if (oldRange != null && oldRange.isTemporary())
    {
      CDOIDMetaRange newRange = repository.getMetaIDRange(oldRange.size());
      ((CDOPackageImpl)newPackage).setMetaIDRange(newRange);

      for (int l = 0; l < oldRange.size(); l++)
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

  private InternalCDORevision[] readNewResources(ExtendedDataInputStream in, IStoreWriter storeWriter)
      throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new resources", size);
    }

    return readRevisions(in, storeWriter, size);
  }

  private InternalCDORevision[] readNewObjects(ExtendedDataInputStream in, IStoreWriter storeWriter) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new objects", size);
    }

    return readRevisions(in, storeWriter, size);
  }

  private CDORevisionDeltaImpl[] readDirtyObjects(ExtendedDataInputStream in, IStoreWriter storeWriter)
      throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} dirty objects", size);
    }

    RevisionManager revisionManager = sessionPackageManager.getRepository().getRevisionManager();
    CDORevisionDeltaImpl[] deltas = new CDORevisionDeltaImpl[size];
    dirtyIDs = new CDOID[size];
    for (int i = 0; i < size; i++)
    {
      deltas[i] = new CDORevisionDeltaImpl(in, transactionPackageManager);
      dirtyIDs[i] = deltas[i].getID();
    }

    return deltas;
  }

  private InternalCDORevision[] readRevisions(ExtendedDataInputStream in, IStoreWriter storeWriter, int size)
      throws IOException
  {
    RevisionManager revisionManager = sessionPackageManager.getRepository().getRevisionManager();
    InternalCDORevision[] revisions = new InternalCDORevision[size];
    for (int i = 0; i < size; i++)
    {
      revisions[i] = (InternalCDORevision)CDORevisionUtil.read(in, revisionManager, transactionPackageManager);
      mapTemporaryID(revisions[i], storeWriter);
    }

    return revisions;
  }

  // TODO Remove newPackages parameter
  private void addPackages(ITransaction<IStoreWriter> storeTransaction, CDOPackage[] newPackages)
  {
    sessionPackageManager.addPackages(storeTransaction, newPackages);
  }

  private void addRevisions(ITransaction<IStoreWriter> storeTransaction, InternalCDORevision[] revisions)
  {
    RevisionManager revisionManager = getRevisionManager();
    for (InternalCDORevision revision : revisions)
    {
      revision.setCreated(timeStamp);
      revision.adjustReferences(idMappings);
      revisionManager.addRevision(storeTransaction, revision);
    }
  }

  // TODO Rename to addRevisionDeltas
  // TODO Remove deltas parameter
  private void writeRevisions(ITransaction<IStoreWriter> storeTransaction, CDORevisionDeltaImpl[] deltas)
  {
    for (CDORevisionDeltaImpl delta : deltas)
    {
      delta.adjustReferences(idMappings);
      getRevisionManager().addRevisionDelta(storeTransaction, delta);
    }
  }

  private void mapTemporaryID(InternalCDORevision revision, IStoreWriter storeWriter)
  {
    CDOID oldID = revision.getID();
    if (oldID.isTemporary())
    {
      CDOID newID = storeWriter.primeNewObject(revision.getCDOClass());
      if (newID == null || newID.isNull() || newID.isMeta() || newID.isTemporary())
      {
        throw new ImplementationError("Store writer returned bad CDOID " + newID);
      }

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
        CDOIDUtil.write(out, oldID);
        CDOIDUtil.write(out, newID);
      }
    }

    CDOIDUtil.write(out, CDOID.NULL);
  }

  /**
   * @author Eike Stepper
   */
  private final class TransactionPackageManager implements CDOPackageManager
  {
    public TransactionPackageManager()
    {
    }

    public CDOIDObjectFactory getCDOIDObjectFactory()
    {
      return sessionPackageManager.getCDOIDObjectFactory();
    }

    public CDOPackage lookupPackage(String uri)
    {
      for (CDOPackage cdoPackage : newPackages)
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
