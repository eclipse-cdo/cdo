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
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.session.remote.CDORemoteSessionManagerImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.view.CDOAuditImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.OptionsEvent;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
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
public abstract class CDOSessionImpl extends Container<CDOView> implements InternalCDOSession
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private int sessionID;

  private CDOSession.Options options;

  private CDOSession.Repository repository;

  private InternalCDOPackageRegistry packageRegistry;

  private CDORevisionManagerImpl revisionManager;

  private InternalCDORemoteSessionManager remoteSessionManager;

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  @ExcludeFromDump
  private CDOIDObjectFactory cdoidObjectFactory;

  @ExcludeFromDump
  private transient QueueRunner invalidationRunner;

  @ExcludeFromDump
  private transient Object invalidationRunnerLock = new Object();

  @ExcludeFromDump
  private transient int lastViewID;

  public CDOSessionImpl()
  {
    options = createOptions();
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
  public CDOSession.Options options()
  {
    return options;
  }

  /**
   * @since 2.0
   */
  protected CDOSession.Options createOptions()
  {
    return new OptionsImpl();
  }

  /**
   * @since 2.0
   */
  public CDOSession.Repository repository()
  {
    return repository;
  }

  /**
   * @param result
   * @since 2.0
   */
  protected CDOSession.Repository createRepository(OpenSessionResult result)
  {
    return new RepositoryImpl(repository.getName(), result);
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

  public void setRepositoryName(String repositoryName)
  {
    repository = new TemporaryRepositoryName(repositoryName);
  }

  /**
   * @since 2.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = (InternalCDOPackageRegistry)packageRegistry;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    if (packageUnit.getOriginalType().isGenerated())
    {
      if (!options().isGeneratedPackageEmulationEnabled())
      {
        throw new CDOException("Generated packages locally not available: " + packageUnit);
      }
    }

    return getSessionProtocol().loadPackages(packageUnit);
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

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOSession[{0}, {1}]", repository().getName(), sessionID);
  }

  /**
   * @since 2.0
   */
  protected InternalCDOPackageRegistry createPackageRegistry()
  {
    return new CDOPackageRegistryImpl();
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
    checkState(repository().getName(), "repository().getName()");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    revisionManager.activate();
    remoteSessionManager.activate();
    if (packageRegistry == null)
    {
      packageRegistry = createPackageRegistry();
    }

    packageRegistry.setPackageProcessor(this);
    packageRegistry.setPackageLoader(this);
    packageRegistry.activate();
    // EMFUtil.registerPackage(EcorePackage.eINSTANCE, packageRegistry);
    // EMFUtil.registerPackage(EresourcePackage.eINSTANCE, packageRegistry);

    String name = repository().getName();
    boolean passiveUpdateEnabled = options().isPassiveUpdateEnabled();
    OpenSessionResult result = getSessionProtocol().openSession(name, passiveUpdateEnabled);

    sessionID = result.getSessionID();
    repository = createRepository(result);
    handleLibraryDescriptor(result.getLibraryDescriptor());

    for (InternalCDOPackageUnit packageUnit : result.getPackageUnits())
    {
      if (EcorePackage.eINSTANCE.getNsURI().equals(packageUnit.getID()))
      {
        EMFUtil.addAdapter(EcorePackage.eINSTANCE, packageUnit.getTopLevelPackageInfo());
        packageUnit.setState(CDOPackageUnit.State.LOADED);
      }
      else if (EresourcePackage.eINSTANCE.getNsURI().equals(packageUnit.getID()))
      {
        EMFUtil.addAdapter(EresourcePackage.eINSTANCE, packageUnit.getTopLevelPackageInfo());
        packageUnit.setState(CDOPackageUnit.State.LOADED);
      }

      packageRegistry.putPackageUnit(packageUnit);
    }
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

    packageRegistry.deactivate();
    packageRegistry = null;
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
    return new File(repos, repository().getUUID());
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
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean generatedPackageEmulationEnabled = false;

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

    public boolean isGeneratedPackageEmulationEnabled()
    {
      return generatedPackageEmulationEnabled;
    }

    public void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled)
    {
      this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
      if (this.generatedPackageEmulationEnabled != generatedPackageEmulationEnabled)
      {
        this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
        // TODO Check inconsistent state if switching off?

        fireEvent(new GeneratedPackageEmulationEventImpl());
      }
    }

    public boolean isPassiveUpdateEnabled()
    {
      return passiveUpdateEnabled;
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
          public CDORevision createRevision(EClass eClass, CDOID id)
          {
            return CDORevisionUtil.create(eClass, id);
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
    private final class GeneratedPackageEmulationEventImpl extends OptionsEvent implements
        GeneratedPackageEmulationEvent
    {
      private static final long serialVersionUID = 1L;

      public GeneratedPackageEmulationEventImpl()
      {
        super(OptionsImpl.this);
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

  /**
   * @author Eike Stepper
   */
  protected class RepositoryImpl implements CDOSession.Repository
  {
    private String name;

    private String uuid;

    private long creationTime;

    private RepositoryTimeResult timeResult;

    private boolean supportingAudits;

    public RepositoryImpl(String name, OpenSessionResult result)
    {
      this.name = name;
      uuid = result.getRepositoryUUID();
      creationTime = result.getRepositoryCreationTime();
      timeResult = result.getRepositoryTimeResult();
      supportingAudits = result.isRepositorySupportingAudits();
    }

    public String getName()
    {
      return name;
    }

    /**
     * Must be callable before session activation has finished!
     */
    public String getUUID()
    {
      return uuid;
    }

    public long getCreationTime()
    {
      checkActive();
      return creationTime;
    }

    public long getCurrentTime()
    {
      return getCurrentTime(false);
    }

    public long getCurrentTime(boolean forceRefresh)
    {
      checkActive();
      if (timeResult == null || forceRefresh)
      {
        timeResult = refreshTime();
      }

      return timeResult.getAproximateRepositoryTime();
    }

    public boolean isSupportingAudits()
    {
      return supportingAudits;
    }

    private RepositoryTimeResult refreshTime()
    {
      return getSessionProtocol().getRepositoryTime();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TemporaryRepositoryName implements CDOSession.Repository
  {
    private String name;

    public TemporaryRepositoryName(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public long getCreationTime()
    {
      throw new UnsupportedOperationException();
    }

    public long getCurrentTime()
    {
      throw new UnsupportedOperationException();
    }

    public long getCurrentTime(boolean forceRefresh)
    {
      throw new UnsupportedOperationException();
    }

    public String getUUID()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isSupportingAudits()
    {
      throw new UnsupportedOperationException();
    }
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
}
