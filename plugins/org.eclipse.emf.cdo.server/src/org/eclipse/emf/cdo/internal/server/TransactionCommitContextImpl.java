/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class TransactionCommitContextImpl implements IStoreWriter.CommitContext, Transaction.InternalCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION,
      TransactionCommitContextImpl.class);

  private TransactionPackageManager packageManager;

  private IStoreWriter storeWriter;

  private long timeStamp;

  private CDOPackage[] newPackages;

  private CDORevision[] newObjects;

  private CDORevision[] dirtyObjects;

  private CDOID[] detachedObjects;

  private List<InternalCDORevision> detachedRevisions = new ArrayList<InternalCDORevision>();;

  private CDORevisionDelta[] dirtyObjectDeltas;

  private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

  private ConcurrentMap<CDOIDTemp, CDOID> idMappings = new ConcurrentHashMap<CDOIDTemp, CDOID>();

  private String rollbackMessage;

  private Transaction transaction;

  public TransactionCommitContextImpl(Transaction transaction)
  {
    this.transaction = transaction;
    packageManager = new TransactionPackageManager();
  }

  public int getTransactionID()
  {
    return transaction.getViewID();
  }

  public Transaction getTransaction()
  {
    return transaction;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public TransactionPackageManager getPackageManager()
  {
    return packageManager;
  }

  public CDOPackage[] getNewPackages()
  {
    return newPackages;
  }

  public CDORevision[] getNewObjects()
  {
    return newObjects;
  }

  public CDORevision[] getDirtyObjects()
  {
    return dirtyObjects;
  }

  public CDOID[] getDetachedObjects()
  {
    return detachedObjects;
  }

  public CDORevisionDelta[] getDirtyObjectDeltas()
  {
    return dirtyObjectDeltas;
  }

  public List<CDOIDMetaRange> getMetaIDRanges()
  {
    return Collections.unmodifiableList(metaIDRanges);
  }

  public Map<CDOIDTemp, CDOID> getIDMappings()
  {
    return Collections.unmodifiableMap(idMappings);
  }

  public void addIDMapping(CDOIDTemp oldID, CDOID newID)
  {
    if (newID == null || newID.isNull() || newID.isTemporary())
    {
      throw new IllegalStateException("newID=" + newID);
    }

    CDOID previousMapping = idMappings.putIfAbsent(oldID, newID);
    if (previousMapping != null)
    {
      throw new IllegalStateException("previousMapping != null");
    }
  }

  public void applyIDMappings()
  {
    applyIDMappings(newObjects);
    applyIDMappings(dirtyObjects);
    for (CDORevisionDelta dirtyObjectDelta : dirtyObjectDeltas)
    {
      ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMappings);
    }
  }

  public String getRollbackMessage()
  {
    return rollbackMessage;
  }

  public void preCommit()
  {
    // Allocate a store writer
    storeWriter = transaction.getRepository().getStore().getWriter(transaction);

    // Make the store writer available in a ThreadLocal variable
    StoreThreadLocal.setStoreReader(storeWriter);
  }

  public void setNewPackages(CDOPackage[] newPackages)
  {
    this.newPackages = newPackages;
  }

  public void setNewObjects(CDORevision[] newObjects)
  {
    this.newObjects = newObjects;
  }

  public void setDirtyObjectDeltas(CDORevisionDelta[] dirtyObjectDeltas)
  {
    this.dirtyObjectDeltas = dirtyObjectDeltas;
  }

  public void setDetachedObjects(CDOID[] detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }

  public void commit()
  {
    try
    {
      storeWriter.commit();
      updateInfraStructure();
    }
    catch (RuntimeException ex)
    {
      handleException(ex);
    }
    catch (Error ex)
    {
      handleException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public void write()
  {
    timeStamp = createTimeStamp();
    dirtyObjects = new CDORevision[dirtyObjectDeltas.length];

    try
    {
      adjustMetaRanges();
      adjustTimeStamps();
      computeDirtyObjects(!transaction.getRepository().isSupportingRevisionDeltas());
      detachObjects();
      storeWriter.write(this);
    }
    catch (RuntimeException ex)
    {
      handleException(ex);
    }
    catch (Error ex)
    {
      handleException(ex);
    }
  }

  private void handleException(Throwable ex)
  {
    OM.LOG.error(ex);
    String storeClass = transaction.getRepository().getStore().getClass().getSimpleName();
    rollbackMessage = "Rollback in " + storeClass + ": " + ex.getMessage();
    rollback();
  }

  protected long createTimeStamp()
  {
    return System.currentTimeMillis();
  }

  public void postCommit(boolean success)
  {
    try
    {
      if (success)
      {
        transaction.getRepository().getNotificationManager().notifyCommit(transaction.getSession(), this);
      }
    }
    finally
    {
      StoreThreadLocal.release();
      storeWriter = null;
      timeStamp = 0L;
      packageManager.clear();
      metaIDRanges.clear();
      idMappings.clear();
      rollbackMessage = null;
      newPackages = null;
      newObjects = null;
      dirtyObjectDeltas = null;
      dirtyObjects = null;
    }
  }

  private void adjustTimeStamps()
  {
    for (CDORevision newObject : newObjects)
    {
      InternalCDORevision revision = (InternalCDORevision)newObject;
      revision.setCreated(timeStamp);
    }
  }

  private void adjustMetaRanges()
  {
    for (CDOPackage newPackage : newPackages)
    {
      if (newPackage.getParentURI() == null)
      {
        adjustMetaRange(newPackage);
      }
    }
  }

  private void adjustMetaRange(CDOPackage newPackage)
  {
    CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()");
    }

    CDOIDMetaRange newRange = transaction.getRepository().getMetaIDRange(oldRange.size());
    ((InternalCDOPackage)newPackage).setMetaIDRange(newRange);
    for (int l = 0; l < oldRange.size(); l++)
    {
      CDOIDTemp oldID = (CDOIDTemp)oldRange.get(l);
      CDOID newID = newRange.get(l);
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping meta ID: {0} --> {1}", oldID, newID);
      }

      idMappings.put(oldID, newID);
    }

    metaIDRanges.add(newRange);
  }

  private void computeDirtyObjects(boolean failOnNull)
  {
    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      dirtyObjects[i] = computeDirtyObject(dirtyObjectDeltas[i], failOnNull);
      if (dirtyObjects[i] == null && failOnNull)
      {
        throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]);
      }
    }
  }

  private CDORevision computeDirtyObject(CDORevisionDelta dirtyObjectDelta, boolean loadOnDemand)
  {
    CDOID id = dirtyObjectDelta.getID();
    int version = dirtyObjectDelta.getOriginVersion();

    CDORevisionResolver revisionResolver = transaction.getRepository().getRevisionManager();
    CDORevision originObject = revisionResolver.getRevisionByVersion(id, CDORevision.UNCHUNKED, version, loadOnDemand);
    if (originObject != null)
    {
      InternalCDORevision dirtyObject = (InternalCDORevision)CDORevisionUtil.copy(originObject);
      dirtyObjectDelta.apply(dirtyObject);
      dirtyObject.setCreated(timeStamp);
      // dirtyObject.setVersion(originObject.getVersion() + 1);
      return dirtyObject;
    }

    return null;
  }

  private void applyIDMappings(CDORevision[] revisions)
  {
    for (CDORevision revision : revisions)
    {
      if (revision != null)
      {
        InternalCDORevision internal = (InternalCDORevision)revision;
        CDOID newID = idMappings.get(internal.getID());
        if (newID != null)
        {
          internal.setID(newID);
        }

        internal.adjustReferences(idMappings);
      }
    }
  }

  protected void rollback()
  {
    if (storeWriter != null)
    {
      try
      {
        storeWriter.rollback();
      }
      catch (RuntimeException ex)
      {
        OM.LOG.warn("Problem while rolling back  the transaction", ex);
      }
    }
  }

  private void updateInfraStructure()
  {
    try
    {
      addNewPackages();
      addRevisions(newObjects);
      addRevisions(dirtyObjects);
      revisedDetachObjects();
    }
    catch (RuntimeException ex)
    {
      // TODO Rethink this case
      OM.LOG.error("FATAL: Memory infrastructure corrupted after successful commit operation of the store");
    }
  }

  private void addNewPackages()
  {
    PackageManager packageManager = transaction.getRepository().getPackageManager();
    for (int i = 0; i < newPackages.length; i++)
    {
      CDOPackage cdoPackage = newPackages[i];
      packageManager.addPackage(cdoPackage);
    }
  }

  private void addRevisions(CDORevision[] revisions)
  {
    RevisionManager revisionManager = transaction.getRepository().getRevisionManager();
    for (CDORevision revision : revisions)
    {
      if (revision != null)
      {
        revisionManager.addCachedRevision((InternalCDORevision)revision);
      }
    }
  }

  private void revisedDetachObjects()
  {
    for (InternalCDORevision revision : detachedRevisions)
    {
      revision.setRevised(getTimeStamp() - 1);
    }
  }

  private void detachObjects()
  {
    detachedRevisions.clear();

    RevisionManager revisionManager = transaction.getRepository().getRevisionManager();

    for (CDOID id : getDetachedObjects())
    {
      InternalCDORevision revision = revisionManager.getRevision(id, CDORevision.UNCHUNKED, false);
      if (revision != null)
      {
        detachedRevisions.add(revision);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class TransactionPackageManager implements CDOPackageManager
  {
    private List<CDOPackage> newPackages = new ArrayList<CDOPackage>();

    private PackageManager repositoryPackageManager = transaction.getRepository().getPackageManager();

    public TransactionPackageManager()
    {
    }

    public void addPackage(CDOPackage cdoPackage)
    {
      newPackages.add(cdoPackage);
    }

    public void clear()
    {
      newPackages.clear();
    }

    public CDOIDObjectFactory getCDOIDObjectFactory()
    {
      return repositoryPackageManager.getCDOIDObjectFactory();
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

      return repositoryPackageManager.lookupPackage(uri);
    }

    public CDOCorePackage getCDOCorePackage()
    {
      return repositoryPackageManager.getCDOCorePackage();
    }

    public CDOResourcePackage getCDOResourcePackage()
    {
      return repositoryPackageManager.getCDOResourcePackage();
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
