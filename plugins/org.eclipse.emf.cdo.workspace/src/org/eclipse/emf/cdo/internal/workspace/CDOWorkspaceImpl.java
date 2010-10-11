/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceMemory;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOWorkspaceImpl implements InternalCDOWorkspace
{
  private static final String PROP_BRANCH_PATH = "org.eclipse.emf.cdo.workspace.branchPath"; //$NON-NLS-1$

  private static final String PROP_TIME_STAMP = "org.eclipse.emf.cdo.workspace.timeStamp"; //$NON-NLS-1$

  private static final String PROP_READ_ONLY = "org.eclipse.emf.cdo.workspace.readOnly"; //$NON-NLS-1$

  private IManagedContainer container;

  private InternalCDOWorkspaceMemory memory;

  private InternalRepository localRepository;

  private InternalCDOSession localSession;

  private String branchPath;

  private long timeStamp;

  private boolean readOnly;

  private CDOSessionConfigurationFactory remoteSessionConfigurationFactory;

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  public CDOWorkspaceImpl(IStore local, InternalCDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote,
      String branchPath, long timeStamp)
  {
    init(local, memory, remote);
    remoteSessionConfigurationFactory = remote;

    this.branchPath = StringUtil.isEmpty(branchPath) ? CDOBranch.MAIN_BRANCH_NAME : branchPath;
    this.timeStamp = timeStamp;
    readOnly = timeStamp != CDOBranchPoint.UNSPECIFIED_DATE;

    saveProperties();
    checkout();
  }

  public CDOWorkspaceImpl(IStore local, InternalCDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote)
  {
    init(local, memory, remote);
    open();
    loadProperties();
  }

  protected void init(IStore local, InternalCDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote)
  {
    container = createContainer(local);
    remoteSessionConfigurationFactory = remote;

    localRepository = createLocalRepository(local);
    localSession = openLocalSession();

    this.memory = memory;
    this.memory.init(this);
  }

  protected void checkout()
  {
    final OMMonitor monitor = new Monitor();
    final Object[] context = { null };

    final IStoreAccessor accessor = localRepository.getStore().getWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      InternalCDOSession session = openRemoteSession();

      try
      {
        InternalCDOPackageUnit[] packageUnits = session.getPackageRegistry().getPackageUnits();
        context[0] = accessor.rawStore(packageUnits, context[0], monitor);

        InternalCDOPackageRegistry repositoryPackageRegistry = localRepository.getPackageRegistry(false);
        InternalCDOPackageRegistry sessionPackageRegistry = localSession.getPackageRegistry();
        for (InternalCDOPackageUnit packageUnit : packageUnits)
        {
          repositoryPackageRegistry.putPackageUnit(packageUnit);
          sessionPackageRegistry.putPackageUnit(packageUnit);
        }

        CDORevisionHandler handler = new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            context[0] = accessor.rawStore((InternalCDORevision)revision, context[0], monitor);
            return true;
          }
        };

        CDOBranch branch = session.getBranchManager().getBranch(branchPath);
        session.getSessionProtocol().handleRevisions(null, branch, false, timeStamp, false, handler);
      }
      finally
      {
        LifecycleUtil.deactivate(session);
      }

      accessor.rawCommit(context[0], monitor);
    }
    finally
    {
      StoreThreadLocal.release();
      monitor.done();
    }
  }

  protected void open()
  {
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public InternalCDOWorkspaceMemory getMemory()
  {
    return memory;
  }

  public CDOView openView()
  {
    CDOView view = localSession.openView();
    initView(view);
    return view;
  }

  public CDOView openView(ResourceSet resourceSet)
  {
    CDOView view = localSession.openView(resourceSet);
    initView(view);
    return view;
  }

  public CDOTransaction openTransaction()
  {
    CDOTransaction transaction = localSession.openTransaction();
    initView(transaction);
    return transaction;
  }

  public CDOTransaction openTransaction(ResourceSet resourceSet)
  {
    CDOTransaction transaction = localSession.openTransaction(resourceSet);
    initView(transaction);
    return transaction;
  }

  public CDOTransaction update(CDOMerger merger)
  {
    return merge(merger, branchPath);
  }

  public CDOTransaction merge(CDOMerger merger, String branchPath)
  {
    return merge(merger, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public CDOTransaction merge(CDOMerger merger, String branchPath, long timeStamp)
  {
    InternalCDOSession session = openRemoteSession();

    try
    {
      InternalCDOBranchManager branchManager = session.getBranchManager();
      CDOBranchPoint base = branchManager.getBranch(branchPath).getPoint(timeStamp);
      CDOBranchPoint remote = branchManager.getBranch(branchPath).getPoint(timeStamp);

      CDOBranchPointRange range = CDOBranchUtil.createRange(base, remote);
      CDOChangeSetData remoteData = session.getSessionProtocol().loadChangeSets(range)[0];
      CDOChangeSet remoteChanges = CDORevisionDeltaUtil.createChangeSet(base, remote, remoteData);

      CDOChangeSetData localData = getLocalChanges();
      CDOChangeSet localChanges = CDORevisionDeltaUtil.createChangeSet(base, null, localData);

      CDOChangeSetData result = merger.merge(localChanges, remoteChanges);

      InternalCDOTransaction transaction = (InternalCDOTransaction)openTransaction();
      transaction.applyChangeSetData(result, memory, this, null);
      return transaction;
    }
    finally
    {
      LifecycleUtil.deactivate(session);
    }
  }

  public void revert()
  {
    // TODO: implement CDOWorkspaceImpl.revert()
    throw new UnsupportedOperationException();
  }

  public void replace(String branchPath, long timeStamp)
  {
    // TODO: implement CDOWorkspaceImpl.replace(branchPath, timeStamp)
    throw new UnsupportedOperationException();
  }

  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  public CDOCommitInfo commit(String comment) throws CommitException
  {
    InternalCDOSession session = openRemoteSession();

    try
    {
      InternalCDOBranch branch = session.getBranchManager().getBranch(branchPath);
      InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction(branch);

      CDOChangeSetData changes = getLocalChanges();

      transaction.applyChangeSetData(changes, memory, this, null);
      transaction.setCommitComment(comment);

      CDOCommitInfo info = transaction.commit();

      memory.clear();
      timeStamp = info.getTimeStamp();
      saveProperties();

      return info;
    }
    finally
    {
      LifecycleUtil.deactivate(session);
    }
  }

  public CDOChangeSetData compare(String branchPath)
  {
    return compare(branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public CDOChangeSetData compare(String branchPath, long timeStamp)
  {
    // TODO: implement CDOWorkspaceImpl.compare(branchPath, timeStamp)
    throw new UnsupportedOperationException();
  }

  public synchronized void close()
  {
    LifecycleUtil.deactivate(localSession);
    localSession = null;

    LifecycleUtil.deactivate(localRepository);
    localRepository = null;

    LifecycleUtil.deactivate(container);
    container = null;
  }

  public synchronized boolean isClosed()
  {
    return container == null;
  }

  public CDORevision getRevision(CDOID id)
  {
    CDORevisionManager revisionManager = localSession.getRevisionManager();
    CDOBranchPoint head = localSession.getBranchManager().getMainBranch().getHead();
    return revisionManager.getRevision(id, head, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  public InternalRepository getLocalRepository()
  {
    return localRepository;
  }

  public CDOChangeSetData getLocalChanges()
  {
    Set<CDOID> ids = memory.getIDs();
    return CDORevisionDeltaUtil.createChangeSetData(ids, memory, this);
  }

  protected IManagedContainer createContainer(IStore local)
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    JVMUtil.prepareContainer(container);
    CDONet4jServerUtil.prepareContainer(container);
    container.activate();
    return container;
  }

  protected IManagedContainer getContainer()
  {
    return container;
  }

  protected String getLocalAcceptorName()
  {
    return "acceptor-for-" + localRepository.getUUID();
  }

  protected IJVMAcceptor getLocalAcceptor()
  {
    String localAcceptorName = getLocalAcceptorName();
    return JVMUtil.getAcceptor(container, localAcceptorName);
  }

  protected IJVMConnector getLocalConnector()
  {
    String localAcceptorName = getLocalAcceptorName();
    return JVMUtil.getConnector(container, localAcceptorName);
  }

  protected InternalCDOSession getLocalSession()
  {
    return localSession;
  }

  protected InternalRepository createLocalRepository(IStore store)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.SUPPORTING_AUDITS, "false");
    props.put(Props.SUPPORTING_BRANCHES, "false");

    Repository repository = new Repository.Default()
    {
      @Override
      protected void initMainBranch(InternalCDOBranchManager branchManager, long lastCommitTimeStamp)
      {
        // Mark the main branch local so that new objects get local IDs
        branchManager.initMainBranch(true, lastCommitTimeStamp);
      }

      @Override
      protected void initRootResource()
      {
        // Don't create the root resource as it will be checked out
        setState(State.INITIAL);
      }
    };

    repository.setName("local");
    repository.setStore((InternalStore)store);
    repository.setProperties(props);

    CDOServerUtil.addRepository(container, repository);
    return repository;
  }

  protected InternalCDOSession openLocalSession()
  {
    getLocalAcceptor();

    IJVMConnector connector = getLocalConnector();
    String repositoryName = localRepository.getName();

    org.eclipse.emf.cdo.net4j.CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    return (InternalCDOSession)configuration.openSession();
  }

  protected InternalCDOView[] getViews()
  {
    synchronized (views)
    {
      return views.toArray(new InternalCDOView[views.size()]);
    }
  }

  protected void initView(CDOView view)
  {
    synchronized (views)
    {
      views.add((InternalCDOView)view);
    }

    view.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle view)
      {
        synchronized (views)
        {
          views.remove(view);
        }
      }
    });

    if (view instanceof CDOTransaction)
    {
      if (readOnly)
      {
        throw new ReadOnlyException("Workspace is read-only");
      }

      CDOTransaction transaction = (CDOTransaction)view;
      transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
      {
        @Override
        public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
          memory.updateAfterCommit(transaction);
        }
      });
    }
  }

  protected CDOSessionConfigurationFactory getRemoteSessionConfigurationFactory()
  {
    return remoteSessionConfigurationFactory;
  }

  protected InternalCDOSession openRemoteSession()
  {
    CDOSessionConfiguration configuration = remoteSessionConfigurationFactory.createSessionConfiguration();
    return (InternalCDOSession)configuration.openSession();
  }

  protected void saveProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(PROP_BRANCH_PATH, branchPath);
    props.put(PROP_TIME_STAMP, String.valueOf(timeStamp));
    props.put(PROP_READ_ONLY, String.valueOf(readOnly));
    localRepository.getStore().setPropertyValues(props);
  }

  protected void loadProperties()
  {
    Set<String> names = new HashSet<String>(Arrays.asList(PROP_BRANCH_PATH, PROP_TIME_STAMP, PROP_READ_ONLY));
    Map<String, String> props = localRepository.getStore().getPropertyValues(names);
    branchPath = props.get(PROP_BRANCH_PATH);
    timeStamp = Integer.parseInt(props.get(PROP_TIME_STAMP));
    readOnly = Boolean.parseBoolean(props.get(PROP_READ_ONLY));
  }
}
