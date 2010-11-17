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
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
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
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
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
import org.eclipse.net4j.util.container.IPluginContainer;
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

  private static final String PROP_FIXED = "org.eclipse.emf.cdo.workspace.fixed"; //$NON-NLS-1$

  private IManagedContainer container;

  private InternalCDOWorkspaceBase base;

  private InternalRepository localRepository;

  private InternalCDOSession localSession;

  private String branchPath;

  private long timeStamp;

  private boolean fixed;

  private CDOSessionConfigurationFactory remoteSessionConfigurationFactory;

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  public CDOWorkspaceImpl(IStore local, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      String branchPath, long timeStamp)
  {
    init(local, base, remote);
    remoteSessionConfigurationFactory = remote;

    this.branchPath = StringUtil.isEmpty(branchPath) ? CDOBranch.MAIN_BRANCH_NAME : branchPath;
    this.timeStamp = timeStamp;
    fixed = timeStamp != CDOBranchPoint.UNSPECIFIED_DATE;

    checkout();
    saveProperties();
  }

  public CDOWorkspaceImpl(IStore local, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    init(local, base, remote);
    loadProperties();
  }

  protected void init(IStore local, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    container = createContainer(local);
    remoteSessionConfigurationFactory = remote;

    localRepository = createLocalRepository(local);

    try
    {
      CDOServerUtil.addRepository(IPluginContainer.INSTANCE, localRepository); // --> CDOServerBrowser
      IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.server.browsers", "default", "7778");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    this.base = base;
    this.base.init(this);
  }

  protected void checkout()
  {
    final OMMonitor monitor = new Monitor();
    final Object[] context = { null };

    final IStoreAccessor.Raw accessor = (IStoreAccessor.Raw)localRepository.getStore().getWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      InternalCDOSession session = openRemoteSession();

      try
      {
        localRepository.setRootResourceID(session.getRepositoryInfo().getRootResourceID());

        InternalCDOPackageUnit[] packageUnits = session.getPackageRegistry().getPackageUnits(false);
        localRepository.getPackageRegistry(false).putPackageUnits(packageUnits, CDOPackageUnit.State.LOADED);
        context[0] = accessor.rawStore(packageUnits, context[0], monitor);

        CDORevisionHandler handler = new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            InternalCDORevision rev = (InternalCDORevision)revision;
            System.err.println(rev);
            context[0] = accessor.rawStore(rev, context[0], monitor);

            long commitTime = revision.getTimeStamp();
            if (commitTime > timeStamp)
            {
              timeStamp = commitTime;
            }

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

  public String getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isFixed()
  {
    return fixed;
  }

  public InternalCDOWorkspaceBase getBase()
  {
    return base;
  }

  public InternalCDOView openView()
  {
    CDOView view = getLocalSession().openView();
    initView(view);
    return (InternalCDOView)view;
  }

  public InternalCDOView openView(ResourceSet resourceSet)
  {
    CDOView view = getLocalSession().openView(resourceSet);
    initView(view);
    return (InternalCDOView)view;
  }

  public InternalCDOTransaction openTransaction()
  {
    CDOTransaction transaction = getLocalSession().openTransaction();
    initView(transaction);
    return (InternalCDOTransaction)transaction;
  }

  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    CDOTransaction transaction = getLocalSession().openTransaction(resourceSet);
    initView(transaction);
    return (InternalCDOTransaction)transaction;
  }

  public InternalCDOTransaction update(CDOMerger merger)
  {
    return merge(merger, branchPath);
  }

  public InternalCDOTransaction merge(CDOMerger merger, String branchPath)
  {
    return merge(merger, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public InternalCDOTransaction merge(CDOMerger merger, String branchPath, long timeStamp)
  {
    InternalCDOSession session = openRemoteSession();

    try
    {
      InternalCDOBranchManager branchManager = session.getBranchManager();
      CDOBranchPoint basePoint = branchManager.getBranch(branchPath).getPoint(this.timeStamp);
      CDOBranchPoint remotePoint = branchManager.getBranch(branchPath).getPoint(timeStamp);

      CDOBranchPointRange range = CDOBranchUtil.createRange(basePoint, remotePoint);
      CDOChangeSetData remoteData = session.getSessionProtocol().loadChangeSets(range)[0];

      CDOChangeSetData localData = getLocalChanges();
      if (!localData.isEmpty())
      {
        CDOChangeSet localChanges = CDORevisionDeltaUtil.createChangeSet(basePoint, null, localData);
        CDOChangeSet remoteChanges = CDORevisionDeltaUtil.createChangeSet(basePoint, remotePoint, remoteData);
        remoteData = merger.merge(localChanges, remoteChanges);
      }

      InternalCDOTransaction transaction = openTransaction();
      transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
      {
        @Override
        public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
          base.clear();
        }
      });

      transaction.applyChangeSetData(remoteData, new CDORevisionProvider()
      {
        public CDORevision getRevision(CDOID id)
        {
          CDORevision revision = base.getRevision(id);
          if (revision == null)
          {
            revision = CDOWorkspaceImpl.this.getRevision(id);
          }

          return revision;
        }
      }, this, null);

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

      transaction.applyChangeSetData(changes, base, this, null);
      transaction.setCommitComment(comment);

      CDOCommitInfo info = transaction.commit();

      base.clear();
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
    InternalCDOSession session = getLocalSession();
    CDORevisionManager revisionManager = session.getRevisionManager();
    CDOBranchPoint head = session.getBranchManager().getMainBranch().getHead();
    return revisionManager.getRevision(id, head, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  public InternalRepository getLocalRepository()
  {
    return localRepository;
  }

  public synchronized InternalCDOSession getLocalSession()
  {
    if (localSession == null)
    {
      localSession = openLocalSession();
    }

    return localSession;
  }

  public CDOChangeSetData getLocalChanges()
  {
    Set<CDOID> ids = base.getIDs();
    return CDORevisionDeltaUtil.createChangeSetData(ids, base, this);
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

  protected InternalRepository createLocalRepository(IStore store)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.SUPPORTING_AUDITS, "false");
    props.put(Props.SUPPORTING_BRANCHES, "false");

    Repository repository = new Repository.Default()
    {
      @Override
      public void initMainBranch(InternalCDOBranchManager branchManager, long lastCommitTimeStamp)
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
    configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP)); // Use repo's cache

    InternalCDOSession session = (InternalCDOSession)configuration.openSession();
    session.setPackageRegistry(localRepository.getPackageRegistry(false)); // Use repo's registry
    return session;
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
      if (fixed)
      {
        throw new ReadOnlyException("Workspace is fixed");
      }

      CDOTransaction transaction = (CDOTransaction)view;
      transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
      {
        @Override
        public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
          CDOWorkspaceImpl.this.committedTransaction(transaction, commitContext);
        }
      });
    }
  }

  protected void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    base.updateAfterCommit(transaction);
  }

  protected CDOSessionConfigurationFactory getRemoteSessionConfigurationFactory()
  {
    return remoteSessionConfigurationFactory;
  }

  protected InternalCDOSession openRemoteSession()
  {
    CDOSessionConfiguration configuration = remoteSessionConfigurationFactory.createSessionConfiguration();
    InternalCDOSession session = (InternalCDOSession)configuration.openSession();

    if (!session.getRepositoryInfo().isSupportingAudits())
    {
      session.close();
      throw new IllegalStateException("Remote repository does not support auditing");
    }

    return session;
  }

  protected void saveProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(PROP_BRANCH_PATH, branchPath);
    props.put(PROP_TIME_STAMP, String.valueOf(timeStamp));
    props.put(PROP_FIXED, String.valueOf(fixed));
    localRepository.getStore().setPropertyValues(props);
  }

  protected void loadProperties()
  {
    Set<String> names = new HashSet<String>(Arrays.asList(PROP_BRANCH_PATH, PROP_TIME_STAMP, PROP_FIXED));
    Map<String, String> props = localRepository.getStore().getPropertyValues(names);
    branchPath = props.get(PROP_BRANCH_PATH);
    timeStamp = Long.parseLong(props.get(PROP_TIME_STAMP));
    fixed = Boolean.parseBoolean(props.get(PROP_FIXED));
  }
}
