/*
 * Copyright (c) 2007-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233273
 *    Simon McDuff - bug 233490
 *    Stefan Winkler - changed order of determining audit and revision delta support.
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.branch.CDODuplicateBranchException;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.AuthorizationException;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.common.util.CurrentTimeProvider;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.server.LockingManager.LockDeltaCollector;
import org.eclipse.emf.cdo.internal.server.LockingManager.LockStateCollector;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ILobCleanup;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStore.CanHandleClientAssignedIDs;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.StoreThreadLocal.NoSessionRegisteredException;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader3;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader4;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader5;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.util.CoreOperations;
import org.eclipse.emf.cdo.spi.server.ContainerQueryHandlerProvider;
import org.eclipse.emf.cdo.spi.server.ICommitConflictResolver;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator.TreeExtension;

import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.collection.Entity.SingleNamespaceStore;
import org.eclipse.net4j.util.collection.Entity.Store;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.security.operations.OperationAuthorizer;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.MergeDataResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Repository extends Container<Object> implements InternalRepository
{
  private static final List<CDOLockDelta> NO_LOCK_DELTAS = Collections.emptyList();

  private static final List<CDOLockState> NO_LOCK_STATES = Collections.emptyList();

  private static final int UNCHUNKED = CDORevision.UNCHUNKED;

  private static final int NONE = CDORevision.DEPTH_NONE;

  private static final String PROP_UUID = "org.eclipse.emf.cdo.server.repositoryUUID"; //$NON-NLS-1$

  private static final Map<String, Repository> REPOSITORIES = new HashMap<>();

  private static final String ENTITY_NAMESPACE = "cdo/repo";

  private static final String ENTITY_NAME_PROPERTIES = "properties";

  private static final Pattern CLIENT_ENTITY_PATTERN = Pattern
      .compile(OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.internal.server.Repository.CLIENT_ENTITY_PATTERN", "cdo/client"));

  private static final boolean DISABLE_LOGIN_PEEKS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.internal.server.Repository.DISABLE_LOGIN_PEEKS");

  private static final String PROP_DISABLE_FEATURE_MAP_CHECKS = "org.eclipse.emf.cdo.internal.server.Repository.DISABLE_FEATURE_MAP_CHECKS";

  private static boolean disableFeatureMapChecks = OMPlatform.INSTANCE.isProperty(PROP_DISABLE_FEATURE_MAP_CHECKS);

  private static boolean enableFeatureMapChecks = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.internal.server.Repository.ENABLE_FEATURE_MAP_CHECKS");

  private static boolean featureMapsChecked;

  private String name;

  private String uuid;

  private InternalStore store;

  private Type type = Type.MASTER;

  private State state = State.INITIAL;

  private Object[] elements = {};

  private final IRegistry<String, Object> propertiesContainer = new HashMapRegistry.AutoCommit<>();

  private Map<String, String> properties;

  private boolean supportingLoginPeeks = !DISABLE_LOGIN_PEEKS;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private boolean supportingUnits;

  private boolean serializingCommits;

  private boolean ensuringReferentialIntegrity;

  private IDGenerationLocation idGenerationLocation;

  private String lobDigestAlgorithm;

  private CommitInfoStorage commitInfoStorage;

  private long optimisticLockingTimeout = 10000L;

  private Entity.Store entityStore;

  private Function<CDORevision, CDOID> idGenerator;

  private CDOTimeProvider timeProvider;

  /**
   * Must not be thread-bound to support XA commits.
   */
  private final Semaphore packageRegistryCommitLock = new Semaphore(1);

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private ICommitConflictResolver commitConflictResolver;

  private InternalSessionManager sessionManager;

  private InternalQueryManager queryManager;

  private InternalCommitManager commitManager;

  private InternalLockManager lockingManager;

  private InternalUnitManager unitManager;

  private IQueryHandlerProvider queryHandlerProvider;

  private IRepositoryProtector protector;

  private final List<OperationAuthorizer<ISession>> operationAuthorizers = new ArrayList<>();

  private final Map<String, Entity> entities = new HashMap<>();

  private final Map<String, Entity> clientEntities = new HashMap<>();

  private IManagedContainer container;

  private final List<ReadAccessHandler> readAccessHandlers = new ArrayList<>();

  private final List<WriteAccessHandler> writeAccessHandlers = new ArrayList<>();

  // Bug 297940
  private final TimeStampAuthority timeStampAuthority = new TimeStampAuthority(this);

  @ExcludeFromDump
  private final transient Object commitTransactionLock = new Object();

  @ExcludeFromDump
  private final transient ReadWriteLock branchingLock = new ReentrantReadWriteLock();

  private boolean skipInitialization;

  private EPackage[] initialPackages;

  private CDOID rootResourceID;

  private long lastTreeRestructuringCommit = -1;

  /**
   * This strong reference is only kept to avoid frequent tag loading.
   */
  @SuppressWarnings("unused")
  private CDOTagList tagList;

  public Repository()
  {
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public String getUUID()
  {
    return uuid;
  }

  @Override
  public InternalStore getStore()
  {
    return store;
  }

  @Override
  public void setStore(InternalStore store)
  {
    this.store = store;
  }

  @Override
  public Type getType()
  {
    return type;
  }

  @Override
  public void setType(Type type)
  {
    checkArg(type, "type"); //$NON-NLS-1$
    if (this.type != type)
    {
      changingType(this.type, type);
    }
  }

  protected void changingType(Type oldType, Type newType)
  {
    type = newType;
    fireEvent(new RepositoryTypeChangedEvent(this, oldType, newType));

    if (sessionManager != null)
    {
      sessionManager.sendRepositoryTypeNotification(oldType, newType);
    }
  }

  @Override
  public State getState()
  {
    return state;
  }

  @Override
  public void setState(State state)
  {
    checkArg(state, "state"); //$NON-NLS-1$
    if (this.state != state)
    {
      changingState(this.state, state);
    }
  }

  protected void changingState(State oldState, State newState)
  {
    state = newState;
    fireEvent(new RepositoryStateChangedEvent(this, oldState, newState));

    if (sessionManager != null)
    {
      sessionManager.sendRepositoryStateNotification(oldState, newState, getRootResourceID());
    }
  }

  @Override
  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    return CDOCommonUtil.waitWhileInitial(this, this, monitor);
  }

  @Override
  public IRegistry<String, Object> properties()
  {
    return propertiesContainer;
  }

  @Override
  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<>();
    }

    return properties;
  }

  @Override
  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  @Override
  public boolean isAuthenticating()
  {
    if (sessionManager != null)
    {
      return sessionManager.getAuthenticator() != null;
    }

    return false;
  }

  @Override
  public boolean isSupportingLoginPeeks()
  {
    return supportingLoginPeeks;
  }

  @Override
  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  @Override
  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  @Override
  public boolean isSupportingUnits()
  {
    return supportingUnits;
  }

  @Override
  public boolean isSerializingCommits()
  {
    return serializingCommits;
  }

  @Override
  public boolean isEnsuringReferentialIntegrity()
  {
    return ensuringReferentialIntegrity;
  }

  @Override
  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  @Override
  public String getLobDigestAlgorithm()
  {
    return lobDigestAlgorithm;
  }

  @Override
  public CommitInfoStorage getCommitInfoStorage()
  {
    return commitInfoStorage;
  }

  @Override
  public long getOptimisticLockingTimeout()
  {
    return optimisticLockingTimeout;
  }

  @Override
  public void setOptimisticLockingTimeout(long optimisticLockingTimeout)
  {
    this.optimisticLockingTimeout = optimisticLockingTimeout;
  }

  @Override
  public String getStoreType()
  {
    return store.getType();
  }

  @Override
  public Set<CDOID.ObjectType> getObjectIDTypes()
  {
    return store.getObjectIDTypes();
  }

  @Override
  public CDOID getRootResourceID()
  {
    return rootResourceID;
  }

  @Override
  public void setRootResourceID(CDOID rootResourceID)
  {
    this.rootResourceID = rootResourceID;
  }

  @Override
  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  @Override
  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadPackageUnit((InternalCDOPackageUnit)packageUnit);
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    if (!isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported by " + this);
    }

    long baseTimeStamp = branchInfo.getBaseTimeStamp();
    long baseTimeStampMax = timeStampAuthority.getMaxBaseTimeForNewBranch();

    if (baseTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE || baseTimeStamp > baseTimeStampMax)
    {
      baseTimeStamp = baseTimeStampMax;
      branchInfo = new BranchInfo(branchInfo.getName(), branchInfo.getBaseBranchID(), baseTimeStamp);
    }

    Lock writeLock = branchingLock.writeLock();
    writeLock.lock();

    try
    {
      authorizeOperation(CoreOperations.createBranch(branchID, //
          branchInfo.getName(), branchInfo.getBaseBranchID(), branchInfo.getBaseTimeStamp()));

      checkDuplicateBranchBase(branchInfo.getBaseBranchID(), branchInfo.getName());

      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return accessor.createBranch(branchID, branchInfo);
    }
    finally
    {
      writeLock.unlock();
    }
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranch(branchID);
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadSubBranches(branchID);
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadBranches(startID, endID, branchHandler);
  }

  @Override
  public CDOBranch[] deleteBranches(int branchID, OMMonitor monitor)
  {
    if (!isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported by " + this);
    }

    if (branchID == CDOBranch.MAIN_BRANCH_ID)
    {
      throw new IllegalArgumentException("Deleting the MAIN branch is not supported");
    }

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (!(accessor instanceof BranchLoader5))
    {
      throw new UnsupportedOperationException("Branch deletion is not supported by " + this);
    }

    Lock writeLock = branchingLock.writeLock();
    writeLock.lock();

    try
    {
      CDOBranchUtil.forEachBranchInTree(getBranchManager().getBranch(branchID), //
          b -> authorizeOperation(CoreOperations.deleteBranch(b.getID())));

      CDOBranch[] branches = ((BranchLoader5)accessor).deleteBranches(branchID, monitor);

      // Close views that "look" at one of the deleted branches.
      for (InternalSession session : sessionManager.getSessions())
      {
        for (InternalView view : session.getViews())
        {
          CDOBranch viewBranch = view.getBranch();
          for (CDOBranch branch : branches)
          {
            if (viewBranch == branch)
            {
              view.close();
            }
          }
        }
      }

      InternalCDORevisionCache revisionCache = getRevisionManager().getCache();
      revisionCache.removeRevisions(branches);

      return branches;
    }
    finally
    {
      writeLock.unlock();
    }
  }

  @Override
  public void renameBranch(int branchID, String oldName, String newName) throws CDODuplicateBranchException
  {
    if (!isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported by " + this);
    }

    if (branchID == CDOBranch.MAIN_BRANCH_ID)
    {
      throw new IllegalArgumentException("Renaming the MAIN branch is not supported");
    }

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (!(accessor instanceof BranchLoader3))
    {
      throw new UnsupportedOperationException("Branch renaming is not supported by " + this);
    }

    Lock writeLock = branchingLock.writeLock();
    writeLock.lock();

    try
    {
      authorizeOperation(CoreOperations.renameBranch(branchID, newName));

      checkDuplicateBranch(branchID, newName);

      ((BranchLoader3)accessor).renameBranch(branchID, oldName, newName);
    }
    finally
    {
      writeLock.unlock();
    }
  }

  private void checkDuplicateBranch(int branchID, String name) throws CDODuplicateBranchException
  {
    CDOBranch branch = branchManager.getBranch(branchID);
    CDOBranch baseBranch = branch.getBase().getBranch();
    int baseBranchID = baseBranch.getID();

    checkDuplicateBranchBase(baseBranchID, name);
  }

  private void checkDuplicateBranchBase(int baseBranchID, String name) throws CDODuplicateBranchException
  {
    CDOBranch baseBranch = branchManager.getBranch(baseBranchID);
    CDOBranch existingBranch = baseBranch.getBranch(name);
    if (existingBranch != null && !existingBranch.isDeleted())
    {
      throw new CDODuplicateBranchException(existingBranch);
    }
  }

  @Override
  public CDOBranchPoint changeTag(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    if (isSupportingAudits())
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      if (accessor instanceof BranchLoader4)
      {
        Lock writeLock = branchingLock.writeLock();
        writeLock.lock();

        try
        {
          if (oldName == null && branchPoint == null)
          {
            // Augment missing tag creation branch point.
            branchPoint = branchManager.getMainBranch().getHead();
          }

          if (branchPoint != null && branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
          {
            // Augment missing time stamp.
            branchPoint = branchPoint.getBranch().getPoint(getTimeStamp());
          }

          switch (InternalCDOBranchManager.getTagChangeKind(oldName, newName, branchPoint))
          {
          case CREATED:
            authorizeOperation(CoreOperations.createTag(newName, branchPoint.getBranch().getID(), branchPoint.getTimeStamp()));
            break;

          case RENAMED:
            authorizeOperation(CoreOperations.renameTag(oldName, newName));
            break;

          case MOVED:
            authorizeOperation(CoreOperations.moveTag(oldName, branchPoint.getBranch().getID(), branchPoint.getTimeStamp()));
            break;

          case DELETED:
            authorizeOperation(CoreOperations.deleteTag(oldName));
            break;

          }

          ((BranchLoader4)accessor).changeTag(modCount, oldName, newName, branchPoint);

          InternalSession sender = null;

          try
          {
            sender = StoreThreadLocal.getSession();
          }
          catch (NoSessionRegisteredException ignore)
          {
            //$FALL-THROUGH$
          }

          // The branch manager's modCount will be bumped later by the caller of this method.
          int newModCount = modCount.get() + 1;

          sessionManager.sendTagNotification(sender, newModCount, oldName, newName, branchPoint);

          return branchPoint;
        }
        finally
        {
          writeLock.unlock();
        }
      }
    }

    throw new UnsupportedOperationException("Branch tagging is not supported by " + this);
  }

  @Override
  public void loadTags(String name, Consumer<BranchInfo> handler)
  {
    if (isSupportingAudits())
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      if (accessor instanceof BranchLoader4)
      {
        ((BranchLoader4)accessor).loadTags(name, handler);
      }
    }
  }

  @Override
  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.loadCommitInfos(branch, startTime, endTime, handler);
  }

  @Override
  public CDOCommitData loadCommitData(long timeStamp)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.loadCommitData(timeStamp);
  }

  @Override
  public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean prefetchLockStates)
  {
    for (RevisionInfo info : infos)
    {
      CDOID id = info.getID();
      RevisionInfo.Type type = info.getType();
      switch (type)
      {
      case AVAILABLE_NORMAL: // direct == false
      {
        RevisionInfo.Available.Normal availableInfo = (RevisionInfo.Available.Normal)info;
        checkArg(availableInfo.isDirect() == false, "Load is not needed"); //$NON-NLS-1$
        break;
      }

      case AVAILABLE_POINTER: // direct == false || target == null
      {
        RevisionInfo.Available.Pointer pointerInfo = (RevisionInfo.Available.Pointer)info;
        boolean needsTarget = !pointerInfo.hasTarget();
        checkArg(pointerInfo.isDirect() == false || needsTarget, "Load is not needed"); //$NON-NLS-1$

        if (needsTarget)
        {
          CDOBranchVersion targetBranchVersion = pointerInfo.getTargetBranchVersion();
          InternalCDORevision target = loadRevisionByVersion(id, targetBranchVersion, referenceChunk);
          PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, pointerInfo.getAvailableBranchVersion().getBranch(),
              CDORevision.UNSPECIFIED_DATE, target);

          info.setResult(target);
          info.setSynthetic(pointer);
          continue;
        }

        break;
      }

      case AVAILABLE_DETACHED: // direct == false
      {
        RevisionInfo.Available.Detached detachedInfo = (RevisionInfo.Available.Detached)info;
        checkArg(detachedInfo.isDirect() == false, "Load is not needed"); //$NON-NLS-1$
        break;
      }

      case MISSING:
      {
        break;
      }

      default:
        throw new IllegalStateException("Invalid revision info type: " + type);
      }

      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision == null)
      {
        if (isSupportingAudits())
        {
          InternalCDORevision target = loadRevisionTarget(id, branchPoint, referenceChunk, accessor);
          if (target != null)
          {
            target = normalizeRevision(target, info, referenceChunk);

            CDOBranch branch = branchPoint.getBranch();
            long revised = loadRevisionRevised(id, branch);
            PointerCDORevision pointer = new PointerCDORevision(target.getEClass(), id, branch, revised, target);
            info.setSynthetic(pointer);
          }

          info.setResult(target);
        }
        else
        {
          DetachedCDORevision detachedRevision = new DetachedCDORevision(EcorePackage.Literals.ECLASS, id, branchPoint.getBranch(), 0,
              CDORevision.UNSPECIFIED_DATE);
          info.setSynthetic(detachedRevision);
        }
      }
      else if (revision instanceof DetachedCDORevision)
      {
        DetachedCDORevision detached = (DetachedCDORevision)revision;
        info.setSynthetic(detached);
      }
      else
      {
        revision.freeze();

        revision = normalizeRevision(revision, info, referenceChunk);
        info.setResult(revision);
      }
    }

    return null;
  }

  private InternalCDORevision normalizeRevision(InternalCDORevision revision, RevisionInfo info, int referenceChunk)
  {
    if (info instanceof RevisionInfo.Available)
    {
      RevisionInfo.Available availableInfo = (RevisionInfo.Available)info;

      CDOBranchVersion availableBranchVersion = availableInfo.getAvailableBranchVersion();
      if (availableBranchVersion instanceof BaseCDORevision)
      {
        BaseCDORevision availableRevision = (BaseCDORevision)availableBranchVersion;
        if (availableRevision.equals(revision))
        {
          ensureChunks(availableRevision, referenceChunk);
          return availableRevision;
        }
      }
    }

    if (referenceChunk == UNCHUNKED)
    {
      revision.setUnchunked();
    }

    return revision;
  }

  private InternalCDORevision loadRevisionTarget(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, IStoreAccessor accessor)
  {
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      InternalCDORevision revision = accessor.readRevision(id, branchPoint, referenceChunk, revisionManager);
      if (revision != null)
      {
        revision.freeze();
        return revision;
      }
    }

    return null;
  }

  private long loadRevisionRevised(CDOID id, CDOBranch branch)
  {
    InternalCDORevision revision = loadRevisionByVersion(id, branch.getVersion(CDORevision.FIRST_VERSION), UNCHUNKED);
    if (revision != null)
    {
      return revision.getTimeStamp() - 1;
    }

    return CDORevision.UNSPECIFIED_DATE;
  }

  @Override
  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return accessor.readRevisionByVersion(id, branchVersion, referenceChunk, revisionManager);
  }

  @Override
  public CDOBranchPointRange loadObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
  {
    CDORevision revision = revisionManager.getRevision(id, branchPoint, UNCHUNKED, NONE, true);
    if (revision == null)
    {
      return null;
    }

    CDORevision firstRevision = getFirstRevision(id, revision);
    if (firstRevision == null)
    {
      return null;
    }

    CDOBranchPoint lastPoint = getLastBranchPoint(revision, branchPoint);
    return CDOBranchUtil.createRange(firstRevision, lastPoint);
  }

  private CDORevision getFirstRevision(CDOID id, CDORevision revision)
  {
    CDOBranch branch = revision.getBranch();

    for (int version = revision.getVersion() - 1; version >= CDOBranchVersion.FIRST_VERSION; --version)
    {
      CDORevision rev = revisionManager.getRevisionByVersion(id, branch.getVersion(version), UNCHUNKED, true);
      if (rev == null)
      {
        return revision;
      }

      revision = rev;
    }

    if (!branch.isMainBranch())
    {
      CDOBranchPoint base = branch.getBase();
      CDORevision baseRevision = revisionManager.getRevision(id, base, UNCHUNKED, NONE, true);
      if (baseRevision != null)
      {
        return getFirstRevision(id, baseRevision);
      }
    }

    return revision;
  }

  private CDOBranchPoint getLastBranchPoint(CDORevision revision, CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    if (revision.getBranch() != branch)
    {
      return branch.getHead();
    }

    CDOID id = revision.getID();
    for (int version = revision.getVersion() + 1; version <= Integer.MAX_VALUE; ++version)
    {
      if (revision.getRevised() == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        break;
      }

      CDORevision rev = revisionManager.getRevisionByVersion(id, branch.getVersion(version), UNCHUNKED, true);
      if (rev == null)
      {
        break;
      }

      revision = rev;
    }

    return branch.getPoint(revision.getRevised());
  }

  /**
   * @deprecated Not used.
   */
  @Deprecated
  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreAccessor accessor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ensureChunks(InternalCDORevision revision)
  {
    ensureChunks(revision, UNCHUNKED);
  }

  @Override
  public void ensureChunks(InternalCDORevision revision, int chunkSize)
  {
    if (revision.isUnchunked())
    {
      return;
    }

    IStoreAccessor accessor = null;
    boolean unchunked = true;
    for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentFeatures())
    {
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getListOrNull(feature);
        if (list != null)
        {
          int size = list.size();
          if (size != 0)
          {
            int chunkSizeToUse = chunkSize;
            if (chunkSizeToUse == UNCHUNKED)
            {
              chunkSizeToUse = size;
            }

            int chunkEnd = Math.min(chunkSizeToUse, size);
            accessor = ensureChunk(revision, feature, accessor, list, 0, chunkEnd);

            if (unchunked)
            {
              for (int i = chunkEnd; i < size; i++)
              {
                if (list.get(i) == InternalCDOList.UNINITIALIZED)
                {
                  unchunked = false;
                  break;
                }
              }
            }
          }
        }
      }
    }

    if (unchunked)
    {
      revision.setUnchunked();
    }
  }

  @Override
  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart, int chunkEnd)
  {
    if (!revision.isUnchunked())
    {
      MoveableList<Object> list = revision.getListOrNull(feature);
      if (list == null)
      {
        return null;
      }

      chunkEnd = Math.min(chunkEnd, list.size());
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      ensureChunk(revision, feature, accessor, list, chunkStart, chunkEnd);

      // TODO Expensive: if the revision is unchunked all lists/elements must be visited
      if (isUnchunked(revision))
      {
        revision.setUnchunked();
      }

      return accessor;
    }

    return null;
  }

  private boolean isUnchunked(InternalCDORevision revision)
  {
    for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentFeatures())
    {
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getListOrNull(feature);
        if (list != null)
        {
          int size = list.size();
          for (int i = 0; i < size; i++)
          {
            if (list.get(i) == InternalCDOList.UNINITIALIZED)
            {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

  protected IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, IStoreAccessor accessor, MoveableList<Object> list,
      int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == InternalCDOList.UNINITIALIZED)
      {
        if (fromIndex == -1)
        {
          fromIndex = j;
        }
      }
      else
      {
        if (fromIndex != -1)
        {
          if (chunkReader == null)
          {
            if (accessor == null)
            {
              accessor = StoreThreadLocal.getAccessor();
            }

            chunkReader = accessor.createChunkReader(revision, feature);
          }

          int toIndex = j;
          if (fromIndex == toIndex - 1)
          {
            chunkReader.addSimpleChunk(fromIndex);
          }
          else
          {
            chunkReader.addRangedChunk(fromIndex, toIndex);
          }

          fromIndex = -1;
        }
      }
    }

    // Add last chunk
    if (fromIndex != -1)
    {
      if (chunkReader == null)
      {
        if (accessor == null)
        {
          accessor = StoreThreadLocal.getAccessor();
        }

        chunkReader = accessor.createChunkReader(revision, feature);
      }

      int toIndex = chunkEnd;
      if (fromIndex == toIndex - 1)
      {
        chunkReader.addSimpleChunk(fromIndex);
      }
      else
      {
        chunkReader.addRangedChunk(fromIndex, toIndex);
      }
    }

    if (chunkReader != null)
    {
      InternalCDOList cdoList = list instanceof InternalCDOList ? (InternalCDOList)list : null;

      List<Chunk> chunks = chunkReader.executeRead();
      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int indexInChunk = 0; indexInChunk < chunk.size(); indexInChunk++)
        {
          Object id = chunk.get(indexInChunk);
          if (cdoList != null)
          {
            cdoList.setWithoutFrozenCheck(startIndex + indexInChunk, id);
          }
          else
          {
            list.set(startIndex + indexInChunk, id);
          }
        }
      }
    }

    return accessor;
  }

  @Override
  public void addEntity(Entity entity)
  {
    checkInactive();
    entities.put(entity.id(), entity);

    if (CLIENT_ENTITY_PATTERN.matcher(entity.namespace()).matches())
    {
      clientEntities.put(entity.id(), entity);
    }
  }

  @Override
  public Map<String, Entity> getEntities()
  {
    return Collections.unmodifiableMap(entities);
  }

  @Override
  public Map<String, Entity> getClientEntities()
  {
    return Collections.unmodifiableMap(clientEntities);
  }

  @Override
  public Entity.Store getEntityStore()
  {
    return entityStore;
  }

  @Override
  public void setEntityStore(Entity.Store entityStore)
  {
    checkInactive();
    this.entityStore = entityStore;
  }

  @Override
  public void addEntityStore(Store entityStore)
  {
    Entity.ComposedStore composedStore;

    if (this.entityStore instanceof Entity.ComposedStore)
    {
      composedStore = (Entity.ComposedStore)this.entityStore;
    }
    else
    {
      composedStore = new Entity.ComposedStore();
      composedStore.addStore(this.entityStore);
      this.entityStore = composedStore;
    }

    composedStore.addStore(entityStore);
  }

  @Override
  public CDOTimeProvider getTimeProvider()
  {
    return timeProvider;
  }

  @Override
  public void setTimeProvider(CDOTimeProvider timeProvider)
  {
    checkInactive();
    this.timeProvider = timeProvider;
  }

  @Override
  public Function<CDORevision, CDOID> getIDGenerator()
  {
    return idGenerator;
  }

  @Override
  public void setIDGenerator(Function<CDORevision, CDOID> idGenerator)
  {
    this.idGenerator = idGenerator;
  }

  @Override
  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext)
  {
    if (considerCommitContext)
    {
      IStoreAccessor.CommitContext commitContext = StoreThreadLocal.getCommitContext();
      if (commitContext != null)
      {
        InternalCDOPackageRegistry contextualPackageRegistry = commitContext.getPackageRegistry();
        if (contextualPackageRegistry != null)
        {
          return contextualPackageRegistry;
        }
      }
    }

    return packageRegistry;
  }

  @Override
  public Semaphore getPackageRegistryCommitLock()
  {
    return packageRegistryCommitLock;
  }

  @Override
  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getPackageRegistry(true);
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    checkInactive();
    this.packageRegistry = packageRegistry;
  }

  @Override
  public InternalSessionManager getSessionManager()
  {
    return sessionManager;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setSessionManager(InternalSessionManager sessionManager)
  {
    checkInactive();
    this.sessionManager = sessionManager;
  }

  @Override
  public InternalUnitManager getUnitManager()
  {
    return unitManager;
  }

  @Override
  public void setUnitManager(InternalUnitManager unitManager)
  {
    checkInactive();
    this.unitManager = unitManager;
  }

  @Override
  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  @Override
  public void setBranchManager(InternalCDOBranchManager branchManager)
  {
    checkInactive();
    this.branchManager = branchManager;
  }

  @Override
  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
  {
    checkInactive();
    this.commitInfoManager = commitInfoManager;
  }

  @Override
  public ICommitConflictResolver getCommitConflictResolver()
  {
    return commitConflictResolver;
  }

  @Override
  public void setCommitConflictResolver(ICommitConflictResolver commitConflictResolver)
  {
    checkInactive();
    this.commitConflictResolver = commitConflictResolver;
  }

  @Override
  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    checkInactive();
    this.revisionManager = revisionManager;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalQueryManager getQueryManager()
  {
    return queryManager;
  }

  /**
   * @since 2.0
   */
  public void setQueryManager(InternalQueryManager queryManager)
  {
    checkInactive();
    this.queryManager = queryManager;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCommitManager getCommitManager()
  {
    return commitManager;
  }

  /**
   * @since 2.0
   */
  public void setCommitManager(InternalCommitManager commitManager)
  {
    checkInactive();
    this.commitManager = commitManager;
  }

  @Override
  public InternalLockManager getLockingManager()
  {
    return lockingManager;
  }

  /**
   * @since 2.0
   */
  public void setLockingManager(InternalLockManager lockingManager)
  {
    checkInactive();
    this.lockingManager = lockingManager;
  }

  @Override
  public IRepositoryProtector getProtector()
  {
    return protector;
  }

  @Override
  public void setProtector(IRepositoryProtector protector)
  {
    if (!isSecurityManagerWrapper(protector))
    {
      checkInactive();
    }

    this.protector = protector;
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return new TransactionCommitContext(transaction);
  }

  @Override
  public long getLastCommitTimeStamp()
  {
    return timeStampAuthority.getLastFinishedTimeStamp();
  }

  @Override
  public void setLastCommitTimeStamp(long lastCommitTimeStamp)
  {
    timeStampAuthority.setLastFinishedTimeStamp(lastCommitTimeStamp);
  }

  @Override
  public long waitForCommit(long timeout)
  {
    return timeStampAuthority.waitForCommit(timeout);
  }

  @Override
  public long[] createCommitTimeStamp(OMMonitor monitor)
  {
    return timeStampAuthority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, monitor);
  }

  @Override
  public long[] forceCommitTimeStamp(long override, OMMonitor monitor)
  {
    return timeStampAuthority.startCommit(override, monitor);
  }

  @Override
  public void endCommit(long timestamp)
  {
    timeStampAuthority.endCommit(timestamp);
  }

  @Override
  public void failCommit(long timestamp)
  {
    timeStampAuthority.failCommit(timestamp);
  }

  @Override
  public void executeOutsideStartCommit(Runnable runnable)
  {
    synchronized (timeStampAuthority)
    {
      runnable.run();
    }
  }

  @Override
  public void commit(InternalCommitContext commitContext, OMMonitor monitor)
  {
    if (!disableFeatureMapChecks)
    {
      InternalCDORevision[] newObjects = commitContext.getNewObjects();
      if (newObjects != null && newObjects.length != 0)
      {
        for (int i = 0; i < newObjects.length; i++)
        {
          InternalCDORevision revision = newObjects[i];
          if (revision.getClassInfo().hasPersistentFeatureMaps())
          {
            throw new CDOException(revision + " contains a feature map");
          }
        }
      }

      if (!featureMapsChecked)
      {
        featureMapsChecked = true;

        if (!enableFeatureMapChecks)
        {
          OM.LOG.info("If no model contains feature maps commit performance can be slightly increased by specifying -D" //
              + PROP_DISABLE_FEATURE_MAP_CHECKS + "=true");
        }
      }
    }

    Lock readLock = branchingLock.readLock();
    readLock.lock();

    try
    {
      if (commitContext.isTreeRestructuring())
      {
        synchronized (commitTransactionLock)
        {
          commitContext.setLastTreeRestructuringCommit(lastTreeRestructuringCommit);
          commitUnsynced(commitContext, monitor);
          lastTreeRestructuringCommit = commitContext.getTimeStamp();
        }
      }
      else if (serializingCommits)
      {
        synchronized (commitTransactionLock)
        {
          commitUnsynced(commitContext, monitor);
        }
      }
      else
      {
        commitUnsynced(commitContext, monitor);
      }
    }
    finally
    {
      readLock.unlock();
    }
  }

  protected void commitUnsynced(InternalCommitContext commitContext, OMMonitor monitor)
  {
    ProgressDistributor distributor = store.getIndicatingCommitDistributor();
    distributor.run(InternalCommitContext.OPS, commitContext, monitor);
  }

  @Override
  public void sendCommitNotification(CommitNotificationInfo info)
  {
    CDOCommitInfo commitInfo = info.getCommitInfo();
    boolean isFailureCommitInfo = commitInfo.getBranch() == null;

    if (isFailureCommitInfo || !commitInfo.isEmpty() || info.getLockChangeInfo() != null)
    {
      sessionManager.sendCommitNotification(info);
      commitInfoManager.notifyCommitInfoHandlers(commitInfo);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public IQueryHandlerProvider getQueryHandlerProvider()
  {
    return queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider)
  {
    this.queryHandlerProvider = queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String language = info.getQueryLanguage();
    if (CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES.equals(language))
    {
      return new ResourcesQueryHandler();
    }

    if (CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES.equals(language))
    {
      return new InstancesQueryHandler();
    }

    if (CDOProtocolConstants.QUERY_LANGUAGE_XREFS.equals(language))
    {
      return new XRefsQueryHandler();
    }

    if (CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT.equals(language))
    {
      return new FingerPrintQueryHandler();
    }

    IStoreAccessor storeAccessor = StoreThreadLocal.getAccessor();
    if (storeAccessor != null)
    {
      IQueryHandler handler = storeAccessor.getQueryHandler(info);
      if (handler != null)
      {
        return handler;
      }
    }

    if (queryHandlerProvider == null)
    {
      IManagedContainer container = getContainer();
      queryHandlerProvider = new ContainerQueryHandlerProvider(container);
    }

    IQueryHandler handler = queryHandlerProvider.getQueryHandler(info);
    if (handler != null)
    {
      return handler;
    }

    return null;
  }

  @Override
  public void addOperationAuthorizer(OperationAuthorizer<ISession> operationAuthorizer)
  {
    checkInactive();
    operationAuthorizers.add(operationAuthorizer);
  }

  @Override
  public boolean isAuthorizingOperations()
  {
    return !operationAuthorizers.isEmpty();
  }

  @Override
  public String authorizeOperation(ISession session, AuthorizableOperation operation)
  {
    if (session == null)
    {
      return "No session";
    }

    for (OperationAuthorizer<ISession> authorizer : operationAuthorizers)
    {
      try
      {
        String veto = authorizer.authorizeOperation(session, operation);
        if (veto != null)
        {
          return veto;
        }
      }
      catch (Error ex)
      {
        throw ex;
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
        return "Error: " + t.getLocalizedMessage();
      }
    }

    // No veto -> authorized.
    return null;
  }

  protected final void authorizeOperation(AuthorizableOperation operation) throws AuthorizationException
  {
    if (isAuthorizingOperations())
    {
      InternalSession session = StoreThreadLocal.getSession();

      String veto = authorizeOperation(session, operation);
      if (veto != null)
      {
        throw new AuthorizationException(veto);
      }
    }
  }

  @Override
  public IManagedContainer getContainer()
  {
    if (container == null)
    {
      return IPluginContainer.INSTANCE;
    }

    return container;
  }

  @Override
  public void setContainer(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    IManagedContainer container = getContainer();
    return ConcurrencyUtil.getExecutorService(container);
  }

  @Override
  public Object[] getElements()
  {
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  /**
   * @since 2.0
   */
  @Override
  public long getCreationTime()
  {
    return store.getCreationTime();
  }

  /**
   * @since 2.0
   */
  @Override
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    long creationTimeStamp = getCreationTime();
    if (timeStamp < creationTimeStamp)
    {
      throw new IllegalArgumentException(MessageFormat.format("timeStamp ({0}) < repository creation time ({1})", //$NON-NLS-1$
          CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(creationTimeStamp)));
    }

    long currentTimeStamp = getTimeStamp();
    if (timeStamp > currentTimeStamp)
    {
      throw new IllegalArgumentException(MessageFormat.format("timeStamp ({0}) > current time ({1})", //$NON-NLS-1$
          CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(currentTimeStamp)));
    }
  }

  @Override
  public long getTimeStamp()
  {
    return timeProvider.getTimeStamp();
  }

  @Override
  public Set<Handler> getHandlers()
  {
    Set<Handler> handlers = new HashSet<>();

    synchronized (readAccessHandlers)
    {
      handlers.addAll(readAccessHandlers);
    }

    synchronized (writeAccessHandlers)
    {
      handlers.addAll(writeAccessHandlers);
    }

    return handlers;
  }

  /**
   * @since 2.0
   */
  @Override
  public void addHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        if (!readAccessHandlers.contains(handler))
        {
          readAccessHandlers.add((ReadAccessHandler)handler);
        }
      }
    }

    if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        if (!writeAccessHandlers.contains(handler))
        {
          writeAccessHandlers.add((WriteAccessHandler)handler);
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void removeHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        readAccessHandlers.remove(handler);
      }
    }

    if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        writeAccessHandlers.remove(handler);
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions, List<CDORevision> additionalRevisions)
  {
    ReadAccessHandler[] handlers;
    synchronized (readAccessHandlers)
    {
      int size = readAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = readAccessHandlers.toArray(new ReadAccessHandler[size]);
    }

    for (ReadAccessHandler handler : handlers)
    {
      // Do *not* protect against unchecked exceptions from handlers!
      handler.handleRevisionsBeforeSending(session, revisions, additionalRevisions);
    }
  }

  @Override
  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext, boolean beforeCommit, OMMonitor monitor)
  {
    WriteAccessHandler[] handlers;
    synchronized (writeAccessHandlers)
    {
      int size = writeAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = writeAccessHandlers.toArray(new WriteAccessHandler[size]);
    }

    try
    {
      monitor.begin(handlers.length);
      for (WriteAccessHandler handler : handlers)
      {
        try
        {
          if (beforeCommit)
          {
            handler.handleTransactionBeforeCommitting(transaction, commitContext, monitor.fork());
          }
          else
          {
            handler.handleTransactionAfterCommitted(transaction, commitContext, monitor.fork());
          }
        }
        catch (RuntimeException ex)
        {
          if (!beforeCommit)
          {
            OM.LOG.error(ex);
          }
          else
          {
            // Do *not* protect against unchecked exceptions from handlers on before case!
            throw ex;
          }
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void setInitialPackages(EPackage... initialPackages)
  {
    checkInactive();
    this.initialPackages = initialPackages;
  }

  @Override
  public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime) throws IOException
  {
    final int fromBranchID = lastReplicatedBranchID + 1;
    final int toBranchID = store.getLastBranchID();

    final long fromCommitTime = lastReplicatedCommitTime + 1L;
    final long toCommitTime = store.getLastCommitTime();

    out.writeXInt(toBranchID);
    out.writeXLong(toCommitTime);

    IStoreAccessor.Raw accessor = (IStoreAccessor.Raw)StoreThreadLocal.getAccessor();
    accessor.rawExport(out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);

    return new CDOReplicationInfo()
    {
      @Override
      public int getLastReplicatedBranchID()
      {
        return toBranchID;
      }

      @Override
      public long getLastReplicatedCommitTime()
      {
        return toCommitTime;
      }

      @Override
      public String[] getLockAreaIDs()
      {
        return null; // TODO (CD) Raw replication of lockAreas
      }
    };
  }

  @Override
  public void replicate(CDOReplicationContext context)
  {
    int startID = context.getLastReplicatedBranchID() + 1;
    branchManager.getBranches(startID, 0, context);

    long startTime = context.getLastReplicatedCommitTime();
    commitInfoManager.getCommitInfos(null, startTime + 1L, CDOBranchPoint.UNSPECIFIED_DATE, context);

    getLockingManager().getLockAreas(null, context);
  }

  @Override
  public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    CDOChangeSetSegment[] segments = CDOBranchUtil.isContainedBy(startPoint, endPoint) ? //
        CDOChangeSetSegment.createFrom(startPoint, endPoint) : //
        CDOChangeSetSegment.createFrom(endPoint, startPoint);

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    Set<CDOID> ids = accessor.readChangeSet(new Monitor(), segments);

    return CDORevisionUtil.createChangeSetData(ids, startPoint, endPoint, revisionManager);
  }

  @Override
  public MergeDataResult getMergeData2(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo,
      CDORevisionAvailabilityInfo targetBaseInfo, CDORevisionAvailabilityInfo sourceBaseInfo, OMMonitor monitor)
  {
    CDOBranchPoint target = targetInfo.getBranchPoint();
    CDOBranchPoint source = sourceInfo.getBranchPoint();

    monitor.begin(5);

    try
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();

      MergeDataResult result = new MergeDataResult();
      Set<CDOID> targetIDs = result.getTargetIDs();
      Set<CDOID> sourceIDs = result.getSourceIDs();

      if (targetBaseInfo == null && sourceBaseInfo == null)
      {
        if (CDOBranchUtil.isContainedBy(source, target))
        {
          // This is a "compare" case, see CDOSessionImpl.compareRevisions().
          targetIDs.addAll(accessor.readChangeSet(monitor.fork(), CDOChangeSetSegment.createFrom(source, target)));
        }
        else if (CDOBranchUtil.isContainedBy(target, source))
        {
          // This is a "compare" case, see CDOSessionImpl.compareRevisions().
          targetIDs.addAll(accessor.readChangeSet(monitor.fork(), CDOChangeSetSegment.createFrom(target, source)));
        }
        else
        {
          CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(target, source);
          targetIDs.addAll(accessor.readChangeSet(monitor.fork(), CDOChangeSetSegment.createFrom(ancestor, target)));
          sourceIDs.addAll(accessor.readChangeSet(monitor.fork(), CDOChangeSetSegment.createFrom(ancestor, source)));
        }
      }
      else
      {
        CDOChangeSetSegment[] targetSegments;
        CDOChangeSetSegment[] sourceSegments;

        if (targetBaseInfo.getBranchPoint() == CDOBranchUtil.AUTO_BRANCH_POINT)
        {
          CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(target, source);
          targetSegments = CDOChangeSetSegment.createFrom(ancestor, target);
          sourceSegments = CDOChangeSetSegment.createFrom(ancestor, source);

          CDOBranchPoint targetBase = ancestor;
          CDOBranchPoint sourceBase = ancestor;
          long ancestorTime = ancestor.getTimeStamp();

          CDOBranchPointRange latestTargetMerge = getLatestMerge(targetSegments, sourceSegments, ancestorTime);
          if (latestTargetMerge != null)
          {
            targetBase = latestTargetMerge.getEndPoint();
            sourceBase = latestTargetMerge.getStartPoint();

            if (!sourceBase.equals(ancestor))
            {
              sourceSegments = CDOChangeSetSegment.createFrom(sourceBase, source);
            }
          }

          CDOBranchPointRange latestSourceMerge = getLatestMerge(sourceSegments, targetSegments, ancestorTime);
          if (latestSourceMerge != null)
          {
            CDOBranchPoint mergeSource = latestSourceMerge.getStartPoint();
            if (targetBase.getTimeStamp() < mergeSource.getTimeStamp())
            {
              targetBase = mergeSource;
            }

            result.setResultBase(sourceBase);
          }

          if (!targetBase.equals(ancestor))
          {
            targetSegments = CDOChangeSetSegment.createFrom(targetBase, target);
          }

          targetBaseInfo.setBranchPoint(targetBase);
          sourceBaseInfo.setBranchPoint(sourceBase);
        }
        else
        {
          CDORevisionAvailabilityInfo sourceBaseInfoToUse = sourceBaseInfo == null ? targetBaseInfo : sourceBaseInfo;
          targetSegments = CDOChangeSetSegment.createFrom(targetBaseInfo.getBranchPoint(), target);
          sourceSegments = CDOChangeSetSegment.createFrom(sourceBaseInfoToUse.getBranchPoint(), source);
        }

        targetIDs.addAll(accessor.readChangeSet(monitor.fork(), targetSegments));
        sourceIDs.addAll(accessor.readChangeSet(monitor.fork(), sourceSegments));
      }

      loadMergeData(targetIDs, targetInfo, monitor.fork());
      loadMergeData(sourceIDs, sourceInfo, monitor.fork());

      if (targetBaseInfo != null)
      {
        loadMergeData(targetIDs, targetBaseInfo, monitor.fork());
      }

      if (sourceBaseInfo != null && !targetBaseInfo.getBranchPoint().equals(sourceBaseInfo.getBranchPoint()))
      {
        loadMergeData(sourceIDs, sourceBaseInfo, monitor.fork());
      }

      return result;
    }
    finally
    {
      monitor.done();
    }
  }

  private CDOBranchPointRange getLatestMerge(CDOChangeSetSegment[] targetSegments, CDOChangeSetSegment[] sourceSegments, long ancestorTime)
  {
    for (int i = targetSegments.length - 1; i >= 0; --i)
    {
      CDOChangeSetSegment targetSegment = targetSegments[i];
      CDOBranch targetBranch = targetSegment.getBranch();
      long startTime = targetSegment.getTimeStamp();
      long endTime = targetSegment.getEndTime();

      while (endTime > startTime || endTime == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        CDOCommitInfo commitInfo = commitInfoManager.getCommitInfo(targetBranch, endTime, false);
        if (commitInfo == null)
        {
          break;
        }

        long timeStamp = commitInfo.getTimeStamp();
        if (timeStamp <= startTime)
        {
          break;
        }

        CDOBranchPoint mergeSource = getMergeSource(commitInfo, sourceSegments, ancestorTime);
        if (mergeSource != null)
        {
          CDOBranchPoint endPoint = CDOBranchUtil.copyBranchPoint(commitInfo);
          return CDOBranchUtil.createRange(mergeSource, endPoint);
        }

        endTime = timeStamp - 1;
      }
    }

    return null;
  }

  private CDOBranchPoint getMergeSource(CDOCommitInfo commitInfo, CDOChangeSetSegment[] sourceSegments, long ancestorTime)
  {
    CDOBranchPoint mergeSource = commitInfo.getMergeSource();
    if (mergeSource != null)
    {
      if (CDOChangeSetSegment.contains(sourceSegments, mergeSource))
      {
        return mergeSource;
      }

      CDOChangeSetSegment[] targetSegments = CDOChangeSetSegment.createFrom(ancestorTime, mergeSource);
      CDOBranchPointRange latestMerge = getLatestMerge(targetSegments, sourceSegments, ancestorTime);
      if (latestMerge != null)
      {
        return latestMerge.getStartPoint();
      }
    }

    return null;
  }

  private void loadMergeData(Set<CDOID> ids, CDORevisionAvailabilityInfo info, OMMonitor monitor)
  {
    int size = ids.size();
    monitor.begin(size);

    try
    {
      CDOBranchPoint branchPoint = info.getBranchPoint();
      for (CDOID id : ids)
      {
        if (info.containsRevision(id))
        {
          info.removeRevision(id);
        }
        else
        {
          InternalCDORevision revision = getRevisionFromBranch(id, branchPoint);
          if (revision != null)
          {
            info.addRevision(revision);
          }
          else
          {
            info.removeRevision(id);
          }
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private InternalCDORevision getRevisionFromBranch(CDOID id, CDOBranchPoint branchPoint)
  {
    return revisionManager.getRevision(id, branchPoint, UNCHUNKED, NONE, true);
  }

  @Override
  public void queryLobs(List<byte[]> ids)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.queryLobs(ids);
  }

  @Override
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.handleLobs(fromTime, toTime, handler);
  }

  @Override
  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.loadLob(id, out);
  }

  @Override
  public void loadLob(CDOLobInfo info, Object outputStream) throws IOException
  {
    loadLob(info.getID(), (OutputStream)outputStream);
  }

  @Override
  public int cleanupLobs(boolean dryRun)
  {
    if (store instanceof ILobCleanup)
    {
      return ((ILobCleanup)store).cleanupLobs(dryRun);
    }

    throw new LobCleanupNotSupportedException();
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, final CDORevisionHandler handler)
  {
    CDORevisionHandler wrapper = handler;
    if (!exactBranch && !branch.isMainBranch())
    {
      if (exactTime && timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        throw new IllegalArgumentException("Time stamp must be specified if exactBranch==false and exactTime==true");
      }

      wrapper = new CDORevisionHandler()
      {
        private Set<CDOID> handled = new HashSet<>();

        @Override
        public boolean handleRevision(CDORevision revision)
        {
          CDOID id = revision.getID();
          if (handled.add(id))
          {
            return handler.handleRevision(revision);
          }

          return true;
        }
      };
    }

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    while (branch != null)
    {
      accessor.handleRevisions(eClass, branch, timeStamp, exactTime, wrapper);
      if (exactBranch)
      {
        break;
      }

      CDOBranchPoint base = branch.getBase();
      branch = base.getBranch();
      timeStamp = base.getTimeStamp();
    }
  }

  private CDORevisionKey[] checkStaleRevisions(InternalView view, List<CDORevisionKey> revisionKeys, List<Object> objectsToLock, LockType lockType,
      long[] requiredTimestamp)
  {
    List<CDORevisionKey> staleRevisions = new LinkedList<>();
    if (revisionKeys != null)
    {
      InternalCDORevisionManager revManager = getRevisionManager();
      CDOBranch viewedBranch = view.getBranch();
      for (CDORevisionKey revKey : revisionKeys)
      {
        CDOID id = revKey.getID();
        InternalCDORevision rev = revManager.getRevision(id, viewedBranch.getHead(), UNCHUNKED, NONE, true);

        if (rev == null)
        {
          throw new IllegalArgumentException(String.format("Object %s not found in branch %s (possibly detached)", id, viewedBranch));
        }

        if (!revKey.equals(rev))
        {
          // Send back the *expected* revision keys, so that the client can check that it really has loaded those.
          staleRevisions.add(CDORevisionUtil.copyRevisionKey(rev));
          requiredTimestamp[0] = Math.max(requiredTimestamp[0], rev.getTimeStamp());
        }
      }
    }

    // Convert the list to an array, to satisfy the API later
    CDORevisionKey[] staleRevisionsArray = new CDORevisionKey[staleRevisions.size()];
    staleRevisions.toArray(staleRevisionsArray);

    return staleRevisionsArray;
  }

  private void sendLockNotifications(IView view, List<CDOLockDelta> lockDeltas, List<CDOLockState> lockStates, boolean administrative)
  {
    CDOBranchPoint branchPoint = view.getBranch().getPoint(getTimeStamp());
    CDOLockOwner lockOwner = view.getLockOwner();
    CDOLockChangeInfo lockChangeInfo = CDOLockUtil.createLockChangeInfo(branchPoint, lockOwner, lockDeltas, lockStates, administrative);

    InternalSession sender = administrative ? null : (InternalSession)view.getSession();
    sessionManager.sendLockNotification(sender, lockChangeInfo);
  }

  @Override
  public LockObjectsResult lock(InternalView view, LockType lockType, List<CDORevisionKey> revKeys, boolean recursive, long timeout)
  {
    List<Object> lockables = revisionKeysToObjects(revKeys, view.getBranch(), isSupportingBranches());
    return doLock(view, lockType, lockables, revKeys, recursive, timeout);
  }

  protected LockObjectsResult doLock(InternalView view, LockType lockType, List<Object> lockables, List<CDORevisionKey> loadedRevs, boolean recursive,
      long timeout)
  {
    LockDeltaCollector lockDeltas = new LockDeltaCollector(Operation.LOCK);
    LockStateCollector lockStates = new LockStateCollector();

    try
    {
      lockingManager.lock(view, lockables, lockType, 1, timeout, recursive, true, lockDeltas, lockStates);
    }
    catch (TimeoutRuntimeException ex)
    {
      return new LockObjectsResult(false, true, false, 0, new CDORevisionKey[0], NO_LOCK_DELTAS, NO_LOCK_STATES, getTimeStamp());
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    long[] requiredTimestamp = { 0L };
    CDORevisionKey[] staleRevisionsArray = null;

    try
    {
      staleRevisionsArray = checkStaleRevisions(view, loadedRevs, lockables, lockType, requiredTimestamp);
    }
    catch (IllegalArgumentException ex)
    {
      lockingManager.unlock(view, lockables, lockType, 1, recursive, true, null, null);
      throw ex;
    }

    // If some of the clients' revisions are stale and it has passiveUpdates disabled,
    // then the locks are useless so we release them and report the stale revisions
    //
    InternalSession session = view.getSession();
    boolean staleNoUpdate = staleRevisionsArray.length > 0 && !session.isPassiveUpdateEnabled();
    if (staleNoUpdate)
    {
      lockingManager.unlock(view, lockables, lockType, 1, recursive, true, null, null);
      return new LockObjectsResult(false, false, false, requiredTimestamp[0], staleRevisionsArray, NO_LOCK_DELTAS, NO_LOCK_STATES, getTimeStamp());
    }

    sendLockNotifications(view, lockDeltas, lockStates, false);

    boolean waitForUpdate = staleRevisionsArray.length > 0;
    return new LockObjectsResult(true, false, waitForUpdate, requiredTimestamp[0], staleRevisionsArray, lockDeltas, lockStates, getTimeStamp());
  }

  @Override
  public UnlockObjectsResult unlock(InternalView view, LockType lockType, List<CDOID> objectIDs, boolean recursive)
  {
    return doUnlock(view, lockType, objectIDs, recursive, false);
  }

  @Override
  public UnlockObjectsResult unlock(InternalView view)
  {
    return doUnlock(view, null, null, false, IRWOLockManager.ALL_LOCKS, false);
  }

  @Override
  public UnlockObjectsResult unlockAdministratively(InternalView view, LockType type, List<CDOID> ids, boolean recursive)
  {
    return null;
  }

  @Override
  public UnlockObjectsResult unlockAdministratively(InternalView view)
  {
    return doUnlock(view, null, null, false, IRWOLockManager.ALL_LOCKS, true);
  }

  protected UnlockObjectsResult doUnlock(InternalView view, LockType lockType, List<CDOID> objectIDs, boolean recursive, boolean notifyAllSessions)
  {
    List<Object> unlockables = null;

    if (objectIDs != null)
    {
      unlockables = new ArrayList<>(objectIDs.size());
      CDOBranch branch = view.getBranch();

      for (CDOID id : objectIDs)
      {
        Object key = supportingBranches ? CDOIDUtil.createIDAndBranch(id, branch) : id;
        unlockables.add(key);
      }
    }

    return doUnlock(view, lockType, unlockables, recursive, 1, notifyAllSessions);
  }

  protected UnlockObjectsResult doUnlock(InternalView view, LockType lockType, List<Object> unlockables, boolean recursive, int count,
      boolean notifyAllSessions)
  {
    LockDeltaCollector lockDeltas = new LockDeltaCollector(Operation.UNLOCK);
    LockStateCollector lockStates = new LockStateCollector();

    lockingManager.unlock(view, unlockables, lockType, count, recursive, true, lockDeltas, lockStates);

    sendLockNotifications(view, lockDeltas, lockStates, notifyAllSessions);

    long timestamp = getTimeStamp();
    return new UnlockObjectsResult(timestamp, lockDeltas, lockStates);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Repository[{0}]", name); //$NON-NLS-1$
  }

  @Override
  public boolean isSkipInitialization()
  {
    return skipInitialization;
  }

  @Override
  public void setSkipInitialization(boolean skipInitialization)
  {
    this.skipInitialization = skipInitialization;
  }

  protected void initProperties()
  {
    // OVERRIDE_UUID
    uuid = properties.get(Props.OVERRIDE_UUID);
    if (uuid != null && uuid.length() == 0)
    {
      uuid = getName();
    }

    // SUPPORTING_AUDITS
    String valueLoginPeeks = properties.get(Props.SUPPORTING_LOGIN_PEEKS);
    if (valueLoginPeeks != null)
    {
      supportingLoginPeeks = Boolean.valueOf(valueLoginPeeks);
    }

    // SUPPORTING_AUDITS
    String valueAudits = properties.get(Props.SUPPORTING_AUDITS);
    if (valueAudits != null)
    {
      supportingAudits = Boolean.valueOf(valueAudits);
    }
    else
    {
      supportingAudits = store.getRevisionTemporality() == IStore.RevisionTemporality.AUDITING;
    }

    // SUPPORTING_BRANCHES
    String valueBranches = properties.get(Props.SUPPORTING_BRANCHES);
    if (valueBranches != null)
    {
      supportingBranches = Boolean.valueOf(valueBranches);
    }
    else
    {
      supportingBranches = store.getRevisionParallelism() == IStore.RevisionParallelism.BRANCHING;
    }

    // SUPPORTING_UNITS
    String valueUnits = properties.get(Props.SUPPORTING_UNITS);
    if (valueUnits != null)
    {
      supportingUnits = Boolean.valueOf(valueUnits);
    }

    // SERIALIZE_COMMITS
    String valueCommits = properties.get(Props.SERIALIZE_COMMITS);
    if (valueCommits != null)
    {
      serializingCommits = Boolean.valueOf(valueCommits);
    }

    // ENSURE_REFERENTIAL_INTEGRITY
    String valueIntegrity = properties.get(Props.ENSURE_REFERENTIAL_INTEGRITY);
    if (valueIntegrity != null)
    {
      ensuringReferentialIntegrity = Boolean.valueOf(valueIntegrity);
    }

    // ID_GENERATION_LOCATION
    String valueIDLocation = properties.get(Props.ID_GENERATION_LOCATION);
    if (valueIDLocation != null)
    {
      idGenerationLocation = IDGenerationLocation.valueOf(valueIDLocation);
    }

    if (idGenerationLocation == null)
    {
      idGenerationLocation = IDGenerationLocation.STORE;
    }

    // LOB_DIGEST_ALGORITHM
    lobDigestAlgorithm = properties.get(Props.LOB_DIGEST_ALGORITHM);
    if (StringUtil.isEmpty(lobDigestAlgorithm))
    {
      lobDigestAlgorithm = CDOLobStoreImpl.DEFAULT_DIGEST_ALGORITHM;
    }

    // COMMIT_INFO_STORAGE
    String valueCommitInfoStorage = properties.get(Props.COMMIT_INFO_STORAGE);
    if (valueCommitInfoStorage != null)
    {
      commitInfoStorage = CommitInfoStorage.valueOf(valueCommitInfoStorage);
    }

    if (commitInfoStorage == null)
    {
      commitInfoStorage = CommitInfoStorage.WITH_MERGE_SOURCE;
    }

    if (commitInfoStorage != CommitInfoStorage.NO && !supportingBranches)
    {
      commitInfoStorage = CommitInfoStorage.YES;
    }

    // ENSURE_REFERENTIAL_INTEGRITY
    String valueTimeout = properties.get(Props.OPTIMISTIC_LOCKING_TIMEOUT);
    if (valueTimeout != null)
    {
      optimisticLockingTimeout = Long.valueOf(valueTimeout);
    }
  }

  @Override
  public void initSystemPackages(final boolean firstStart)
  {
    List<InternalCDOPackageUnit> newPackageUnits = new ArrayList<>();
    long timeStamp;

    if (firstStart)
    {
      timeStamp = store.getCreationTime();
      newPackageUnits.add(initPackage(timeStamp, EcorePackage.eINSTANCE));
      newPackageUnits.add(initPackage(timeStamp, EresourcePackage.eINSTANCE));
      newPackageUnits.add(initPackage(timeStamp, EtypesPackage.eINSTANCE));
    }
    else
    {
      readPackageUnits(); // Makes sure that all mapped package units have proper originalType/timeStamp.

      timeStamp = getTimeStamp();
      initPackage(timeStamp, EcorePackage.eINSTANCE);
      initPackage(timeStamp, EresourcePackage.eINSTANCE);
      initPackage(timeStamp, EtypesPackage.eINSTANCE);
    }

    if (initialPackages != null)
    {
      for (EPackage initialPackage : initialPackages)
      {
        if (!packageRegistry.containsKey(initialPackage.getNsURI()))
        {
          newPackageUnits.add(initPackage(timeStamp, initialPackage));
        }
      }
    }

    if (!newPackageUnits.isEmpty())
    {
      IStoreAccessor writer = store.getWriter(null);
      StoreThreadLocal.setAccessor(writer);

      try
      {
        InternalCDOPackageUnit[] packageUnits = newPackageUnits.toArray(new InternalCDOPackageUnit[newPackageUnits.size()]);
        writer.writePackageUnits(packageUnits, new Monitor());
        writer.commit(new Monitor());
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    fireEvent(new PackagesInitializedEvent()
    {
      @Override
      public InternalRepository getSource()
      {
        return Repository.this;
      }

      @Override
      public boolean isFirstStart()
      {
        return firstStart;
      }

      @Override
      public List<InternalCDOPackageUnit> getPackageUnits()
      {
        return Collections.unmodifiableList(newPackageUnits);
      }
    });
  }

  protected InternalCDOPackageUnit initPackage(long timeStamp, EPackage ePackage)
  {
    EMFUtil.registerPackage(ePackage, packageRegistry);
    InternalCDOPackageInfo packageInfo = packageRegistry.getPackageInfo(ePackage);

    InternalCDOPackageUnit packageUnit = packageInfo.getPackageUnit();
    packageUnit.setState(CDOPackageUnit.State.LOADED);

    if (packageUnit.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      packageUnit.setTimeStamp(timeStamp);
    }

    return packageUnit;
  }

  @Override
  public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp)
  {
    branchManager.initMainBranch(false, timeStamp);
  }

  protected void initRootResource()
  {
    CDOBranchPoint head = branchManager.getMainBranch().getHead();

    CDORevisionFactory factory = getRevisionManager().getFactory();
    InternalCDORevision rootResource = (InternalCDORevision)factory.createRevision(EresourcePackage.Literals.CDO_RESOURCE);

    rootResource.setBranchPoint(head);
    rootResource.setContainerID(CDOID.NULL);
    rootResource.setContainingFeatureID(0);

    CDOID id = createRootResourceID();
    rootResource.setID(id);
    rootResource.setResourceID(id);

    InternalSession session = getSessionManager().openSession(null);
    InternalTransaction transaction = session.openTransaction(1, head);
    InternalCommitContext commitContext = new TransactionCommitContext(transaction)
    {
      @Override
      protected long[] createTimeStamp(OMMonitor monitor)
      {
        InternalRepository repository = getTransaction().getSession().getRepository();
        return repository.forceCommitTimeStamp(store.getCreationTime(), monitor);
      }

      @Override
      public String getUserID()
      {
        return SYSTEM_USER_ID;
      }

      @Override
      public String getCommitComment()
      {
        return "<initialize>"; //$NON-NLS-1$
      }
    };

    commitContext.setNewObjects(new InternalCDORevision[] { rootResource });
    commitContext.preWrite();
    commitContext.write(new Monitor());
    commitContext.commit(new Monitor());

    String rollbackMessage = commitContext.getRollbackMessage();
    if (rollbackMessage != null)
    {
      throw new TransactionException(rollbackMessage);
    }

    rootResourceID = id instanceof CDOIDTemp ? commitContext.getIDMappings().get(id) : id;

    commitContext.postCommit(true);
    session.close();
  }

  protected CDOID createRootResourceID()
  {
    if (getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      return CDOIDUtil.createTempObject(1);
    }

    return CDOIDGenerator.UUID.generateCDOID(null);
  }

  protected void readRootResource()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      CDOBranchPoint head = branchManager.getMainBranch().getHead();
      rootResourceID = reader.readResourceID(CDOID.NULL, null, head);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected void readPackageUnits()
  {
    IStoreAccessor reader = store.getReader(null);
    StoreThreadLocal.setAccessor(reader);

    try
    {
      Collection<InternalCDOPackageUnit> packageUnits = reader.readPackageUnits();
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        packageRegistry.putPackageUnit(packageUnit);
      }

      // Bug 521029: Initialize EPackages early from the main thread to avoid multi-threading issues.
      // This could be made optional at some point.
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
        {
          packageInfo.getEPackage(true); // Trigger initialization.
        }
      }
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected void setPostActivateState()
  {
    setState(State.ONLINE);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();

    checkState(!StringUtil.isEmpty(name), "name is empty"); //$NON-NLS-1$
    checkState(store, "store"); //$NON-NLS-1$
    checkState(packageRegistry, "packageRegistry"); //$NON-NLS-1$
    checkState(sessionManager, "sessionManager"); //$NON-NLS-1$
    checkState(branchManager, "branchManager"); //$NON-NLS-1$
    checkState(revisionManager, "revisionManager"); //$NON-NLS-1$
    checkState(queryManager, "queryManager"); //$NON-NLS-1$
    checkState(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    checkState(commitManager, "commitManager"); //$NON-NLS-1$
    checkState(lockingManager, "lockingManager"); //$NON-NLS-1$

    packageRegistry.setReplacingDescriptors(true);

    if (packageRegistry.getPackageProcessor() == null)
    {
      packageRegistry.setPackageProcessor(this);
    }

    if (packageRegistry.getPackageLoader() == null)
    {
      packageRegistry.setPackageLoader(this);
    }

    if (branchManager.getRepository() == null)
    {
      branchManager.setRepository(this);
    }

    if (branchManager.getBranchLoader() == null)
    {
      branchManager.setBranchLoader(this);
    }

    if (revisionManager.getRevisionLoader() == null)
    {
      revisionManager.setRevisionLoader(this);
    }

    if (sessionManager.getRepository() == null)
    {
      sessionManager.setRepository(this);
    }

    if (queryManager.getRepository() == null)
    {
      queryManager.setRepository(this);
    }

    if (commitInfoManager.getRepository() == null)
    {
      commitInfoManager.setRepository(this);
    }

    if (commitInfoManager.getBranchManager() == null)
    {
      commitInfoManager.setBranchManager(branchManager);
    }

    if (commitInfoManager.getCommitInfoLoader() == null)
    {
      commitInfoManager.setCommitInfoLoader(this);
    }

    if (commitManager.getRepository() == null)
    {
      commitManager.setRepository(this);
    }

    if (lockingManager.getRepository() == null)
    {
      lockingManager.setRepository(this);
    }

    if (store.getRepository() == null)
    {
      store.setRepository(this);
    }

    if (protector != null && protector.getRepository() == null)
    {
      protector.setRepository(this);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    initProperties();
    if (idGenerationLocation == IDGenerationLocation.CLIENT && !(store instanceof CanHandleClientAssignedIDs))
    {
      throw new IllegalStateException("Store can not handle client-assigned IDs: " + store);
    }

    store.setRevisionTemporality(supportingAudits ? IStore.RevisionTemporality.AUDITING : IStore.RevisionTemporality.NONE);
    store.setRevisionParallelism(supportingBranches ? IStore.RevisionParallelism.BRANCHING : IStore.RevisionParallelism.NONE);
    revisionManager.setSupportingAudits(supportingAudits);
    revisionManager.setSupportingBranches(supportingBranches);

    LifecycleUtil.activate(store);

    Map<String, String> persistentProperties = store.getPersistentProperties(Collections.singleton(PROP_UUID));
    String persistentUUID = persistentProperties.get(PROP_UUID);

    if (uuid == null)
    {
      if (persistentUUID == null)
      {
        uuid = UUID.randomUUID().toString();
      }
      else
      {
        uuid = persistentUUID;
      }
    }

    if (persistentUUID == null || !persistentUUID.equals(uuid))
    {
      persistentProperties.put(PROP_UUID, uuid);
      store.setPersistentProperties(persistentProperties);
    }

    LifecycleUtil.activate(packageRegistry);
    LifecycleUtil.activate(sessionManager);
    LifecycleUtil.activate(revisionManager);
    LifecycleUtil.activate(branchManager);
    LifecycleUtil.activate(queryManager);
    LifecycleUtil.activate(commitInfoManager);
    LifecycleUtil.activate(commitManager);
    LifecycleUtil.activate(queryHandlerProvider);

    if (supportingUnits)
    {
      LifecycleUtil.activate(unitManager);
    }

    if (!skipInitialization)
    {
      long creationTime = store.getCreationTime();
      initMainBranch(branchManager, creationTime);

      long lastCommitTime = Math.max(creationTime, store.getLastCommitTime());
      timeStampAuthority.setLastFinishedTimeStamp(lastCommitTime);
      commitInfoManager.setLastCommitOfBranch(null, lastCommitTime);

      if (store.isFirstStart())
      {
        initSystemPackages(true);
        initRootResource();
      }
      else
      {
        initSystemPackages(false);
        readRootResource();
      }

      branchManager.setTagModCount(0);

      if (supportingAudits)
      {
        // Load all tags and keep a strong reference to avoid frequent tag loading.
        IStoreAccessor reader = store.getReader(null);
        StoreThreadLocal.setAccessor(reader);

        try
        {
          tagList = branchManager.getTagList();
        }
        finally
        {
          StoreThreadLocal.release();
        }
      }
    }

    LifecycleUtil.activate(lockingManager); // Needs an initialized main branch / branch manager
    LifecycleUtil.activate(protector);
    LifecycleUtil.activate(entityStore);

    setPostActivateState();

    synchronized (REPOSITORIES)
    {
      if (REPOSITORIES.putIfAbsent(uuid, this) != null)
      {
        OM.LOG.warn("Attempt to register repository with duplicate UUID: " + uuid);
      }
    }
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    List<Object> elements = new ArrayList<>();
    CollectionUtil.addNotNull(elements, packageRegistry);
    CollectionUtil.addNotNull(elements, branchManager);
    CollectionUtil.addNotNull(elements, revisionManager);
    CollectionUtil.addNotNull(elements, sessionManager);
    CollectionUtil.addNotNull(elements, queryManager);
    CollectionUtil.addNotNull(elements, commitManager);
    CollectionUtil.addNotNull(elements, commitConflictResolver);
    CollectionUtil.addNotNull(elements, commitInfoManager);
    CollectionUtil.addNotNull(elements, lockingManager);
    CollectionUtil.addNotNull(elements, unitManager);
    CollectionUtil.addNotNull(elements, store);
    this.elements = elements.toArray();

    List<PostActivateable> postActivateables = elements.stream() //
        .filter(PostActivateable.class::isInstance)//
        .map(PostActivateable.class::cast)//
        .collect(Collectors.toList());

    if (!postActivateables.isEmpty())
    {
      InternalSession session = getSessionManager().openSession(null);

      try
      {
        StoreThreadLocal.wrap(session, () -> {
          for (PostActivateable postActivateable : postActivateables)
          {
            postActivateable.doPostActivate(session);
          }
        }).run();
      }
      finally
      {
        session.close();
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (REPOSITORIES)
    {
      REPOSITORIES.remove(uuid);
    }

    LifecycleUtil.deactivate(entityStore);
    LifecycleUtil.deactivate(protector);
    LifecycleUtil.deactivate(unitManager);
    LifecycleUtil.deactivate(lockingManager);
    LifecycleUtil.deactivate(queryHandlerProvider);
    LifecycleUtil.deactivate(commitManager);
    LifecycleUtil.deactivate(commitInfoManager);
    LifecycleUtil.deactivate(queryManager);
    LifecycleUtil.deactivate(revisionManager);
    LifecycleUtil.deactivate(sessionManager);
    LifecycleUtil.deactivate(store);
    LifecycleUtil.deactivate(branchManager);
    LifecycleUtil.deactivate(packageRegistry);
    super.doDeactivate();
  }

  private static boolean isSecurityManagerWrapper(IRepositoryProtector protector)
  {
    return protector != null && protector.getAuthorizationStrategy() == null;
  }

  public static List<Object> revisionKeysToObjects(List<CDORevisionKey> revisionKeys, CDOBranch viewedBranch, boolean isSupportingBranches)
  {
    List<Object> lockables = new ArrayList<>();
    for (CDORevisionKey revKey : revisionKeys)
    {
      CDOID id = revKey.getID();
      if (isSupportingBranches)
      {
        lockables.add(CDOIDUtil.createIDAndBranch(id, viewedBranch));
      }
      else
      {
        lockables.add(id);
      }
    }

    return lockables;
  }

  public static Repository get(String uuid)
  {
    synchronized (REPOSITORIES)
    {
      return REPOSITORIES.get(uuid);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class EntityExtension extends TreeExtension
  {
    public EntityExtension()
    {
    }

    @Override
    protected String configureRepository(InternalRepository repository, Tree config, Map<String, String> parameters, IManagedContainer container)
    {
      Entity entity = Entity.builder(config).build();
      repository.addEntity(entity);
      return null;
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Default extends Repository
  {
    public Default()
    {
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (getEntityStore() == null)
      {
        setEntityStore(createEntityStore());
      }

      if (getTimeProvider() == null)
      {
        setTimeProvider(createTimeProvider());
      }

      if (getPackageRegistry(false) == null)
      {
        setPackageRegistry(createPackageRegistry());
      }

      if (getSessionManager() == null)
      {
        setSessionManager(createSessionManager());
      }

      if (getBranchManager() == null)
      {
        setBranchManager(createBranchManager());
      }

      if (getRevisionManager() == null)
      {
        setRevisionManager(createRevisionManager());
      }

      if (getQueryManager() == null)
      {
        setQueryManager(createQueryManager());
      }

      if (getCommitInfoManager() == null)
      {
        setCommitInfoManager(createCommitInfoManager());
      }

      if (getCommitManager() == null)
      {
        setCommitManager(createCommitManager());
      }

      if (getLockingManager() == null)
      {
        setLockingManager(createLockingManager());
      }

      if (getUnitManager() == null)
      {
        setUnitManager(createUnitManager());
      }

      super.doBeforeActivate();
    }

    protected Entity.Store createEntityStore()
    {
      Entity.ComposedStore composedStore = new Entity.ComposedStore();

      Map<String, SingleNamespaceStore> stores = new HashMap<>();
      for (Entity entity : getEntities().values())
      {
        String namespace = entity.namespace();

        SingleNamespaceStore store = stores.computeIfAbsent(namespace, k -> {
          SingleNamespaceStore s = new SingleNamespaceStore(namespace);
          composedStore.addStore(s);
          return s;
        });

        store.addEntity(entity);
      }

      composedStore.addStore(new Entity.SingleNamespaceComputer(ENTITY_NAMESPACE)
      {
        @Override
        protected Collection<String> computeNames()
        {
          return Collections.singleton(ENTITY_NAME_PROPERTIES);
        }

        @Override
        protected Entity computeEntity(String name)
        {
          if (ENTITY_NAME_PROPERTIES.equals(name))
          {
            Map<String, String> properties = getProperties();
            return entityBuilder(ENTITY_NAME_PROPERTIES).properties(properties).build();
          }

          return null;
        }
      });

      Store storeStore = Entity.Store.of(getStore());
      if (storeStore != null)
      {
        composedStore.addStore(storeStore);
      }

      IRepositoryProtector protector = getProtector();
      if (protector != null)
      {
        Entity.Store entityStore = Entity.Store.of(protector);
        if (entityStore != null)
        {
          composedStore.addStore(entityStore);
        }

        entityStore = Entity.Store.of(protector.getUserAuthenticator());
        if (entityStore != null)
        {
          composedStore.addStore(entityStore);
        }
      }

      return composedStore;
    }

    protected CDOTimeProvider createTimeProvider()
    {
      return CurrentTimeProvider.INSTANCE;
    }

    protected InternalCDOPackageRegistry createPackageRegistry()
    {
      return new CDOPackageRegistryImpl();
    }

    protected InternalSessionManager createSessionManager()
    {
      return new SessionManager();
    }

    protected InternalCDOBranchManager createBranchManager()
    {
      return CDOBranchUtil.createBranchManager();
    }

    protected InternalCDORevisionManager createRevisionManager()
    {
      return (InternalCDORevisionManager)CDORevisionUtil.createRevisionManager();
    }

    protected InternalQueryManager createQueryManager()
    {
      return new QueryManager();
    }

    protected InternalCDOCommitInfoManager createCommitInfoManager()
    {
      return CDOCommitInfoUtil.createCommitInfoManager();
    }

    protected InternalCommitManager createCommitManager()
    {
      return new CommitManager();
    }

    protected InternalUnitManager createUnitManager()
    {
      return new UnitManager(this);
    }

    public LockingManager createLockingManager()
    {
      return new LockingManager();
    }
  }

  @Override
  @Deprecated
  public boolean isSupportingEcore()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void renameBranch(int branchID, String newName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public InternalLockManager getLockManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public CDOCommitInfoHandler[] getCommitInfoHandlers()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void addCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void removeCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo, boolean clearResourcePathCache)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo, CDORevisionAvailabilityInfo targetBaseInfo,
      CDORevisionAvailabilityInfo sourceBaseInfo, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void initSystemPackages()
  {
    throw new UnsupportedOperationException();
  }
}
