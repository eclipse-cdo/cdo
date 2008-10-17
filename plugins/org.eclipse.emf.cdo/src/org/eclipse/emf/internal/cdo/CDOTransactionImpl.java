/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.CDOViewResourcesEvent;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements InternalCDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  /**
   * TODO Optimize by storing an array. See {@link Notifier}.
   */
  private List<CDOTransactionHandler> handlers = new ArrayList<CDOTransactionHandler>(0);

  private CDOSavepointImpl lastSavepoint = new CDOSavepointImpl(this, null);

  private CDOSavepointImpl firstSavepoint = lastSavepoint;

  private boolean dirty;

  private boolean conflict;

  private long commitTimeout;

  private long lastCommitTime = CDORevision.UNSPECIFIED_DATE;

  private int lastTemporaryID;

  private CDOTransactionStrategy transactionStrategy;

  public CDOTransactionImpl(int id, CDOSessionImpl session)
  {
    super(session, id);
    commitTimeout = OM.PREF_DEFAULT_COMMIT_TIMEOUT.getValue();
  }

  @Override
  public Type getViewType()
  {
    return Type.TRANSACTION;
  }

  public void addHandler(CDOTransactionHandler handler)
  {
    synchronized (handlers)
    {
      if (!handlers.contains(handler))
      {
        handlers.add(handler);
      }
    }
  }

  public void removeHandler(CDOTransactionHandler handler)
  {
    synchronized (handlers)
    {
      handlers.remove(handler);
    }
  }

  public CDOTransactionHandler[] getHandlers()
  {
    synchronized (handlers)
    {
      return handlers.toArray(new CDOTransactionHandler[handlers.size()]);
    }
  }

  @Override
  public boolean isDirty()
  {
    checkOpen();
    return dirty;
  }

  @Override
  public boolean hasConflict()
  {
    checkOpen();
    return conflict;
  }

  public void setConflict(InternalCDOObject object)
  {
    ConflictEvent event = new ConflictEvent(object, !conflict);
    conflict = true;
    fireEvent(event);
  }

  public long getCommitTimeout()
  {
    return commitTimeout;
  }

  public void setCommitTimeout(long timeout)
  {
    commitTimeout = timeout;
  }

  /**
   * @since 2.0
   */
  public long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public CDOIDTemp getNextTemporaryID()
  {
    return CDOIDUtil.createTempObject(++lastTemporaryID);
  }

  public CDOResource createResource(String path)
  {
    checkOpen();
    URI createURI = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().createResource(createURI);
  }

  public CDOResource getOrCreateResource(String path)
  {
    checkOpen();

    try
    {
      CDOID id = getResourceID(path);
      if (id != null && !id.isNull())
      {
        return (CDOResource)getObject(id);
      }
    }
    catch (Exception expected)
    {
      TRACER.trace(expected);
    }

    return createResource(path);
  }

  /**
   * @since 2.0
   */

  public void attach(CDOResourceImpl cdoResource)
  {
    try
    {
      CDOStateMachine.INSTANCE.attach(cdoResource, this);
      fireEvent(new ResourcesEvent(cdoResource.getPath(), ResourcesEvent.Kind.ADDED));
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);

      try
      {
        ((InternalCDOObject)cdoResource).cdoInternalSetState(CDOState.NEW);
        getResourceSet().getResources().remove(cdoResource);
      }
      catch (RuntimeException ignore)
      {
      }

      throw ex;
    }
  }

  /**
   * @since 2.0
   */

  public void detach(CDOResourceImpl cdoResource)
  {
    CDOStateMachine.INSTANCE.detach(cdoResource);
    fireEvent(new ResourcesEvent(cdoResource.getPath(), ResourcesEvent.Kind.REMOVED));
  }

  /**
   * @since 2.0
   */
  public CDOSavepointImpl getLastSavepoint()
  {
    checkOpen();
    return lastSavepoint;
  }

  /**
   * @since 2.0
   */
  public CDOTransactionStrategy getTransactionStrategy()
  {
    if (transactionStrategy == null)
    {
      transactionStrategy = CDOTransactionStrategy.DEFAULT;
      transactionStrategy.setTarget(this);
    }

    return transactionStrategy;
  }

  /**
   * @since 2.0
   */
  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy)
  {
    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.unsetTarget(this);
    }

    this.transactionStrategy = transactionStrategy;

    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.setTarget(this);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOID getResourceID(String path)
  {
    CDOID id = super.getResourceID(path);
    return isDetached(id) ? CDOID.NULL : id;
  }

  private boolean isDetached(CDOID id)
  {
    return lastSavepoint.getSharedDetachedObjects().contains(id);
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkOpen();

    if (id == null || id.isNull())
    {
      return null;
    }

    if (isDetached(id))
    {
      FSMUtil.validate(id, null);
    }

    return super.getObject(id, loadOnDemand);
  }

  /**
   * @since 2.0
   */
  public CDOCommitContext createCommitContext()
  {
    return new CDOCommitContextImpl();
  }

  public void commit() throws TransactionException
  {
    checkOpen();

    try
    {
      getTransactionStrategy().commit(this);
    }
    catch (TransactionException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransactionException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public void rollback()
  {
    checkOpen();
    rollback(firstSavepoint);
    cleanUp();
  }

  private void removeObjects(Collection<? extends CDOObject> objects)
  {
    if (!objects.isEmpty())
    {
      for (CDOObject object : objects)
      {
        ((InternalCDOObject)object).cdoInternalSetState(CDOState.TRANSIENT);
        removeObject(object.cdoID());

        if (object instanceof CDOResource)
        {
          getResourceSet().getResources().remove(object);
        }

        ((InternalCDOObject)object).cdoInternalSetID(null);
        ((InternalCDOObject)object).cdoInternalSetRevision(null);
        ((InternalCDOObject)object).cdoInternalSetView(null);
      }
    }
  }

  private Set<CDOID> rollbackCompletely(CDOSavepoint savepoint)
  {
    Set<CDOID> idsOfNewObjectWithDeltas = new HashSet<CDOID>();

    // Start from the last savepoint and come back up to the active
    for (CDOSavepointImpl itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint
        .getPreviousSavepoint())
    {
      // Rollback new objects created after the save point
      removeObjects(itrSavepoint.getNewResources().values());
      removeObjects(itrSavepoint.getNewObjects().values());

      Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavepoint.getRevisionDeltas();
      if (!revisionDeltas.isEmpty())
      {
        for (CDORevisionDelta dirtyObject : revisionDeltas.values())
        {
          if (dirtyObject.getID().isTemporary())
          {
            idsOfNewObjectWithDeltas.add(dirtyObject.getID());
          }
        }
      }

      // Rollback every persisted objects
      Map<CDOID, CDOObject> detachedObjects = itrSavepoint.getDetachedObjects();
      if (!detachedObjects.isEmpty())
      {
        for (Entry<CDOID, CDOObject> entryDirty : detachedObjects.entrySet())
        {
          if (entryDirty.getKey().isTemporary())
          {
            idsOfNewObjectWithDeltas.add(entryDirty.getKey());
          }
          else
          {
            InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirty.getValue();
            cleanObject(internalDirtyObject, getRevision(entryDirty.getKey(), true));
            registerObject(internalDirtyObject);
          }
        }
      }

      for (Entry<CDOID, CDOObject> entryDirtyObject : itrSavepoint.getDirtyObjects().entrySet())
      {
        // Rollback every persisted objects
        if (!entryDirtyObject.getKey().isTemporary())
        {
          InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();
          // cleanObject(internalDirtyObject, getRevision(entryDirtyObject.getKey(), true));
          CDOStateMachine.INSTANCE.rollback(internalDirtyObject);
        }
      }

      if (savepoint == itrSavepoint)
      {
        break;
      }
    }

    return idsOfNewObjectWithDeltas;
  }

  private void loadSavepoint(CDOSavepoint savepoint, Set<CDOID> idsOfNewObjectWithDeltas)
  {
    lastSavepoint.recalculateSharedDetachedObjects();

    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();
    Map<CDOID, CDOResource> newResources = getNewResources();
    Map<CDOID, CDORevision> newBaseRevision = getBaseNewObjects();
    Map<CDOID, CDOObject> detachedObjects = getDetachedObjects();

    // Reload the objects (NEW) with their base.
    for (CDOID id : idsOfNewObjectWithDeltas)
    {
      if (detachedObjects.containsKey(id))
      {
        continue;
      }

      InternalCDOObject object = (InternalCDOObject)newObjMaps.get(id);
      if (object == null)
      {
        object = (InternalCDOObject)newResources.get(id);
      }

      CDORevision revision = newBaseRevision.get(id);
      if (revision != null)
      {
        object.cdoInternalSetRevision(CDORevisionUtil.copy(revision));
        object.cdoInternalSetView(this);
        object.cdoInternalSetID(revision.getID());
        object.cdoInternalSetState(CDOState.NEW);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    // We need to register back new objects that are not removed anymore there.
    for (Entry<CDOID, CDOObject> entryNewObject : newObjMaps.entrySet())
    {
      InternalCDOObject object = (InternalCDOObject)entryNewObject.getValue();
      if (!isObjectRegistered(entryNewObject.getKey()))
      {
        registerObject(object);
      }

      // Go back to the previous state
      cleanObject(object, (InternalCDORevision)object.cdoRevision());
      object.cdoInternalSetState(CDOState.NEW);
    }

    for (Entry<CDOID, CDOObject> entryDirtyObject : dirtyObjects.entrySet())
    {
      if (detachedObjects.containsKey(entryDirtyObject.getKey()))
      {
        continue;
      }

      // Rollback every persisted objects
      InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();
      cleanObject(internalDirtyObject, getRevision(entryDirtyObject.getKey(), true));
    }

    for (CDOSavepointImpl itrSavepoint = firstSavepoint; itrSavepoint != savepoint; itrSavepoint = itrSavepoint
        .getNextSavepoint())
    {
      CDOObjectMerger merger = new CDOObjectMerger();
      for (CDORevisionDelta delta : itrSavepoint.getRevisionDeltas().values())
      {
        if (delta.getID().isTemporary() && !idsOfNewObjectWithDeltas.contains(delta.getID())
            || detachedObjects.containsKey(delta.getID()))
        {
          continue;
        }

        Map<CDOID, CDOObject> map = delta.getID().isTemporary() ? newObjMaps : dirtyObjects;
        InternalCDOObject object = (InternalCDOObject)map.get(delta.getID());
        if (object == null)
        {
          object = (InternalCDOObject)newResources.get(delta.getID());
        }

        // Change state of the objects
        merger.merge(object, delta);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    dirty = ((CDOSavepointImpl)savepoint).isDirty();
  }

  /**
   * @since 2.0
   */
  public void detachObject(InternalCDOObject object)
  {
    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.detachingObject(this, object);
    }

    // deregister object
    deregisterObject(object);

    if (object.cdoState() == CDOState.NEW)
    {
      Map<CDOID, ? extends CDOObject> map = object instanceof CDOResource ? getLastSavepoint().getNewResources()
          : getLastSavepoint().getNewObjects();

      // Determine if we added object
      if (map.containsKey(object.cdoID()))
      {
        map.remove(object.cdoID());
      }
      else
      {
        getLastSavepoint().getDetachedObjects().put(object.cdoID(), object);
      }
    }
    else
    {
      getLastSavepoint().getDetachedObjects().put(object.cdoID(), object);
    }

    if (!dirty)
    {
      dirty = true;
      fireEvent(new StartedEvent());
    }
  }

  /**
   * @since 2.0
   */
  public void rollback(CDOSavepoint savepoint)
  {
    checkOpen();
    getTransactionStrategy().rollback(this, savepoint);
  }

  /**
   * @since 2.0
   */
  public void handleRollback(CDOSavepoint savepoint)
  {
    if (savepoint == null)
    {
      throw new IllegalArgumentException("Save point is null");
    }

    if (savepoint.getUserTransaction() != this)
    {
      throw new IllegalArgumentException("Save point to rollback doesn't belong to this transaction: " + savepoint);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("handleRollback()");
    }

    try
    {
      if (!savepoint.isValid())
      {
        throw new IllegalArgumentException("Savepoint isn't valid : " + savepoint);
      }

      // Rollback objects
      Set<CDOID> idsOfNewObjectWithDeltas = rollbackCompletely(savepoint);

      lastSavepoint = (CDOSavepointImpl)savepoint;
      // Make savepoint active. Erase savepoint that could have be after
      lastSavepoint.setNextSavepoint(null);
      lastSavepoint.clear();

      // Load from first savepoint up to current savepoint
      loadSavepoint(lastSavepoint, idsOfNewObjectWithDeltas);

      Map<CDOIDTemp, CDOID> idMappings = Collections.emptyMap();
      fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.ROLLED_BACK, idMappings));
      for (CDOTransactionHandler handler : getHandlers())
      {
        try
        {
          handler.rolledBackTransaction(this);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransactionException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public CDOSavepoint handleSetSavepoint()
  {
    // Take a copy of all new objects for the current save point
    addToBase(lastSavepoint.getNewObjects());
    addToBase(lastSavepoint.getNewResources());

    lastSavepoint = new CDOSavepointImpl(this, lastSavepoint);
    return lastSavepoint;
  }

  /**
   * @since 2.0
   */
  public CDOSavepoint setSavepoint()
  {
    checkOpen();
    return getTransactionStrategy().setSavepoint(this);
  }

  private void addToBase(Map<CDOID, ? extends CDOObject> objects)
  {
    for (CDOObject object : objects.values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavepoint.getBaseNewObjects().put(object.cdoID(), CDORevisionUtil.copy(object.cdoRevision()));
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOTransaction({0})", getViewID());
  }

  public void registerNew(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering new object {0}", object);
    }

    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.attachingObject(this, object);
    }

    if (object instanceof CDOResourceImpl)
    {
      registerNew(lastSavepoint.getNewResources(), object);
    }
    else
    {
      registerNew(lastSavepoint.getNewObjects(), object);
    }
  }

  /**
   * Receives notification for new and dirty objects
   */
  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    boolean needToSaveFeatureDelta = true;
    if (object.cdoState() == CDOState.NEW)
    {
      // Register Delta for new objects only if objectA doesn't belong to this savepoint
      if (getLastSavepoint().getPreviousSavepoint() == null || featureDelta == null)
      {
        needToSaveFeatureDelta = false;
      }
      else
      {
        Map<CDOID, ? extends CDOObject> map = object instanceof CDOResource ? getLastSavepoint().getNewResources()
            : getLastSavepoint().getNewObjects();
        needToSaveFeatureDelta = !map.containsKey(object.cdoID());
      }
    }

    if (needToSaveFeatureDelta)
    {
      CDORevisionDelta revisionDelta = lastSavepoint.getRevisionDeltas().get(object.cdoID());
      if (revisionDelta == null)
      {
        revisionDelta = CDORevisionDeltaUtil.create(object.cdoRevision());
        lastSavepoint.getRevisionDeltas().put(object.cdoID(), revisionDelta);
      }

      ((InternalCDORevisionDelta)revisionDelta).addFeatureDelta(featureDelta);
    }

    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.modifyingObject(this, object, featureDelta);
    }
  }

  public void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    lastSavepoint.getRevisionDeltas().putIfAbsent(revisionDelta.getID(), revisionDelta);
  }

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    if (featureDelta != null)
    {
      registerFeatureDelta(object, featureDelta);
    }

    registerNew(lastSavepoint.getDirtyObjects(), object);
  }

  /**
   * TODO Simon: Should this method go to CDOSavePointImpl?
   */
  @SuppressWarnings("unchecked")
  private void registerNew(Map map, InternalCDOObject object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new ImplementationError("Duplicate ID: " + object);
    }

    if (!dirty)
    {
      dirty = true;
      fireEvent(new StartedEvent());
    }
  }

  @SuppressWarnings("unchecked")
  private List<CDOPackage> analyzeNewPackages()
  {
    CDOSessionPackageManagerImpl packageManager = getSession().getPackageManager();
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    Set<EPackage> usedNewPackages = new HashSet<EPackage>();
    for (CDOObject object : getNewObjects().values())
    {
      EPackage ePackage = object.eClass().getEPackage();
      if (usedPackages.add(ePackage))
      {
        EPackage topLevelPackage = ModelUtil.getTopLevelPackage(ePackage);
        if (ePackage == topLevelPackage || usedPackages.add(topLevelPackage))
        {
          CDOPackage cdoPackage = ModelUtil.getCDOPackage(topLevelPackage, packageManager);
          if (!cdoPackage.isPersistent() && !cdoPackage.isSystem())
          {
            usedNewPackages.add(topLevelPackage);
          }
        }
      }
    }

    if (usedNewPackages.size() > 0)
    {
      return analyzeNewPackages(usedNewPackages, packageManager);
    }

    return Collections.EMPTY_LIST;
  }

  private static List<CDOPackage> analyzeNewPackages(Collection<EPackage> usedTopLevelPackages,
      CDOSessionPackageManagerImpl packageManager)
  {
    // Determine which of the corresdonding CDOPackages are new
    List<CDOPackage> newPackages = new ArrayList<CDOPackage>();

    IPackageClosure closure = new CompletePackageClosure();
    usedTopLevelPackages = closure.calculate(usedTopLevelPackages);

    for (EPackage usedPackage : usedTopLevelPackages)
    {
      CDOPackage cdoPackage = ModelUtil.getCDOPackage(usedPackage, packageManager);
      if (cdoPackage == null)
      {
        throw new IllegalStateException("Missing CDO package: " + usedPackage.getNsURI());
      }

      if (!(cdoPackage.isPersistent() || cdoPackage.isSystem()))
      {
        newPackages.add(cdoPackage);
      }
    }

    return newPackages;
  }

  private void cleanUp()
  {
    lastSavepoint = firstSavepoint;
    firstSavepoint.clear();
    firstSavepoint.setNextSavepoint(null);
    firstSavepoint.getSharedDetachedObjects().clear();
    dirty = false;
    conflict = false;
    lastTemporaryID = 0;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    checkOpen();
    return lastSavepoint.getAllDirtyObjects();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    checkOpen();
    return lastSavepoint.getAllNewObjects();
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    checkOpen();
    return lastSavepoint.getAllNewResources();
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    checkOpen();
    return lastSavepoint.getAllBaseNewObjects();
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    checkOpen();
    return lastSavepoint.getAllRevisionDeltas();
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    checkOpen();
    return lastSavepoint.getAllDetachedObjects();
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed");
    }
  }

  /**
   * @author Simon McDuff
   */
  private class CDOCommitContextImpl implements CDOCommitContext
  {
    private Map<CDOID, CDOResource> newResources;

    private Map<CDOID, CDOObject> newObjects;

    private Map<CDOID, CDOObject> dirtyObjects;

    private Map<CDOID, CDORevisionDelta> revisionDeltas;

    private Set<CDOID> detachedObjects;

    private List<CDOPackage> newPackages;

    public CDOCommitContextImpl()
    {
      CDOTransactionImpl transaction = getTransaction();
      newResources = transaction.getNewResources();
      newObjects = transaction.getNewObjects();
      dirtyObjects = transaction.getDirtyObjects();
      detachedObjects = transaction.getDetachedObjects().keySet();
      revisionDeltas = transaction.getRevisionDeltas();
      newPackages = transaction.analyzeNewPackages();
    }

    public Map<CDOID, CDOObject> getDirtyObjects()
    {
      return dirtyObjects;
    }

    public Map<CDOID, CDOObject> getNewObjects()
    {
      return newObjects;
    }

    public List<CDOPackage> getNewPackages()
    {
      return newPackages;
    }

    public Map<CDOID, CDOResource> getNewResources()
    {
      return newResources;
    }

    public Set<CDOID> getDetachedObjects()
    {
      return detachedObjects;
    }

    public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
    {
      return revisionDeltas;
    }

    public CDOTransactionImpl getTransaction()
    {
      return CDOTransactionImpl.this;
    }

    public void preCommit()
    {
      if (getTransaction().isDirty())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("commit()");
        }

        for (CDOTransactionHandler handler : getTransaction().getHandlers())
        {
          handler.committingTransaction(getTransaction());
        }

        try
        {
          preCommit(getNewResources());
          preCommit(getNewObjects());
          preCommit(getDirtyObjects());
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new TransactionException(ex);
        }
      }
    }

    public void postCommit(CommitTransactionResult result)
    {
      if (getTransaction().isDirty())
      {
        try
        {
          Collection<CDORevisionDelta> deltas = getRevisionDeltas().values();

          postCommit(getNewResources(), result);
          postCommit(getNewObjects(), result);
          postCommit(getDirtyObjects(), result);

          CDOSessionImpl session = getTransaction().getSession();

          for (CDOPackage newPackage : newPackages)
          {
            ((InternalCDOPackage)newPackage).setPersistent(true);
          }

          ChangeSubscriptionManager changeSubscriptionManager = getTransaction().getChangeSubscriptionManager();
          changeSubscriptionManager.handleNewObjects(getNewObjects().values());
          changeSubscriptionManager.handleNewObjects(getNewResources().values());
          changeSubscriptionManager.handleDetachedObjects(getDetachedObjects());

          long timeStamp = result.getTimeStamp();

          Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
          Set<CDOIDAndVersion> dirtyIDs = new HashSet<CDOIDAndVersion>();
          for (CDOObject dirtyObject : dirtyObjects.values())
          {
            CDORevision revision = dirtyObject.cdoRevision();
            CDOIDAndVersion dirtyID = CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion() - 1);
            dirtyIDs.add(dirtyID);
          }

          if (!dirtyIDs.isEmpty() || !getDetachedObjects().isEmpty())
          {
            Set<CDOID> detachedIDs = new HashSet<CDOID>(getDetachedObjects());
            Collection<CDORevisionDelta> deltasCopy = new ArrayList<CDORevisionDelta>(deltas);
            session.handleCommitNotification(timeStamp, dirtyIDs, detachedIDs, deltasCopy, getTransaction());
          }

          getTransaction().cleanUp();
          lastCommitTime = timeStamp;
          Map<CDOIDTemp, CDOID> idMappings = result.getIDMappings();
          getTransaction().fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.COMMITTED, idMappings));
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new TransactionException(ex);
        }
      }
    }

    @SuppressWarnings("unchecked")
    private void preCommit(Map objects)
    {
      if (!objects.isEmpty())
      {
        for (Object object : objects.values())
        {
          ((InternalCDOObject)object).cdoInternalPreCommit();
        }
      }
    }

    @SuppressWarnings("unchecked")
    private void postCommit(Map objects, CommitTransactionResult result)
    {
      if (!objects.isEmpty())
      {
        for (Object object : objects.values())
        {
          CDOStateMachine.INSTANCE.commit((InternalCDOObject)object, result);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StartedEvent extends Event implements CDOTransactionStartedEvent
  {
    private static final long serialVersionUID = 1L;

    private StartedEvent()
    {
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionStartedEvent[source={0}]", getSource());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FinishedEvent extends Event implements CDOTransactionFinishedEvent
  {
    private static final long serialVersionUID = 1L;

    private Type type;

    private Map<CDOIDTemp, CDOID> idMappings;

    private FinishedEvent(Type type, Map<CDOIDTemp, CDOID> idMappings)
    {
      this.type = type;
      this.idMappings = idMappings;
    }

    public Type getType()
    {
      return type;
    }

    public Map<CDOIDTemp, CDOID> getIDMappings()
    {
      return idMappings;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionFinishedEvent[source={0}, type={1}, idMappings={2}]", getSource(),
          getType(), idMappings == null ? 0 : idMappings.size());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConflictEvent extends Event implements CDOTransactionConflictEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOObject conflictingObject;

    private boolean firstConflict;

    public ConflictEvent(InternalCDOObject conflictingObject, boolean firstConflict)
    {
      this.conflictingObject = conflictingObject;
      this.firstConflict = firstConflict;
    }

    public InternalCDOObject getConflictingObject()
    {
      return conflictingObject;
    }

    public boolean isFirstConflict()
    {
      return firstConflict;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionConflictEvent[source={0}, conflictingObject={1}, firstConflict={2}]",
          getSource(), getConflictingObject(), isFirstConflict());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ResourcesEvent extends Event implements CDOViewResourcesEvent
  {
    private static final long serialVersionUID = 1L;

    private String resourcePath;

    private Kind kind;

    public ResourcesEvent(String resourcePath, Kind kind)
    {
      this.resourcePath = resourcePath;
      this.kind = kind;
    }

    public String getResourcePath()
    {
      return resourcePath;
    }

    public Kind getKind()
    {
      return kind;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewResourcesEvent[source={0}, {1}={2}]", getSource(), resourcePath, kind);
    }
  }
}
