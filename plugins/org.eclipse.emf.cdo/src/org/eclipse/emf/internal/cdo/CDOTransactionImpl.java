/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/201266
 *    Simon McDuff - https://bugs.eclipse.org/233314
 *    Simon McDuff - https://bugs.eclipse.org/215688    
 *    Simon McDuff - https://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MultiMap;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.om.trace.ContextTracer;
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

  private CDOSavepointImpl lastSavepoint = new CDOSavepointImpl(this, null);

  private CDOSavepointImpl firstSavepoint = lastSavepoint;

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
    return CDOIDUtil.createTempObject(++lastTemporaryID);
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

  /**
   * @since 2.0
   */
  public CDOSavepointImpl getLastSavepoint()
  {
    return this.lastSavepoint;
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

        Collection<CDORevisionDelta> deltas = getRevisionDeltas().values();

        for (CDOSavepointImpl itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint
            .getPreviousSavepoint())
        {
          preCommit(itrSavepoint.getNewObjects());
          preCommit(itrSavepoint.getDirtyObjects());
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

        for (CDOSavepointImpl irtSavepoint = lastSavepoint; irtSavepoint != null; irtSavepoint = irtSavepoint
            .getPreviousSavepoint())
        {
          postCommit(irtSavepoint.getNewResources(), result);
          postCommit(irtSavepoint.getNewObjects(), result);
          postCommit(irtSavepoint.getDirtyObjects(), result);
        }

        for (CDOPackage newPackage : newPackages)
        {
          ((InternalCDOPackage)newPackage).setPersistent(true);
        }

        changeSubscriptionManager.notifyDirtyObjects();

        Map<CDOID, CDOObject> dirtyObjects = this.getDirtyObjects();

        if (!dirtyObjects.isEmpty())
        {
          Set<CDOIDAndVersion> dirtyIDs = new HashSet<CDOIDAndVersion>();
          for (CDOObject dirtyObject : dirtyObjects.values())
          {
            CDORevision revision = dirtyObject.cdoRevision();
            CDOIDAndVersion dirtyID = CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion());
            dirtyIDs.add(dirtyID);
          }

          session.handleCommitNotification(result.getTimeStamp(), dirtyIDs, deltas, this);
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
    rollback(firstSavepoint, remote);

    cleanUp();
  }

  /**
   * @since 2.0
   */
  public void rollback()
  {
    rollback(true);
  }

  /**
   * @since 2.0
   */
  public void rollback(CDOSavepoint savepoint)
  {
    rollback(savepoint, true);
  }

  private void removeObjects(Collection<? extends CDOObject> objects)
  {

    if (!objects.isEmpty())
    {
      for (CDOObject object : objects)
      {
        ((InternalCDOObject)object).cdoInternalSetState(CDOState.TRANSIENT);

        removeObject(object.cdoID());

        if (object instanceof CDOResource) getResourceSet().getResources().remove(object);

        // TODO Should call detach transition : not there yet
        // TODO How to remove it from Resource?
        // CDOStateMachine.INSTANCE.detach(newObject);

      }
    }

  }

  private Set<CDOID> rollbackCompletely(CDOSavepoint savepoint, boolean remote)
  {
    Set<CDOID> idsOfNewObjectWithDeltas = new HashSet<CDOID>();

    // TODO Add comments - what is it used for.
    boolean isSavepointActive = false;

    // Start from the last savepoint and come back up to the active

    for (CDOSavepointImpl itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint
        .getPreviousSavepoint())
    {

      if (isSavepointActive == false)
      {
        // Rollback new objects created after the save point
        removeObjects(itrSavepoint.getNewResources().values());
        removeObjects(itrSavepoint.getNewObjects().values());

        Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavepoint.getRevisionDeltas();

        if (!revisionDeltas.isEmpty())
        {
          for (CDORevisionDelta dirtyObject : revisionDeltas.values())
          {
            if (dirtyObject.getID().isTemporary()) idsOfNewObjectWithDeltas.add(dirtyObject.getID());
          }
        }
      }

      // Rollback every persisted objects
      Map<CDOID, CDOObject> dirtyObjects = itrSavepoint.getDirtyObjects();
      if (!dirtyObjects.isEmpty())
      {
        for (CDOObject dirtyObject : dirtyObjects.values())
        {
          CDOStateMachine.INSTANCE.rollback((InternalCDOObject)dirtyObject, remote);
        }
      }

      if (savepoint == itrSavepoint) isSavepointActive = true;
    }

    return idsOfNewObjectWithDeltas;
  }

  private void loadSavepoint(CDOSavepoint savepoint, Set<CDOID> idsOfNewObjectWithDeltas)
  {
    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();

    Map<CDOID, CDOResource> newResources = getNewResources();

    Map<CDOID, CDORevision> newBaseRevision = getBaseNewObjects();

    // Reload the objects (NEW) with their base.
    for (CDOID id : idsOfNewObjectWithDeltas)
    {
      InternalCDOObject object = (InternalCDOObject)newObjMaps.get(id);
      if (object == null) object = (InternalCDOObject)newResources.get(id);

      CDORevision revision = newBaseRevision.get(id);

      if (revision != null)
      {
        object.cdoInternalSetRevision(CDORevisionUtil.copy(revision));

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    for (CDOSavepointImpl itrSavepoint = firstSavepoint; itrSavepoint != savepoint; itrSavepoint = itrSavepoint
        .getNextSavepoint())
    {
      CDOObjectMerger merger = new CDOObjectMerger();
      for (CDORevisionDelta delta : itrSavepoint.getRevisionDeltas().values())
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
    dirty = ((CDOSavepointImpl)savepoint).isDirty();
  }

  /**
   * @since 2.0
   */
  public void rollback(CDOSavepoint savepoint, boolean remote)
  {
    if (savepoint == null) throw new IllegalArgumentException("Save point is null");

    if (savepoint.getTransaction() != this)
      throw new IllegalArgumentException("Save point to rollback doesn't belong to this transaction: " + savepoint);

    if (TRACER.isEnabled())
    {
      TRACER.trace("commit()");
    }

    try
    {

      if (!savepoint.isValid()) throw new IllegalArgumentException("Savepoint isn't valid : " + savepoint);

      // Rollback objects
      Set<CDOID> idsOfNewObjectWithDeltas = rollbackCompletely(savepoint, remote);

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
  public CDOSavepoint setSavepoint()
  {
    // Take a copy of all new objects for the current save point
    addToBase(lastSavepoint.getNewObjects());
    addToBase(lastSavepoint.getNewResources());

    lastSavepoint = new CDOSavepointImpl(this, lastSavepoint);

    return lastSavepoint;
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
      handler.addingObject(this, object);
    }

    if (object instanceof CDOResourceImpl)
    {
      register(this.lastSavepoint.getNewResources(), object);
    }
    else
    {
      register(this.lastSavepoint.getNewObjects(), object);
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
      if (this.getLastSavepoint().getPreviousSavepoint() == null || featureDelta == null)
      {
        needToSaveFeatureDelta = false;
      }
      else
      {
        Map<CDOID, ? extends CDOObject> map = object instanceof CDOResource ? this.getLastSavepoint().getNewResources()
            : this.getLastSavepoint().getNewObjects();

        needToSaveFeatureDelta = !map.containsKey(object.cdoID());
      }
    }

    if (needToSaveFeatureDelta)
    {
      CDORevisionDelta revisionDelta = (CDORevisionDelta)lastSavepoint.getRevisionDeltas().get(object.cdoID());

      if (revisionDelta == null)
      {
        revisionDelta = (CDORevisionDelta)CDORevisionDeltaUtil.create(object.cdoRevision());
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

    registerFeatureDelta(object, featureDelta);
    register(lastSavepoint.getDirtyObjects(), object);
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
    lastSavepoint = firstSavepoint;
    firstSavepoint.clear();
    firstSavepoint.setNextSavepoint(null);
    dirty = false;
    conflict = false;
    lastTemporaryID = 0;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    if (this.lastSavepoint.getPreviousSavepoint() == null) return lastSavepoint.getDirtyObjects();

    MultiMap.ListBased<CDOID, CDOObject> dirtyObjects = new MultiMap.ListBased<CDOID, CDOObject>();

    for (CDOSavepointImpl savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      dirtyObjects.getDelegates().add(savepoint.getDirtyObjects());
    }
    return dirtyObjects;
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    if (this.lastSavepoint.getPreviousSavepoint() == null)
      return Collections.unmodifiableMap(lastSavepoint.getNewObjects());

    MultiMap.ListBased<CDOID, CDOObject> newObjects = new MultiMap.ListBased<CDOID, CDOObject>();
    for (CDOSavepointImpl savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      newObjects.getDelegates().add(savepoint.getNewObjects());
    }
    return newObjects;
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    if (this.lastSavepoint.getPreviousSavepoint() == null)
      return Collections.unmodifiableMap(lastSavepoint.getNewResources());

    MultiMap.ListBased<CDOID, CDOResource> newResources = new MultiMap.ListBased<CDOID, CDOResource>();
    for (CDOSavepointImpl savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      newResources.getDelegates().add(savepoint.getNewResources());
    }
    return newResources;
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    if (this.lastSavepoint.getPreviousSavepoint() == null)
      return Collections.unmodifiableMap(lastSavepoint.getBaseNewObjects());

    MultiMap.ListBased<CDOID, CDORevision> newObjects = new MultiMap.ListBased<CDOID, CDORevision>();
    for (CDOSavepointImpl savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      newObjects.getDelegates().add(savepoint.getBaseNewObjects());
    }
    return newObjects;
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    if (this.lastSavepoint.getPreviousSavepoint() == null) return lastSavepoint.getRevisionDeltas();

    // We need to combined the result for all delta in different Savepoint
    Map<CDOID, CDORevisionDelta> revisionDeltas = new ConcurrentHashMap<CDOID, CDORevisionDelta>();

    for (CDOSavepointImpl savepoint = firstSavepoint; savepoint != null; savepoint = savepoint.getNextSavepoint())
    {
      for (Entry<CDOID, CDORevisionDelta> entry : savepoint.getRevisionDeltas().entrySet())
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
