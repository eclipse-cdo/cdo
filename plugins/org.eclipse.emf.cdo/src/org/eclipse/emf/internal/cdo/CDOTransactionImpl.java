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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

  private Map<CDOID, CDOResource> newResources = new HashMap<CDOID, CDOResource>();

  private Map<CDOID, CDOObject> newObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDOObject> dirtyObjects = new HashMap<CDOID, CDOObject>();

  private ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = new ConcurrentHashMap<CDOID, CDORevisionDelta>();

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

  public Map<CDOID, CDOResource> getNewResources()
  {
    return Collections.unmodifiableMap(newResources);
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return Collections.unmodifiableMap(newObjects);
  }

  /**
   * TODO Consolidate with {@link #getRevisionDeltas()}
   */
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return Collections.unmodifiableMap(dirtyObjects);
  }

  /**
   * TODO Consolidate with {@link #getDirtyObjects()}
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return Collections.unmodifiableMap(revisionDeltas);
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

  public void commit() throws TransactionException
  {
    if (dirty)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("commit()");
      }

      if (hasConflict())
      {
        throw new TransactionException("This transaction has conflicts");
      }

      for (CDOTransactionHandler handler : getHandlers())
      {
        handler.committingTransaction(this);
      }

      try
      {
        newPackages = analyzeNewPackages();

        preCommit(newObjects);
        preCommit(dirtyObjects);

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

        postCommit(newResources, result);
        postCommit(newObjects, result);
        postCommit(dirtyObjects, result);

        for (CDOPackage newPackage : newPackages)
        {
          ((InternalCDOPackage)newPackage).setPersistent(true);
        }

        if (!dirtyObjects.isEmpty())
        {
          Set<CDOIDAndVersion> dirtyIDs = new HashSet<CDOIDAndVersion>();
          for (CDOObject dirtyObject : dirtyObjects.values())
          {
            CDORevision revision = dirtyObject.cdoRevision();
            CDOIDAndVersion dirtyID = CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion());
            dirtyIDs.add(dirtyID);
          }

          session.notifyInvalidation(result.getTimeStamp(), dirtyIDs, this);
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
    try
    {
      if (!newResources.isEmpty())
      {
        for (CDOObject newResource : newResources.values())
        {
          removeObject(newResource.cdoID());
          getResourceSet().getResources().remove(newResource);
        }
      }

      if (!newObjects.isEmpty())
      {
        for (CDOObject newObject : newObjects.values())
        {
          removeObject(newObject.cdoID());
        }
      }

      if (!dirtyObjects.isEmpty())
      {
        for (CDOObject dirtyObject : dirtyObjects.values())
        {
          CDOStateMachine.INSTANCE.rollback((InternalCDOObject)dirtyObject, remote);
        }
      }

      cleanUp();
      Map<CDOIDTemp, CDOID> idMappings = Collections.emptyMap();
      fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.ROLLED_BACK, idMappings));
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
      register(newResources, object);
    }
    else
    {
      register(newObjects, object);
    }
  }

  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)revisionDeltas.get(object.cdoID());
    if (revisionDelta == null)
    {
      revisionDelta = (InternalCDORevisionDelta)CDORevisionDeltaUtil.create(object.cdoRevision());
      revisionDeltas.put(object.cdoID(), revisionDelta);
    }

    revisionDelta.addFeatureDelta(featureDelta);
    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.modifyingObject(this, object, featureDelta);
    }
  }

  public void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    revisionDeltas.putIfAbsent(revisionDelta.getID(), revisionDelta);
  }

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    registerFeatureDelta(object, featureDelta);
    register(dirtyObjects, object);
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
    Set<EClass> usedClasses = new HashSet<EClass>();
    for (CDOObject object : newObjects.values())
    {
      findAllUsedEClasses(object.eClass(), usedClasses);
    }

    return analyzeNewPackages(usedClasses, getSession().getPackageManager());
  }

  /**
   * Find all used classes, their super classes and their referenced classes
   */
  private static void findAllUsedEClasses(EClass eClass, Set<EClass> foundClasses)
  {
    if (foundClasses.add(eClass))
    {
      for (EClass superType : eClass.getEAllSuperTypes())
      {
        findAllUsedEClasses(superType, foundClasses);
      }

      for (EReference eReference : eClass.getEAllReferences())
      {
        findAllUsedEClasses(eReference.getEReferenceType(), foundClasses);
      }
    }
  }

  private static List<CDOPackage> analyzeNewPackages(Collection<EClass> eClasses,
      CDOSessionPackageManagerImpl packageManager)
  {
    // Calculate the top level EPackages of the used classes
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    for (EClass eClass : eClasses)
    {
      EPackage topLevelPackage = ModelUtil.getTopLevelPackage(eClass.getEPackage());
      usedPackages.add(topLevelPackage);
    }

    IPackageClosure closure = new CompletePackageClosure();
    usedPackages = closure.calculate(usedPackages);

    // Determine which of the corresdonding CDOPackages are new
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
    newResources.clear();
    newObjects.clear();
    dirtyObjects.clear();
    revisionDeltas.clear();
    dirty = false;
    conflict = false;
    lastTemporaryID = 0;
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
