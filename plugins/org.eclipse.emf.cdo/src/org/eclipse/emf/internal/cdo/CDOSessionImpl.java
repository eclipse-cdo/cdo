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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
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
import org.eclipse.emf.internal.cdo.protocol.LoadLibrariesRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionResult;
import org.eclipse.emf.internal.cdo.protocol.PassiveUpdateRequest;
import org.eclipse.emf.internal.cdo.protocol.SyncRevisionRequest;
import org.eclipse.emf.internal.cdo.protocol.ViewsChangedRequest;
import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverEvent;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.NOOPFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
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

  private int referenceChunkSize;

  private IFailOverStrategy failOverStrategy;

  @ExcludeFromDump
  private IListener failOverStrategyListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IFailOverEvent)
      {
        IFailOverEvent e = (IFailOverEvent)event;
        handleFailOver(e.getOldChannel(), e.getNewChannel(), e.getNewConnector());
      }
    }
  };

  private IConnector connector;

  private IChannel channel;

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

  private CDOPackageRegistry packageRegistry;

  private CDOSessionPackageManagerImpl packageManager;

  private CDORevisionManagerImpl revisionManager;

  private Set<CDOViewImpl> views = new HashSet<CDOViewImpl>();

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
    packageManager = createPackageManager();
    revisionManager = createRevisionManager();

    // TODO Remove preferences from core
    referenceChunkSize = OM.PREF_REFERENCE_CHUNK_SIZE.getValue();
  }

  public int getSessionID()
  {
    return sessionID;
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

  public int getReferenceChunkSize()
  {
    return referenceChunkSize;
  }

  public void setReferenceChunkSize(int referenceChunkSize)
  {
    if (referenceChunkSize <= 0)
    {
      referenceChunkSize = CDORevision.UNCHUNKED;
    }

    this.referenceChunkSize = referenceChunkSize;
  }

  public synchronized IFailOverStrategy getFailOverStrategy()
  {
    if (failOverStrategy == null)
    {
      failOverStrategy = new NOOPFailOverStrategy();
    }

    return failOverStrategy;
  }

  public synchronized void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    if (this.failOverStrategy != null)
    {
      this.failOverStrategy.removeListener(failOverStrategyListener);
    }

    this.failOverStrategy = failOverStrategy;
    if (this.failOverStrategy != null)
    {
      this.failOverStrategy.addListener(failOverStrategyListener);
    }
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  public IChannel getChannel()
  {
    return channel;
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

  public boolean isOpen()
  {
    return channel != null;
  }

  public void close()
  {
    deactivate();
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
    CDOTransactionImpl transaction = createTransaction(++lastViewID);
    attach(resourceSet, transaction);
    return transaction;
  }

  protected CDOTransactionImpl createTransaction(int id)
  {
    return new CDOTransactionImpl(id, this);
  }

  public CDOTransactionImpl openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  public CDOViewImpl openView(ResourceSet resourceSet)
  {
    CDOViewImpl view = createView(++lastViewID);
    attach(resourceSet, view);
    return view;
  }

  protected CDOViewImpl createView(int id)
  {
    return new CDOViewImpl(this, id);
  }

  public CDOViewImpl openView()
  {
    return openView(createResourceSet());
  }

  public CDOAuditImpl openAudit(ResourceSet resourceSet, long timeStamp)
  {
    CDOAuditImpl audit = createAudit(++lastViewID, timeStamp);
    attach(resourceSet, audit);
    return audit;
  }

  protected CDOAuditImpl createAudit(int id, long timeStamp)
  {
    return new CDOAuditImpl(id, this, timeStamp);
  }

  public CDOAuditImpl openAudit(long timeStamp)
  {
    return openAudit(createResourceSet(), timeStamp);
  }

  public CDOView getView(int viewID)
  {
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
    return views.isEmpty();
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

    try
    {
      getFailOverStrategy().send(new ViewsChangedRequest(channel, view.getViewID(), CDOProtocolConstants.VIEW_CLOSED));
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    fireElementRemovedEvent(view);
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
      notifyInvalidation(timeStamp, dirtyOIDs, detachedObjects, excludedView);
    }

    handleChangeSubcription(deltas, excludedView);
  }

  /**
   * @since 2.0
   */
  @SuppressWarnings("unchecked")
  public void handleSync(Set<CDOIDAndVersion> dirtyOIDs)
  {
    notifyInvalidation(CDORevision.UNSPECIFIED_DATE, dirtyOIDs, Collections.EMPTY_LIST, null);
  }

  private void notifyInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects,
      CDOViewImpl excludedView)
  {
    for (CDOIDAndVersion dirtyOID : dirtyOIDs)
    {
      InternalCDORevision revision = getRevisionManager().getRevisionByVersion(dirtyOID.getID(), 0,
          dirtyOID.getVersion(), false);
      if (revision != null)
      {
        revision.setRevised(timeStamp - 1);
      }
    }

    for (CDOID id : detachedObjects)
    {
      InternalCDORevision revision = getRevisionManager().getRevision(id, 0, false);
      if (revision != null)
      {
        revision.setRevised(timeStamp - 1);
      }
    }

    dirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
    detachedObjects = Collections.unmodifiableCollection(detachedObjects);

    for (CDOViewImpl view : getViews())
    {
      if (view != excludedView)
      {
        try
        {
          view.handleInvalidation(timeStamp, dirtyOIDs, detachedObjects);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    fireInvalidationEvent(timeStamp, dirtyOIDs, excludedView);
  }

  private void handleChangeSubcription(Collection<CDORevisionDelta> deltas, CDOViewImpl excludedView)
  {
    if (deltas == null || deltas.size() <= 0)
    {
      return;
    }

    for (CDOViewImpl view : getViews())
    {
      if (view != excludedView)
      {
        try
        {
          view.handleChangeSubscription(deltas);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  public void fireInvalidationEvent(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, CDOViewImpl excludedView)
  {
    fireEvent(new InvalidationEvent(excludedView, timeStamp, dirtyOIDs));
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
    return MessageFormat.format("CDOSession[{0}/{1}]", connector, repositoryName);
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
    CDOViewSet viewSet = CDOUtil.prepareResourceSet(resourceSet);
    synchronized (views)
    {
      views.add(view);
    }

    // Link ViewSet with View
    view.setViewSet(viewSet);
    ((CDOViewSetImpl)viewSet).add(view);

    sendViewsNotification(view);
    fireElementAddedEvent(view);
  }

  protected void handleFailOver(IChannel oldChannel, IChannel newChannel, IConnector newConnector)
  {
    EventUtil.removeListener(oldChannel, channelListener);
    EventUtil.addListener(newChannel, channelListener);

    channel = newChannel;
    connector = newConnector;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (channel == null && connector == null)
    {
      throw new IllegalStateException("channel == null && connector == null");
    }

    if (repositoryName == null)
    {
      throw new IllegalStateException("repositoryName == null");
    }
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

    if (channel == null)
    {
      channel = connector.openChannel(CDOProtocolConstants.PROTOCOL_NAME, this);
    }

    OpenSessionRequest request = new OpenSessionRequest(channel, repositoryName, passiveUpdateEnabled);
    OpenSessionResult result = getFailOverStrategy().send(request);

    sessionID = result.getSessionID();
    repositoryUUID = result.getRepositoryUUID();
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
    EventUtil.removeListener(channel, channelListener);
    revisionManager.deactivate();
    packageManager.deactivate();
    synchronized (views)
    {
      for (CDOViewImpl view : getViews())
      {
        try
        {
          view.close();
        }
        catch (RuntimeException ignore)
        {
        }
      }
    }

    channel.close();
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
        LoadLibrariesRequest request = new LoadLibrariesRequest(channel, missingLibraries, cacheFolder);
        getFailOverStrategy().send(request);
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

  private void sendViewsNotification(CDOViewImpl view)
  {
    try
    {
      int id = view.getViewID();
      byte kind = getKind(view);
      ViewsChangedRequest request = new ViewsChangedRequest(channel, id, kind);
      getFailOverStrategy().send(request);
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
    }

    throw new ImplementationError("Invalid view type: " + type);
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
          PassiveUpdateRequest request = new PassiveUpdateRequest(getChannel(), this, allRevisions,
              getReferenceChunkSize(), passiveUpdateEnabled);
          getFailOverStrategy().send(request);
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
  public Set<CDOIDAndVersion> refresh()
  {
    // If passive update is turned on we don`t need to refresh.
    // We do not throw an exception since the client could turn that feature on or off without affecting their code.
    if (!isPassiveUpdateEnabled())
    {
      Map<CDOID, CDORevision> allRevisions = getAllRevisions();

      try
      {
        if (!allRevisions.isEmpty())
        {
          SyncRevisionRequest request = new SyncRevisionRequest(getChannel(), this, allRevisions,
              getReferenceChunkSize());
          return getFailOverStrategy().send(request);
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    return Collections.emptySet();
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

    public InvalidationEvent(CDOViewImpl view, long timeStamp, Set<CDOIDAndVersion> dirtyOIDs)
    {
      super(CDOSessionImpl.this);
      this.view = view;
      this.timeStamp = timeStamp;
      this.dirtyOIDs = dirtyOIDs;
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

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent: " + dirtyOIDs;
    }
  }
}
