/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.protocol.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Transaction extends View implements ITransaction, IStoreWriter.CommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, Transaction.class);

  private IRepository repository;

  private IPackageManager repositoryPackageManager;

  private TransactionPackageManager packageManager;

  private IStoreWriter storeWriter;

  private long timeStamp;

  private CDOPackage[] newPackages;

  private CDORevision[] newObjects;

  private CDORevision[] dirtyObjects;

  private CDORevisionDelta[] dirtyObjectDeltas;

  private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

  private ConcurrentMap<CDOIDTemp, CDOID> idMappings = new ConcurrentHashMap<CDOIDTemp, CDOID>();

  private String rollbackMessage;

  public Transaction(Session session, int viewID)
  {
    super(session, viewID, Type.TRANSACTION);
    repository = session.getSessionManager().getRepository();
    repositoryPackageManager = repository.getPackageManager();
    packageManager = new TransactionPackageManager();

  }

  public int getTransactionID()
  {
    return getViewID();
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
    storeWriter = repository.getStore().getWriter(this);
    StoreUtil.setReader(storeWriter);
  }

  public void commit(CDOPackage[] newPackages, CDORevision[] newObjects, CDORevisionDelta[] dirtyObjectDeltas)
  {
    timeStamp = System.currentTimeMillis();
    this.newPackages = newPackages;
    this.newObjects = newObjects;
    this.dirtyObjectDeltas = dirtyObjectDeltas;
    dirtyObjects = new CDORevision[dirtyObjectDeltas.length];

    try
    {
      adjustMetaRanges();
      adjustTimeStamps();
      computeDirtyObjects(!repository.isSupportingRevisionDeltas());

      storeWriter.commit(this);
      updateInfraStructure();
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      rollbackMessage = ex.getMessage();
      rollback();
    }
  }

  public void postCommit(boolean success)
  {
    try
    {
      int modifications = dirtyObjectDeltas.length;
      if (success && modifications > 0)
      {
        List<CDOID> dirtyIDs = new ArrayList<CDOID>(modifications);
        for (int i = 0; i < modifications; i++)
        {
          dirtyIDs.add(dirtyObjectDeltas[i].getID());
        }

        SessionManager sessionManager = (SessionManager)repository.getSessionManager();
        sessionManager.notifyInvalidation(timeStamp, dirtyIDs, getSession());
      }
    }
    finally
    {
      // TODO Do this while indcating instead of responding
      if (storeWriter != null)
      {
        StoreUtil.setReader(null);
        storeWriter.release();
        storeWriter = null;
      }

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
      adjustMetaRange(newPackage);
    }
  }

  private void adjustMetaRange(CDOPackage newPackage)
  {
    CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()");
    }

    CDOIDMetaRange newRange = repository.getMetaIDRange(oldRange.size());
    ((CDOPackageImpl)newPackage).setMetaIDRange(newRange);
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
      dirtyObjects[i] = computeDirtyObject(dirtyObjectDeltas[i]);
      if (dirtyObjects[i] == null && failOnNull)
      {
        throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]);
      }
    }
  }

  private CDORevision computeDirtyObject(CDORevisionDelta dirtyObjectDelta)
  {
    CDOID id = dirtyObjectDelta.getID();
    int version = dirtyObjectDelta.getOriginVersion();

    CDORevisionResolver revisionResolver = repository.getRevisionManager();
    CDORevision originObject = revisionResolver.getRevisionByVersion(id, CDORevision.UNCHUNKED, version, false);
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

  private void rollback()
  {
    if (storeWriter != null)
    {
      try
      {
        storeWriter.rollback(this);
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
    }
    catch (RuntimeException ex)
    {
      // TODO Rethink this case
      OM.LOG.error("FATAL: Memory infrastructure corrupted after successful commit operation of the store");
    }
  }

  private void addNewPackages()
  {
    PackageManager packageManager = (PackageManager)repository.getPackageManager();
    for (int i = 0; i < newPackages.length; i++)
    {
      CDOPackage cdoPackage = newPackages[i];
      packageManager.addPackage(cdoPackage);
    }
  }

  private void addRevisions(CDORevision[] revisions)
  {
    RevisionManager revisionManager = (RevisionManager)repository.getRevisionManager();
    for (CDORevision revision : revisions)
    {
      if (revision != null)
      {
        revisionManager.addRevision((InternalCDORevision)revision);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class TransactionPackageManager implements CDOPackageManager
  {

    private List<CDOPackage> newPackages = new ArrayList<CDOPackage>();

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
