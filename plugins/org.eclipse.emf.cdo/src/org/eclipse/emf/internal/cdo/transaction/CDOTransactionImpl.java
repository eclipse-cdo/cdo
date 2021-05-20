/*
 * Copyright (c) 2009-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 *    Andre Dietisheim - bug 256649
 *    Caspar De Groot - bug 290032 (Sticky views)
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.id.CDOIdentifiable;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOOriginSizeProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver2;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver3;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOStaleReferenceCleaner;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction.Options.AutoReleaseLocksEvent.AutoReleaseLocksEnabledEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction.Options.AutoReleaseLocksEvent.AutoReleaseLocksExemptionsEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler3;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.transaction.CDOUndoDetector;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.DanglingIntegrityException;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.util.LocalCommitConflictException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.object.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;
import org.eclipse.emf.internal.cdo.object.CDOObjectWrapper;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
import org.eclipse.emf.internal.cdo.util.CommitIntegrityCheck;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.AbstractCloseableIterator;
import org.eclipse.net4j.util.collection.ByteArrayWrapper;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.ComposedIterator;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.DelegatingCloseableIterator;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.MonitorCanceledException;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.CommitToken;
import org.eclipse.emf.spi.cdo.InternalCDOSession.InvalidationData;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements InternalCDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, CDOTransactionImpl.class);

  private static final boolean X_COMPRESSION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.transaction.X_COMPRESSION");

  private static final Method COPY_OBJECT_METHOD;

  static
  {
    Method cdoInternalCopyTo = null;

    try
    {
      cdoInternalCopyTo = ReflectUtil.getMethod(CDOObjectImpl.class, "cdoInternalCopyTo", CDOObjectImpl.class);
    }
    catch (Throwable ex)
    {
      OM.LOG.error(ex);
    }

    COPY_OBJECT_METHOD = cdoInternalCopyTo;
  }

  private Object transactionHandlersLock = new Object();

  private ConcurrentArray<CDOTransactionHandler1> transactionHandlers1 = new ConcurrentArray<CDOTransactionHandler1>()
  {
    @Override
    protected CDOTransactionHandler1[] newArray(int length)
    {
      return new CDOTransactionHandler1[length];
    }
  };

  private ConcurrentArray<CDOTransactionHandler2> transactionHandlers2 = new ConcurrentArray<CDOTransactionHandler2>()
  {
    @Override
    protected CDOTransactionHandler2[] newArray(int length)
    {
      return new CDOTransactionHandler2[length];
    }
  };

  private InternalCDOSavepoint lastSavepoint = createSavepoint(null);

  private InternalCDOSavepoint firstSavepoint = lastSavepoint;

  private boolean dirty;

  private int conflict;

  private CDOTransactionStrategy transactionStrategy;

  private CDOIDGenerator idGenerator;

  private volatile long lastCommitTime = UNSPECIFIED_DATE;

  private String commitComment;

  private CommitToken commitToken;

  private CDOBranchPoint commitMergeSource;

  // Bug 283985 (Re-attachment)
  private final ThreadLocal<Boolean> providingCDOID = new InheritableThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  /**
   * An optional set to specify which objects in this TX are to be committed by {@link #commit()}
   */
  private Set<? extends EObject> committables;

  /**
   * A map to hold a clean (i.e. unmodified) revision for objects that have been modified or detached.
   */
  private Map<InternalCDOObject, InternalCDORevision> cleanRevisions = new CleanRevisionsMap();

  public CDOTransactionImpl(CDOSession session, CDOBranch branch)
  {
    super(session, branch, UNSPECIFIED_DATE);
  }

  public CDOTransactionImpl(CDOSession session, String durableLockingID)
  {
    super(session, durableLockingID);
  }

  /**
   * @since 2.0
   */
  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  /**
   * @since 2.0
   */
  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  public boolean isReadOnly()
  {
    return false;
  }

  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (branchPoint.getTimeStamp() != UNSPECIFIED_DATE)
        {
          throw new IllegalArgumentException("Changing the target time is not supported by transactions");
        }

        if (isDirty() && !getBranch().equals(branchPoint.getBranch()))
        {
          throw new IllegalStateException("Changing the target branch is impossible while transaction is dirty");
        }

        return super.setBranchPoint(branchPoint);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void addTransactionHandler(CDOTransactionHandlerBase handler)
  {
    synchronized (transactionHandlersLock)
    {
      if (handler instanceof CDOTransactionHandler1)
      {
        transactionHandlers1.add((CDOTransactionHandler1)handler);
      }

      if (handler instanceof CDOTransactionHandler2)
      {
        transactionHandlers2.add((CDOTransactionHandler2)handler);
      }
    }
  }

  @Override
  public void removeTransactionHandler(CDOTransactionHandlerBase handler)
  {
    synchronized (transactionHandlersLock)
    {
      if (handler instanceof CDOTransactionHandler1)
      {
        transactionHandlers1.remove((CDOTransactionHandler1)handler);
      }

      if (handler instanceof CDOTransactionHandler2)
      {
        transactionHandlers2.remove((CDOTransactionHandler2)handler);
      }
    }
  }

  @Override
  public CDOTransactionHandler[] getTransactionHandlers()
  {
    Set<CDOTransactionHandler> result = new HashSet<>();
    synchronized (transactionHandlersLock)
    {
      CDOTransactionHandler1[] handlers1 = transactionHandlers1.get();
      if (handlers1 != null)
      {
        for (CDOTransactionHandler1 handler : handlers1)
        {
          if (handler instanceof CDOTransactionHandler)
          {
            result.add((CDOTransactionHandler)handler);
          }
        }
      }

      CDOTransactionHandler2[] handlers2 = transactionHandlers2.get();
      if (handlers2 != null)
      {
        for (CDOTransactionHandler2 handler : handlers2)
        {
          if (handler instanceof CDOTransactionHandler)
          {
            result.add((CDOTransactionHandler)handler);
          }
        }
      }
    }

    return result.toArray(new CDOTransactionHandler[result.size()]);
  }

  @Override
  public CDOTransactionHandler1[] getTransactionHandlers1()
  {
    synchronized (transactionHandlersLock)
    {
      return transactionHandlers1.get();
    }
  }

  @Override
  public CDOTransactionHandler2[] getTransactionHandlers2()
  {
    synchronized (transactionHandlersLock)
    {
      return transactionHandlers2.get();
    }
  }

  @Override
  public boolean isDirty()
  {
    if (isClosed())
    {
      return false;
    }

    return dirty;
  }

  @Override
  public void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;

      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        IEvent event = dirty ? new StartedEvent() : new FinishedEvent(false);
        fireEvent(event, listeners);
      }
    }
  }

  @Override
  public boolean hasConflict()
  {
    checkActive();
    return conflict > 0;
  }

  @Override
  public void setConflict(InternalCDOObject object)
  {
    IEvent event = null;
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        event = new ConflictEvent(object, conflict == 0);
        ++conflict;
      }
      finally
      {
        unlockView();
      }
    }

    fireEvent(event);
  }

  @Override
  public void removeConflict(InternalCDOObject object)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (conflict > 0)
        {
          --conflict;
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public Set<CDOObject> getConflicts()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        Set<CDOObject> conflicts = new HashSet<>();
        for (CDOObject object : getDirtyObjects().values())
        {
          if (object.cdoConflict())
          {
            conflicts.add(object);
          }
        }

        for (CDOObject object : getDetachedObjects().values())
        {
          if (object.cdoConflict())
          {
            conflicts.add(object);
          }
        }

        return conflicts;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOChangeSetData getChangeSetData()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllChangeSetData();
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOChangeSetData merge(CDOBranch source, CDOMerger merger)
  {
    return merge(source.getHead(), merger);
  }

  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger)
  {
    return merge(source, CDOBranchUtil.AUTO_BRANCH_POINT, merger);
  }

  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger)
  {
    return merge(source, sourceBase, null, merger);
  }

  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOBranchPoint targetBase, CDOMerger merger)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (isDirty())
        {
          throw new IllegalStateException("Merging into dirty transactions not yet supported");
        }

        long now = getLastUpdateTime();
        CDOBranchPoint target = getBranch().getPoint(now);

        if (source.getTimeStamp() == UNSPECIFIED_DATE)
        {
          source = source.getBranch().getPoint(now);
        }

        if (CDOBranchUtil.isContainedBy(source, target))
        {
          throw new IllegalArgumentException("Source is already contained in " + target);
        }

        if (sourceBase != CDOBranchUtil.AUTO_BRANCH_POINT)
        {
          if (sourceBase != null && !CDOBranchUtil.isContainedBy(sourceBase, source))
          {
            throw new IllegalArgumentException("Source base is not contained in " + source);
          }

          if (targetBase != null && !CDOBranchUtil.isContainedBy(targetBase, target))
          {
            throw new IllegalArgumentException("Target base is not contained in " + target);
          }
        }

        InternalCDOSession session = getSession();
        MergeData mergeData = session.getMergeData(target, source, targetBase, sourceBase, true);

        CDOChangeSet targetChanges = mergeData.getTargetChanges();
        CDOChangeSet sourceChanges = mergeData.getSourceChanges();
        CDOChangeSetData result = merger.merge(targetChanges, sourceChanges);
        if (result == null)
        {
          return null;
        }

        CDORevisionProvider targetProvider = mergeData.getTargetInfo();
        CDORevisionProvider resultBaseProvider;

        CDORevisionManager revisionManager = session.getRevisionManager();
        CDOBranchPoint resultBase = mergeData.getResultBase();
        if (resultBase != null)
        {
          resultBaseProvider = new ManagedRevisionProvider(revisionManager, resultBase);
        }
        else
        {
          resultBaseProvider = mergeData.getTargetBaseInfo();
        }

        ApplyChangeSetResult changeSet = applyChangeSet(result, resultBaseProvider, targetProvider, source, false);
        commitMergeSource = source;
        return changeSet.getChangeSetData();
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOChangeSetData remerge(CDOBranchPoint source, CDOMerger merger)
  {
    return merge(source, CDOBranchUtil.AUTO_BRANCH_POINT, merger);
  }

  @Override
  @Deprecated
  public Pair<CDOChangeSetData, Pair<Map<CDOID, CDOID>, List<CDOID>>> applyChangeSetData(CDOChangeSetData changeSetData, CDORevisionProvider targetBaseProvider,
      CDORevisionProvider targetProvider, CDOBranchPoint source)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ApplyChangeSetResult applyChangeSet(CDOChangeSetData changeSetData, CDORevisionProvider resultBaseProvider, CDORevisionProvider targetProvider,
      CDOBranchPoint source, boolean keepVersions) throws ChangeSetOutdatedException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        ApplyChangeSetResult result = new ApplyChangeSetResult();

        // Merges from local offline branches may require additional ID mappings: localID -> tempID
        if (source != null && source.getBranch().isLocal()
            && getSession().getRepositoryInfo().getIDGenerationLocation() == CDOCommonRepository.IDGenerationLocation.STORE)
        {
          applyLocalIDMapping(changeSetData, result);
        }

        CDOChangeSetData resultData = result.getChangeSetData();

        // New objects
        applyNewObjects(changeSetData.getNewObjects(), resultData.getNewObjects());

        // Detached objects
        Set<CDOObject> detachedSet = applyDetachedObjects(changeSetData.getDetachedObjects(), resultData.getDetachedObjects());

        // Changed objects
        Map<CDOID, InternalCDORevision> oldRevisions = applyChangedObjects(changeSetData.getChangedObjects(), resultBaseProvider, targetProvider, keepVersions,
            resultData.getChangedObjects());

        // Delta notifications
        Collection<CDORevisionDelta> notificationDeltas = lastSavepoint.getRevisionDeltas2().values();
        if (!notificationDeltas.isEmpty() || !detachedSet.isEmpty())
        {
          sendDeltaNotifications(notificationDeltas, detachedSet, oldRevisions);
        }

        return result;
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void applyLocalIDMapping(CDOChangeSetData changeSetData, ApplyChangeSetResult result)
  {
    Map<CDOID, CDOID> idMappings = result.getIDMappings();

    // Collect needed ID mappings
    for (CDOIDAndVersion key : changeSetData.getNewObjects())
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      if (revision.getBranch().isLocal())
      {
        CDOID oldID = revision.getID();
        CDOID newID = createIDForNewObject(null);
        idMappings.put(oldID, newID);

        revision.setID(newID);
        revision.setVersion(0);
      }
    }

    if (!idMappings.isEmpty())
    {
      // Apply collected ID mappings
      CDOIDMapper idMapper = new CDOIDMapper(idMappings);
      idMapper.setAllowUnmappedTempIDs(true);

      for (CDOIDAndVersion key : changeSetData.getNewObjects())
      {
        InternalCDORevision revision = (InternalCDORevision)key;
        revision.adjustReferences(idMapper);
      }

      for (CDORevisionKey key : changeSetData.getChangedObjects())
      {
        InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)key;
        if (revisionDelta.adjustReferences(idMapper))
        {
          result.getAdjustedObjects().add(revisionDelta.getID());
        }
      }
    }
  }

  private void applyNewObjects(List<CDOIDAndVersion> newObjects, List<CDOIDAndVersion> result)
  {
    List<InternalCDOObject> objects = new ArrayList<>();

    for (CDOIDAndVersion key : newObjects)
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      CDOID id = revision.getID();
      if (getObjectIfExists(id) == null)
      {
        revision = revision.copy();
        revision.setBranchPoint(getBranchPoint());
        revision.setVersion(0);

        InternalCDOObject object = newInstance(revision.getEClass());
        object.cdoInternalSetView(this);
        object.cdoInternalSetRevision(revision);
        object.cdoInternalSetState(CDOState.NEW);
        objects.add(object);

        registerObject(object);
        result.add(revision);
      }
    }

    if (!objects.isEmpty())
    {
      for (InternalCDOObject object : objects)
      {
        object.cdoInternalPostLoad();
        registerAttached(object, true);
      }

      setDirty(true);
    }
  }

  private Set<CDOObject> applyDetachedObjects(List<CDOIDAndVersion> detachedObjects, List<CDOIDAndVersion> result)
  {
    Set<CDOObject> detachedSet = new HashSet<>();
    for (CDOIDAndVersion key : detachedObjects)
    {
      CDOID id = key.getID();
      InternalCDOObject object = getObjectIfExists(id);
      if (object != null)
      {
        result.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
        CDOStateMachine.INSTANCE.detach(object);
        detachedSet.add(object);
        setDirty(true);
      }
    }

    return detachedSet;
  }

  private Map<CDOID, InternalCDORevision> applyChangedObjects(List<CDORevisionKey> changedObjects, CDORevisionProvider resultBaseProvider,
      CDORevisionProvider targetProvider, boolean keepVersions, List<CDORevisionKey> result) throws ChangeSetOutdatedException
  {
    Map<CDOID, InternalCDORevision> oldRevisions = CDOIDUtil.createMap();

    Map<CDOID, CDOObject> detachedObjects = lastSavepoint.getDetachedObjects();
    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getDirtyObjects();
    Map<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas2();

    for (CDORevisionKey key : changedObjects)
    {
      InternalCDORevisionDelta resultBaseGoalDelta = (InternalCDORevisionDelta)key;
      resultBaseGoalDelta.setTarget(null);
      CDOID id = resultBaseGoalDelta.getID();
      InternalCDORevision resultBaseRevision = (InternalCDORevision)resultBaseProvider.getRevision(id);

      InternalCDOObject object = getObject(id);
      boolean revisionChanged = false;

      InternalCDORevision targetRevision = object.cdoRevision();
      if (targetRevision == null)
      {
        targetRevision = (InternalCDORevision)targetProvider.getRevision(id);
        object.cdoInternalSetRevision(targetRevision);
        revisionChanged = true;
      }

      oldRevisions.put(id, targetRevision);

      InternalCDORevision goalRevision = resultBaseRevision.copy();
      goalRevision.setBranchPoint(this);
      if (!keepVersions)
      {
        goalRevision.setVersion(targetRevision.getVersion());
      }

      goalRevision.setRevised(UNSPECIFIED_DATE);
      resultBaseGoalDelta.applyTo(goalRevision);

      InternalCDORevisionDelta targetGoalDelta = goalRevision.compare(targetRevision);
      targetGoalDelta.setTarget(null);

      if (!targetGoalDelta.isEmpty())
      {
        if (keepVersions && targetGoalDelta.getVersion() != resultBaseRevision.getVersion())
        {
          throw new ChangeSetOutdatedException();
        }

        revisionDeltas.put(id, targetGoalDelta);
        result.add(targetGoalDelta);

        // handle reattached objects.
        if (detachedObjects.containsKey(id))
        {
          CDOStateMachine.INSTANCE.internalReattach(object, this);
        }

        object.cdoInternalSetRevision(goalRevision);
        object.cdoInternalSetState(CDOState.DIRTY);
        revisionChanged = true;

        dirtyObjects.put(id, object);
        setDirty(true);
      }

      if (revisionChanged)
      {
        object.cdoInternalPostLoad();
      }
    }

    return oldRevisions;
  }

  private InternalCDOObject getObjectIfExists(CDOID id)
  {
    try
    {
      return getObject(id);
    }
    catch (ObjectNotFoundException ex)
    {
      return null;
    }
  }

  /*
   * Synchronized through InvalidationRunnable.run()
   */
  @Override
  protected void handleConflicts(long lastUpdateTime, Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts, List<CDORevisionDelta> deltas)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOConflictResolver[] resolvers = options().getConflictResolvers();
        if (resolvers.length == 0)
        {
          return;
        }

        if (conflicts == null)
        {
          for (CDOConflictResolver resolver : resolvers)
          {
            if (resolver instanceof CDOConflictResolver.NonConflictAware)
            {
              ((CDOConflictResolver.NonConflictAware)resolver).handleNonConflict(lastUpdateTime);
            }
          }

          return;
        }

        // Remember original state to be able to restore it after an exception
        List<CDOState> states = new ArrayList<>(conflicts.size());
        List<CDORevision> revisions = new ArrayList<>(conflicts.size());
        for (CDOObject conflict : conflicts.keySet())
        {
          states.add(conflict.cdoState());
          revisions.add(conflict.cdoRevision());
        }

        int resolved = 0;

        try
        {
          Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> remaining = new HashMap<>(conflicts);
          for (CDOConflictResolver resolver : resolvers)
          {
            if (resolver instanceof CDOConflictResolver2)
            {
              ((CDOConflictResolver2)resolver).resolveConflicts(Collections.unmodifiableMap(remaining), deltas);
            }
            else
            {
              resolver.resolveConflicts(Collections.unmodifiableSet(remaining.keySet()));
            }

            for (Iterator<CDOObject> it = remaining.keySet().iterator(); it.hasNext();)
            {
              CDOObject object = it.next();
              if (!object.cdoConflict())
              {
                ++resolved;
                it.remove();
              }
            }
          }
        }
        catch (Exception ex)
        {
          // Restore original state
          Iterator<CDOState> state = states.iterator();
          Iterator<CDORevision> revision = revisions.iterator();
          for (CDOObject object : conflicts.keySet())
          {
            ((InternalCDOObject)object).cdoInternalSetRevision(revision.next());
            ((InternalCDOObject)object).cdoInternalSetState(state.next());
          }

          throw WrappedException.wrap(ex);
        }

        conflict -= resolved;

        Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
        setDirty(!dirtyObjects.isEmpty());
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @deprecated {@link #createIDForNewObject(EObject object)} is called since 4.1.
   */
  @Override
  @Deprecated
  public CDOIDTemp getNextTemporaryID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOID createIDForNewObject(EObject object)
  {
    return idGenerator.generateCDOID(object);
  }

  @Override
  public CDOResourceFolder createResourceFolder(String path) throws CDOResourceNodeNotFoundException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (path.endsWith(CDOURIUtil.SEGMENT_SEPARATOR))
        {
          path = path.substring(0, path.length() - 1);
        }

        CDOResourceFolder folder = EresourceFactory.eINSTANCE.createCDOResourceFolder();
        int pos = path.lastIndexOf(CDOURIUtil.SEGMENT_SEPARATOR_CHAR);
        if (pos <= 0)
        {
          String name = path.substring(pos == 0 ? 1 : 0);
          folder.setName(name);

          getRootResource().getContents().add(folder);
        }
        else
        {
          String name = path.substring(pos + 1);
          folder.setName(name);

          path = path.substring(0, pos);
          CDOResourceNode parent = null;

          try
          {
            parent = getResourceNode(path);
          }
          catch (Exception ex)
          {
            parent = createResourceFolder(path);
          }

          if (parent instanceof CDOResourceFolder)
          {
            ((CDOResourceFolder)parent).getNodes().add(folder);
          }
          else
          {
            throw new CDOException("Parent is not a folder: " + parent);
          }
        }

        return folder;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOResource createResource(String path)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        URI uri = CDOURIUtil.createResourceURI(this, path);
        return (CDOResource)getResourceSet().createResource(uri);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOResource getOrCreateResource(String path)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        try
        {
          CDOID id = getResourceNodeID(path);
          if (!CDOIDUtil.isNull(id))
          {
            return (CDOResource)getObject(id);
          }
        }
        catch (CDOResourceNodeNotFoundException ignore)
        {
          // Just create the missing resource.
        }

        return createResource(path);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOTextResource createTextResource(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOTextResource resource = EresourceFactory.eINSTANCE.createCDOTextResource();
        createFileResource(path, resource);
        return resource;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOTextResource getOrCreateTextResource(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        try
        {
          CDOID id = getResourceNodeID(path);
          if (!CDOIDUtil.isNull(id))
          {
            return (CDOTextResource)getObject(id);
          }
        }
        catch (CDOResourceNodeNotFoundException ignore)
        {
          // Just create the missing resource.
        }

        return createTextResource(path);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOBinaryResource createBinaryResource(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOBinaryResource resource = EresourceFactory.eINSTANCE.createCDOBinaryResource();
        createFileResource(path, resource);
        return resource;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOBinaryResource getOrCreateBinaryResource(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        try
        {
          CDOID id = getResourceNodeID(path);
          if (!CDOIDUtil.isNull(id))
          {
            return (CDOBinaryResource)getObject(id);
          }
        }
        catch (CDOResourceNodeNotFoundException ignore)
        {
          // Just create the missing resource
        }

        return createBinaryResource(path);
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void createFileResource(String path, CDOFileResource<?> resource)
  {
    int lastSlash = path.lastIndexOf('/');
    if (lastSlash == -1)
    {
      resource.setName(path);
      getRootResource().getContents().add(resource);
    }
    else
    {
      resource.setName(path.substring(lastSlash + 1));

      CDOResourceFolder folder = getOrCreateResourceFolder(path.substring(0, lastSlash));
      folder.getNodes().add(resource);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void attachResource(CDOResourceImpl resource)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (resource.isExisting())
        {
          super.attachResource(resource);
        }
        else
        {
          // ResourceSet.createResource(uri) was called!!
          attachNewResource(resource);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void attachNewResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    List<String> names = CDOURIUtil.analyzePath(uri);
    String resourceName = names.isEmpty() ? null : names.remove(names.size() - 1);

    CDOResourceFolder folder = getOrCreateResourceFolder(names);
    attachNewResourceNode(folder, resourceName, resource);
  }

  @Override
  public CDOResourceFolder getOrCreateResourceFolder(String path)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        try
        {
          CDOID id = getResourceNodeID(path);
          if (!CDOIDUtil.isNull(id))
          {
            return (CDOResourceFolder)getObject(id);
          }
        }
        catch (CDOResourceNodeNotFoundException ignore)
        {
          // Just create the missing resource.
        }

        return createResourceFolder(path);
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @return never <code>null</code>;
   * @since 2.0
   */
  @Override
  public CDOResourceFolder getOrCreateResourceFolder(List<String> names)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOResourceFolder folder = null;
        for (String name : names)
        {
          CDOResourceNode node;

          try
          {
            CDOID folderID = folder == null ? null : folder.cdoID();
            node = getResourceNode(folderID, name);
          }
          catch (CDOResourceNodeNotFoundException ex)
          {
            node = EresourceFactory.eINSTANCE.createCDOResourceFolder();
            attachNewResourceNode(folder, name, node);
          }

          if (node instanceof CDOResourceFolder)
          {
            folder = (CDOResourceFolder)node;
          }
          else
          {
            throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.0"), node)); //$NON-NLS-1$
          }
        }

        return folder;
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void attachNewResourceNode(CDOResourceFolder folder, String name, CDOResourceNode newNode)
  {
    CDOResourceNodeImpl node = (CDOResourceNodeImpl)newNode;
    node.basicSetName(name, false);
    if (folder == null)
    {
      if (node.isRoot())
      {
        CDOStateMachine.INSTANCE.attach(node, this);
      }
      else
      {
        getRootResource().getContents().add(node);
      }
    }
    else
    {
      node.basicSetFolder(folder, false);
    }
  }

  /**
   * @since 4.1
   */
  @Override
  public InternalCDOSavepoint getFirstSavepoint()
  {
    return firstSavepoint;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOSavepoint getLastSavepoint()
  {
    checkActive();
    return lastSavepoint;
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOTransactionStrategy getTransactionStrategy()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (transactionStrategy == null)
        {
          transactionStrategy = CDOTransactionStrategy.DEFAULT;
          transactionStrategy.setTarget(this);
        }

        return transactionStrategy;
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
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
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected CDOID getRootOrTopLevelResourceNodeID(String name) throws CDOResourceNodeNotFoundException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (dirty)
        {
          CDOResourceNode node = getRootResourceNode(name, getDirtyObjects().values());
          if (node != null)
          {
            return node.cdoID();
          }

          node = getRootResourceNode(name, getNewObjects().values());
          if (node != null)
          {
            return node.cdoID();
          }
        }

        CDOID id = super.getRootOrTopLevelResourceNodeID(name);
        if (isObjectDetached(id) || isObjectDirty(id))
        {
          throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.1"), name)); //$NON-NLS-1$
        }

        return id;
      }
      finally
      {
        unlockView();
      }
    }
  }

  private CDOResourceNode getRootResourceNode(String name, Collection<? extends CDOObject> objects)
  {
    for (CDOObject object : objects)
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        if (node.getFolder() == null && ObjectUtil.equals(name, node.getName()))
        {
          return node;
        }
      }
    }

    return null;
  }

  @Override
  protected InternalCDOObject getObjectUnsynced(CDOID id, boolean loadOnDemand)
  {
    if (isObjectNew(id) && isObjectDetached(id))
    {
      throw new ObjectNotFoundException(id, this);
    }

    return super.getObjectUnsynced(id, loadOnDemand);
  }

  @Override
  public boolean isObjectNew(CDOID id)
  {
    return lastSavepoint.isNewObject(id);
  }

  private boolean isObjectDirty(CDOID id)
  {
    return lastSavepoint.getDirtyObject(id) != null;
  }

  private boolean isObjectDetached(CDOID id)
  {
    return lastSavepoint.getDetachedObject(id) != null;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOCommitContext createCommitContext()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return new CDOCommitContextImpl(this);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOCommitInfo commit(IProgressMonitor monitor) throws CommitException
  {
    CDOConflictResolver[] conflictResolvers = options().getConflictResolvers();
    if (conflictResolvers.length != 0)
    {
      try
      {
        checkActive();

        // Reach out to conflict resolvers before synchronizing on this transaction to avoid deadlocks in UI thread.
        for (CDOConflictResolver resolver : conflictResolvers)
        {
          if (resolver instanceof CDOConflictResolver3)
          {
            if (!((CDOConflictResolver3)resolver).preCommit())
            {
              return null;
            }
          }
        }
      }
      catch (Exception ex)
      {
        throw new CommitException(ex);
      }
    }

    CDOCommitInfo info = commitSynced(monitor);
    if (info != null)
    {
      waitForCommitInfo(info.getTimeStamp());
    }

    return info;
  }

  private void waitForCommitInfo(long timeStamp)
  {
    long timeout = options().getCommitInfoTimeout();
    if (!waitForUpdate(timeStamp, timeout))
    {
      throw new TimeoutRuntimeException("Did not receive an update: " + this);
    }
  }

  /**
   * Overridden by Bugzilla_547640_Test to resume the repository's commit notification sending.
   */
  protected void waitForBaseline(long timeStamp)
  {
    waitForCommitInfo(timeStamp);
  }

  private CDOCommitInfo commitSynced(IProgressMonitor progressMonitor) throws DanglingIntegrityException, CommitException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        InternalCDOSession session = getSession();

        try
        {
          checkActive();
          if (hasConflict())
          {
            throw new LocalCommitConflictException(Messages.getString("CDOTransactionImpl.2")); //$NON-NLS-1$
          }

          commitToken = (CommitToken)session.startLocalCommit();

          CDOTransactionStrategy transactionStrategy = getTransactionStrategy();
          CDOCommitInfo info = transactionStrategy.commit(this, progressMonitor);
          if (info != null)
          {
            lastCommitTime = info.getTimeStamp();
          }

          return info;
        }
        catch (DanglingReferenceException ex)
        {
          throw new DanglingIntegrityException(ex);
        }
        catch (CommitException ex)
        {
          throw ex;
        }
        catch (Throwable t)
        {
          try
          {
            // The commit may have succeeded on the server, but after that network problems or timeouts have hit us.
            // Let's see if we can recover...
            CDOCommitInfo info = session.getSessionProtocol().resetTransaction(getViewID(), commitToken.getCommitNumber());
            if (info != null)
            {
              lastCommitTime = info.getTimeStamp();

              InvalidationData invalidationData = new InvalidationData();
              invalidationData.setCommitInfo(info);
              invalidationData.setSender(this);
              invalidationData.setClearResourcePathCache(true);
              invalidationData.setSecurityImpact(CDOProtocol.CommitNotificationInfo.IMPACT_NONE);
              invalidationData.setNewPermissions(null);
              invalidationData.setLockChangeInfo(null);

              session.invalidate(invalidationData);

              // At this point the session (invalidator) is recovered.
              // Continue to rethrow the exception and let the client call rollback()...
            }
          }
          catch (Throwable ex)
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace(ex);
            }
          }

          Throwable cause = t.getCause();
          if (cause instanceof MonitorCanceledException)
          {
            if (!(cause.getCause() instanceof TimeoutRuntimeException))
            {
              throw new OperationCanceledException("CDOTransactionImpl.7");//$NON-NLS-1$
            }
          }

          throw new CommitException(t);
        }
        finally
        {
          session.endLocalCommit(commitToken);
          commitToken = null;

          clearResourcePathCacheIfNecessary(null);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  @Deprecated
  public <T> CommitResult<T> commit(final Callable<T> callable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    return commit(callable, org.eclipse.net4j.util.Predicates.toJava8(retry), monitor);
  }

  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    final Object[] result = { null };
    final Exception[] exception = { null };

    Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          result[0] = callable.call();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    CDOCommitInfo info = commit(runnable, retry, monitor);
    if (exception[0] != null)
    {
      throw exception[0];
    }

    if (info == null)
    {
      return null;
    }

    @SuppressWarnings("unchecked")
    T r = (T)result[0];
    return new CommitResult<>(r, info);
  }

  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, Exception
  {
    return commit(callable, new CountedRetryPredicate(attempts), monitor);
  }

  @Override
  @Deprecated
  public CDOCommitInfo commit(Runnable runnable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    return commit(runnable, org.eclipse.net4j.util.Predicates.toJava8(retry), monitor);
  }

  @Override
  public CDOCommitInfo commit(Runnable runnable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    long start = System.currentTimeMillis();
    SubMonitor subMonitor = SubMonitor.convert(monitor);

    for (;;)
    {
      subMonitor.setWorkRemaining(100);

      synchronized (getViewMonitor())
      {
        lockView();

        runnable.run();

        try
        {
          return commit(subMonitor.split(1));
        }
        catch (ConcurrentAccessException ex)
        {
          if (retry.test(System.currentTimeMillis() - start))
          {
            rollback();
            continue;
          }

          throw ex;
        }
        finally
        {
          unlockView();
        }
      }
    }
  }

  @Override
  public CDOCommitInfo commit(Runnable runnable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return commit(runnable, new CountedRetryPredicate(attempts), monitor);
  }

  @Override
  public CommitToken getCommitToken()
  {
    return commitToken;
  }

  /**
   * @since 2.0
   */
  @Override
  public void rollback()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOTransactionStrategy strategy = getTransactionStrategy();
        strategy.rollback(this, firstSavepoint);

        cleanUp(null);
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void removeObject(CDOID id, final CDOObject object)
  {
    removeObject(id);

    if (object instanceof CDOResource)
    {
      InternalCDOViewSet viewSet = getViewSet();
      viewSet.executeWithoutNotificationHandling(new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          EList<Resource> resources = getResourceSet().getResources();
          resources.remove(object);
          return true;
        }
      });
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void detachObject(InternalCDOObject object)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOTransactionHandler1[] handlers = getTransactionHandlers1();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler1 handler = handlers[i];
          handler.detachingObject(this, object);
        }

        // deregister object
        CDOID id = object.cdoID();
        if (object.cdoState() == CDOState.NEW)
        {
          Map<CDOID, CDOObject> map = lastSavepoint.getNewObjects();

          // Determine if we added object
          if (map.containsKey(id))
          {
            map.remove(id);
          }
          else
          {
            lastSavepoint.getDetachedObjects().put(id, object);
          }

          // deregister object
          deregisterObject(object);
        }
        else
        {
          if (!cleanRevisions.containsKey(object))
          {
            cleanRevisions.put(object, object.cdoRevision());
          }

          lastSavepoint.getDetachedObjects().put(id, object);

          // Object may have been reattached previously, in which case it must
          // here be removed from the collection of reattached objects
          lastSavepoint.getReattachedObjects().remove(id);
        }

        getUnitManager().removeObject(object);
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleRollback(InternalCDOSavepoint savepoint)
  {
    if (savepoint == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.3")); //$NON-NLS-1$
    }

    if (savepoint.getTransaction() != this)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.4"), savepoint)); //$NON-NLS-1$
    }

    if (!savepoint.isValid())
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.6"), savepoint)); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("handleRollback()"); //$NON-NLS-1$
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        // Remember current revisions
        Map<CDOObject, CDORevision> oldRevisions = new HashMap<>();
        collectRevisions(oldRevisions, getDirtyObjects());
        collectRevisions(oldRevisions, getNewObjects());

        // Rollback objects
        Map<CDOObject, CDORevision> newRevisions = new HashMap<>();
        Set<CDOID> idsOfNewObjectWithDeltas = rollbackCompletely(savepoint, newRevisions);

        lastSavepoint = savepoint;
        lastSavepoint.setNextSavepoint(null);
        lastSavepoint.clear();

        // Load from first savepoint up to current savepoint
        loadSavepoint(lastSavepoint, idsOfNewObjectWithDeltas);

        if (lastSavepoint == firstSavepoint && options().isAutoReleaseLocksEnabled())
        {
          CDORepositoryInfo repositoryInfo = getSession().getRepositoryInfo();
          if (isDurableView() && repositoryInfo.getState() == CDOCommonRepository.State.ONLINE || repositoryInfo.getType() == CDOCommonRepository.Type.MASTER)
          {
            // Unlock all objects
            unlockObjects(null, null);
          }
        }

        // Send notifications.
        for (Entry<CDOObject, CDORevision> entry : oldRevisions.entrySet())
        {
          InternalCDOObject object = (InternalCDOObject)entry.getKey();
          InternalCDORevision oldRevision = (InternalCDORevision)entry.getValue();

          InternalCDORevision newRevision = object.cdoRevision();
          if (newRevision == null)
          {
            newRevision = (InternalCDORevision)newRevisions.get(object);
          }

          if (newRevision == null && !FSMUtil.isTransient(object))
          {
            // This can happen for example for conflicting objects which have been set to PROXY in rollbackCompletely().
            CDOID id = oldRevision.getID();
            newRevision = getRevision(id, true);
            object.cdoInternalSetRevision(newRevision);
            object.cdoInternalSetState(CDOState.CLEAN);
          }

          if (newRevision != null)
          {
            InternalCDORevisionDelta delta = newRevision.compare(oldRevision);
            if (!delta.isEmpty())
            {
              Set<CDOObject> detachedObjects = Collections.emptySet();

              CDONotificationBuilder builder = new CDONotificationBuilder(this);
              NotificationChain notification = builder.buildNotification(object, oldRevision, delta, detachedObjects);
              if (notification != null)
              {
                notification.dispatch();
              }
            }
          }
        }

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new FinishedEvent(true), listeners);
        }

        CDOTransactionHandler2[] handlers = getTransactionHandlers2();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler2 handler = handlers[i];

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
      finally
      {
        unlockView();
      }
    }
  }

  private Set<CDOID> rollbackCompletely(CDOUserSavepoint savepoint, Map<CDOObject, CDORevision> newRevisions)
  {
    Set<CDOID> idsOfNewObjectsWithDeltas = new HashSet<>();
    Set<InternalCDOObject> newObjects = new HashSet<>();

    // Start from the last savepoint and come back up to the active
    for (InternalCDOSavepoint itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint.getPreviousSavepoint())
    {
      Set<Object> toBeDetached = new HashSet<>();

      // Rollback new objects attached after the save point
      Map<CDOID, CDOObject> newObjectsMap = itrSavepoint.getNewObjects();
      for (CDOID id : newObjectsMap.keySet())
      {
        InternalCDOObject object = (InternalCDOObject)newObjectsMap.get(id);

        toBeDetached.add(id);
        toBeDetached.add(object);
        toBeDetached.add(object.cdoInternalInstance());
        removeObject(id, object);
        newObjects.add(object);
      }

      // Rollback new objects re-attached after the save point
      Map<CDOID, CDOObject> reattachedObjectsMap = itrSavepoint.getReattachedObjects();
      Set<CDOID> detachedIDs = itrSavepoint.getDetachedObjects().keySet();
      for (CDOObject reattachedObject : reattachedObjectsMap.values())
      {
        CDOID id = reattachedObject.cdoID();
        if (!detachedIDs.contains(id))
        {
          InternalCDOObject internal = (InternalCDOObject)reattachedObject;

          toBeDetached.add(id);
          toBeDetached.add(internal);
          toBeDetached.add(internal.cdoInternalInstance());
          removeObject(id, internal);

          internal.cdoInternalSetView(null);
          internal.cdoInternalSetID(null);
          internal.cdoInternalSetState(CDOState.TRANSIENT);
        }
      }

      Map<CDOID, CDORevision> attachedRevisions = options().getAttachedRevisionsMap();
      for (Object idOrObject : toBeDetached)
      {
        InternalCDORevision revision = null;

        if (idOrObject instanceof InternalCDOObject)
        {
          InternalCDOObject object = (InternalCDOObject)idOrObject;
          CDOID id = object.cdoID();

          if (attachedRevisions != null)
          {
            revision = (InternalCDORevision)attachedRevisions.get(id);
          }

          if (revision == null)
          {
            revision = object.cdoRevision();
            if (revision != null)
            {
              // Copy the revision, so that the revision modifications below don't apply to the oldRevisions,
              // which have been remembered in handleRollback() for the computation of delta notifications.
              revision = revision.copy();
            }
          }

          if (revision != null)
          {
            newRevisions.put((CDOObject)idOrObject, revision);
          }

          Resource.Internal directResource = object.eDirectResource();
          EObject container = object.eContainer();
          boolean topLevel = !toBeDetached.contains(directResource) && !toBeDetached.contains(container);
          if (topLevel)
          {
            if (revision != null)
            {
              // Unset direct resource and container in the revision.
              // Later cdoInternalPostDetach() will migrate these values into the EObject.
              // See CDOStateMachine.RollbackTransition.execute().
              revision.setResourceID(null);
              revision.setContainerID(null);
              revision.setContainingFeatureID(0);
            }
          }

          if (idOrObject instanceof CDOObjectImpl)
          {
            CDOObjectImpl impl = (CDOObjectImpl)idOrObject;

            if (revision != null)
            {
              impl.cdoInternalSetRevision(revision);
            }
            else if (topLevel)
            {
              // Unset direct resource and eContainer in the EObject.
              impl.cdoInternalSetResource(null);
            }
          }
          else if (idOrObject instanceof CDOObjectWrapper)
          {
            CDOObjectWrapper wrapper = (CDOObjectWrapper)idOrObject;

            if (revision != null)
            {
              wrapper.cdoInternalRollback(revision);
            }

            if (topLevel)
            {
              wrapper.setInstanceResource(null);
              wrapper.setInstanceContainer(null, 0);
            }
          }
        }
      }

      Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavepoint.getRevisionDeltas2();
      if (!revisionDeltas.isEmpty())
      {
        for (CDORevisionDelta dirtyObject : revisionDeltas.values())
        {
          CDOID id = dirtyObject.getID();
          if (isObjectNew(id))
          {
            idsOfNewObjectsWithDeltas.add(id);
          }
        }
      }

      // Rollback all detached objects.
      Map<CDOID, CDOObject> detachedObjectsMap = itrSavepoint.getDetachedObjects();
      if (!detachedObjectsMap.isEmpty())
      {
        for (Entry<CDOID, CDOObject> detachedObjectEntry : detachedObjectsMap.entrySet())
        {
          CDOID id = detachedObjectEntry.getKey();
          if (isObjectNew(id))
          {
            idsOfNewObjectsWithDeltas.add(id);
          }
          else
          {
            InternalCDOObject detachedObject = (InternalCDOObject)detachedObjectEntry.getValue();
            InternalCDORevision cleanRev = cleanRevisions.get(detachedObject);
            cleanObject(detachedObject, cleanRev);
          }
        }
      }

      // Rollback dirty objects.
      for (Entry<CDOID, CDOObject> entryDirtyObject : itrSavepoint.getDirtyObjects().entrySet())
      {
        CDOID id = entryDirtyObject.getKey();
        if (!isObjectNew(id))
        {
          InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();

          // Bug 283985 (Re-attachment): Skip objects that were reattached, because
          // they were already reset to TRANSIENT earlier in this method
          if (!reattachedObjectsMap.values().contains(internalDirtyObject))
          {
            CDOStateMachine.INSTANCE.rollback(internalDirtyObject, this);
          }
        }
      }

      if (savepoint == itrSavepoint)
      {
        break;
      }
    }

    // Rollback new objects.
    for (InternalCDOObject internalCDOObject : newObjects)
    {
      if (FSMUtil.isNew(internalCDOObject))
      {
        CDOStateMachine.INSTANCE.rollback(internalCDOObject, this);
        internalCDOObject.cdoInternalSetID(null);
        internalCDOObject.cdoInternalSetView(null);
      }
    }

    return idsOfNewObjectsWithDeltas;
  }

  private void loadSavepoint(CDOSavepoint savepoint, Set<CDOID> idsOfNewObjectWithDeltas)
  {
    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();
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
      CDORevision revision = newBaseRevision.get(id);
      if (revision != null)
      {
        object.cdoInternalSetRevision(revision.copy());
        object.cdoInternalSetView(this);
        object.cdoInternalSetState(CDOState.NEW);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
        if (super.getObject(object.cdoID(), false) == null)
        {
          registerObject(object);
        }
      }
    }

    // We need to register back new objects that are not removed anymore there.
    for (Entry<CDOID, CDOObject> entryNewObject : newObjMaps.entrySet())
    {
      InternalCDOObject object = (InternalCDOObject)entryNewObject.getValue();

      // Go back to the previous state
      cleanObject(object, object.cdoRevision());
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

    CDOObjectMerger merger = new CDOObjectMerger();
    for (InternalCDOSavepoint itrSavepoint = firstSavepoint; itrSavepoint != savepoint; itrSavepoint = itrSavepoint.getNextSavepoint())
    {
      for (CDORevisionDelta delta : itrSavepoint.getRevisionDeltas2().values())
      {
        CDOID id = delta.getID();
        boolean isNew = isObjectNew(id);
        if (isNew && !idsOfNewObjectWithDeltas.contains(id) || detachedObjects.containsKey(id))
        {
          continue;
        }

        Map<CDOID, CDOObject> map = isNew ? newObjMaps : dirtyObjects;
        InternalCDOObject object = (InternalCDOObject)map.get(id);

        // Change state of the objects
        merger.merge(object, delta);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    dirty = savepoint.wasDirty();
  }

  private void collectRevisions(Map<CDOObject, CDORevision> revisions, Map<CDOID, CDOObject> objects)
  {
    for (CDOObject object : objects.values())
    {
      CDORevision oldRevision = object.cdoRevision();
      if (oldRevision != null)
      {
        revisions.put(object, oldRevision);
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOSavepoint handleSetSavepoint()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        addToBase(lastSavepoint.getNewObjects());
        lastSavepoint = createSavepoint(lastSavepoint);
        return lastSavepoint;
      }
      finally
      {
        unlockView();
      }
    }
  }

  private CDOSavepointImpl createSavepoint(InternalCDOSavepoint lastSavepoint)
  {
    return new CDOSavepointImpl(this, lastSavepoint);
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOSavepoint setSavepoint()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return (InternalCDOSavepoint)getTransactionStrategy().setSavepoint(this);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public boolean hasMultipleSavepoints()
  {
    return lastSavepoint != firstSavepoint;
  }

  private void addToBase(Map<CDOID, CDOObject> objects)
  {
    for (CDOObject object : objects.values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavepoint.getBaseNewObjects().put(object.cdoID(), object.cdoRevision().copy());
    }
  }

  @Override
  protected String getClassName()
  {
    return "CDOTransaction"; //$NON-NLS-1$
  }

  @Override
  public void registerAttached(InternalCDOObject object, boolean isNew)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering new object {0}", object); //$NON-NLS-1$
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (isNew)
        {
          registerNewPackage(object.eClass().getEPackage());
        }

        CDOTransactionHandler1[] handlers = getTransactionHandlers1();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler1 handler = handlers[i];
          handler.attachingObject(this, object);
        }

        if (isNew)
        {
          registerNew(lastSavepoint.getNewObjects(), object);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void registerNewPackage(EPackage ePackage)
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    if (!packageRegistry.containsKey(ePackage.getNsURI()))
    {
      packageRegistry.putEPackage(ePackage);
    }
  }

  private CDOOriginSizeProvider getOriginSizeProvider(InternalCDOObject object, CDOFeatureDelta featureDelta, InternalCDORevision cleanRevision)
  {
    EStructuralFeature feature = featureDelta.getFeature();
    if (feature.isMany())
    {
      if (cleanRevision == null)
      {
        cleanRevision = cleanRevisions.get(object);
        if (cleanRevision == null)
        {
          cleanRevision = object.cdoRevision();
        }
      }

      CDOList list = cleanRevision.getListOrNull(feature);
      final int originSize = list == null ? 0 : list.size();

      return new CDOOriginSizeProvider()
      {
        @Override
        public int getOriginSize()
        {
          return originSize;
        }
      };
    }

    return null;
  }

  /**
   * Receives notification for new and dirty objects
   */
  @Override
  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        registerFeatureDelta(object, featureDelta, null);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta, InternalCDORevision cleanRevision)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOID id = object.cdoID();
        boolean needToSaveFeatureDelta = true;

        if (object.cdoState() == CDOState.NEW)
        {
          // Register Delta for new objects only if objectA doesn't belong to this savepoint
          if (lastSavepoint.getPreviousSavepoint() == null || featureDelta == null)
          {
            needToSaveFeatureDelta = false;
          }
          else
          {
            Map<CDOID, CDOObject> map = lastSavepoint.getNewObjects();
            needToSaveFeatureDelta = !map.containsKey(id);
          }
        }

        if (needToSaveFeatureDelta)
        {
          Map<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas2();
          InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)revisionDeltas.get(id);
          if (revisionDelta == null)
          {
            InternalCDORevision revision = object.cdoRevision();

            revisionDelta = (InternalCDORevisionDelta)CDORevisionUtil.createDelta(revision);
            revisionDeltas.put(id, revisionDelta);
          }

          CDOOriginSizeProvider originSizeProvider = getOriginSizeProvider(object, featureDelta, cleanRevision);
          revisionDelta.addFeatureDelta(featureDelta, originSizeProvider);
        }

        CDOTransactionHandler1[] handlers = getTransactionHandlers1();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler1 handler = handlers[i];
          handler.modifyingObject(this, object, featureDelta);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        Map<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas2();
        CDOID id = revisionDelta.getID();
        if (!revisionDeltas.containsKey(id))
        {
          revisionDeltas.put(id, revisionDelta);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    registerDirty(object, featureDelta, null);
  }

  @Override
  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta, InternalCDORevision cleanRevision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object); //$NON-NLS-1$
    }

    if (featureDelta != null)
    {
      registerFeatureDelta(object, featureDelta, cleanRevision);
    }

    registerNew(lastSavepoint.getDirtyObjects(), object);
  }

  /**
   * TODO Simon: Should this method go to CDOSavePointImpl?
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void registerNew(Map map, InternalCDOObject object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOTransactionImpl.10"), object)); //$NON-NLS-1$
    }

    setDirty(true);
  }

  public List<CDOPackageUnit> analyzeNewPackages()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
        Set<EPackage> usedPackages = new HashSet<>();
        Set<EPackage> usedNewPackages = new HashSet<>();
        for (CDOObject object : getNewObjects().values())
        {
          EPackage ePackage = object.eClass().getEPackage();
          if (usedPackages.add(ePackage))
          {
            EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
            if (ePackage == topLevelPackage || usedPackages.add(topLevelPackage))
            {
              // if (!CDOModelUtil.isSystemPackage(topLevelPackage))
              {
                CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(topLevelPackage);
                if (packageUnit.getState() == CDOPackageUnit.State.NEW)
                {
                  usedNewPackages.add(topLevelPackage);
                }
              }
            }
          }
        }

        if (usedNewPackages.size() > 0)
        {
          Set<CDOPackageUnit> result = new HashSet<>();
          for (EPackage usedNewPackage : analyzeNewPackages(usedNewPackages, packageRegistry))
          {
            CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedNewPackage);
            result.add(packageUnit);
          }

          return new ArrayList<>(result);
        }

        return Collections.emptyList();
      }
      finally
      {
        unlockView();
      }
    }
  }

  private static List<EPackage> analyzeNewPackages(Collection<EPackage> usedTopLevelPackages, CDOPackageRegistry packageRegistry)
  {
    // Determine which of the corresponding EPackages are new
    List<EPackage> newPackages = new ArrayList<>();

    IPackageClosure closure = new CompletePackageClosure();
    usedTopLevelPackages = closure.calculate(usedTopLevelPackages);

    for (EPackage usedPackage : usedTopLevelPackages)
    {
      // if (!CDOModelUtil.isSystemPackage(usedPackage))
      {
        CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedPackage);
        if (packageUnit == null)
        {
          throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.11"), usedPackage)); //$NON-NLS-1$
        }

        if (packageUnit.getState() == CDOPackageUnit.State.NEW)
        {
          newPackages.add(usedPackage);
        }
      }
    }

    return newPackages;
  }

  private void cleanUp(CDOCommitContext commitContext)
  {
    Map<CDOObject, CDOLockState> lockStates = getLockStates();
    if (commitContext == null || !commitContext.isPartialCommit())
    {
      if (commitContext != null)
      {
        for (CDOObject object : commitContext.getDetachedObjects().values())
        {
          cleanUpLockState(lockStates, object);
        }
      }

      lastSavepoint = firstSavepoint;
      firstSavepoint.clear();
      firstSavepoint.setNextSavepoint(null);

      cleanRevisions.clear();

      Map<CDOID, CDORevision> attachedRevisions = options().getAttachedRevisionsMap();
      if (attachedRevisions != null)
      {
        attachedRevisions.clear();
      }

      dirty = false;
      conflict = 0;
      idGenerator.reset();
    }
    else
    {
      collapseSavepoints(commitContext);

      for (CDOObject object : commitContext.getDetachedObjects().values())
      {
        cleanRevisions.remove(object);
        cleanUpLockState(lockStates, object);
      }

      for (CDOObject object : commitContext.getDirtyObjects().values())
      {
        cleanRevisions.remove(object);
      }
    }

    // Reset partial-commit filter
    committables = null;
  }

  private void cleanUpLockState(Map<CDOObject, CDOLockState> lockStates, CDOObject object)
  {
    InternalCDOLockState lockState = (InternalCDOLockState)lockStates.remove(object);
    if (lockState != null)
    {
      lockState.dispose();
    }
  }

  private void collapseSavepoints(CDOCommitContext commitContext)
  {
    InternalCDOSavepoint newSavepoint = createSavepoint(null);
    copyUncommitted(lastSavepoint.getAllNewObjects(), commitContext.getNewObjects(), newSavepoint.getNewObjects());
    copyUncommitted(lastSavepoint.getAllDirtyObjects(), commitContext.getDirtyObjects(), newSavepoint.getDirtyObjects());
    copyUncommitted(lastSavepoint.getAllRevisionDeltas(), commitContext.getRevisionDeltas(), newSavepoint.getRevisionDeltas2());
    copyUncommitted(lastSavepoint.getAllDetachedObjects(), commitContext.getDetachedObjects(), newSavepoint.getDetachedObjects());
    lastSavepoint = newSavepoint;
    firstSavepoint = lastSavepoint;
  }

  private <T> void copyUncommitted(Map<CDOID, T> oldSavepointMap, Map<CDOID, T> commitContextMap, Map<CDOID, T> newSavepointMap)
  {
    for (Entry<CDOID, T> entry : oldSavepointMap.entrySet())
    {
      if (!commitContextMap.containsKey(entry.getKey()))
      {
        newSavepointMap.put(entry.getKey(), entry.getValue());
      }
    }
  }

  @Override
  public CDOSavepoint[] exportChanges(OutputStream stream) throws IOException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        @SuppressWarnings("all")
        CDODataOutput out = new CDODataOutputImpl(new ExtendedDataOutputStream(stream))
        {
          @Override
          public CDOIDProvider getIDProvider()
          {
            return CDOTransactionImpl.this;
          }

          @Override
          public CDOPackageRegistry getPackageRegistry()
          {
            return getSession().getPackageRegistry();
          }

          @Override
          public CDORevisionUnchunker getRevisionUnchunker()
          {
            return getSession();
          }

          @Override
          protected boolean isXCompression()
          {
            return X_COMPRESSION;
          }
        };

        List<CDOSavepoint> savepoints = new ArrayList<>();
        int totalNewObjects = 0;

        InternalCDOSavepoint savepoint = firstSavepoint;
        while (savepoint != null)
        {
          Collection<CDOObject> newObjects = savepoint.getNewObjects().values();
          totalNewObjects += newObjects.size();

          savepoint = savepoint.getNextSavepoint();
        }

        out.writeXInt(totalNewObjects);

        savepoint = firstSavepoint;
        while (savepoint != null)
        {
          Collection<CDOObject> newObjects = savepoint.getNewObjects().values();
          Collection<CDORevisionDelta> revisionDeltas = savepoint.getRevisionDeltas2().values();
          if (newObjects.isEmpty() && revisionDeltas.isEmpty())
          {
            savepoint = savepoint.getNextSavepoint();
            continue;
          }

          savepoints.add(savepoint);
          out.writeBoolean(true);

          out.writeXInt(newObjects.size());
          for (CDOObject newObject : newObjects)
          {
            out.writeCDORevision(newObject.cdoRevision(), CDORevision.UNCHUNKED);
          }

          out.writeXInt(revisionDeltas.size());
          for (CDORevisionDelta revisionDelta : revisionDeltas)
          {
            out.writeCDORevisionDelta(revisionDelta);
          }

          savepoint = savepoint.getNextSavepoint();
        }

        out.writeBoolean(false);
        return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOSavepoint[] importChanges(InputStream stream, boolean reconstructSavepoints) throws IOException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDOSavepoint> savepoints = new ArrayList<>();
        if (stream.available() > 0)
        {
          CDODataInput in = new CDODataInputImpl(new ExtendedDataInputStream(stream))
          {
            @Override
            public CDOPackageRegistry getPackageRegistry()
            {
              return getSession().getPackageRegistry();
            }

            @Override
            protected CDOBranchManager getBranchManager()
            {
              return getSession().getBranchManager();
            }

            @Override
            protected CDOCommitInfoManager getCommitInfoManager()
            {
              return getSession().getCommitInfoManager();
            }

            @Override
            protected CDORevisionFactory getRevisionFactory()
            {
              return getSession().getRevisionManager().getFactory();
            }

            @Override
            protected CDOLobStore getLobStore()
            {
              return getSession().getLobStore();
            }

            @Override
            protected CDOListFactory getListFactory()
            {
              return CDOListWithElementProxiesImpl.FACTORY;
            }

            @Override
            protected boolean isXCompression()
            {
              return X_COMPRESSION;
            }
          };

          // Increase the internal tempID counter to prevent ID collisions during mapping
          int totalNewObjects = in.readXInt();
          for (int i = 0; i < totalNewObjects; i++)
          {
            createIDForNewObject(null);
          }

          Map<CDOID, CDOID> idMappings = CDOIDUtil.createMap();
          while (in.readBoolean())
          {
            if (reconstructSavepoints)
            {
              InternalCDOSavepoint savepoint = setSavepoint();
              savepoints.add(savepoint);
            }

            // Import revisions and deltas
            List<InternalCDORevision> revisions = new ArrayList<>();
            importNewRevisions(in, revisions, idMappings);
            List<InternalCDORevisionDelta> revisionDeltas = importRevisionDeltas(in);

            if (!idMappings.isEmpty())
            {
              // Re-map temp IDs
              CDOIDMapper idMapper = new CDOIDMapper(idMappings);
              for (InternalCDORevision revision : revisions)
              {
                revision.adjustReferences(idMapper);
              }

              for (InternalCDORevisionDelta delta : revisionDeltas)
              {
                delta.adjustReferences(idMapper);
              }
            }

            if (!revisions.isEmpty())
            {
              // Create new objects
              List<InternalCDOObject> newObjects = new ArrayList<>();
              for (InternalCDORevision revision : revisions)
              {
                InternalCDOObject object = newInstance(revision);
                registerObject(object);
                registerAttached(object, true);

                newObjects.add(object);
              }

              // Post-load new objects (important for legacy objects!)
              for (InternalCDOObject object : newObjects)
              {
                object.cdoInternalPostLoad();
              }
            }

            // Apply deltas
            CDOObjectMerger merger = new CDOObjectMerger();
            for (InternalCDORevisionDelta delta : revisionDeltas)
            {
              InternalCDOObject object = getObject(delta.getID());
              int oldVersion = object.cdoRevision().getVersion();

              merger.merge(object, delta);
              registerRevisionDelta(delta);
              registerDirty(object, null);

              if (delta.getVersion() < oldVersion)
              {
                setConflict(object);
              }
            }
          }
        }

        return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void importNewRevisions(CDODataInput in, List<InternalCDORevision> revisions, Map<CDOID, CDOID> idMappings) throws IOException
  {
    int size = in.readXInt();
    for (int i = 0; i < size; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)in.readCDORevision(false);

      CDOID oldID = revision.getID();
      if (oldID.isTemporary())
      {
        CDOID newID = createIDForNewObject(null);
        idMappings.put(oldID, newID);
        revision.setID(newID);
      }

      revisions.add(revision);
    }
  }

  private List<InternalCDORevisionDelta> importRevisionDeltas(CDODataInput in) throws IOException
  {
    int size = in.readXInt();
    List<InternalCDORevisionDelta> deltas = new ArrayList<>(size);
    for (int i = 0; i < size; i++)
    {
      InternalCDORevisionDelta delta = (InternalCDORevisionDelta)in.readCDORevisionDelta();
      deltas.add(delta);
    }

    return deltas;
  }

  private InternalCDOObject newInstance(InternalCDORevision revision)
  {
    InternalCDOObject object = newInstance(revision.getEClass());
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetView(this);
    object.cdoInternalSetState(CDOState.NEW);
    return object;
  }

  @Override
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllDirtyObjects();
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public Map<CDOID, CDOObject> getNewObjects()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllNewObjects();
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllBaseNewObjects();
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllRevisionDeltas();
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastSavepoint.getAllDetachedObjects();
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  protected CloseableIterator<CDOResourceNode> queryResourcesUnsynced(CDOResourceFolder folder, final String name, final boolean exactMatch)
  {
    if (!isDirty())
    {
      return super.queryResourcesUnsynced(folder, name, exactMatch);
    }

    CDOState state;
    CDOList list = null;

    if (folder == null)
    {
      CDOResource rootResource = getRootResource();
      state = rootResource.cdoState();
      if (state.isLocal())
      {
        InternalCDORevision revision = (InternalCDORevision)rootResource.cdoRevision();
        if (revision != null)
        {
          list = revision.getListOrNull(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
        }
      }
    }
    else
    {
      state = folder.cdoState();
      if (state == CDOState.TRANSIENT)
      {
        throw new CDOException("Folder " + folder + " is transient");
      }

      if (state.isLocal())
      {
        InternalCDORevision revision = (InternalCDORevision)folder.cdoRevision();
        if (revision != null)
        {
          list = revision.getListOrNull(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES);
        }
      }
    }

    if (state.isRemote())
    {
      Set<CDOID> validIDs = null;
      List<CDOResourceNode> addedNodes = null;

      if (list != null)
      {
        for (Object element : list)
        {
          if (element instanceof CDOResourceNode)
          {
            CDOResourceNode node = (CDOResourceNode)element;

            if (addedNodes == null)
            {
              addedNodes = new ArrayList<>();
            }

            addedNodes.add(node);
          }
          else
          {
            CDOID id = (CDOID)element;
            if (isObjectNew(id))
            {
              CDOResourceNode node = (CDOResourceNode)getObject(id);
              addedNodes.add(node);
            }
            else
            {
              if (validIDs == null)
              {
                validIDs = new HashSet<>();
              }

              validIDs.add(id);
            }
          }
        }
      }

      final Set<CDOID> finalValidIDs = validIDs;
      final List<CDOResourceNode> finalAddedNodes = addedNodes;
      final CloseableIterator<CDOResourceNode> delegate = super.queryResourcesUnsynced(folder, name, exactMatch);

      return new AbstractCloseableIterator<CDOResourceNode>()
      {
        private Iterator<CDOResourceNode> addedNodesIterator = finalAddedNodes == null ? null : finalAddedNodes.iterator();

        @Override
        protected Object computeNextElement()
        {
          if (addedNodesIterator != null)
          {
            if (isClosed())
            {
              return END_OF_DATA;
            }

            while (addedNodesIterator.hasNext())
            {
              CDOResourceNode node = addedNodesIterator.next();

              // Locally added node is not matched by the server; match it now.
              if (isResourceMatch(node.getName(), name, exactMatch))
              {
                return node;
              }
            }

            addedNodesIterator = null;
          }

          if (finalValidIDs != null)
          {
            while (delegate.hasNext())
            {
              CDOResourceNode node = delegate.next();
              if (finalValidIDs.contains(node.cdoID()))
              {
                if (node.cdoState().isLocal())
                {
                  // Locally changed node is not matched by the server; match it now.
                  if (isResourceMatch(node.getName(), name, exactMatch))
                  {
                    return node;
                  }
                }
                else
                {
                  // Locally unchanged node is matched by the server already; just return it.
                  return node;
                }
              }
            }
          }

          return END_OF_DATA;
        }

        @Override
        public void close()
        {
          delegate.close();
        }

        @Override
        public boolean isClosed()
        {
          return delegate.isClosed();
        }
      };
    }

    final CDOList finalList = list;

    return new AbstractCloseableIterator<CDOResourceNode>()
    {

      private Iterator<Object> listIterator = finalList == null ? null : finalList.iterator();

      @Override
      protected Object computeNextElement()
      {
        if (listIterator != null)
        {
          while (listIterator.hasNext())
          {
            Object element = listIterator.next();
            CDOResourceNode node = getResourceNode(element);

            // Locally added node is not matched by the server; match it now.
            if (isResourceMatch(node.getName(), name, exactMatch))
            {
              return node;
            }
          }

          listIterator = null;
        }

        return END_OF_DATA;
      }

      private CDOResourceNode getResourceNode(Object element)
      {
        if (element instanceof CDOResourceNode)
        {
          return (CDOResourceNode)element;
        }

        return (CDOResourceNode)getObject((CDOID)element);
      }

      @Override
      public void close()
      {
        listIterator = null;
      }

      @Override
      public boolean isClosed()
      {
        return listIterator == null;
      }

    };
  }

  @Override
  protected <T extends EObject> CloseableIterator<T> queryInstancesUnsynced(final EClass type, final boolean exact)
  {
    if (!isDirty())
    {
      return super.queryInstancesUnsynced(type, exact);
    }

    final Map<CDOID, CDOObject> newObjects = lastSavepoint.getAllNewObjects();
    final CloseableIterator<T> delegate = super.queryInstancesUnsynced(type, exact);

    return new AbstractCloseableIterator<T>()
    {
      private Iterator<CDOObject> newObjectsIterator = newObjects.isEmpty() ? null : newObjects.values().iterator();

      @Override
      protected Object computeNextElement()
      {
        if (newObjectsIterator != null)
        {
          if (isClosed())
          {
            return END_OF_DATA;
          }

          while (newObjectsIterator.hasNext())
          {
            EObject newObject = CDOUtil.getEObject(newObjectsIterator.next());

            // Locally added node is not matched by the server; match it now.
            if (exact ? type == newObject.eClass() : type.isInstance(newObject))
            {
              return newObject;
            }
          }

          newObjectsIterator = null;
        }

        while (delegate.hasNext())
        {
          T object = delegate.next();
          CDOObject cdoObject = CDOUtil.getCDOObject(object);
          System.out.println(cdoObject);

          if (!FSMUtil.isTransient(cdoObject))
          {
            return object;
          }
        }

        return END_OF_DATA;
      }

      @Override
      public void close()
      {
        delegate.close();
      }

      @Override
      public boolean isClosed()
      {
        return delegate.isClosed();
      }
    };
  }

  @Override
  protected CloseableIterator<CDOObjectReference> queryXRefsUnsynced(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    if (!isDirty())
    {
      return super.queryXRefsUnsynced(targetObjects, sourceReferences);
    }

    final Set<CDOID> targetIDs = new HashSet<>();
    final Set<EReference> relevantReferences = toSet(sourceReferences);
    final CDOQuery query = createXRefsQuery(false, targetIDs, targetObjects, sourceReferences);

    if (query.getQueryString() != null)
    {
      final Set<CDOID> localIDs = new HashSet<>();
      final List<CDOObjectReference> localXRefs = queryXRefsLocal(localIDs, targetIDs, relevantReferences);
      final CloseableIterator<CDOObjectReference> delegate = query.getResultAsync(CDOObjectReference.class);

      return new AbstractCloseableIterator<CDOObjectReference>()
      {
        private Iterator<CDOObjectReference> localXRefsIterator = localXRefs == null ? null : localXRefs.iterator();

        @Override
        protected Object computeNextElement()
        {
          if (localXRefsIterator != null)
          {
            if (isClosed())
            {
              return END_OF_DATA;
            }

            if (localXRefsIterator.hasNext())
            {
              return localXRefsIterator.next();
            }

            localXRefsIterator = null;
          }

          while (delegate.hasNext())
          {
            CDOObjectReference ref = delegate.next();
            if (!localIDs.contains(ref.getSourceID()))
            {
              return ref;
            }
          }

          return END_OF_DATA;
        }

        @Override
        public void close()
        {
          delegate.close();
        }

        @Override
        public boolean isClosed()
        {
          return delegate.isClosed();
        }
      };
    }

    List<CDOObjectReference> localXRefs = queryXRefsLocal(null, targetIDs, relevantReferences);
    if (localXRefs != null)
    {
      return new DelegatingCloseableIterator<>(localXRefs.iterator());
    }

    return AbstractCloseableIterator.emptyCloseable();
  }

  private List<CDOObjectReference> queryXRefsLocal(Set<CDOID> localIDs, Set<CDOID> targetIDs, Set<EReference> relevantReferences)
  {
    List<CDOObjectReference> refs = null;

    Map<CDOID, CDOObject> newObjects = lastSavepoint.getAllNewObjects();
    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getAllDirtyObjects();

    if (localIDs != null)
    {
      localIDs.addAll(newObjects.keySet());
      localIDs.addAll(dirtyObjects.keySet());
      localIDs.addAll(lastSavepoint.getAllDetachedObjects().keySet());
    }

    Iterator<CDOObject> it = new ComposedIterator<>( //
        newObjects.values().iterator(), //
        dirtyObjects.values().iterator());

    while (it.hasNext())
    {
      CDOObject object = it.next();
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();

      for (EReference reference : revision.getClassInfo().getAllPersistentReferences())
      {
        if (!reference.isContainer() && !reference.isContainment() && (relevantReferences == null || relevantReferences.contains(reference)))
        {
          if (reference.isMany())
          {
            CDOList list = revision.getListOrNull(reference);
            if (list != null)
            {
              int index = 0;
              for (Object value : list)
              {
                refs = addXRefLocal(refs, targetIDs, object, reference, index, value);
                ++index;
              }
            }
          }
          else
          {
            Object value = revision.getValue(reference);
            refs = addXRefLocal(refs, targetIDs, object, reference, 0, value);
          }
        }
      }
    }

    return refs;
  }

  private List<CDOObjectReference> addXRefLocal(List<CDOObjectReference> refs, Set<CDOID> targetIDs, CDOObject object, EReference reference, int index,
      Object value)
  {
    if (value != null && value != CDORevisionData.NIL)
    {
      CDOID targetID = value instanceof CDOID ? (CDOID)value : getXRefID(CDOUtil.getCDOObject((EObject)value));
      if (!CDOIDUtil.isNull(targetID) && targetIDs.contains(targetID))
      {
        CDOIDReference delegateRef = new CDOIDReference(targetID, object.cdoID(), reference, index);
        CDOObjectReference ref = new CDOObjectReferenceImpl(this, delegateRef);

        if (refs == null)
        {
          refs = new ArrayList<>();
        }

        refs.add(ref);
      }
    }

    return refs;
  }

  private CDOID getXRefID(CDOObject object)
  {
    CDOID id = object.cdoID();
    if (id == null)
    {
      CDORevisionKey key = cleanRevisions.get(object);
      if (key != null)
      {
        id = key.getID();
      }
    }

    return id;
  }

  @Override
  protected CDOID getXRefTargetID(CDOObject target)
  {
    CDORevisionKey key = cleanRevisions.get(target);
    if (key != null)
    {
      return key.getID();
    }

    return super.getXRefTargetID(target);
  }

  @Override
  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOID id = super.getID(object, onlyPersistedID);
        if (id != null)
        {
          return id;
        }

        // Don't perform the trickery that follows later in this method, if we are being called
        // indirectly through provideCDOID. This occurs when deltas or revisions are
        // being written out to a stream; in which case null must be returned (for transients) so that
        // the caller will detect a dangling reference
        if (providingCDOID.get() == Boolean.TRUE)
        {
          return null;
        }

        // The super implementation returns null for a transient (unattached) object;
        // but in a transaction, a transient object may have been attached previously.
        // So we consult the cleanRevisions if that's the case.
        CDORevisionKey revisionKey = cleanRevisions.get(object);
        if (revisionKey != null)
        {
          CDOID revisionID = revisionKey.getID();
          if (isObjectDetached(revisionID))
          {
            return revisionID;
          }
        }

        return null;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOID provideCDOID(Object idOrObject)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        try
        {
          providingCDOID.set(Boolean.TRUE);
          return super.provideCDOID(idOrObject);
        }
        finally
        {
          providingCDOID.remove();
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    return createQuery(language, queryString, context, false);
  }

  @Override
  public CDOQueryImpl createQuery(String language, String queryString, boolean considerDirtyState)
  {
    return createQuery(language, queryString, null, considerDirtyState);
  }

  @Override
  public CDOQueryImpl createQuery(String language, String queryString, Object context, boolean considerDirtyState)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOQueryImpl query = super.createQuery(language, queryString, context);
        if (considerDirtyState && isDirty())
        {
          query.setChangeSetData(getChangeSetData());
        }

        return query;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    InternalCDOSession session = getSession();
    if (session.getRepositoryInfo().getIDGenerationLocation() == CDOCommonRepository.IDGenerationLocation.STORE)
    {
      idGenerator = new TempIDGenerator();
    }
    else
    {
      idGenerator = session.getIDGenerator();
      if (idGenerator == null)
      {
        idGenerator = CDOIDGenerator.UUID;
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    providingCDOID.remove();
    options().disposeConflictResolvers();
    lastSavepoint = null;
    firstSavepoint = null;
    transactionStrategy = null;
    idGenerator = null;
    super.doDeactivate();
  }

  /**
   * Bug 298561: This override removes references to remotely detached objects that are present in any DIRTY or NEW
   * objects.
   *
   * @since 3.0
   */
  @Override
  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate( //
      List<CDORevisionKey> allChangedObjects, //
      List<CDOIDAndVersion> allDetachedObjects, //
      List<CDORevisionDelta> deltas, //
      Map<CDOObject, CDORevisionDelta> revisionDeltas, //
      Set<CDOObject> detachedObjects, //
      Map<CDOID, InternalCDORevision> oldRevisions)
  {
    if (!allDetachedObjects.isEmpty())
    {
      // Remove stale references from locally changed or new objects to remotely detached objects
      Set<CDOObject> remotelyDetachedObjects = getIdentifiedObjects(allDetachedObjects);
      removeCrossReferences(remotelyDetachedObjects, getDirtyObjects().values());
      removeCrossReferences(remotelyDetachedObjects, getNewObjects().values());
      invalidateNewChildrenOfRemotelyDetached(remotelyDetachedObjects);
    }

    // Bug 290032 - Sticky views
    InternalCDOSession session = getSession();
    if (session.isSticky())
    {
      session.clearCommittedSinceLastRefresh();
    }

    Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = super.invalidate( //
        allChangedObjects, //
        allDetachedObjects, //
        deltas, //
        revisionDeltas, //
        detachedObjects, //
        oldRevisions);

    if (!allChangedObjects.isEmpty())
    {
      // Remove stale references from remotely changed objects to locally detached objects
      Set<CDOObject> remotelyChangedObjects = getIdentifiedObjects(allChangedObjects);
      removeCrossReferences(getDetachedObjects().values(), remotelyChangedObjects);
    }

    return conflicts;
  }

  private void invalidateNewChildrenOfRemotelyDetached(Set<CDOObject> remotelyDetachedObjects)
  {
    // Test EObject.eInternalContainer()/eDirectResource() implicit references to have them return null in legacy for
    // objects in NEW state whose a direct/indirect parent has been remotely detached
    for (CDOObject referencer : getNewObjects().values())
    {

      if (CDOUtil.isLegacyObject(referencer))
      {
        InternalEObject referencerInternalEObject = (InternalEObject)referencer;
        InternalEObject eContainer = referencerInternalEObject.eInternalContainer();
        if (eContainer != null)
        {
          CDOObject containerCDOObject = CDOUtil.getCDOObject(eContainer);
          if (remotelyDetachedObjects.contains(containerCDOObject))
          {
            CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)referencer, referencer.cdoRevision());
          }
        }

        Resource.Internal eDirectResource = referencerInternalEObject.eDirectResource();
        if (eDirectResource instanceof CDOResource)
        {
          CDOObject cdoResource = (CDOObject)eDirectResource;
          if (remotelyDetachedObjects.contains(cdoResource))
          {
            CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)referencer, referencer.cdoRevision());
          }
        }
      }
    }
  }

  @Override
  protected Map<String, CDOResourceNode> collectNewResourceNodes()
  {
    Map<String, CDOResourceNode> result = new HashMap<>();

    for (CDOObject object : getNewObjects().values())
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        String path = node.getPath();
        result.put(path, node);
      }
    }

    return result;
  }

  private Set<CDOObject> getIdentifiedObjects(Collection<? extends CDOIdentifiable> identifiables)
  {
    Set<CDOObject> result = new HashSet<>();
    for (CDOIdentifiable identifiable : identifiables)
    {
      CDOID id = identifiable.getID();
      InternalCDOObject object = getObject(id, false);
      if (object != null)
      {
        result.add(object);
      }
    }

    return result;
  }

  private void removeCrossReferences(Collection<CDOObject> possibleTargets, Collection<CDOObject> referencers)
  {
    CDOStaleReferenceCleaner staleReferenceCleaner = options().getStaleReferenceCleaner();
    Collection<Pair<EStructuralFeature.Setting, EObject>> staleReferencesToClean = new LinkedList<>();

    for (CDOObject referencer : referencers)
    {
      InternalCDOObject internalReferencer = (InternalCDOObject)referencer;
      InternalCDOClassInfo referencerClassInfo = internalReferencer.cdoClassInfo();

      EContentsEList.FeatureIterator<EObject> it = getChangeableCrossReferences(referencer);
      while (it.hasNext())
      {
        EObject referencedObject = it.next();
        CDOObject referencedCDOObject = CDOUtil.getCDOObject(referencedObject);

        if (possibleTargets.contains(referencedCDOObject))
        {
          EReference reference = (EReference)it.feature();

          // In the case of DIRTY, we must investigate further: Is the referencer dirty
          // because a reference to the referencedObject was added? Only in this case
          // should we remove it. If this is not the case (i.e. it is dirty in a different
          // way), we skip it. (If the reference is not persistent, then this exception
          // doesn't apply: it must be removed for sure.)
          if (referencer.cdoState() == CDOState.DIRTY && referencerClassInfo.isPersistent(reference))
          {
            InternalCDORevision cleanRevision = cleanRevisions.get(referencer);

            if (reference.isMany())
            {
              CDOList list = cleanRevision.getListOrNull(reference);
              if (list != null)
              {
                for (Object value : list)
                {
                  if (value == referencedCDOObject.cdoID() || value == referencedObject)
                  {
                    continue;
                  }
                }
              }
            }
            else
            {
              Object value = cleanRevision.getValue(reference);
              if (value == referencedCDOObject.cdoID() || value == referencedObject)
              {
                continue;
              }
            }
          }

          EStructuralFeature.Setting setting = internalReferencer.eSetting(reference);
          staleReferencesToClean.add(Pair.create(setting, referencedObject));
        }
      }
    }

    if (!staleReferencesToClean.isEmpty())
    {
      staleReferenceCleaner.cleanStaleReferences(staleReferencesToClean);
    }
  }

  private EContentsEList.FeatureIterator<EObject> getChangeableCrossReferences(EObject object)
  {
    EClassImpl.FeatureSubsetSupplier features = (EClassImpl.FeatureSubsetSupplier)object.eClass().getEAllStructuralFeatures();

    EStructuralFeature[] crossReferences = features.crossReferences();
    if (crossReferences != null)
    {
      List<EStructuralFeature> changeableReferences = new ArrayList<>();
      for (int i = 0; i < crossReferences.length; i++)
      {
        EStructuralFeature reference = crossReferences[i];

        // Filter out derived references
        if (reference.isDerived())
        {
          continue;
        }

        // Filter out unchangeable references
        if (!reference.isChangeable())
        {
          continue;
        }

        changeableReferences.add(reference);
      }

      if (!changeableReferences.isEmpty())
      {
        EStructuralFeature[] collectedStructuralFeatures = changeableReferences.toArray(new EStructuralFeature[changeableReferences.size()]);
        return new EContentsEList.ResolvingFeatureIteratorImpl<>(object, collectedStructuralFeatures);
      }
    }

    return (EContentsEList.FeatureIterator<EObject>)ECrossReferenceEList.<EObject> emptyContentsEList().iterator();
  }

  @Override
  public long getLastCommitTime()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return lastCommitTime;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public String getCommitComment()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return commitComment;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void setCommitComment(String comment)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        commitComment = comment;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public CDOBranchPoint getCommitMergeSource()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return commitMergeSource;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void setCommitMergeSource(CDOBranchPoint mergeSource)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        commitMergeSource = mergeSource;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void setCommittables(Set<? extends EObject> committables)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        this.committables = committables;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public Set<? extends EObject> getCommittables()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return committables;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public Map<InternalCDOObject, InternalCDORevision> getCleanRevisions()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return cleanRevisions;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  protected InternalCDORevision getViewedRevision(InternalCDOObject object)
  {
    InternalCDORevision rev = super.getViewedRevision(object);

    // Bug 336590: If we have a clean revision for this object, return that instead
    if (rev != null)
    {
      InternalCDORevision cleanRev = cleanRevisions.get(object);
      if (cleanRev != null)
      {
        return cleanRev;
      }
    }

    return rev;
  }

  @Override
  protected InternalCDORevision getRevision(CDOObject object)
  {
    if (object.cdoState() == CDOState.TRANSIENT)
    {
      InternalCDORevision revision = cleanRevisions.get(object);
      if (revision == null)
      {
        throw new IllegalStateException("No revision for transient object " + object);
      }

      return revision;
    }

    return super.getRevision(object);
  }

  @Override
  protected InternalCDOLockState createUpdatedLockStateForNewObject(CDOObject object, IRWLockManager.LockType lockType, boolean on)
  {
    CheckUtil.checkState(FSMUtil.isNew(object), "Object is not in NEW state");
    CheckUtil.checkArg(lockType, "lockType");

    Map<CDOObject, CDOLockState> lockStates = getLockStates();
    InternalCDOLockState lockState = (InternalCDOLockState)lockStates.get(object);
    if (lockState == null)
    {
      CheckUtil.checkArg(on == true, "on != true");
      Object lockTarget = getLockTarget(object);
      lockState = (InternalCDOLockState)CDOLockUtil.createLockState(lockTarget);
    }
    else
    {
      lockState = (InternalCDOLockState)CDOLockUtil.copyLockState(lockState);
    }

    CDOLockOwner lockOwner = CDOLockUtil.createLockOwner(this);

    if (on)
    {
      switch (lockType)
      {
      case READ:
        lockState.addReadLockOwner(lockOwner);
        break;
      case WRITE:
        lockState.setWriteLockOwner(lockOwner);
        break;
      case OPTION:
        lockState.setWriteOptionOwner(lockOwner);
        break;
      default:
        throw new IllegalArgumentException("Unknown lock type " + lockType);
      }
    }
    else
    {
      switch (lockType)
      {
      case READ:
        lockState.removeReadLockOwner(lockOwner);
        break;
      case WRITE:
        lockState.setWriteLockOwner(null);
        break;
      case OPTION:
        lockState.setWriteOptionOwner(null);
        break;
      default:
        throw new IllegalArgumentException("Unknown lock type " + lockType);
      }
    }

    return lockState;
  }

  @Override
  protected List<CDOLockState> createUnlockedLockStatesForAllNewObjects()
  {
    List<CDOLockState> locksOnNewObjects = new LinkedList<>();
    for (CDOObject object : getNewObjects().values())
    {
      Object lockTarget = getLockTarget(object);
      CDOLockState lockState = CDOLockUtil.createLockState(lockTarget);
      locksOnNewObjects.add(lockState);
    }

    return locksOnNewObjects;
  }

  public static boolean isResourceMatch(String nodeName, String name, boolean exactMatch)
  {
    boolean useEquals = exactMatch || nodeName == null || name == null;
    return useEquals ? ObjectUtil.equals(nodeName, name) : nodeName.startsWith(name);
  }

  public static void resurrectObject(CDOObject object, CDOID id)
  {
    if (object.cdoState() != CDOState.NEW)
    {
      throw new IllegalStateException("Object is not new: " + object);
    }

    CDOID oldID = object.cdoID();
    if (oldID == id)
    {
      return;
    }

    InternalCDORevision revision = (InternalCDORevision)object.cdoRevision(false);
    revision.setID(id);

    InternalCDOTransaction transaction = (InternalCDOTransaction)object.cdoView();
    transaction.remapObject(oldID);

    Map<CDOID, CDOObject> newObjects = transaction.getLastSavepoint().getNewObjects();
    newObjects.remove(oldID);
    newObjects.put(id, object);

    CDORevision detachedRevision = getDetachedRevision(transaction, id);
    if (detachedRevision != null)
    {
      revision.setVersion(detachedRevision.getVersion());
    }
  }

  private static CDORevision getDetachedRevision(InternalCDOTransaction transaction, CDOID id)
  {
    SyntheticCDORevision[] synthetics = new SyntheticCDORevision[1];
    InternalCDORevisionManager revisionManager = transaction.getSession().getRevisionManager();

    InternalCDORevision result = revisionManager.getRevision(id, transaction, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true, synthetics);

    if (result != null)
    {
      throw new IllegalStateException("An object with the same id already exists on this branch");
    }

    return synthetics[0];
  }

  @SafeVarargs
  private static <T> Set<T> toSet(T... objects)
  {
    Set<T> result = null;
    if (objects.length != 0)
    {
      result = new HashSet<>();
      for (T object : objects)
      {
        result.add(object);
      }
    }

    return result;
  }

  /**
   * @author Eike Stepper
   */
  private final class CleanRevisionsMap extends HashMap<InternalCDOObject, InternalCDORevision>
  {
    private static final long serialVersionUID = 1L;

    public CleanRevisionsMap()
    {
    }

    @Override
    public InternalCDORevision get(Object key)
    {
      if (key instanceof EObject)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject((EObject)key);
        InternalCDORevision revision = super.get(cdoObject);
        if (revision != null)
        {
          getSession().resolveAllElementProxies(revision);
        }

        return revision;
      }

      return null;
    }

    @Override
    public InternalCDORevision remove(Object key)
    {
      if (key instanceof EObject)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject((EObject)key);
        return super.remove(cdoObject);
      }

      return null;
    }

    @Override
    public boolean containsKey(Object key)
    {
      if (key instanceof EObject)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject((EObject)key);
        return super.containsKey(cdoObject);
      }

      return false;
    }

    @Override
    public Set<InternalCDOObject> keySet()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Generates {@link CDOIDTemp temporary} ID values.
   *
   * @author Eike Stepper
   */
  private static final class TempIDGenerator implements CDOIDGenerator
  {
    private AtomicInteger lastTemporaryID = new AtomicInteger();

    public TempIDGenerator()
    {
    }

    @Override
    public CDOID generateCDOID(EObject object)
    {
      return CDOIDUtil.createTempObject(lastTemporaryID.incrementAndGet());
    }

    @Override
    public void reset()
    {
      lastTemporaryID.set(0);
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class CDOCommitContextImpl implements InternalCDOCommitContext
  {
    private InternalCDOTransaction transaction;

    /**
     * Tracks whether this commit is *actually* partial or not. (Having tx.committables != null does not in itself mean
     * that the commit will be partial, because the committables could cover all dirty/new/detached objects. But this
     * boolean gets set to reflect whether the commit will really commit less than all dirty/new/detached objects.)
     */
    private boolean isPartialCommit;

    private CDOCommitData commitData;

    private Map<CDOID, CDOObject> newObjects;

    private Map<CDOID, CDOObject> detachedObjects;

    private Map<CDOID, CDORevisionDelta> revisionDeltas;

    private Map<CDOID, CDOObject> dirtyObjects;

    private Map<ByteArrayWrapper, CDOLob<?>> lobs = new HashMap<>();

    private List<CDOLockState> locksOnNewObjects = new ArrayList<>();

    private List<CDOID> idsToUnlock = new ArrayList<>();

    public CDOCommitContextImpl(InternalCDOTransaction transaction)
    {
      this.transaction = transaction;
      calculateCommitData();
    }

    private void calculateCommitData()
    {
      OptionsImpl options = (OptionsImpl)transaction.options();

      List<CDOPackageUnit> newPackageUnits = analyzeNewPackages();

      newObjects = filterCommittables(transaction.getNewObjects());
      revisionDeltas = filterCommittables(transaction.getRevisionDeltas());
      detachedObjects = filterCommittables(transaction.getDetachedObjects());
      dirtyObjects = filterCommittables(transaction.getDirtyObjects());

      List<CDOIDAndVersion> revisions = new ArrayList<>(newObjects.size());
      List<CDORevisionKey> deltas = new ArrayList<>(revisionDeltas.size());
      List<CDOIDAndVersion> detached = new ArrayList<>(detachedObjects.size());

      Map<CDOObject, CDOLockState> lockStates = getLockStates();
      for (CDOObject newObject : newObjects.values())
      {
        revisions.add(newObject.cdoRevision());

        CDOLockState lockState = lockStates.get(newObject);
        if (lockState != null)
        {
          if (!options.isEffectiveAutoReleaseLock(newObject))
          {
            locksOnNewObjects.add(lockState);
          }
        }
      }

      for (CDORevisionDelta delta : revisionDeltas.values())
      {
        deltas.add(delta);
      }

      for (Entry<CDOID, CDOObject> entry : detachedObjects.entrySet())
      {
        CDOObject object = entry.getValue();
        InternalCDORevision cleanRevision = cleanRevisions.get(object);
        if (cleanRevision == null)
        {
          // Can happen after merged detachments
          CDORevision revision = object.cdoRevision();
          if (revision != null)
          {
            detached.add(CDOIDUtil.createIDAndVersion(revision));
          }
        }
        else if (cleanRevision.getBranch() == getBranch())
        {
          detached.add(CDOIDUtil.createIDAndVersion(cleanRevision));
        }
        else
        {
          detached.add(cleanRevision);
        }
      }

      for (CDOObject lockedObject : lockStates.keySet())
      {
        if (!FSMUtil.isTransient(lockedObject))
        {
          if (options.isEffectiveAutoReleaseLock(lockedObject))
          {
            idsToUnlock.add(lockedObject.cdoID());
          }
        }
      }

      commitData = CDOCommitInfoUtil.createCommitData(newPackageUnits, revisions, deltas, detached);
    }

    private <T> Map<CDOID, T> filterCommittables(Map<CDOID, T> map)
    {
      if (committables == null)
      {
        // No partial commit filter -- nothing to do
        return map;
      }

      Map<CDOID, T> newMap = CDOIDUtil.createMap();
      for (Entry<CDOID, T> entry : map.entrySet())
      {
        CDOID id = entry.getKey();
        CDOObject o = getObject(id);
        if (committables.contains(o))
        {
          newMap.put(id, entry.getValue());
        }
        else
        {
          isPartialCommit = true;
        }
      }

      return newMap;
    }

    @Override
    public String getUserID()
    {
      return transaction.getSession().getUserID();
    }

    @Override
    public int getViewID()
    {
      return transaction.getViewID();
    }

    @Override
    public CDOBranch getBranch()
    {
      return transaction.getBranch();
    }

    @Override
    public InternalCDOTransaction getTransaction()
    {
      return transaction;
    }

    @Override
    public boolean isPartialCommit()
    {
      return isPartialCommit;
    }

    @Override
    public String getCommitComment()
    {
      return transaction.getCommitComment();
    }

    @Override
    public CDOBranchPoint getCommitMergeSource()
    {
      return transaction.getCommitMergeSource();
    }

    @Override
    public CDOCommitData getCommitData()
    {
      return commitData;
    }

    @Override
    public Map<CDOID, CDOObject> getDirtyObjects()
    {
      return dirtyObjects;
    }

    @Override
    public Map<CDOID, CDOObject> getNewObjects()
    {
      return newObjects;
    }

    @Override
    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitData.getNewPackageUnits();
    }

    @Override
    public Map<CDOID, CDOObject> getDetachedObjects()
    {
      return detachedObjects;
    }

    @Override
    public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
    {
      return revisionDeltas;
    }

    @Override
    public Collection<CDOLob<?>> getLobs()
    {
      return lobs.values();
    }

    @Override
    @Deprecated
    public boolean isAutoReleaseLocks()
    {
      return transaction.options().isAutoReleaseLocksEnabled();
    }

    @Override
    public Collection<CDOLockState> getLocksOnNewObjects()
    {
      return locksOnNewObjects;
    }

    @Override
    public List<CDOID> getIDsToUnlock()
    {
      return idsToUnlock;
    }

    @Override
    public void preCommit()
    {
      if (isDirty())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("commit()"); //$NON-NLS-1$
        }

        CDOTransactionHandler2[] handlers = getTransactionHandlers2();
        if (handlers.length != 0)
        {
          final boolean[] modifiedAgain = { false };
          CDOTransactionHandler1 modifiedAgainHandler = new CDODefaultTransactionHandler1()
          {
            @Override
            public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
            {
              modifiedAgain[0] = true;
            }
          };

          addTransactionHandler(modifiedAgainHandler);

          try
          {
            for (int i = 0; i < handlers.length; i++)
            {
              modifiedAgain[0] = false;
              CDOTransactionHandler2 handler = handlers[i];
              handler.committingTransaction(getTransaction(), this);
              if (modifiedAgain[0])
              {
                calculateCommitData();
              }
            }
          }
          finally
          {
            removeTransactionHandler(modifiedAgainHandler);
          }
        }

        try
        {
          // TODO (CD) It might be wise to always do the checks,
          // instead of only for partial commits
          if (isPartialCommit)
          {
            new CommitIntegrityCheck(this, CommitIntegrityCheck.Style.EXCEPTION_FAST).check();
          }

          preCommit(getNewObjects(), lobs);
          preCommit(getDirtyObjects(), lobs);

          if (!lobs.isEmpty())
          {
            CDOSessionProtocol sessionProtocol = getSession().getSessionProtocol();
            List<byte[]> alreadyKnown = sessionProtocol.queryLobs(ByteArrayWrapper.toByteArray(lobs.keySet()));

            for (byte[] id : alreadyKnown)
            {
              lobs.remove(new ByteArrayWrapper(id));
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
    }

    @Override
    public void postCommit(CommitTransactionResult result)
    {
      try
      {
        InternalCDOSession session = getSession();
        long timeStamp = result.getTimeStamp();
        long previousTimeStamp = result.getPreviousTimeStamp();
        boolean clearResourcePathCache = result.isClearResourcePathCache();

        if (result.getRollbackMessage() != null)
        {
          InternalCDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
          CDOCommitInfo commitInfo = new FailureCommitInfo(commitInfoManager, timeStamp, previousTimeStamp);

          InvalidationData invalidationData = new InvalidationData();
          invalidationData.setCommitInfo(commitInfo);
          invalidationData.setSender(transaction);
          invalidationData.setClearResourcePathCache(clearResourcePathCache);
          invalidationData.setSecurityImpact(CDOProtocol.CommitNotificationInfo.IMPACT_NONE);
          invalidationData.setNewPermissions(null);
          invalidationData.setLockChangeInfo(null);

          session.invalidate(invalidationData);
          return;
        }

        CDOBranch oldBranch = getBranch();
        CDOBranch branch = result.getBranch();
        boolean branchChanged = branch != getBranch();
        if (branchChanged)
        {
          basicSetBranchPoint(branch.getHead());
        }

        for (CDOPackageUnit newPackageUnit : getNewPackageUnits())
        {
          ((InternalCDOPackageUnit)newPackageUnit).setState(CDOPackageUnit.State.LOADED);
        }

        CDOLockChangeInfo unlockChangeInfo = makeUnlockChangeInfo(result, branchChanged ? branch : null);
        CDOCommitInfo commitInfo = null;

        CommitData newCommitData = result.getNewCommitData();
        if (newCommitData == null)
        {
          Map<CDOID, CDOObject> newObjects = getNewObjects();
          postCommit(newObjects, result);

          Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
          postCommit(dirtyObjects, result);

          for (CDORevisionDelta delta : getRevisionDeltas().values())
          {
            ((InternalCDORevisionDelta)delta).adjustReferences(result.getReferenceAdjuster());
          }

          for (CDOID id : getDetachedObjects().keySet())
          {
            removeObject(id);
          }

          commitInfo = makeCommitInfo(timeStamp, previousTimeStamp);
          if (!commitInfo.isEmpty())
          {
            InvalidationData sessionInvalidationData = new InvalidationData();
            sessionInvalidationData.setCommitInfo(commitInfo);
            sessionInvalidationData.setSender(transaction);
            sessionInvalidationData.setClearResourcePathCache(clearResourcePathCache);
            sessionInvalidationData.setSecurityImpact(result.getSecurityImpact());
            sessionInvalidationData.setNewPermissions(result.getNewPermissions());
            sessionInvalidationData.setLockChangeInfo(unlockChangeInfo);

            session.invalidate(sessionInvalidationData);
          }
          else
          {
            CDOLockState[] newLockStates = result.getNewLockStates();
            if (newLockStates != null)
            {
              CDOLockChangeInfo lockChangeInfo = makeLockChangeInfo(CDOLockChangeInfo.Operation.UNLOCK, null, result.getTimeStamp(), newLockStates);
              session.handleLockNotification(lockChangeInfo, transaction, true);
            }
          }

          // Bug 290032 - Sticky views
          if (session.isSticky())
          {
            CDOBranchPoint commitBranchPoint = CDOBranchUtil.copyBranchPoint(result);

            // Note: keySet() does not work because ID mappings are not applied there!
            for (CDOObject object : newObjects.values())
            {
              session.setCommittedSinceLastRefresh(object.cdoID(), commitBranchPoint);
            }

            for (CDOID id : dirtyObjects.keySet())
            {
              session.setCommittedSinceLastRefresh(id, commitBranchPoint);
            }

            for (CDOID id : getDetachedObjects().keySet())
            {
              session.setCommittedSinceLastRefresh(id, commitBranchPoint);
            }
          }
        }
        else
        {
          applyNewCommitData(newCommitData, result.getIDMappings(), timeStamp);
        }

        CDOTransactionHandler2[] handlers = getTransactionHandlers2();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler2 handler = handlers[i];
          if (handler instanceof CDOTransactionHandler3)
          {
            CDOTransactionHandler3 handler3 = (CDOTransactionHandler3)handler;
            handler3.committedTransaction(transaction, this, commitInfo);
          }
          else
          {
            handler.committedTransaction(transaction, this);
          }
        }

        getChangeSubscriptionManager().committedTransaction(transaction, this);
        getAdapterManager().committedTransaction(transaction, this);

        if (newCommitData == null)
        {
          cleanUp(this);
        }

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          if (branchChanged)
          {
            fireViewTargetChangedEvent(oldBranch.getHead(), listeners);
          }

          Map<CDOID, CDOID> idMappings = result.getIDMappings();
          fireEvent(new FinishedEvent(idMappings), listeners);
        }

        if (unlockChangeInfo != null && isActive())
        {
          // session.handleLockNotification(unlockChangeInfo, transaction, true);
          fireLocksChangedEvent(CDOTransactionImpl.this, unlockChangeInfo);
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

    private CDOCommitInfo makeCommitInfo(long timeStamp, long previousTimeStamp)
    {
      InternalCDOSession session = getSession();
      CDOBranch branch = getBranch();
      String userID = session.getUserID();
      String comment = getCommitComment();
      CDOBranchPoint mergeSource = getCommitMergeSource();

      InternalCDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, mergeSource, commitData);
    }

    private CDOLockChangeInfo makeUnlockChangeInfo(CommitTransactionResult result, CDOBranch newBranch)
    {
      List<CDOLockState> objectsToUnlock;

      Map<CDOObject, CDOLockState> lockStates = getLockStates();
      if (lockStates.isEmpty())
      {
        return null;
      }

      objectsToUnlock = new ArrayList<>();
      Map<CDOObject, CDOLockState> newLockStates = new HashMap<>();
      Map<CDOID, CDOID> idMappings = result.getIDMappings();

      for (Map.Entry<CDOObject, CDOLockState> entry : lockStates.entrySet())
      {
        CDOObject object = entry.getKey();
        InternalCDOLockState lockState = (InternalCDOLockState)entry.getValue();

        Object lockedObject = lockState.getLockedObject();
        if (lockedObject instanceof CDOID)
        {
          CDOID id = (CDOID)lockedObject;
          CDOID newID = idMappings.get(id);
          if (newID == null)
          {
            newID = id;
          }

          if (newID != id)
          {
            lockState = (InternalCDOLockState)CDOLockUtil.copyLockState(lockState, newID);
            newLockStates.put(object, lockState);
          }
        }
        else
        {
          CDOIDAndBranch idAndBranch = (CDOIDAndBranch)lockedObject;
          CDOID id = idAndBranch.getID();
          CDOBranch branch = idAndBranch.getBranch();

          CDOID newID = idMappings.get(id);
          if (newID == null)
          {
            newID = id;
          }

          if (newID != id || newBranch != null && newBranch != branch)
          {
            CDOIDAndBranch newLockedObject = CDOIDUtil.createIDAndBranch(newID, newBranch != null ? newBranch : branch);

            lockState = (InternalCDOLockState)CDOLockUtil.copyLockState(lockState, newLockedObject);
            newLockStates.put(object, lockState);
          }
        }

        if (options().isEffectiveAutoReleaseLock(object))
        {
          lockState.dispose();
          objectsToUnlock.add(lockState);
        }
      }

      lockStates.putAll(newLockStates); // Side effect!!!

      int size = objectsToUnlock.size();
      if (size == 0)
      {
        return null;
      }

      CDOLockState[] array = objectsToUnlock.toArray(new CDOLockState[size]);
      return makeLockChangeInfo(CDOLockChangeInfo.Operation.UNLOCK, null, result.getTimeStamp(), array);
    }

    private void preCommit(Map<CDOID, CDOObject> objects, Map<ByteArrayWrapper, CDOLob<?>> lobs)
    {
      if (!objects.isEmpty())
      {
        for (CDOObject object : objects.values())
        {
          collectLobs((InternalCDORevision)object.cdoRevision(), lobs);
          ((InternalCDOObject)object).cdoInternalPreCommit();
        }
      }
    }

    private void collectLobs(InternalCDORevision revision, Map<ByteArrayWrapper, CDOLob<?>> lobs)
    {
      EStructuralFeature[] features = revision.getClassInfo().getAllPersistentFeatures();
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        if (CDOModelUtil.isLob(feature.getEType()))
        {
          CDOLob<?> lob = (CDOLob<?>)revision.getValue(feature);
          if (lob != null)
          {
            lobs.put(new ByteArrayWrapper(lob.getID()), lob);
          }
        }
      }
    }

    private void postCommit(Map<CDOID, CDOObject> objects, CommitTransactionResult result)
    {
      if (!objects.isEmpty())
      {
        CDOStateMachine.INSTANCE.commit(objects, result);
      }
    }

    private void applyNewCommitData(CommitData newCommitData, Map<CDOID, CDOID> idMappings, long timeStamp)
    {
      Map<CDOID, CDOObject> rememberedNewObjects = CDOIDUtil.createMap();
      rememberedNewObjects.putAll(newObjects);

      bypassRegistrationHandlers(() -> {
        getTransactionStrategy().rollback(transaction, firstSavepoint); // Transitions objects to CLEAN.
        waitForBaseline(timeStamp); // Transitions objects to PROXY.

        Map<CDOID, InternalCDOObject> objects = getModifiableObjects();

        for (Map.Entry<CDOID, CDOObject> entry : rememberedNewObjects.entrySet())
        {
          InternalCDOObject rememberedNewObject = (InternalCDOObject)entry.getValue();
          if (rememberedNewObject instanceof CDOObjectImpl)
          {
            CDOID tempID = entry.getKey();
            CDOID permID = idMappings.get(tempID);

            if (permID != null)
            {
              CDOObjectImpl object = (CDOObjectImpl)getObject(permID);
              ReflectUtil.invokeMethod(COPY_OBJECT_METHOD, object, rememberedNewObject);
              objects.put(permID, rememberedNewObject);
            }
          }
        }
      });
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
      return MessageFormat.format("CDOTransactionStartedEvent[source={0}]", getSource()); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FinishedEvent extends Event implements CDOTransactionFinishedEvent
  {
    private static final long serialVersionUID = 1L;

    private final Cause cause;

    private final Map<CDOID, CDOID> idMappings;

    private FinishedEvent(Map<CDOID, CDOID> idMappings)
    {
      cause = Cause.COMMITTED;
      this.idMappings = idMappings;
    }

    private FinishedEvent(boolean rolledBack)
    {
      cause = rolledBack ? Cause.ROLLED_BACK : Cause.UNDONE;
      idMappings = Collections.emptyMap();
    }

    @Override
    @Deprecated
    public Type getType()
    {
      switch (cause)
      {
      case COMMITTED:
        return Type.COMMITTED;

      case ROLLED_BACK:
      case UNDONE:
        return Type.ROLLED_BACK;

      default:
        throw new IllegalStateException("Illegal cause: " + cause);
      }
    }

    @Override
    public Cause getCause()
    {
      return cause;
    }

    @Override
    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionFinishedEvent[source={0}, cause={1}, idMappings={2}]", getSource(), //$NON-NLS-1$
          getCause(), idMappings == null ? 0 : idMappings.size());
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

    @Override
    public InternalCDOObject getConflictingObject()
    {
      return conflictingObject;
    }

    @Override
    public boolean isFirstConflict()
    {
      return firstConflict;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionConflictEvent[source={0}, conflictingObject={1}, firstConflict={2}]", //$NON-NLS-1$
          getSource(), getConflictingObject(), isFirstConflict());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountedRetryPredicate implements Predicate<Long>
  {
    private final int attempts;

    private int attempt;

    private CountedRetryPredicate(int attempts)
    {
      this.attempts = attempts;
    }

    @Override
    public boolean test(Long startMillis)
    {
      return ++attempt < attempts;
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected final class OptionsImpl extends CDOViewImpl.OptionsImpl implements CDOTransaction.Options
  {
    private CDOUndoDetector undoDetector = DEFAULT_UNDO_DETECTOR;

    private final List<CDOConflictResolver> conflictResolvers = new ArrayList<>();

    private CDOStaleReferenceCleaner staleReferenceCleaner = CDOStaleReferenceCleaner.DEFAULT;

    private boolean autoReleaseLocksEnabled = true;

    private final Map<EObject, Boolean> autoReleaseLocksExemptions = new WeakHashMap<>();

    private long commitInfoTimeout = DEFAULT_COMMIT_INFO_TIMEOUT;

    private Map<CDOID, CDORevision> attachedRevisionsMap;

    public OptionsImpl()
    {
    }

    @Override
    public CDOUndoDetector getUndoDetector()
    {
      return undoDetector;
    }

    @Override
    public void setUndoDetector(CDOUndoDetector undoDetector)
    {
      checkActive();

      if (undoDetector == null)
      {
        undoDetector = DEFAULT_UNDO_DETECTOR;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (this.undoDetector != undoDetector)
          {
            this.undoDetector = undoDetector;
            event = new UndoDetectorEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDOTransactionImpl getContainer()
    {
      return (CDOTransactionImpl)super.getContainer();
    }

    @Override
    public CDOConflictResolver[] getConflictResolvers()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return conflictResolvers.toArray(new CDOConflictResolver[conflictResolvers.size()]);
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setConflictResolvers(CDOConflictResolver[] resolvers)
    {
      checkActive();
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (CDOConflictResolver resolver : conflictResolvers)
          {
            resolver.setTransaction(null);
          }

          conflictResolvers.clear();

          for (CDOConflictResolver resolver : resolvers)
          {
            validateResolver(resolver);
            conflictResolvers.add(resolver);
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(new ConflictResolversEventImpl());
    }

    @Override
    public void addConflictResolver(CDOConflictResolver resolver)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          validateResolver(resolver);
          conflictResolvers.add(resolver);
          event = new ConflictResolversEventImpl();
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public void removeConflictResolver(CDOConflictResolver resolver)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (conflictResolvers.remove(resolver))
          {
            resolver.setTransaction(null);
            event = new ConflictResolversEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDOStaleReferenceCleaner getStaleReferenceCleaner()
    {
      return staleReferenceCleaner;
    }

    @Override
    public void setStaleReferenceCleaner(CDOStaleReferenceCleaner staleReferenceCleaner)
    {
      checkActive();

      if (staleReferenceCleaner == null)
      {
        staleReferenceCleaner = CDOStaleReferenceCleaner.DEFAULT;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (this.staleReferenceCleaner != staleReferenceCleaner)
          {
            this.staleReferenceCleaner = staleReferenceCleaner;
            event = new StaleReferenceCleanerEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    public void disposeConflictResolvers()
    {
      try
      {
        // Do not call getConflictResolvers() because that method may block!
        CDOConflictResolver[] array = conflictResolvers.toArray(new CDOConflictResolver[conflictResolvers.size()]);
        for (CDOConflictResolver resolver : array)
        {
          try
          {
            resolver.setTransaction(null);
          }
          catch (Exception ignore)
          {
          }
        }
      }
      catch (Exception ignore)
      {
      }
    }

    private void validateResolver(CDOConflictResolver resolver)
    {
      if (resolver.getTransaction() != null)
      {
        throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.17")); //$NON-NLS-1$
      }

      resolver.setTransaction(CDOTransactionImpl.this);
    }

    @Override
    public boolean isAutoReleaseLocksEnabled()
    {
      return autoReleaseLocksEnabled;
    }

    @Override
    public void setAutoReleaseLocksEnabled(boolean on)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (autoReleaseLocksEnabled != on)
          {
            autoReleaseLocksEnabled = on;
            event = new AutoReleaseLocksEnabledEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public Set<? extends EObject> getAutoReleaseLocksExemptions()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return new HashSet<>(autoReleaseLocksExemptions.keySet());
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public boolean isAutoReleaseLocksExemption(EObject object)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return autoReleaseLocksExemptions.get(CDOUtil.getCDOObject(object)) == Boolean.TRUE;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void clearAutoReleaseLocksExemptions()
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (!autoReleaseLocksExemptions.isEmpty())
          {
            autoReleaseLocksExemptions.clear();
            event = new AutoReleaseLocksExemptionsEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public void addAutoReleaseLocksExemptions(boolean recursive, EObject... objects)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (EObject object : objects)
          {
            if (autoReleaseLocksExemptions.put(CDOUtil.getCDOObject(object), Boolean.TRUE) == null)
            {
              event = new AutoReleaseLocksExemptionsEventImpl();
            }

            if (recursive)
            {
              for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
              {
                EObject child = it.next();
                if (autoReleaseLocksExemptions.put(CDOUtil.getCDOObject(child), Boolean.TRUE) == null && event == null)
                {
                  event = new AutoReleaseLocksExemptionsEventImpl();
                }
              }
            }
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public void removeAutoReleaseLocksExemptions(boolean recursive, EObject... objects)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (EObject object : objects)
          {
            if (autoReleaseLocksExemptions.remove(CDOUtil.getCDOObject(object)) != null)
            {
              event = new AutoReleaseLocksExemptionsEventImpl();
            }

            if (recursive)
            {
              for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
              {
                EObject child = it.next();
                if (autoReleaseLocksExemptions.remove(CDOUtil.getCDOObject(child)) != null && event == null)
                {
                  event = new AutoReleaseLocksExemptionsEventImpl();
                }
              }
            }
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    public boolean isEffectiveAutoReleaseLock(CDOObject newObject)
    {
      boolean effectiveAutoReleaseLock = autoReleaseLocksEnabled;
      if (autoReleaseLocksExemptions.containsKey(newObject))
      {
        effectiveAutoReleaseLock = !effectiveAutoReleaseLock;
      }

      return effectiveAutoReleaseLock;
    }

    @Override
    public Map<CDOID, CDORevision> getAttachedRevisionsMap()
    {
      return attachedRevisionsMap;
    }

    @Override
    public void setAttachedRevisionsMap(Map<CDOID, CDORevision> attachedRevisionsMap)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (this.attachedRevisionsMap != attachedRevisionsMap)
          {
            this.attachedRevisionsMap = attachedRevisionsMap;
            event = new AttachedRevisionsMapImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public long getCommitInfoTimeout()
    {
      return commitInfoTimeout;
    }

    @Override
    public void setCommitInfoTimeout(long commitInfoTimeout)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (this.commitInfoTimeout != commitInfoTimeout)
          {
            this.commitInfoTimeout = commitInfoTimeout;
            event = new CommitInfoTimeoutImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    /**
     * @author Eike Stepper
     */
    private final class UndoDetectorEventImpl extends OptionsEvent implements UndoDetectorEvent
    {
      private static final long serialVersionUID = 1L;

      public UndoDetectorEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ConflictResolversEventImpl extends OptionsEvent implements ConflictResolversEvent
    {
      private static final long serialVersionUID = 1L;

      public ConflictResolversEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class StaleReferenceCleanerEventImpl extends OptionsEvent implements StaleReferenceCleanerEvent
    {
      private static final long serialVersionUID = 1L;

      public StaleReferenceCleanerEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class AutoReleaseLocksEnabledEventImpl extends OptionsEvent implements AutoReleaseLocksEnabledEvent
    {
      private static final long serialVersionUID = 1L;

      public AutoReleaseLocksEnabledEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class AutoReleaseLocksExemptionsEventImpl extends OptionsEvent implements AutoReleaseLocksExemptionsEvent
    {
      private static final long serialVersionUID = 1L;

      public AutoReleaseLocksExemptionsEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class AttachedRevisionsMapImpl extends OptionsEvent implements AttachedRevisionsMap
    {
      private static final long serialVersionUID = 1L;

      public AttachedRevisionsMapImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CommitInfoTimeoutImpl extends OptionsEvent implements CommitInfoTimeout
    {
      private static final long serialVersionUID = 1L;

      public CommitInfoTimeoutImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }
}
