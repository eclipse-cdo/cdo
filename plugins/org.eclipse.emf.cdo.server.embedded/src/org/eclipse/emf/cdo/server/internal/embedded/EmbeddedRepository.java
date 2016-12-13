package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class EmbeddedRepository extends Repository.Default
{
  private static final String JVM_ACCEPTOR_PREFIX = "cdo_embedded_repo_";

  private CDOEmbeddedRepositoryConfig config;

  private IAcceptor acceptor;

  private IConnector connector;

  public EmbeddedRepository(CDOEmbeddedRepositoryConfig config)
  {
    this.config = config;
  }

  public CDONet4jSession openClientSession()
  {
    CDORevisionManager revisionManager = new DelegatingRevisionManager(getRevisionManager());
    CDONet4jSessionConfiguration configuration = createSessionConfiguration(connector, revisionManager);

    CDONet4jSession session = configuration.openNet4jSession();
    session.options().setCommitTimeout(Integer.MAX_VALUE);
    config.modifySessionConfiguration(this, configuration);
    return session;
  }

  @Override
  public void initSystemPackages(boolean firstStart)
  {
    if (firstStart)
    {
      // Initialize packages.
      List<EPackage> packages = new ArrayList<EPackage>();
      config.initPackages(this, packages);
      if (!packages.isEmpty())
      {
        setInitialPackages(packages.toArray(new EPackage[packages.size()]));
      }
    }

    super.initSystemPackages(firstStart);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    if (!OMPlatform.INSTANCE.isExtensionRegistryAvailable())
    {
      IManagedContainer container = getContainer();
      Net4jUtil.prepareContainer(container);
      JVMUtil.prepareContainer(container);
      CDONet4jServerUtil.prepareContainer(container);
    }

    super.doBeforeActivate();

    InternalCDORevisionManager revisionManager = getRevisionManager();
    revisionManager.setRevisionLoader(new ClientAwareRevisionLoader(this));
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    IManagedContainer container = getContainer();
    container.putElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE, getName(), this);

    acceptor = createAcceptor(container);
    connector = createConnector(container);

    InternalStore store = getStore();
    if (store.isFirstStart())
    {
      config.afterFirstStart(this);
    }
    else
    {
      config.afterReStart(this);
    }
  }

  protected void doAfterFirstStart()
  {
    // Subclasses may override.
  }

  protected void doAfterReStart()
  {
    // Subclasses may override.
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    super.doBeforeDeactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(connector);
    connector = null;

    LifecycleUtil.deactivate(acceptor);
    acceptor = null;

    super.doDeactivate();
  }

  protected IAcceptor createAcceptor(IManagedContainer container)
  {
    return JVMUtil.getAcceptor(container, JVM_ACCEPTOR_PREFIX + getName());
  }

  protected IConnector createConnector(IManagedContainer container)
  {
    return JVMUtil.getConnector(container, JVM_ACCEPTOR_PREFIX + getName());
  }

  protected CDONet4jSessionConfiguration createSessionConfiguration(IConnector connector, CDORevisionManager revisionManager)
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(getName());
    configuration.setSignalTimeout(Integer.MAX_VALUE);
    configuration.setRevisionManager(revisionManager);
    config.modifySessionConfiguration(this, configuration);
    return configuration;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ClientAwareRevisionLoader implements RevisionLoader
  {
    private static final ThreadLocal<InternalSession> SERVER_SESSION = new ThreadLocal<InternalSession>();

    private final RevisionLoader delegate;

    private ClientAwareRevisionLoader(RevisionLoader delegate)
    {
      this.delegate = delegate;
    }

    public RevisionLoader getDelegate()
    {
      return delegate;
    }

    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
    {
      InternalSession serverSession = SERVER_SESSION.get();
      if (serverSession != null)
      {
        try
        {
          StoreThreadLocal.setSession(serverSession);
          return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth);
        }
        finally
        {
          StoreThreadLocal.release();
        }
      }

      return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth);
    }

    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
    {
      InternalSession serverSession = SERVER_SESSION.get();
      if (serverSession != null)
      {
        try
        {
          StoreThreadLocal.setSession(serverSession);
          return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
        }
        finally
        {
          StoreThreadLocal.release();
        }
      }

      return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
    }

    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
    {
      InternalSession serverSession = SERVER_SESSION.get();
      if (serverSession != null)
      {
        try
        {
          StoreThreadLocal.setSession(serverSession);
          delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
        }
        finally
        {
          StoreThreadLocal.release();
        }
      }
      else
      {
        delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DelegatingRevisionManager implements InternalCDORevisionManager
  {
    private final InternalCDORevisionManager delegate;

    private LifecycleState lifecycleState = LifecycleState.INACTIVE;

    private CDONet4jSession clientSession;

    private InternalSession serverSession;

    public DelegatingRevisionManager(InternalCDORevisionManager delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasListeners()
    {
      return delegate.hasListeners();
    }

    public IListener[] getListeners()
    {
      return delegate.getListeners();
    }

    public void addListener(IListener listener)
    {
      delegate.addListener(listener);
    }

    public void removeListener(IListener listener)
    {
      delegate.removeListener(listener);
    }

    public LifecycleState getLifecycleState()
    {
      return lifecycleState;
    }

    public boolean isActive()
    {
      return lifecycleState == LifecycleState.ACTIVE;
    }

    public void activate() throws LifecycleException
    {
      lifecycleState = LifecycleState.ACTIVE;
    }

    public Exception deactivate()
    {
      lifecycleState = LifecycleState.INACTIVE;
      serverSession = null;
      clientSession = null;
      return null;
    }

    public RevisionLocker getRevisionLocker()
    {
      return delegate.getRevisionLocker();
    }

    public void setRevisionLocker(RevisionLocker revisionLocker)
    {
      if (revisionLocker instanceof CDONet4jSession)
      {
        clientSession = (CDONet4jSession)revisionLocker;

        ClientAwareRevisionLoader revisionLoader = (ClientAwareRevisionLoader)delegate.getRevisionLoader();
        InternalRepository repository = (InternalRepository)revisionLoader.getDelegate();
        InternalSessionManager sessionManager = repository.getSessionManager();
        serverSession = sessionManager.getSession(clientSession.getSessionID());
      }
    }

    public RevisionLoader getRevisionLoader()
    {
      return delegate.getRevisionLoader();
    }

    public void setRevisionLoader(RevisionLoader revisionLoader)
    {
      // Do nothing.
    }

    public CDORevisionFactory getFactory()
    {
      return delegate.getFactory();
    }

    public void setFactory(CDORevisionFactory factory)
    {
      // Do nothing.
    }

    public InternalCDORevisionCache getCache()
    {
      return delegate.getCache();
    }

    public void setCache(CDORevisionCache cache)
    {
      // Do nothing.
    }

    public boolean isSupportingAudits()
    {
      return delegate.isSupportingAudits();
    }

    public void setSupportingAudits(boolean on)
    {
      // Do nothing.
    }

    public boolean isSupportingBranches()
    {
      return delegate.isSupportingBranches();
    }

    public void setSupportingBranches(boolean on)
    {
      // Do nothing.
    }

    public EClass getObjectType(CDOID id)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getObjectType(id);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getObjectType(id, branchManagerForLoadOnDemand);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public CDOBranchPointRange getObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getObjectLifetime(id, branchPoint);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.containsRevision(id, branchPoint);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.containsRevisionByVersion(id, branchVersion);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
        SyntheticCDORevision[] synthetics)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
        SyntheticCDORevision[] synthetics)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getRevisionByVersion(id, branchVersion, referenceChunk, loadOnDemand);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
    {
      try
      {
        setServerSession(serverSession);
        return delegate.getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
    {
      try
      {
        setServerSession(serverSession);
        delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public void addRevision(CDORevision revision)
    {
      try
      {
        setServerSession(serverSession);
        delegate.addRevision(revision);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public void reviseLatest(CDOID id, CDOBranch branch)
    {
      try
      {
        setServerSession(serverSession);
        delegate.reviseLatest(id, branch);
      }
      finally
      {
        unsetServerSession();
      }
    }

    public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
    {
      try
      {
        setServerSession(serverSession);
        delegate.reviseVersion(id, branchVersion, timeStamp);
      }
      finally
      {
        unsetServerSession();
      }
    }

    private static void setServerSession(InternalSession serverSession)
    {
      ClientAwareRevisionLoader.SERVER_SESSION.set(serverSession);
    }

    private static void unsetServerSession()
    {
      ClientAwareRevisionLoader.SERVER_SESSION.remove();
    }
  }
}
