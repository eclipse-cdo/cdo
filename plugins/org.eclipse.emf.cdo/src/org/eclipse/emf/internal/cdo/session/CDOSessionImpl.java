/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDTempMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOPackageRegistry;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSession.Repository;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.session.remote.CDORemoteSessionManagerImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;
import org.eclipse.emf.internal.cdo.view.CDOAuditImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.IOptions;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.OptionsEvent;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RepositoryTimeResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionImpl extends Container<CDOView> implements InternalCDOSession, Repository
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private int sessionID;

  private String repositoryName;

  private String repositoryUUID;

  private long repositoryCreationTime;

  private RepositoryTimeResult repositoryTimeResult;

  private boolean repositorySupportingAudits;

  private CDOPackageRegistry packageRegistry;

  private CDOSessionPackageManagerImpl packageManager;

  private CDORevisionManagerImpl revisionManager;

  private InternalCDORemoteSessionManager remoteSessionManager;

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  private QueueRunner invalidationRunner;

  private Object invalidationRunnerLock = new Object();

  @ExcludeFromDump
  private transient Map<CDOID, InternalEObject> idToMetaInstanceMap = new HashMap<CDOID, InternalEObject>();

  @ExcludeFromDump
  private transient Map<InternalEObject, CDOID> metaInstanceToIDMap = new HashMap<InternalEObject, CDOID>();

  @ExcludeFromDump
  private transient int lastViewID;

  @ExcludeFromDump
  private transient int lastTempMetaID;

  @ExcludeFromDump
  private transient StringCompressor packageURICompressor;

  @ExcludeFromDump
  private CDOIDObjectFactory cdoidObjectFactory;

  /**
   * @since 2.0
   */
  protected IOptions options;

  public CDOSessionImpl()
  {
    options = createOptions();
    packageManager = createPackageManager();
    revisionManager = createRevisionManager();
    remoteSessionManager = createRemoteSessionManager();
  }

  public int getSessionID()
  {
    return sessionID;
  }

  /**
   * @since 2.0
   */
  public OptionsImpl options()
  {
    return (OptionsImpl)options;
  }

  /**
   * @since 2.0
   */
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  /**
   * @since 2.0
   */
  public Repository repository()
  {
    return this;
  }

  public CDOIDObject createCDOIDObject(ExtendedDataInput in)
  {
    return cdoidObjectFactory.createCDOIDObject(in);
  }

  /**
   * @since 2.0
   */
  public CDOIDObject createCDOIDObject(String in)
  {
    return cdoidObjectFactory.createCDOIDObject(in);
  }

  /**
   * @since 2.0
   */
  public String getName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  /**
   * @since 2.0
   */
  public String getUUID()
  {
    return repositoryUUID;
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    checkActive();
    return repositoryCreationTime;
  }

  /**
   * @since 2.0
   */
  public long getCurrentTime()
  {
    return getCurrentTime(false);
  }

  /**
   * @since 2.0
   */
  public long getCurrentTime(boolean forceRefresh)
  {
    checkActive();
    if (repositoryTimeResult == null || forceRefresh)
    {
      repositoryTimeResult = sendRepositoryTimeRequest();
    }

    return repositoryTimeResult.getAproximateRepositoryTime();
  }

  private RepositoryTimeResult sendRepositoryTimeRequest()
  {
    return getSessionProtocol().getRepositoryTime();
  }

  /**
   * @since 2.0
   */
  public boolean isSupportingAudits()
  {
    return repositorySupportingAudits;
  }

  public void close()
  {
    deactivate();
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  /**
   * @since 2.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public CDOSessionPackageManagerImpl getPackageManager()
  {
    return packageManager;
  }

  public CDORevisionManagerImpl getRevisionManager()
  {
    return revisionManager;
  }

  public InternalCDORemoteSessionManager getRemoteSessionManager()
  {
    return remoteSessionManager;
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction();
    initView(transaction, resourceSet);
    return transaction;
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  /**
   * @since 2.0
   */
  protected InternalCDOTransaction createTransaction()
  {
    return new CDOTransactionImpl();
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView(ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView();
    initView(view, resourceSet);
    return view;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView()
  {
    return openView(createResourceSet());
  }

  /**
   * @since 2.0
   */
  protected InternalCDOView createView()
  {
    return new CDOViewImpl();
  }

  public CDOAuditImpl openAudit(ResourceSet resourceSet, long timeStamp)
  {
    checkActive();
    CDOAuditImpl audit = createAudit(timeStamp);
    initView(audit, resourceSet);
    return audit;
  }

  public CDOAuditImpl openAudit(long timeStamp)
  {
    return openAudit(createResourceSet(), timeStamp);
  }

  /**
   * @since 2.0
   */
  protected CDOAuditImpl createAudit(long timeStamp)
  {
    return new CDOAuditImpl(timeStamp);
  }

  /**
   * @since 2.0
   */
  public void viewDetached(InternalCDOView view)
  {
    // Detach viewset from the view
    view.getViewSet().remove(view);
    synchronized (views)
    {
      if (!views.remove(view))
      {
        return;
      }
    }

    if (isActive())
    {
      try
      {
        LifecycleUtil.deactivate(view);
        // new ViewsChangedRequest(protocol, view.getViewID(), CDOProtocolConstants.VIEW_CLOSED,
        // CDOCommonView.UNSPECIFIED_DATE).send();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    fireElementRemovedEvent(view);
  }

  public CDOView getView(int viewID)
  {
    checkActive();
    for (InternalCDOView view : getViews())
    {
      if (view.getViewID() == viewID)
      {
        return view;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView[] getViews()
  {
    checkActive();
    synchronized (views)
    {
      return views.toArray(new InternalCDOView[views.size()]);
    }
  }

  public CDOView[] getElements()
  {
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  public synchronized CDOIDMetaRange getTempMetaIDRange(int count)
  {
    CDOIDTemp lowerBound = CDOIDUtil.createTempMeta(lastTempMetaID + 1);
    lastTempMetaID += count;
    return CDOIDUtil.createMetaRange(lowerBound, count);
  }

  public InternalEObject lookupMetaInstance(CDOID id)
  {
    InternalEObject metaInstance = idToMetaInstanceMap.get(id);
    if (metaInstance == null)
    {
      CDOPackage[] cdoPackages = packageManager.getPackages();
      for (CDOPackage cdoPackage : cdoPackages)
      {
        CDOIDMetaRange metaIDRange = cdoPackage.getMetaIDRange();
        if (metaIDRange != null && metaIDRange.contains(id))
        {
          EPackage ePackage = ModelUtil.getEPackage(cdoPackage, packageRegistry);
          registerEPackage(ePackage);
          metaInstance = idToMetaInstanceMap.get(id);
          break;
        }
      }
    }

    return metaInstance;
  }

  public CDOID lookupMetaInstanceID(InternalEObject metaInstance)
  {
    return metaInstanceToIDMap.get(metaInstance);
  }

  public void registerEPackage(EPackage ePackage, CDOIDMetaRange metaIDRange)
  {
    if (metaIDRange.isTemporary())
    {
      throw new IllegalArgumentException("metaIDRange.isTemporary()");
    }

    CDOIDMetaRange range = CDOIDUtil.createMetaRange(metaIDRange.getLowerBound(), 0);
    range = SessionUtil
        .registerMetaInstance((InternalEObject)ePackage, range, idToMetaInstanceMap, metaInstanceToIDMap);
    if (range.size() != metaIDRange.size())
    {
      throw new IllegalStateException("range.size() != metaIDRange.size()");
    }
  }

  public CDOIDMetaRange registerEPackage(EPackage ePackage)
  {
    CDOIDMetaRange range = SessionUtil.registerEPackage(ePackage, lastTempMetaID + 1, idToMetaInstanceMap,
        metaInstanceToIDMap);
    lastTempMetaID = ((CDOIDTempMeta)range.getUpperBound()).getIntValue();
    return range;
  }

  public void remapMetaInstance(CDOID oldID, CDOID newID)
  {
    InternalEObject metaInstance = idToMetaInstanceMap.remove(oldID);
    if (metaInstance == null)
    {
      throw new IllegalArgumentException("Unknown meta instance id: " + oldID);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping meta instance: {0} --> {1} <-> {2}", oldID, newID, metaInstance);
    }

    idToMetaInstanceMap.put(newID, metaInstance);
    metaInstanceToIDMap.put(metaInstance, newID);
  }

  /**
   * @since 2.0
   */
  public void handleSyncResponse(long timestamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
  {
    handleCommitNotification(timestamp, dirtyOIDs, detachedObjects, null, null, true, false);
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      final Collection<CDOID> detachedObjects, final Collection<CDORevisionDelta> deltas, InternalCDOView excludedView)
  {
    handleCommitNotification(timeStamp, dirtyOIDs, detachedObjects, deltas, excludedView, options()
        .isPassiveUpdateEnabled(), true);
  }

  private void handleCommitNotification(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      final Collection<CDOID> detachedObjects, final Collection<CDORevisionDelta> deltas, InternalCDOView excludedView,
      final boolean passiveUpdate, boolean async)
  {
    if (passiveUpdate)
    {
      updateRevisionForRemoteChanges(timeStamp, dirtyOIDs, detachedObjects, excludedView);
    }

    final Set<CDOIDAndVersion> finalDirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
    final Collection<CDOID> finalDetachedObjects = Collections.unmodifiableCollection(detachedObjects);
    final boolean skipChangeSubscription = (deltas == null || deltas.size() <= 0)
        && (detachedObjects == null || detachedObjects.size() <= 0);

    for (final InternalCDOView view : getViews())
    {
      if (view != excludedView)
      {
        Runnable runnable = new Runnable()
        {
          public void run()
          {
            try
            {
              Set<CDOObject> conflicts = null;
              if (passiveUpdate)
              {
                conflicts = view.handleInvalidation(timeStamp, finalDirtyOIDs, finalDetachedObjects);
              }

              if (!skipChangeSubscription)
              {
                view.handleChangeSubscription(deltas, detachedObjects);
              }

              if (conflicts != null)
              {
                ((InternalCDOTransaction)view).handleConflicts(conflicts);
              }
            }
            catch (RuntimeException ex)
            {
              OM.LOG.error(ex);
            }
          }
        };

        if (async)
        {
          QueueRunner runner = getInvalidationRunner();
          runner.addWork(runnable);
        }
        else
        {
          runnable.run();
        }
      }
    }

    fireInvalidationEvent(timeStamp, dirtyOIDs, detachedObjects, excludedView);
  }

  private void updateRevisionForRemoteChanges(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedObjects, InternalCDOView excludedView)
  {
    // revised is done automatically when postCommit is CDOTransaction.postCommit is happening
    // Detached are not revised through postCommit
    if (excludedView == null || timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      for (CDOIDAndVersion dirtyOID : dirtyOIDs)
      {
        CDOID id = dirtyOID.getID();
        int version = dirtyOID.getVersion();
        InternalCDORevision revision = revisionManager.getRevisionByVersion(id, 0, version, false);
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          revisionManager.removeCachedRevision(revision.getID(), revision.getVersion());
        }
        else if (revision != null)
        {
          revision.setRevised(timeStamp - 1);
        }
      }
    }

    for (CDOID id : detachedObjects)
    {
      InternalCDORevision revision = revisionManager.getRevision(id, 0, false);
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        revisionManager.removeCachedRevision(revision.getID(), revision.getVersion());
      }
      else if (revision != null)
      {
        revision.setRevised(timeStamp - 1);
      }
    }
  }

  private QueueRunner getInvalidationRunner()
  {
    synchronized (invalidationRunnerLock)
    {
      if (invalidationRunner == null)
      {
        invalidationRunner = new QueueRunner();
        invalidationRunner.activate();
      }
    }

    return invalidationRunner;
  }

  /**
   * @since 2.0
   */
  public void fireInvalidationEvent(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects,
      InternalCDOView excludedView)
  {
    fireEvent(new InvalidationEvent(excludedView, timeStamp, dirtyOIDs, detachedObjects));
  }

  /**
   * @since 2.0
   */
  public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
  {
    packageURICompressor.write(out, uri);
  }

  /**
   * @since 2.0
   */
  public String readPackageURI(ExtendedDataInput in) throws IOException
  {
    return packageURICompressor.read(in);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOSession[{0}, {1}]", repositoryName, sessionID);
  }

  /**
   * @since 2.0
   */
  protected CDOPackageRegistry createPackageRegistry()
  {
    return new CDOPackageRegistryImpl();
  }

  protected CDOSessionPackageManagerImpl createPackageManager()
  {
    return new CDOSessionPackageManagerImpl(this);
  }

  protected CDORevisionManagerImpl createRevisionManager()
  {
    return new CDORevisionManagerImpl(this);
  }

  protected InternalCDORemoteSessionManager createRemoteSessionManager()
  {
    return new CDORemoteSessionManagerImpl(this);
  }

  protected ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  /**
   * @since 2.0
   */
  protected void initView(InternalCDOView view, ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = SessionUtil.prepareResourceSet(resourceSet);
    synchronized (views)
    {
      view.setSession(this);
      view.setViewID(++lastViewID);
      views.add(view);
    }

    // Link ViewSet with View
    view.setViewSet(viewSet);
    viewSet.add(view);

    try
    {
      view.activate();
      fireElementAddedEvent(view);
    }
    catch (RuntimeException ex)
    {
      synchronized (views)
      {
        views.remove(view);
      }

      viewSet.remove(view);
      throw ex;
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(getSessionProtocol(), "sessionProtocol");
    checkState(repositoryName, "repositoryName");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (packageRegistry == null)
    {
      packageRegistry = createPackageRegistry();
    }

    packageRegistry.setSession(this);

    OpenSessionResult result = getSessionProtocol().openSession(repositoryName, options().isPassiveUpdateEnabled());
    sessionID = result.getSessionID();
    repositoryUUID = result.getRepositoryUUID();
    repositoryCreationTime = result.getRepositoryCreationTime();
    repositoryTimeResult = result.getRepositoryTimeResult();
    repositorySupportingAudits = result.isRepositorySupportingAudits();
    handleLibraryDescriptor(result.getLibraryDescriptor());
    packageURICompressor = result.getCompressor();
    packageManager.addPackageProxies(result.getPackageInfos());
    packageManager.activate();
    revisionManager.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (InternalCDOView view : views.toArray(new InternalCDOView[views.size()]))
    {
      try
      {
        view.close();
      }
      catch (RuntimeException ignore)
      {
      }
    }

    views.clear();
    views = null;

    if (invalidationRunner != null)
    {
      invalidationRunner.deactivate();
      invalidationRunner = null;
    }

    revisionManager.deactivate();
    revisionManager = null;

    packageManager.deactivate();
    packageManager = null;
    super.doDeactivate();
  }

  private void handleLibraryDescriptor(CDOIDLibraryDescriptor libraryDescriptor) throws Exception
  {
    String factoryName = libraryDescriptor.getFactoryName();
    if (TRACER.isEnabled())
    {
      TRACER.format("Using CDOID factory: {0}", factoryName);
    }

    File cacheFolder = getCacheFolder();
    ClassLoader classLoader = OM.class.getClassLoader();

    Set<String> neededLibraries = createSet(libraryDescriptor.getLibraryNames());
    if (!neededLibraries.isEmpty())
    {

      IOUtil.mkdirs(cacheFolder);
      Set<String> existingLibraries = createSet(cacheFolder.list());
      Set<String> missingLibraries = new HashSet<String>(neededLibraries);
      missingLibraries.removeAll(existingLibraries);
      if (!missingLibraries.isEmpty())
      {
        getSessionProtocol().loadLibraries(missingLibraries, cacheFolder);
      }
    }

    int i = 0;
    URL[] urls = new URL[neededLibraries.size()];
    for (String neededLibrary : neededLibraries)
    {
      File lib = new File(cacheFolder, neededLibrary);
      if (TRACER.isEnabled())
      {
        TRACER.format("Using CDOID library: {0}", lib.getAbsolutePath());
      }

      urls[i++] = new URL("file:///" + lib.getAbsolutePath());
    }

    classLoader = new URLClassLoader(urls, classLoader);
    Class<?> factoryClass = classLoader.loadClass(factoryName);
    cdoidObjectFactory = (CDOIDObjectFactory)factoryClass.newInstance();
  }

  private File getCacheFolder()
  {
    String stateLocation = OM.BUNDLE.getStateLocation();
    File repos = new File(stateLocation, "repos");
    return new File(repos, repositoryUUID);
  }

  private Set<String> createSet(String[] fileNames)
  {
    Set<String> set = new HashSet<String>();
    for (String fileName : fileNames)
    {
      if (fileName.endsWith(".jar"))
      {
        set.add(fileName);
      }
    }

    return set;
  }

  private Map<CDOID, CDORevision> getAllRevisions()
  {
    Map<CDOID, CDORevision> uniqueObjects = new HashMap<CDOID, CDORevision>();
    for (InternalCDOView view : getViews())
    {
      for (InternalCDOObject internalCDOObject : view.getObjectsArray())
      {
        if (internalCDOObject.cdoRevision() != null && !internalCDOObject.cdoID().isTemporary()
            && !uniqueObjects.containsKey(internalCDOObject.cdoID()))
        {
          uniqueObjects.put(internalCDOObject.cdoID(), internalCDOObject.cdoRevision());
        }
      }
    }

    // Need to add Revision from revisionManager since we do not have all objects in view.
    for (CDORevision revision : getRevisionManager().getCachedRevisions())
    {
      if (!uniqueObjects.containsKey(revision.getID()))
      {
        uniqueObjects.put(revision.getID(), revision);
      }
    }

    return uniqueObjects;
  }

  /**
   * @since 2.0
   */
  public Collection<CDOTimeStampContext> refresh()
  {
    // If passive update is turned on we don`t need to refresh.
    // We do not throw an exception since the client could turn
    // that feature on or off without affecting their code.
    checkActive();
    if (!options().isPassiveUpdateEnabled())
    {
      Map<CDOID, CDORevision> allRevisions = getAllRevisions();

      try
      {
        if (!allRevisions.isEmpty())
        {
          int initialChunkSize = options().getCollectionLoadingPolicy().getInitialChunkSize();
          return getSessionProtocol().syncRevisions(allRevisions, initialChunkSize);
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    return Collections.emptyList();
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOView view;

    private long timeStamp;

    private Set<CDOIDAndVersion> dirtyOIDs;

    private Collection<CDOID> detachedObjects;

    public InvalidationEvent(InternalCDOView view, long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
        Collection<CDOID> detachedObjects)
    {
      super(CDOSessionImpl.this);
      this.view = view;
      this.timeStamp = timeStamp;
      this.dirtyOIDs = dirtyOIDs;
      this.detachedObjects = detachedObjects;
    }

    public CDOSession getSession()
    {
      return (CDOSession)getSource();
    }

    public InternalCDOView getView()
    {
      return view;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Set<CDOIDAndVersion> getDirtyOIDs()
    {
      return dirtyOIDs;
    }

    public Collection<CDOID> getDetachedObjects()
    {
      return detachedObjects;
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent: " + dirtyOIDs;
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean passiveUpdateEnabled = true;

    private CDOCollectionLoadingPolicy collectionLoadingPolicy;

    private CDORevisionFactory revisionFactory;

    public OptionsImpl()
    {
      // TODO Remove preferences from core
      int value = OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.getValue();
      collectionLoadingPolicy = CDOUtil.createCollectionLoadingPolicy(value, value);
    }

    public IOptionsContainer getContainer()
    {
      return CDOSessionImpl.this;
    }

    public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
      if (this.passiveUpdateEnabled != passiveUpdateEnabled)
      {
        this.passiveUpdateEnabled = passiveUpdateEnabled;

        // Need to refresh if we change state
        Map<CDOID, CDORevision> allRevisions = getAllRevisions();
        if (!allRevisions.isEmpty())
        {
          int initialChunkSize = collectionLoadingPolicy.getInitialChunkSize();
          getSessionProtocol().setPassiveUpdate(allRevisions, initialChunkSize, passiveUpdateEnabled);
        }

        fireEvent(new PassiveUpdateEventImpl());
      }
    }

    public boolean isPassiveUpdateEnabled()
    {
      return passiveUpdateEnabled;
    }

    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      return collectionLoadingPolicy;
    }

    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
    {
      if (policy == null)
      {
        policy = CDOCollectionLoadingPolicy.DEFAULT;
      }

      if (collectionLoadingPolicy != policy)
      {
        collectionLoadingPolicy = policy;
        fireEvent(new CollectionLoadingPolicyEventImpl());
      }
    }

    public synchronized CDORevisionFactory getRevisionFactory()
    {
      if (revisionFactory == null)
      {
        revisionFactory = new CDORevisionFactory()
        {
          public CDORevision createRevision(CDOClass cdoClass, CDOID id)
          {
            return CDORevisionUtil.create(cdoClass, id);
          }

          public CDORevision createRevision(CDODataInput in) throws IOException
          {
            return CDORevisionUtil.read(in);
          }

          @Override
          public String toString()
          {
            return "DefaultRevisionFactory";
          }
        };
      }

      return revisionFactory;
    }

    public synchronized void setRevisionFactory(CDORevisionFactory revisionFactory)
    {
      if (this.revisionFactory != revisionFactory)
      {
        this.revisionFactory = revisionFactory;
        fireEvent(new RevisionFactoryEventImpl());
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class PassiveUpdateEventImpl extends OptionsEvent implements PassiveUpdateEvent
    {
      private static final long serialVersionUID = 1L;

      public PassiveUpdateEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CollectionLoadingPolicyEventImpl extends OptionsEvent implements CollectionLoadingPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public CollectionLoadingPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class RevisionFactoryEventImpl extends OptionsEvent implements RevisionFactoryEvent
    {
      private static final long serialVersionUID = 1L;

      public RevisionFactoryEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }
}
