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
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=233314
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=215688    
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavePoint;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.InternalCDOPackage;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.protocol.id.CDOIDUtil;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.event.Notifier;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MultiMap;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements CDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  /**
   * TODO Optimize by storing an array. See {@link Notifier}.
   */
  private List<CDOTransactionHandler> handlers = new ArrayList<CDOTransactionHandler>(0);

  private List<CDOPackage> newPackages;

  private CDOSavePointImpl lastSavePoint = new CDOSavePointImpl(this, null);

  private CDOSavePointImpl firstSavePoint = lastSavePoint;

  private boolean dirty;

  private boolean conflict;

  private long commitTimeout;

  private int lastTemporaryID;

  public CDOTransactionImpl(int id, CDOSessionImpl session)
  {
    super(id, session);
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
      handlers.add(handler);
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
    return dirty;
  }

  @Override
  public boolean hasConflict()
  {
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

  public List<CDOPackage> getNewPackages()
  {
    return Collections.unmodifiableList(newPackages);
  }

  public CDOIDTemp getNextTemporaryID()
  {
    return CDOIDUtil.createCDOIDTempObject(++lastTemporaryID);
  }

  public CDOResource createResource(String path)
  {
    URI createURI = CDOUtil.createResourceURI(path);
    return (CDOResource)getResourceSet().createResource(createURI);
  }

  public CDOResource getOrCreateResource(String path)
  {
    CDOID id = getResourceID(path);
    if (id == null || id.isNull())
    {
      return createResource(path);
    }
    else
    {
      return addResource(id, path);
    }
  }

  public CDOSavePointImpl getLastSavePoint()
  {
    return this.lastSavePoint;
  }

  public void commit() throws TransactionException
  {
    if (dirty)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("commit()");
      }

      for (CDOTransactionHandler handler : getHandlers())
      {
        handler.committingTransaction(this);
      }

      try
      {
        newPackages = analyzeNewPackages();

        for (CDOSavePointImpl itrSavePoint = lastSavePoint; itrSavePoint != null; itrSavePoint = itrSavePoint
            .getPreviousSavePoint())
        {
          preCommit(itrSavePoint.getNewObjects());
          preCommit(itrSavePoint.getDirtyObjects());
        }

        CDOSessionImpl session = getSession();
        IChannel channel = session.getChannel();
        IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
        CommitTransactionRequest request = new CommitTransactionRequest(channel, this);

        CommitTransactionResult result = failOverStrategy.send(request, commitTimeout);
        String rollbackMessage = result.getRollbackMessage();
        if (rollbackMessage != null)
        {
          throw new TransactionException(rollbackMessage);
        }

        for (CDOSavePointImpl irtSavePoint = lastSavePoint; irtSavePoint != null; irtSavePoint = irtSavePoint
            .getPreviousSavePoint())
        {
          postCommit(irtSavePoint.getNewResources(), result);
          postCommit(irtSavePoint.getNewObjects(), result);
          postCommit(irtSavePoint.getDirtyObjects(), result);
        }
        for (CDOPackage newPackage : newPackages)
        {
          ((InternalCDOPackage)newPackage).setPersistent(true);
        }

        Map<CDOID, CDOObject> dirtyObjects = this.getDirtyObjects();

        if (!dirtyObjects.isEmpty())
        {
          session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), this);
        }

        cleanUp();
        Map<CDOIDTemp, CDOID> idMappings = result.getIDMappings();
        fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.COMMITTED, idMappings));
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

  public void rollback(boolean remote)
  {
    rollback(firstSavePoint, remote);
  
    cleanUp();
  }

  private Set<CDOID> rollbackTo(CDOSavePoint savePoint,  boolean remote)
  {
    Set<CDOID> newObjectsDelta = new HashSet<CDOID>(); 
    
    boolean isActiveSavePoint = false;

    // Start from the last savepoint and come back up to the active

    for (CDOSavePointImpl itrSavePoint = lastSavePoint; itrSavePoint != null; itrSavePoint = itrSavePoint
        .getPreviousSavePoint())
    {

      if (isActiveSavePoint == false)
      {
        // Rollback new objects created after the save point
        Map<CDOID, CDOResource> newResources = itrSavePoint.getNewResources();
        Map<CDOID, CDOObject> newObjects = itrSavePoint.getNewObjects();

        if (!newResources.isEmpty())
        {
          for (CDOObject newResource : newResources.values())
          {
            // TODO Should call detach transition : not there yet
            ((InternalCDOObject)newResource).cdoInternalSetState(CDOState.TRANSIENT);

            removeObject(newResource.cdoID());
            getResourceSet().getResources().remove(newResource);
          }
        }

        if (!newObjects.isEmpty())
        {
          for (CDOObject newObject : newObjects.values())
          {
            ((InternalCDOObject)newObject).cdoInternalSetState(CDOState.TRANSIENT);
            
            removeObject(newObject.cdoID());

            // TODO Should call detach transition : not there yet
            // TODO How to remove it from Resource?            
            // CDOStateMachine.INSTANCE.detach(newObject);
          }
        }

        Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavePoint.getRevisionDeltas();

        if (!revisionDeltas.isEmpty())
        {
          for (CDORevisionDelta dirtyObject : revisionDeltas.values())
          {
            if (dirtyObject.getID().isTemporary()) newObjectsDelta.add(dirtyObject.getID());
          }
        }
      }

      // Rollback every persisted objects
      Map<CDOID, CDOObject> dirtyObjects = itrSavePoint.getDirtyObjects();
      if (!dirtyObjects.isEmpty())
      {
        for (CDOObject dirtyObject : dirtyObjects.values())
        {
          CDOStateMachine.INSTANCE.rollback((InternalCDOObject)dirtyObject, remote);
        }
      }
      if (savePoint == itrSavePoint) isActiveSavePoint = true;
    }
    
    return newObjectsDelta;
  }
  
  private void loadSavePoint(CDOSavePoint savePoint, Set<CDOID> newObjectsDelta)
  {
    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();

    Map<CDOID, CDOResource> newResources = getNewResources();

    Map<CDOID, CDORevisionImpl> newBaseRevision = getBaseNewObjects();
    
    // Reload the objects (NEW) with their base.
    for (CDOID newObject : newObjectsDelta)
    {
      InternalCDOObject object = (InternalCDOObject)newObjMaps.get(newObject);
      if (object == null) object = (InternalCDOObject)newResources.get(newObject);

      CDORevisionImpl revisionImpl = newBaseRevision.get(newObject);

      if (revisionImpl != null)
      {
        object.cdoInternalSetRevision(new CDORevisionImpl(revisionImpl));

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    for (CDOSavePointImpl itrSavePoint = firstSavePoint; itrSavePoint != savePoint; itrSavePoint = itrSavePoint
        .getNextSavePoint())
    {
      CDOObjectMerger merger = new CDOObjectMerger();
      for (CDORevisionDelta delta : itrSavePoint.getRevisionDeltas().values())
      {
        Map<CDOID, CDOObject> map = delta.getID().isTemporary() ? newObjMaps : dirtyObjects;

        InternalCDOObject object = (InternalCDOObject)map.get(delta.getID());
        if (object == null) object = (InternalCDOObject)newResources.get(delta.getID());

        // Change state of the objects
        merger.merge(object, delta);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }
    dirty = ((CDOSavePointImpl)savePoint).isDirty();
  }
  
  public void rollback(CDOSavePoint savePoint, boolean remote)
  {
    if (savePoint == null || savePoint.getTransaction() != this)
      throw new IllegalArgumentException("Save point to rollback doesn't belong to this transaction: " + savePoint);

    if (TRACER.isEnabled())
    {
      TRACER.trace("commit()");
    }

    try
    {

      boolean isPresent = false;

      for (CDOSavePointImpl indexsavePoint = lastSavePoint; indexsavePoint != null; indexsavePoint = indexsavePoint
          .getPreviousSavePoint())
      {
        if (indexsavePoint == savePoint)
        {
          isPresent = true;
          break;
        }
      }
      if (isPresent == false)
        throw new IllegalArgumentException("Save point to rollback isn't valid for this transaction: " + savePoint);

      // Rollback objects
      Set<CDOID> newObjectsDelta = rollbackTo(savePoint, remote);

      this.lastSavePoint = (CDOSavePointImpl)savePoint;

      // Make savePoint active. Erase savepoint that could have be after
      this.lastSavePoint.setNextSavePoint(null);
      this.lastSavePoint.clear();

      // Load from first savepoint up to current savepoint
      loadSavePoint(lastSavePoint, newObjectsDelta);

      Map<CDOIDTemp, CDOID> idMappings = Collections.emptyMap();

      fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.ROLLED_BACK, idMappings));

      for (CDOTransactionHandler handler : getHandlers())
      {
        handler.rollingbackTransaction(this);
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

  public CDOSavePoint createSavePoint()
  {
    // Take a copy of all new objects for the current save point
    for (CDOObject object : lastSavePoint.getNewObjects().values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavePoint.getBaseNewObjects().put(object.cdoID(), new CDORevisionImpl((CDORevisionImpl)object.cdoRevision()));
    }
    // Take a copy of all new resources for the current save point
    for (CDOObject object : lastSavePoint.getNewResources().values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavePoint.getBaseNewObjects().put(object.cdoID(), new CDORevisionImpl((CDORevisionImpl)object.cdoRevision()));
    }

    lastSavePoint = new CDOSavePointImpl(this, lastSavePoint);
    
    return lastSavePoint;
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
      handler.addingObject(this, object);
    }

    if (object instanceof CDOResourceImpl)
    {
      register(this.lastSavePoint.getNewResources(), object);
    }
    else
    {
      register(this.lastSavePoint.getNewObjects(), object);
    }
  }

  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    CDORevisionDelta revisionDelta = (CDORevisionDelta)lastSavePoint.getRevisionDeltas().get(object.cdoID());

    if (revisionDelta == null)
    {
      revisionDelta = (CDORevisionDelta)CDORevisionDeltaUtil.create(object.cdoRevision());
      lastSavePoint.getRevisionDeltas().put(object.cdoID(), revisionDelta);
    }

    ((InternalCDORevisionDelta)revisionDelta).addFeatureDelta(featureDelta);
    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.modifyingObject(this, object, featureDelta);
    }
  }

  public void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    lastSavePoint.getRevisionDeltas().putIfAbsent(revisionDelta.getID(), revisionDelta);
  }

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    registerFeatureDelta(object, featureDelta);
    register(lastSavePoint.getDirtyObjects(), object);
  }

  @SuppressWarnings("unchecked")
  private void register(Map map, InternalCDOObject object)
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

  private List<CDOPackage> analyzeNewPackages()
  {
    // Find all used classes and their super classes
    Set<EClass> usedClasses = new HashSet<EClass>();
    for (CDOObject object : getNewObjects().values())
    {
      EClass usedClass = object.eClass();
      if (usedClasses.add(usedClass))
      {
        for (EClass superType : usedClass.getEAllSuperTypes())
        {
          usedClasses.add(superType);
        }
      }
    }

    return analyzeNewPackages(usedClasses);
  }

  private List<CDOPackage> analyzeNewPackages(Collection<EClass> eClasses)
  {
    // Calculate the top level packages of the used classes
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    for (EClass eClass : eClasses)
    {
      EPackage topLevelPackage = ModelUtil.getTopLevelPackage(eClass.getEPackage());
      usedPackages.add(topLevelPackage);
    }

    // Determine which of the used packages are new
    CDOSessionPackageManagerImpl packageManager = getSession().getPackageManager();
    List<CDOPackage> newPackages = new ArrayList<CDOPackage>();
    for (EPackage usedPackage : usedPackages)
    {
      CDOPackage cdoPackage = ModelUtil.getCDOPackage(usedPackage, packageManager);
      if (cdoPackage == null)
      {
        throw new IllegalStateException("Missing CDO package: " + usedPackage.getNsURI());
      }

      if (!cdoPackage.isPersistent() && !cdoPackage.isSystem())
      {
        newPackages.add(cdoPackage);
        CDOPackage[] subPackages = cdoPackage.getSubPackages(true);
        for (CDOPackage subPackage : subPackages)
        {
          newPackages.add(subPackage);
        }
      }
    }

    return newPackages;
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

  private void cleanUp()
  {
    newPackages = null;
    lastSavePoint = firstSavePoint;
    firstSavePoint.clear();
    firstSavePoint.setNextSavePoint(null);
    dirty = false;
    conflict = false;
    lastTemporaryID = 0;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    if (this.lastSavePoint.getPreviousSavePoint() == null) return lastSavePoint.getDirtyObjects();

    MultiMap.ListBased<CDOID, CDOObject> dirtyObjects = new MultiMap.ListBased<CDOID, CDOObject>();

    for (CDOSavePointImpl savePoint = lastSavePoint; savePoint != null; savePoint = savePoint.getPreviousSavePoint())
    {
      dirtyObjects.getDelegates().add(savePoint.getDirtyObjects());
    }
    return dirtyObjects;
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    if (this.lastSavePoint.getPreviousSavePoint() == null)
      return Collections.unmodifiableMap(lastSavePoint.getNewObjects());

    MultiMap.ListBased<CDOID, CDOObject> newObjects = new MultiMap.ListBased<CDOID, CDOObject>();
    for (CDOSavePointImpl savePoint = lastSavePoint; savePoint != null; savePoint = savePoint.getPreviousSavePoint())
    {
      newObjects.getDelegates().add(savePoint.getNewObjects());
    }
    return newObjects;
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    if (this.lastSavePoint.getPreviousSavePoint() == null)
      return Collections.unmodifiableMap(lastSavePoint.getNewResources());

    MultiMap.ListBased<CDOID, CDOResource> newResources = new MultiMap.ListBased<CDOID, CDOResource>();
    for (CDOSavePointImpl savePoint = lastSavePoint; savePoint != null; savePoint = savePoint.getPreviousSavePoint())
    {
      newResources.getDelegates().add(savePoint.getNewResources());
    }
    return newResources;
  }

  public Map<CDOID, CDORevisionImpl> getBaseNewObjects()
  {
    if (this.lastSavePoint.getPreviousSavePoint() == null)
      return Collections.unmodifiableMap(lastSavePoint.getBaseNewObjects());

    MultiMap.ListBased<CDOID, CDORevisionImpl> newObjects = new MultiMap.ListBased<CDOID, CDORevisionImpl>();
    for (CDOSavePointImpl savePoint = lastSavePoint; savePoint != null; savePoint = savePoint.getPreviousSavePoint())
    {
      newObjects.getDelegates().add(savePoint.getBaseNewObjects());
    }
    return newObjects;
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    if (this.lastSavePoint.getPreviousSavePoint() == null) return lastSavePoint.getRevisionDeltas();

    // We need to combined the result for all delta in different SavePoint
    Map<CDOID, CDORevisionDelta> revisionDeltas = new ConcurrentHashMap<CDOID, CDORevisionDelta>();

    for (CDOSavePointImpl savePoint = firstSavePoint; savePoint != null; savePoint = savePoint.getNextSavePoint())
    {
      for (Entry<CDOID, CDORevisionDelta> entry : savePoint.getRevisionDeltas().entrySet())
      {
        // Skipping temporary
        if (entry.getKey().isTemporary()) continue;

        CDORevisionDeltaImpl revisionDelta = (CDORevisionDeltaImpl)revisionDeltas.get(entry.getKey());
        if (revisionDelta == null)
          revisionDeltas.put(entry.getKey(), entry.getValue());
        else
        {
          for (CDOFeatureDelta delta : entry.getValue().getFeatureDeltas())
          {
            if (delta instanceof CDOListFeatureDelta)
            {
              for (CDOFeatureDelta subDelta : ((CDOListFeatureDelta)delta).getListChanges())
                revisionDelta.addFeatureDelta(subDelta);
            }
            else
              revisionDelta.addFeatureDelta(delta);
          }
        }
      }
    }
    return revisionDeltas;
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
}
