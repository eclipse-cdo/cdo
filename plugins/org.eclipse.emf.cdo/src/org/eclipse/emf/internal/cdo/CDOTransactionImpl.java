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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.event.Notifier;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.transaction.TransactionException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements CDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  private static final long INITIAL_TEMPORARY_ID = -2L;

  private transient long nextTemporaryID = INITIAL_TEMPORARY_ID;

  /**
   * TODO Optimize by storing an array. See {@link Notifier}.
   */
  private List<CDOTransactionHandler> handlers = new ArrayList<CDOTransactionHandler>(0);

  private List<CDOPackage> newPackages;

  private Map<CDOID, CDOResource> newResources = new HashMap<CDOID, CDOResource>();

  private Map<CDOID, CDOObject> newObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDOObject> dirtyObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDORevisionDelta> revisionDeltas = new HashMap<CDOID, CDORevisionDelta>();

  private boolean dirty;

  private boolean conflict;

  private long commitTimeout;

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
    return newPackages;
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    return newResources;
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return newObjects;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return dirtyObjects;
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return revisionDeltas;
  }

  public CDOID getNextTemporaryID()
  {
    long id = nextTemporaryID;
    --nextTemporaryID;
    --nextTemporaryID;
    return CDOIDImpl.create(id);
  }

  public CDOResource createResource(String path)
  {
    URI createURI = CDOUtil.createResourceURI(path);
    return (CDOResource)getResourceSet().createResource(createURI);
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
        CDOSessionImpl session = getSession();
        newPackages = analyzeNewPackages(session);

        preCommit(newObjects);
        preCommit(dirtyObjects);

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
          ((CDOPackageImpl)newPackage).setPersistent(true);
        }

        if (!dirtyObjects.isEmpty())
        {
          session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), this);
        }

        cleanUp();
        Map<CDOID, CDOID> idMappings = result.getIDMappings();
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
      Map<CDOID, CDOID> idMappings = Collections.emptyMap();
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
    CDORevisionDeltaImpl revisionDelta = (CDORevisionDeltaImpl)revisionDeltas.get(object.cdoID());
    if (revisionDelta == null)
    {
      revisionDelta = new CDORevisionDeltaImpl(object.cdoRevision());
      revisionDeltas.put(object.cdoID(), revisionDelta);
    }

    revisionDelta.addFeatureDelta(featureDelta);
    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.modifyingObject(this, object, featureDelta);
    }
  }

  public void registerRevisionDelta(CDORevisionDelta delta)
  {
    CDORevisionDelta changeSet = revisionDeltas.get(delta.getId());
    if (changeSet == null)
    {
      revisionDeltas.put(delta.getId(), changeSet);
    }
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

  private List<CDOPackage> analyzeNewPackages(CDOSessionImpl session)
  {
    // Find all used classes and their super classes
    Set<EClass> usedClasses = new HashSet<EClass>();
    for (CDOObject object : newObjects.values())
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

    // Calculate the packages of the used classes
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    for (EClass usedClass : usedClasses)
    {
      usedPackages.add(usedClass.getEPackage());
    }

    // Determine which of the used packages are new
    CDOSessionPackageManager packageManager = session.getPackageManager();
    List<CDOPackage> newPackages = new ArrayList<CDOPackage>();
    for (EPackage usedPackage : usedPackages)
    {
      CDOPackageImpl cdoPackage = ModelUtil.getCDOPackage(usedPackage, packageManager);
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
    nextTemporaryID = INITIAL_TEMPORARY_ID;
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

    private Map<CDOID, CDOID> idMappings;

    private FinishedEvent(Type type, Map<CDOID, CDOID> idMappings)
    {
      this.type = type;
      this.idMappings = idMappings;
    }

    public Type getType()
    {
      return type;
    }

    public Map<CDOID, CDOID> getIDMappings()
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
