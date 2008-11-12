/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOAudit;
import org.eclipse.emf.cdo.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOTimeStampContext;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDTempMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.protocol.LoadLibrariesRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionResult;
import org.eclipse.emf.internal.cdo.protocol.RepositoryTimeRequest;
import org.eclipse.emf.internal.cdo.protocol.RepositoryTimeResult;
import org.eclipse.emf.internal.cdo.protocol.SetPassiveUpdateRequest;
import org.eclipse.emf.internal.cdo.protocol.SyncRevisionRequest;
import org.eclipse.emf.internal.cdo.protocol.ViewsChangedRequest;
import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

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
public class CDOSessionImpl extends Container<CDOView> implements CDOSession, CDOIDObjectFactory,
    CDOPackageURICompressor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private int sessionID;

  private boolean passiveUpdateEnabled = true;

  private CDOCollectionLoadingPolicy collectionLoadingPolicy;

  private IConnector connector;

  private CDOClientProtocol protocol;

  @ExcludeFromDump
  private IListener channelListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  private String repositoryName;

  private String repositoryUUID;

  private long repositoryCreationTime;

  private RepositoryTimeResult repositoryTimeResult;

  private CDOPackageRegistry packageRegistry;

  private CDOSessionPackageManagerImpl packageManager;

  private CDORevisionManagerImpl revisionManager;

  private Set<CDOViewImpl> views = new HashSet<CDOViewImpl>();

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

  public CDOSessionImpl()
  {
    protocol = new CDOClientProtocol();
    protocol.setInfraStructure(this);

    packageManager = createPackageManager();
    revisionManager = createRevisionManager();

    // TODO Remove preferences from core
    collectionLoadingPolicy = CDOUtil.createCollectionLoadingPolicy(OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.getValue(),
        OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.getValue());
  }

  public int getSessionID()
  {
    return sessionID;
  }

  /**
   * @since 2.0
   */
  public String getUserID()
  {
    IChannel channel = protocol.getChannel();
    return channel == null ? null : channel.getUserID();
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
  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    return collectionLoadingPolicy;
  }

  /**
   * @since 2.0
   */
  public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
  {
    if (policy == null)
    {
      policy = CDOCollectionLoadingPolicy.DEFAULT;
    }

    collectionLoadingPolicy = policy;
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  /**
   * @noreference This method is not intended to be referenced by clients.
   */
  public CDOClientProtocol getProtocol()
  {
    return protocol;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public String getRepositoryUUID()
  {
    return repositoryUUID;
  }

  /**
   * @since 2.0
   */
  public long getRepositoryCreationTime()
  {
    checkActive();
    return repositoryCreationTime;
  }

  /**
   * @since 2.0
   */
  public long getRepositoryTime()
  {
    return getRepositoryTime(false);
  }

  /**
   * @since 2.0
   */
  public long getRepositoryTime(boolean forceRefresh)
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
    try
    {
      return new RepositoryTimeRequest(protocol).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public boolean isOpen()
  {
    IChannel channel = protocol.getChannel();
    return channel != null;
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

  public CDOTransactionImpl openTransaction(ResourceSet resourceSet)
  {
    checkActive();
    CDOTransactionImpl transaction = createTransaction(++lastViewID);
    attach(resourceSet, transaction);
    return transaction;
  }

  public CDOTransactionImpl openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  protected CDOTransactionImpl createTransaction(int id)
  {
    return new CDOTransactionImpl(id, this);
  }

  public CDOViewImpl openView(ResourceSet resourceSet)
  {
    checkActive();
    CDOViewImpl view = createView(++lastViewID);
    attach(resourceSet, view);
    return view;
  }

  public CDOViewImpl openView()
  {
    return openView(createResourceSet());
  }

  protected CDOViewImpl createView(int id)
  {
    return new CDOViewImpl(this, id);
  }

  public CDOAuditImpl openAudit(ResourceSet resourceSet, long timeStamp)
  {
    checkActive();
    CDOAuditImpl audit = createAudit(++lastViewID, timeStamp);
    attach(resourceSet, audit);
    return audit;
  }

  public CDOAuditImpl openAudit(long timeStamp)
  {
    return openAudit(createResourceSet(), timeStamp);
  }

  protected CDOAuditImpl createAudit(int id, long timeStamp)
  {
    return new CDOAuditImpl(id, this, timeStamp);
  }

  public void viewDetached(CDOViewImpl view)
  {
    // Detach viewset from the view
    ((CDOViewSetImpl)view.getViewSet()).remove(view);
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
        new ViewsChangedRequest(protocol, view.getViewID(), CDOProtocolConstants.VIEW_CLOSED,
            CDOProtocolView.UNSPECIFIED_DATE).send();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    fireElementRemovedEvent(view);
  }

  private void sendViewsChangedRequest(CDOViewImpl view)
  {
    try
    {
      int id = view.getViewID();
      byte kind = getKind(view);
      long timeStamp = CDOProtocolView.UNSPECIFIED_DATE;
      if (view instanceof CDOAudit)
      {
        timeStamp = ((CDOAudit)view).getTimeStamp();
      }

      new ViewsChangedRequest(protocol, id, kind, timeStamp).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private byte getKind(CDOViewImpl view)
  {
    CDOView.Type type = view.getViewType();
    switch (type)
    {
    case TRANSACTION:
      return CDOProtocolConstants.VIEW_TRANSACTION;

    case READONLY:
      return CDOProtocolConstants.VIEW_READONLY;

    case AUDIT:
      return CDOProtocolConstants.VIEW_AUDIT;

    default:
      throw new ImplementationError("Invalid view type: " + type);
    }
  }

  public CDOView getView(int viewID)
  {
    checkActive();
    for (CDOViewImpl view : getViews())
    {
      if (view.getViewID() == viewID)
      {
        return view;
      }
    }

    return null;
  }

  public CDOViewImpl[] getViews()
  {
    checkActive();
    synchronized (views)
    {
      return views.toArray(new CDOViewImpl[views.size()]);
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
    range = registerMetaInstance((InternalEObject)ePackage, range, idToMetaInstanceMap, metaInstanceToIDMap);
    if (range.size() != metaIDRange.size())
    {
      throw new IllegalStateException("range.size() != metaIDRange.size()");
    }
  }

  public CDOIDMetaRange registerEPackage(EPackage ePackage)
  {
    CDOIDMetaRange range = registerEPackage(ePackage, lastTempMetaID + 1, idToMetaInstanceMap, metaInstanceToIDMap);
    lastTempMetaID = ((CDOIDTempMeta)range.getUpperBound()).getIntValue();
    return range;
  }

  public static CDOIDMetaRange registerEPackage(EPackage ePackage, int firstMetaID,
      Map<CDOID, InternalEObject> idToMetaInstances, Map<InternalEObject, CDOID> metaInstanceToIDs)
  {
    CDOIDTemp lowerBound = CDOIDUtil.createTempMeta(firstMetaID);
    CDOIDMetaRange range = CDOIDUtil.createMetaRange(lowerBound, 0);
    range = registerMetaInstance((InternalEObject)ePackage, range, idToMetaInstances, metaInstanceToIDs);
    return range;
  }

  public static CDOIDMetaRange registerMetaInstance(InternalEObject metaInstance, CDOIDMetaRange range,
      Map<CDOID, InternalEObject> idToMetaInstances, Map<InternalEObject, CDOID> metaInstanceToIDs)
  {
    range = range.increase();
    CDOID id = range.getUpperBound();
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance);
    }

    if (idToMetaInstances != null)
    {
      if (idToMetaInstances.put(id, metaInstance) != null)
      {
        throw new IllegalStateException("Duplicate meta ID: " + id + " --> " + metaInstance);
      }
    }

    if (metaInstanceToIDs != null)
    {
      if (metaInstanceToIDs.put(metaInstance, id) != null)
      {
        throw new IllegalStateException("Duplicate metaInstance: " + metaInstance + " --> " + id);
      }
    }

    for (EObject content : metaInstance.eContents())
    {
      range = registerMetaInstance((InternalEObject)content, range, idToMetaInstances, metaInstanceToIDs);
    }

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
  public void handleCommitNotification(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedObjects, Collection<CDORevisionDelta> deltas, CDOViewImpl excludedView)
  {
    if (isPassiveUpdateEnabled())
    {
      notifyInvalidation(timeStamp, dirtyOIDs, detachedObjects, excludedView, true);
    }

    handleChangeSubcription(deltas, detachedObjects, excludedView);

  }

  /**
   * @since 2.0
   */
  public void handleSyncResponse(long timestamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
  {
    notifyInvalidation(timestamp, dirtyOIDs, detachedObjects, null, false);
  }

  private void notifyInvalidation(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedObjects, CDOViewImpl excludedView, boolean async)
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

    final Set<CDOIDAndVersion> finalDirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
    final Collection<CDOID> finalDetachedObjects = Collections.unmodifiableCollection(detachedObjects);

    for (final CDOViewImpl view : getViews())
    {
      if (view != excludedView)
      {
        if (async)
        {
          QueueRunner runner = getInvalidationRunner();
          runner.addWork(new Runnable()
          {
            public void run()
            {
              try
              {
                view.handleInvalidation(timeStamp, finalDirtyOIDs, finalDetachedObjects);
              }
              catch (RuntimeException ex)
              {
                OM.LOG.error(ex);
              }
            }
          });
        }
        else
        {
          view.handleInvalidation(timeStamp, finalDirtyOIDs, finalDetachedObjects);
        }
      }
    }

    fireInvalidationEvent(timeStamp, dirtyOIDs, detachedObjects, excludedView);
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

  private void handleChangeSubcription(Collection<CDORevisionDelta> deltas, Collection<CDOID> detachedObjects,
      CDOViewImpl excludedView)
  {
    if ((deltas == null || deltas.size() <= 0) && (detachedObjects == null || detachedObjects.size() <= 0))
    {
      return;
    }
    for (CDOViewImpl view : getViews())
    {
      if (view != excludedView)
      {
        try
        {
          view.handleChangeSubscription(deltas, detachedObjects);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void fireInvalidationEvent(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects,
      CDOViewImpl excludedView)
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
    IChannel channel = protocol.getChannel();
    return MessageFormat.format("CDOSession[{0}/{1}]", channel, repositoryName);
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

  protected ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  protected void attach(ResourceSet resourceSet, CDOViewImpl view)
  {
    CDOViewSet viewSet = CDOSessionImpl.prepareResourceSet(resourceSet);
    synchronized (views)
    {
      views.add(view);
    }

    // Link ViewSet with View
    view.setViewSet(viewSet);
    ((CDOViewSetImpl)viewSet).add(view);

    sendViewsChangedRequest(view);
    fireElementAddedEvent(view);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(connector, "connector == null");
    checkState(repositoryName, "repositoryName == null");
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
    IChannel channel = protocol.open(connector);

    OpenSessionResult result = new OpenSessionRequest(protocol, repositoryName, passiveUpdateEnabled).send();

    sessionID = result.getSessionID();
    repositoryUUID = result.getRepositoryUUID();
    repositoryCreationTime = result.getRepositoryCreationTime();
    repositoryTimeResult = result.getRepositoryTimeResult();
    handleLibraryDescriptor(result.getLibraryDescriptor());
    packageURICompressor = result.getCompressor();
    packageManager.addPackageProxies(result.getPackageInfos());
    packageManager.activate();
    revisionManager.activate();
    EventUtil.addListener(channel, channelListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IChannel channel = protocol.getChannel();
    EventUtil.removeListener(channel, channelListener);
    if (invalidationRunner != null)
    {
      invalidationRunner.deactivate();
      invalidationRunner = null;
    }

    revisionManager.deactivate();
    revisionManager = null;

    packageManager.deactivate();
    packageManager = null;

    for (CDOViewImpl view : views.toArray(new CDOViewImpl[views.size()]))
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

    channel.close();
    channel = null;
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
        new LoadLibrariesRequest(protocol, missingLibraries, cacheFolder).send();
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

  /**
   * @since 2.0
   */
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  private Map<CDOID, CDORevision> getAllRevisions()
  {
    Map<CDOID, CDORevision> uniqueObjects = new HashMap<CDOID, CDORevision>();
    for (CDOViewImpl view : getViews())
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
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    if (this.passiveUpdateEnabled != passiveUpdateEnabled)
    {
      this.passiveUpdateEnabled = passiveUpdateEnabled;

      // Need to refresh if we change state
      Map<CDOID, CDORevision> allRevisions = getAllRevisions();

      try
      {
        if (!allRevisions.isEmpty())
        {
          new SetPassiveUpdateRequest(protocol, this, allRevisions, collectionLoadingPolicy.getInitialChunkSize(),
              passiveUpdateEnabled).send();
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
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
    if (!isPassiveUpdateEnabled())
    {
      Map<CDOID, CDORevision> allRevisions = getAllRevisions();

      try
      {
        if (!allRevisions.isEmpty())
        {
          return new SyncRevisionRequest(protocol, this, allRevisions, collectionLoadingPolicy.getInitialChunkSize())
              .send();
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
   * @since 2.0
   */
  public static CDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    CDOViewSetImpl viewSet = null;
    synchronized (resourceSet)
    {
      viewSet = (CDOViewSetImpl)CDOUtil.getViewSet(resourceSet);
      if (viewSet == null)
      {
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }

    return viewSet;
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOViewImpl view;

    private long timeStamp;

    private Set<CDOIDAndVersion> dirtyOIDs;

    private Collection<CDOID> detachedObjects;

    public InvalidationEvent(CDOViewImpl view, long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
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
      return CDOSessionImpl.this;
    }

    public CDOViewImpl getView()
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
