/*
 * Copyright (c) 2010-2016, 2018-2021, 2023-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitHistoryProviderImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDORegistrationHandler;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent;
import org.eclipse.emf.cdo.view.CDOViewEvent;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewProviderChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOLegacyAdapter;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.AbstractCloseableIterator;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.CriticalSection.SynchronizedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.SelfAttachingContainerListener.DoNotDescend;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.KeyedReference;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap2;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOView extends CDOCommitHistoryProviderImpl<CDOObject, CDOObjectHistory> implements InternalCDOView, DoNotDescend
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, AbstractCDOView.class);

  private static final String REPOSITORY_NAME_KEY = "cdo.repository.name";

  private static final String SAFE_RENAME = "~renamed";

  private static final Set<String> LEGACY_MODELS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.view.REPORT_LEGACY_MODELS")
      ? Collections.synchronizedSet(new HashSet<>())
      : null;

  private static final ThreadLocal<Lock> NEXT_VIEW_LOCK = new ThreadLocal<>();

  private final ViewAndState[] viewAndStates = ViewAndState.create(this);

  private final CDOURIHandler uriHandler = new CDOURIHandler(this);

  protected final CriticalSection sync;

  protected final Condition viewLockCondition;

  private final BranchPointLock branchPointLock = new BranchPointLock();

  private CDOBranchPoint branchPoint;

  private CDOBranchPoint normalizedBranchPoint;

  private URI uri;

  private CDOViewProvider provider;

  private InternalCDOViewSet viewSet;

  private Map<CDOID, InternalCDOObject> objects;

  private int objectCreationCounter;

  private CDOStore store = new CDOStoreImpl(this);

  private CDOResourceImpl rootResource;

  private CDOID rootResourceID;

  private final ConcurrentArray<CDOObjectHandler> objectHandlers = new ConcurrentArray<CDOObjectHandler>()
  {
    @Override
    protected CDOObjectHandler[] newArray(int length)
    {
      return new CDOObjectHandler[length];
    }
  };

  private final ConcurrentArray<CDORegistrationHandler> registrationHandlers = new ConcurrentArray<CDORegistrationHandler>()
  {
    @Override
    protected CDORegistrationHandler[] newArray(int length)
    {
      return new CDORegistrationHandler[length];
    }
  };

  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

  @ExcludeFromDump
  private transient Map<String, CDOID> resourcePathCache = new HashMap<>();

  @ExcludeFromDump
  private transient CDOID lastLookupID;

  @ExcludeFromDump
  private transient InternalCDOObject lastLookupObject;

  @ExcludeFromDump
  private transient boolean bypassRegistrationHandlers;

  public AbstractCDOView(CDOSession session, CDOBranchPoint branchPoint)
  {
    this(session);

    if (session != null)
    {
      // The basicSetBranchPoint() method adjusts the branch point with the "session" field.
      // As this field is not yet set adjust the branch point already here:
      branchPoint = CDOBranchUtil.adjustBranchPoint(branchPoint, session.getBranchManager());
    }

    basicSetBranchPoint(branchPoint);
  }

  public AbstractCDOView(CDOSession session)
  {
    Lock lock = NEXT_VIEW_LOCK.get();
    if (lock != null)
    {
      NEXT_VIEW_LOCK.remove();
    }
    else if (session != null && session.options().isDelegableViewLockEnabled())
    {
      lock = new DelegableReentrantLock();
    }

    sync = lock != null ? new LockedCriticalSection(lock) : new SynchronizedCriticalSection(this);
    viewLockCondition = sync.newCondition();

    initObjectsMap(ReferenceType.SOFT);
  }

  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Override
  public String getRepositoryName()
  {
    Object repositoryName = properties.get(REPOSITORY_NAME_KEY);
    if (repositoryName instanceof String)
    {
      return (String)repositoryName;
    }

    return getSession().getRepositoryInfo().getName();
  }

  @Override
  public void setRepositoryName(String repositoryName)
  {
    properties.put(REPOSITORY_NAME_KEY, repositoryName);
  }

  @Override
  public boolean isReadOnly()
  {
    return true;
  }

  @Override
  public boolean isHistorical()
  {
    synchronized (branchPointLock)
    {
      return branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
    }
  }

  @Override
  @Deprecated
  public boolean isLegacyModeEnabled()
  {
    return true;
  }

  protected final Map<CDOID, InternalCDOObject> getModifiableObjects()
  {
    return objects;
  }

  public int purgeUnusedObjects()
  {
    if (objects instanceof ReferenceValueMap2)
    {
      ReferenceValueMap2<CDOID, InternalCDOObject> map = (ReferenceValueMap2<CDOID, InternalCDOObject>)objects;
      Method method = ReflectUtil.getMethod(ReferenceValueMap2.class, "internalPurgeQueue");
      return (Integer)ReflectUtil.invokeMethod(method, map);
    }

    return 0;
  }

  @Override
  public Map<CDOID, InternalCDOObject> getObjects()
  {
    return sync.supply(() -> {
      if (objects == null)
      {
        return Collections.emptyMap();
      }

      return Collections.unmodifiableMap(objects);
    });
  }

  protected final void setObjects(Map<CDOID, InternalCDOObject> objects)
  {
    sync.run(() -> this.objects = objects);
  }

  protected boolean initObjectsMap(ReferenceType referenceType)
  {
    ReferenceValueMap2<CDOID, InternalCDOObject> newObjects;

    switch (referenceType)
    {
    case STRONG:
    {
      if (objects instanceof ReferenceValueMap2.Strong<?, ?>)
      {
        return false;
      }

      Map<CDOID, KeyedReference<CDOID, InternalCDOObject>> map = CDOIDUtil.createMap();
      newObjects = new ReferenceValueMap2.Strong<CDOID, InternalCDOObject>(map)
      {
        @Override
        protected void purged(CDOID id)
        {
          objectCollected(id);
        }
      };

      break;
    }

    case SOFT:
    {
      if (objects instanceof ReferenceValueMap2.Soft<?, ?>)
      {
        return false;
      }

      Map<CDOID, KeyedReference<CDOID, InternalCDOObject>> map = CDOIDUtil.createMap();
      newObjects = new ReferenceValueMap2.Soft<CDOID, InternalCDOObject>(map)
      {
        @Override
        protected void purged(CDOID id)
        {
          objectCollected(id);
        }
      };

      break;
    }

    case WEAK:
    {
      if (objects instanceof ReferenceValueMap2.Weak<?, ?>)
      {
        return false;
      }

      Map<CDOID, KeyedReference<CDOID, InternalCDOObject>> map = CDOIDUtil.createMap();
      newObjects = new ReferenceValueMap2.Weak<CDOID, InternalCDOObject>(map)
      {
        @Override
        protected void purged(CDOID id)
        {
          objectCollected(id);
        }
      };

      break;
    }

    default:
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.29")); //$NON-NLS-1$
    }

    if (objects == null)
    {
      setObjects(newObjects);
    }
    else
    {
      for (

      Map.Entry<CDOID, InternalCDOObject> entry : objects.entrySet())
      {

        InternalCDOObject object = entry.getValue();
        if (object != null)
        {
          newObjects.put(entry.getKey(), object);
        }
      }

      Map<CDOID, InternalCDOObject> oldObjects = objects;

      setObjects(newObjects);
      oldObjects.clear();
    }

    return true;

  }

  @Override
  public ViewAndState getViewAndState(CDOState state)
  {
    return viewAndStates[state.ordinal()];
  }

  @Override
  public CDOStore getStore()
  {
    checkActive();
    return store;
  }

  @Override
  public ResourceSet getResourceSet()
  {
    InternalCDOViewSet viewSet = getViewSet();
    return viewSet == null ? null : viewSet.getResourceSet();
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOViewSet getViewSet()
  {
    return viewSet;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setViewSet(InternalCDOViewSet viewSet)
  {
    this.viewSet = viewSet;
    if (viewSet != null)
    {
      viewSet.getResourceSet().getURIConverter().getURIHandlers().add(0, getURIHandler());
    }
  }

  @Override
  public CriticalSection sync()
  {
    return sync;
  }

  @Override
  @Deprecated
  public final Object getViewMonitor()
  {
    if (sync instanceof LockedCriticalSection)
    {
      return new NOOPMonitor();
    }

    return this;
  }

  @Override
  @Deprecated
  public final Lock getViewLock()
  {
    if (sync instanceof LockedCriticalSection)
    {
      return ((LockedCriticalSection)sync).getLock();
    }

    return null;
  }

  @Override
  @Deprecated
  public final void lockView()
  {
    if (sync instanceof LockedCriticalSection)
    {
      ((LockedCriticalSection)sync).getLock().lock();
    }
  }

  @Override
  @Deprecated
  public final void unlockView()
  {
    if (sync instanceof LockedCriticalSection)
    {
      ((LockedCriticalSection)sync).getLock().unlock();
    }
  }

  @Override
  @Deprecated
  public void syncExec(Runnable runnable)
  {
    sync.run(runnable);
  }

  @Override
  @Deprecated
  public <V> V syncExec(Callable<V> callable) throws Exception
  {
    return sync.call(callable);
  }

  @Override
  public CDOViewProvider getProvider()
  {
    return provider;
  }

  @Override
  public void setProvider(CDOViewProvider provider)
  {
    CDOViewProvider oldProvider = this.provider;
    if (provider != oldProvider)
    {
      this.provider = provider;

      if (viewSet != null)
      {
        viewSet.remapView(this);
      }

      if (provider instanceof CDOViewProvider2)
      {
        CDOViewProvider2 provider2 = (CDOViewProvider2)provider;
        uri = provider2.getViewURI(this);
        uriHandler.setViewProvider2(provider2);
      }
      else
      {
        uri = null;
        uriHandler.setViewProvider2(null);
      }

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new ViewProviderChangedEvent(oldProvider, provider), listeners);
      }
    }
  }

  @Override
  public URI getURI()
  {
    return uri;
  }

  @Override
  public void setSession(InternalCDOSession session)
  {
    rootResourceID = session.getRepositoryInfo().getRootResourceID();
    if (rootResourceID == null || rootResourceID.isNull())
    {
      throw new IllegalStateException("RootResourceID is null; is the repository not yet initialized?");
    }
  }

  @Override
  public CDOResourceImpl getRootResource()
  {
    checkActive();

    return sync.supply(() -> {
      if (rootResource == null)
      {
        getObject(rootResourceID);
        CheckUtil.checkState(rootResource, "rootResource"); //$NON-NLS-1$
      }

      return rootResource;
    });
  }

  private void setRootResource(CDOResourceImpl resource)
  {
    rootResource = resource;
    rootResource.setRoot(true);
    registerObject(rootResource);

    try
    {
      rootResource.load(null);
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public URI createResourceURI(String path)
  {
    if (provider != null)
    {
      URI uri = provider.getResourceURI(this, path);
      if (uri != null)
      {
        return uri;
      }
    }

    InternalCDOSession session = getSession();
    return CDOURIUtil.createResourceURI(session, path);
  }

  @Override
  public boolean isEmpty()
  {
    return sync.supply(() -> {
      CDOResource rootResource = getRootResource();
      if (rootResource.cdoPermission() == CDOPermission.NONE)
      {
        return true;
      }

      boolean empty = rootResource.getContents().isEmpty();
      ensureContainerAdapter(rootResource);
      return empty;
    });
  }

  @Override
  public CDOResourceNode[] getElements()
  {
    List<CDOResourceNode> elements = new ArrayList<>(0);

    sync.run(() -> {
      if (isActive())
      {
        CDOResource rootResource = getRootResource();
        EList<EObject> contents = rootResource.getContents();

        for (EObject object : contents)
        {
          if (object instanceof CDOResourceNode)
          {
            CDOResourceNode element = (CDOResourceNode)object;
            elements.add(element);
          }
        }

        ensureContainerAdapter(rootResource);
      }
    });

    return elements.toArray(new CDOResourceNode[elements.size()]);
  }

  private void ensureContainerAdapter(final CDOResource rootResource)
  {
    EList<Adapter> adapters = rootResource.eAdapters();
    ContainerAdapter adapter = getContainerAdapter(adapters);
    if (adapter == null)
    {
      adapter = new ContainerAdapter();
      adapters.add(adapter);

      options().addChangeSubscriptionPolicy(new CDOAdapterPolicy()
      {
        @Override
        public boolean isValid(EObject eObject, Adapter adapter)
        {
          return eObject == rootResource;
        }
      });
    }
  }

  private ContainerAdapter getContainerAdapter(EList<Adapter> adapters)
  {
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof ContainerAdapter && ((ContainerAdapter)adapter).getView() == this)
      {
        return (ContainerAdapter)adapter;
      }
    }

    return null;
  }

  @Override
  public CDOURIHandler getURIHandler()
  {
    return uriHandler;
  }

  protected CDOBranchPoint getBranchPoint()
  {
    synchronized (branchPointLock)
    {
      return branchPoint;
    }
  }

  protected CDOBranchPoint getNormalizedBranchPoint()
  {
    synchronized (branchPointLock)
    {
      return normalizedBranchPoint;
    }
  }

  @Override
  public boolean setBranch(CDOBranch branch)
  {
    return setBranchPoint(branch, getTimeStamp(), null);
  }

  @Override
  public boolean setBranch(CDOBranch branch, IProgressMonitor monitor)
  {
    return setBranchPoint(branch, getTimeStamp(), monitor);
  }

  @Override
  public boolean setTimeStamp(long timeStamp)
  {
    return setBranchPoint(getBranch(), timeStamp, null);
  }

  @Override
  public boolean setTimeStamp(long timeStamp, IProgressMonitor monitor)
  {
    return setBranchPoint(getBranch(), timeStamp, monitor);
  }

  @Override
  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return setBranchPoint(branch, timeStamp, null);
  }

  @Override
  public boolean setBranchPoint(CDOBranch branch, long timeStamp, IProgressMonitor monitor)
  {
    CDOBranchPoint branchPoint = branch.getPoint(timeStamp);
    return setBranchPoint(branchPoint, monitor);
  }

  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    return setBranchPoint(branchPoint, null);
  }

  protected CDOBranchPoint basicSetBranchPoint(CDOBranchPoint branchPoint)
  {
    synchronized (branchPointLock)
    {
      this.branchPoint = adjustBranchPoint(branchPoint);
      normalizedBranchPoint = CDOBranchUtil.normalizeBranchPoint(this.branchPoint);
      return this.branchPoint;
    }
  }

  protected final CDOBranchPoint adjustBranchPoint(CDOBranchPoint branchPoint)
  {
    CDOSession session = getSession();
    if (session != null)
    {
      CDOBranchManager branchManager = session.getBranchManager();
      branchPoint = CDOBranchUtil.adjustBranchPoint(branchPoint, branchManager);
    }

    return CDOBranchUtil.copyBranchPoint(branchPoint);
  }

  @Override
  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  @Override
  public CDOBranch getBranch()
  {
    synchronized (branchPointLock)
    {
      return branchPoint.getBranch();
    }
  }

  @Override
  public long getTimeStamp()
  {
    synchronized (branchPointLock)
    {
      return branchPoint.getTimeStamp();
    }
  }

  protected void fireViewTargetChangedEvent(CDOBranchPoint oldBranchPoint, IListener[] listeners)
  {
    fireEvent(new ViewTargetChangedEvent(oldBranchPoint, branchPoint), listeners);
  }

  @Override
  public boolean isDirty()
  {
    return false;
  }

  @Override
  public boolean hasConflict()
  {
    return false;
  }

  @Override
  public boolean hasResource(String path)
  {
    return sync.supply(() -> {
      try
      {
        checkActive();
        getResourceNodeID(path);
        return true;
      }
      catch (CDOResourceNodeNotFoundException ex)
      {
        return false;
      }
    });
  }

  @Override
  public CDOQueryImpl createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null);
  }

  @Override
  public CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    checkActive();
    return sync.supply(() -> new CDOQueryImpl(this, language, queryString, context));
  }

  @Override
  public CDOResourceNode getResourceNode(String path) throws CDOResourceNodeNotFoundException
  {
    return sync.supply(() -> {
      CDOID id = getResourceNodeID(path);
      if (id != null) // Should always be true
      {
        InternalCDOObject object = getObject(id);
        if (object instanceof CDOResourceNode)
        {
          return (CDOResourceNode)object;
        }
      }

      throw new CDOResourceNodeNotFoundException("Resource node not found: " + path);
    });
  }

  private CDOID getCachedResourceNodeID(String path)
  {
    if (resourcePathCache != null)
    {
      path = CDOURIUtil.sanitizePath(path);
      return resourcePathCache.get(path);
    }

    return null;
  }

  private void setCachedResourceNodeID(String path, CDOID id)
  {
    if (resourcePathCache != null)
    {
      path = CDOURIUtil.sanitizePath(path);
      if (id == null)
      {
        resourcePathCache.remove(path);
      }
      else
      {
        resourcePathCache.put(path, id);
      }
    }
  }

  @Override
  public void setResourcePathCache(Map<String, CDOID> resourcePathCache)
  {
    sync.run(() -> this.resourcePathCache = resourcePathCache);
  }

  /**
   * If <code>delta == null</code> the cache is cleared unconditionally.
   * If <code>delta != null</code> the cache is cleared only if the delta can have an impact on the resource tree structure.
   */
  @Override
  public void clearResourcePathCacheIfNecessary(CDORevisionDelta delta)
  {
    sync.run(() -> {
      if (resourcePathCache != null && !resourcePathCache.isEmpty())
      {
        if (delta == null)
        {
          resourcePathCache.clear();
        }
        else
        {
          if (canHaveResourcePathImpact(delta, rootResourceID))
          {
            resourcePathCache.clear();
          }
        }
      }
    });
  }

  /**
   * @return never <code>null</code>
   */
  @Override
  public CDOID getResourceNodeID(String path)
  {
    if (StringUtil.isEmpty(path))
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.1")); //$NON-NLS-1$
    }

    return sync.supply(() -> {
      String p = path;

      CDOID id = getCachedResourceNodeID(p);
      if (id == null)
      {
        if (CDOURIUtil.SEGMENT_SEPARATOR.equals(p))
        {
          id = getResourceNodeIDChecked(null, null);
          setCachedResourceNodeID(p, id);
        }
        else
        {
          List<String> names = CDOURIUtil.analyzePath(p);
          p = "";

          for (String name : names)
          {
            p += CDOURIUtil.SEGMENT_SEPARATOR + name;

            CDOID cached = getCachedResourceNodeID(p);
            if (cached != null)
            {
              id = cached;
            }
            else
            {
              id = getResourceNodeIDChecked(id, name);
              setCachedResourceNodeID(p, id);
            }
          }
        }
      }

      return id;
    });
  }

  /**
   * @return never <code>null</code>
   */
  private CDOID getResourceNodeIDChecked(CDOID folderID, String name) throws CDOResourceNodeNotFoundException
  {
    CDOID id = getResourceNodeID(folderID, name);
    if (id == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.2"), name)); //$NON-NLS-1$
    }

    return id;
  }

  /**
   * @return never <code>null</code>
   */
  protected CDOResourceNode getResourceNode(CDOID folderID, String name) throws CDOResourceNodeNotFoundException
  {
    return sync.supply(() -> {
      try
      {
        CDOID id = getResourceNodeID(folderID, name);
        return (CDOResourceNode)getObject(id);
      }
      catch (CDOException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new CDOException(ex);
      }
    });
  }

  protected CDOID getResourceNodeID(CDOID folderID, String name) throws CDOResourceNodeNotFoundException
  {
    return sync.supply(() -> {
      if (folderID == null)
      {
        return getRootOrTopLevelResourceNodeID(name);
      }

      if (name == null)
      {
        throw new IllegalArgumentException(Messages.getString("CDOViewImpl.3")); //$NON-NLS-1$
      }

      InternalCDORevision folderRevision = getLocalRevision(folderID);
      EClass resourceFolderClass = EresourcePackage.eINSTANCE.getCDOResourceFolder();
      if (folderRevision.getEClass() != resourceFolderClass)
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.4"), folderID)); //$NON-NLS-1$
      }

      CDOList list;
      boolean bypassPermissionChecks = folderRevision.bypassPermissionChecks(true);

      try
      {
        list = folderRevision.getListOrNull(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES);
      }
      finally
      {
        folderRevision.bypassPermissionChecks(bypassPermissionChecks);
      }

      CDOStore store = getStore();
      int size = list == null ? 0 : list.size();
      for (int i = 0; i < size; i++)
      {
        Object value = list.get(i);
        value = store.resolveProxy(folderRevision, EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES, i, value);

        CDOID childID = (CDOID)convertObjectToID(value);
        InternalCDORevision childRevision = getLocalRevision(childID);
        String childName = (String)childRevision.get(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, 0);
        if (name.equals(childName))
        {
          return childID;
        }
      }

      throw new CDOResourceNodeNotFoundException(MessageFormat.format(Messages.getString("CDOViewImpl.5"), name)); //$NON-NLS-1$
    });
  }

  protected CDOID getRootOrTopLevelResourceNodeID(String name) throws CDOResourceNodeNotFoundException
  {
    if (name == null)
    {
      return rootResourceID;
    }

    return sync.supply(() -> {
      CDOID id = getRootOrTopLevelResourceNodeIDCached(name);
      if (id != null)
      {
        return id;
      }

      CDOQuery resourceQuery = createResourcesQuery(null, name, true);
      resourceQuery.setMaxResults(1);

      List<CDOID> ids = resourceQuery.getResult(CDOID.class);
      if (ids.isEmpty())
      {
        throw new CDOResourceNodeNotFoundException(MessageFormat.format(Messages.getString("CDOViewImpl.7"), name)); //$NON-NLS-1$
      }

      return ids.get(0);
    });
  }

  private CDOID getRootOrTopLevelResourceNodeIDCached(String name)
  {
    InternalCDORevision rootResourceRevision = getRevision(rootResourceID, false);
    if (rootResourceRevision != null)
    {
      boolean bypassPermissionChecksRoot = rootResourceRevision.bypassPermissionChecks(true);

      try
      {
        CDOList contents = rootResourceRevision.getListOrNull(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
        if (contents != null)
        {
          for (Object element : contents)
          {
            if (element instanceof CDOID)
            {
              CDOID id = (CDOID)element;
              InternalCDORevision revision = getRevision(id, false);
              if (revision != null)
              {
                boolean bypassPermissionChecks = revision.bypassPermissionChecks(true);

                try
                {
                  if (name.equals(CDORevisionUtil.getResourceNodeName(revision)))
                  {
                    return id;
                  }
                }
                finally
                {
                  revision.bypassPermissionChecks(bypassPermissionChecks);
                }
              }
            }
            else if (element instanceof CDOResourceNode)
            {
              CDOResourceNode node = (CDOResourceNode)element;
              if (name.equals(node.getName()))
              {
                return node.cdoID();
              }
            }
          }
        }
      }
      finally
      {
        rootResourceRevision.bypassPermissionChecks(bypassPermissionChecksRoot);
      }
    }

    return null;
  }

  private InternalCDORevision getLocalRevision(CDOID id)
  {
    InternalCDORevision revision = null;
    InternalCDOObject object = getObject(id, false);
    if (object != null && object.cdoState() != CDOState.PROXY)
    {
      revision = object.cdoRevision();
    }

    if (revision == null)
    {
      revision = getRevision(id, true);
    }

    if (revision == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.9"), id)); //$NON-NLS-1$
    }

    return revision;
  }

  protected InternalCDORevision getRevision(CDOObject object)
  {
    if (object.cdoState() == CDOState.NEW)
    {
      return null;
    }

    InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
    if (revision == null)
    {
      revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    }

    return revision;
  }

  @Override
  public CDOLockState[] getLockStatesOfObjects(Collection<? extends CDOObject> objects)
  {
    List<CDOID> ids = new ArrayList<>();
    for (CDOObject object : objects)
    {
      CDOID id = getID((InternalCDOObject)object, true);
      if (id != null)
      {
        ids.add(id);
      }
    }

    return getLockStates(ids);
  }

  @Override
  public List<InternalCDOObject> getObjectsList()
  {
    return sync.supply(() -> {
      List<InternalCDOObject> result = new ArrayList<>();
      for (InternalCDOObject value : objects.values())
      {
        if (value != null)
        {
          result.add(value);
        }
      }

      return result;
    });
  }

  @Override
  public CDOResource getResource(String path) throws CDOResourceNodeNotFoundException
  {
    return getResource(path, true);
  }

  @Override
  public CDOResource getResource(String path, boolean loadOnDemand) throws CDOResourceNodeNotFoundException
  {
    checkActive();

    return sync.supply(() -> {
      URI uri = CDOURIUtil.createResourceURI(this, path);
      ResourceSet resourceSet = getResourceSet();
      ensureURIs(resourceSet); // Bug 337523

      try
      {
        return (CDOResource)resourceSet.getResource(uri, loadOnDemand);
      }
      catch (RuntimeException ex)
      {
        EList<Resource> resources = resourceSet.getResources();
        for (int i = resources.size() - 1; i >= 0; --i)
        {
          Resource resource = resources.get(i);
          if (uri.equals(resource.getURI()))
          {
            resources.remove(i);
            break;
          }
        }

        throw ex;
      }
    });
  }

  @Override
  public CDOTextResource getTextResource(String path) throws CDOResourceNodeNotFoundException
  {
    return (CDOTextResource)getResourceNode(path);
  }

  @Override
  public CDOBinaryResource getBinaryResource(String path) throws CDOResourceNodeNotFoundException
  {
    return (CDOBinaryResource)getResourceNode(path);
  }

  @Override
  public CDOResourceFolder getResourceFolder(String path) throws CDOResourceNodeNotFoundException
  {
    return (CDOResourceFolder)getResourceNode(path);
  }

  /**
   * Ensures that the URIs of all resources in this resourceSet, can be fetched without triggering the loading of
   * additional resources. Without calling this first, it is dangerous to iterate over the resources to collect their
   * URI's, because
   */
  private void ensureURIs(ResourceSet resourceSet)
  {
    EList<Resource> resources = resourceSet.getResources();
    Resource[] resourceArr = null;

    int size = 0;
    int i;

    do
    {
      i = size;
      size = resources.size();
      if (size == 0)
      {
        break;
      }

      if (resourceArr == null || resourceArr.length < size)
      {
        resourceArr = new Resource[size * 2];
      }

      resourceArr = resources.toArray(resourceArr);
      for (; i < size; i++)
      {
        resourceArr[i].getURI();
      }
    } while (resources.size() > size);
  }

  @Override
  public final List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return sync.supply(() -> {
      CloseableIterator<CDOResourceNode> it = queryResourcesUnsynced(folder, name, exactMatch);

      try
      {
        List<CDOResourceNode> result = new ArrayList<>();

        while (it.hasNext())
        {
          CDOResourceNode node = it.next();
          result.add(node);
        }
        return result;
      }
      finally
      {
        it.close();
      }
    });
  }

  @Override
  public final CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return sync.supply(() -> queryResourcesUnsynced(folder, name, exactMatch));
  }

  protected CloseableIterator<CDOResourceNode> queryResourcesUnsynced(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResultAsync(CDOResourceNode.class);
  }

  private CDOQuery createResourcesQuery(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    checkActive();
    CDOQueryImpl query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, name);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID, folder == null ? null : folder.cdoID());
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH, exactMatch);
    return query;
  }

  @Override
  public final <T extends EObject> List<T> queryInstances(EClass type)
  {
    return sync.supply(() -> {
      CloseableIterator<T> it = queryInstancesUnsynced(type, false);

      try
      {
        List<T> result = new ArrayList<>();

        while (it.hasNext())
        {
          T object = it.next();
          result.add(object);
        }

        return result;
      }
      finally
      {
        it.close();
      }
    });
  }

  @Override
  public final <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type)
  {
    return queryInstancesAsync(type, false);
  }

  @Override
  public final <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type, boolean exact)
  {
    if (exact && (type.isInterface() || type.isAbstract()))
    {
      return AbstractCloseableIterator.emptyCloseable();
    }

    return sync.supply(() -> queryInstancesUnsynced(type, exact));
  }

  protected <T extends EObject> CloseableIterator<T> queryInstancesUnsynced(EClass type, boolean exact)
  {
    CDOQuery query = createInstancesQuery(type, exact);
    return query.getResultAsync();
  }

  private CDOQuery createInstancesQuery(EClass type, boolean exact)
  {
    CDOQuery query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES, null);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES_TYPE, type);

    if (exact)
    {
      query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES_EXACT, Boolean.TRUE);
    }

    return query;
  }

  @Override
  public final List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences)
  {
    return queryXRefs(Collections.singleton(targetObject), sourceReferences);
  }

  @Override
  public final List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return sync.supply(() -> {
      CloseableIterator<CDOObjectReference> it = queryXRefsUnsynced(targetObjects, sourceReferences);

      try
      {
        List<CDOObjectReference> result = new ArrayList<>();

        while (it.hasNext())
        {
          CDOObjectReference object = it.next();
          result.add(object);
        }

        return result;
      }
      finally
      {
        it.close();
      }
    });
  }

  @Override
  public final CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return sync.supply(() -> queryXRefsUnsynced(targetObjects, sourceReferences));
  }

  protected CloseableIterator<CDOObjectReference> queryXRefsUnsynced(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    CDOQuery query = createXRefsQuery(true, null, targetObjects, sourceReferences);
    if (query.getQueryString() != null)
    {
      return query.getResultAsync(CDOObjectReference.class);
    }

    return AbstractCloseableIterator.emptyCloseable();
  }

  protected final CDOQuery createXRefsQuery(boolean excludeNewObjects, Set<CDOID> targetIDs, Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    StringBuilder builder = null;

    for (CDOObject target : targetObjects)
    {
      CDOID id = getXRefTargetID(target);

      if (targetIDs != null)
      {
        targetIDs.add(id);
      }

      if (isObjectNew(id))
      {
        if (excludeNewObjects)
        {
          throw new IllegalArgumentException("Cross referencing for uncommitted new objects is not supported: " + target);
        }
      }
      else
      {
        if (builder == null)
        {
          builder = new StringBuilder();
        }
        else
        {
          builder.append("|");
        }

        builder.append(id.isExternal() ? "e" : "i");
        builder.append(id.toURIFragment());

        if (!(id instanceof CDOClassifierRef.Provider))
        {
          builder.append("|");
          CDOClassifierRef classifierRef = new CDOClassifierRef(target.eClass());
          builder.append(classifierRef.getURI());
        }
      }
    }

    String queryString = builder == null ? null : builder.toString();
    CDOQuery query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_XREFS, queryString);

    if (sourceReferences.length != 0)
    {
      String sourceReferencesParam = createXRefsQuerySourceReferences(sourceReferences);
      query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_XREFS_SOURCE_REFERENCES, sourceReferencesParam);
    }

    return query;
  }

  private String createXRefsQuerySourceReferences(EReference[] sourceReferences)
  {
    StringBuilder builder = new StringBuilder();
    for (EReference sourceReference : sourceReferences)
    {
      StringUtil.appendSeparator(builder, '|');

      CDOClassifierRef classifierRef = new CDOClassifierRef(sourceReference.getEContainingClass());
      builder.append(classifierRef.getURI());
      builder.append('|');
      builder.append(sourceReference.getName());
    }

    return builder.toString();
  }

  protected CDOID getXRefTargetID(CDOObject target)
  {
    if (FSMUtil.isTransient(target))
    {
      throw new IllegalArgumentException("Cross referencing for transient objects not supported " + target);
    }

    return target.cdoID();
  }

  public CDOResourceImpl getResource(CDOID resourceID)
  {
    if (CDOIDUtil.isNull(resourceID))
    {
      throw new IllegalArgumentException("resourceID: " + resourceID); //$NON-NLS-1$
    }

    return (CDOResourceImpl)getObject(resourceID);
  }

  public InternalCDOObject newInstance(EClass eClass)
  {
    return sync.supply(() -> {
      EObject eObject = EcoreUtil.create(eClass);
      return FSMUtil.adapt(eObject, this);
    });
  }

  @Override
  public InternalCDORevision getRevision(CDOID id)
  {
    return getRevision(id, true);
  }

  @Override
  public void loadLob(CDOLobInfo info, Object outputStreamOrWriter) throws IOException
  {
    getSession().loadLob(info, outputStreamOrWriter);
  }

  @Override
  public Map<CDOID, CDOObject> getObjects(Collection<CDOID> ids)
  {
    Map<CDOID, CDOObject> result = CDOIDUtil.createMap();
    List<CDOID> missingIDs = new ArrayList<>();

    for (CDOID id : ids)
    {
      result.computeIfAbsent(id, i -> {
        InternalCDOObject object = getObject(i, false);
        if (object == null)
        {
          missingIDs.add(i);
        }

        return object;
      });
    }

    if (!missingIDs.isEmpty())
    {
      InternalCDOSession session = getSession();
      int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();

      CDORevisionManager revisionManager = session.getRevisionManager();
      List<CDORevision> revisions = revisionManager.getRevisions(missingIDs, this, initialChunkSize, CDORevision.DEPTH_NONE, true);

      for (CDORevision revision : revisions)
      {
        if (revision != null)
        {
          result.computeIfAbsent(revision.getID(), i -> getObject(i, false));
        }
      }
    }

    return result;
  }

  @Override
  public InternalCDOObject getObject(CDOID id)
  {
    return getObject(id, true);
  }

  @Override
  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    return sync.supply(() -> getObjectUnsynced(id, loadOnDemand));
  }

  protected InternalCDOObject getObjectUnsynced(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (rootResource != null && rootResource.cdoID() == id)
    {
      return rootResource;
    }

    if (id == lastLookupID)
    {
      return lastLookupObject;
    }

    lastLookupID = null;
    lastLookupObject = null;
    InternalCDOObject localLookupObject = null;

    if (id.isExternal())
    {
      URI uri = URI.createURI(((CDOIDExternal)id).getURI());
      ResourceSet resourceSet = getResourceSet();

      localLookupObject = (InternalCDOObject)CDOUtil.getCDOObject(resourceSet.getEObject(uri, loadOnDemand));
      if (localLookupObject == null)
      {
        if (!loadOnDemand)
        {
          return null;
        }

        throw new ObjectNotFoundException(id, this);
      }
    }
    else
    {
      // Needed for recursive call to getObject. (from createObject/cleanObject/getResource/getObject)
      localLookupObject = objects.get(id);
      if (localLookupObject == null)
      {
        if (!loadOnDemand)
        {
          return null;
        }

        excludeNewObject(id);
        localLookupObject = createObject(id);

        if (id == rootResourceID)
        {
          setRootResource((CDOResourceImpl)localLookupObject);
        }
      }
    }

    lastLookupID = id;
    lastLookupObject = localLookupObject;
    return lastLookupObject;
  }

  protected void excludeNewObject(CDOID id)
  {
    if (isObjectNew(id))
    {
      throw new ObjectNotFoundException(id, this);
    }
  }

  @Override
  public boolean isObjectNew(CDOID id)
  {
    return id.isTemporary();
  }

  /**
   * @since 2.0
   */
  @Override
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    checkActive();

    return sync.supply(() -> {
      CDOObject object = CDOUtil.getCDOObject(objectFromDifferentView);
      CDOView view = object.cdoView();
      if (view == null)
      {
        return null;
      }

      if (view != this)
      {
        if (!view.getSession().getRepositoryInfo().getUUID().equals(getSession().getRepositoryInfo().getUUID()))
        {
          throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOViewImpl.11"), objectFromDifferentView)); //$NON-NLS-1$
        }

        CDOID id = object.cdoID();
        InternalCDOObject contextified = getObject(id, true);

        if (objectFromDifferentView instanceof CDOLegacyAdapter)
        {
          @SuppressWarnings("unchecked")
          T cast = (T)contextified;
          return cast;
        }

        @SuppressWarnings("unchecked")
        T cast = (T)CDOUtil.getEObject(contextified);
        return cast;
      }

      return objectFromDifferentView;
    });
  }

  @Override
  public boolean isObjectRegistered(CDOID id)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return false;
    }

    return sync.supply(() -> objects.containsKey(id));
  }

  public InternalCDOObject removeObject(CDOID id)
  {
    if (id == null)
    {
      return null;
    }

    return sync.supply(() -> {
      if (id == lastLookupID)
      {
        lastLookupID = null;
        lastLookupObject = null;
      }

      InternalCDOObject object = objects.remove(id);
      if (object != null)
      {
        objectDeregistered(object);
      }

      return object;
    });
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createObject(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating object for " + id); //$NON-NLS-1$
    }

    // Remember the current object creation count to be able to optimize the map lookup
    // away if no objects get created during the getRevision() call.
    int originalCount = objectCreationCounter;

    InternalCDORevision revision = getRevision(id, true);
    if (revision == null)
    {
      throw new ObjectNotFoundException(id, this);
    }

    // If the object has been created and registered during revision loading
    // (for example by lock state prefetcher) don't create a duplicate.
    InternalCDOObject object = objectCreationCounter == originalCount ? null : objects.get(id);
    if (object == null)
    {
      EClass eClass = revision.getEClass();

      if (CDOModelUtil.isResource(eClass) && id != rootResourceID)
      {
        object = (InternalCDOObject)newResourceInstance(revision);
        // object is PROXY
      }
      else
      {
        object = newInstance(eClass);
        // object is TRANSIENT
      }

      ++objectCreationCounter;

      cleanObject(object, revision);
      CDOStateMachine.INSTANCE.dispatchLoadNotification(object);

      // Bug 435198: Have object's resource added to the ResourceSet on call to CDOView.getObject(CDOID)
      if (!CDOModelUtil.isResource(eClass))
      {
        getStore().getResource(object);
      }

      if (CDOModelUtil.isResourceNode(eClass))
      {
        String path = getResourcePath(revision);
        setCachedResourceNodeID(path, id);
      }
    }

    return object;
  }

  private CDOResource newResourceInstance(InternalCDORevision revision)
  {
    String path = getResourcePath(revision);
    URI uri = CDOURIUtil.createResourceURI(this, path);
    ResourceSet resourceSet = getResourceSet();

    // Bug 334995: Check if locally there is already a resource with the same URI.
    CDOResource existingResource = (CDOResource)resourceSet.getResource(uri, false);
    if (existingResource != null && !isReadOnly())
    {
      preventURIClash(existingResource);
    }

    return getResource(path, true);
  }

  private String getResourcePath(InternalCDORevision revision)
  {
    CDORevisionData data = revision.data();
    CDOID folderID;

    Object containerID = data.getContainerID();
    if (containerID instanceof CDOWithID)
    {
      folderID = ((CDOWithID)containerID).cdoID();
    }
    else
    {
      folderID = (CDOID)containerID;
    }

    String name = (String)revision.data().get(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, 0);
    if (CDOIDUtil.isNull(folderID))
    {
      if (name == null)
      {
        return CDOURIUtil.SEGMENT_SEPARATOR;
      }

      return CDOURIUtil.SEGMENT_SEPARATOR + name;
    }

    InternalCDOObject object = getObject(folderID, true);
    if (object instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)object;
      String path = folder.getPath();
      return path + CDOURIUtil.SEGMENT_SEPARATOR + name;
    }

    throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.14"), object)); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  protected void cleanObject(InternalCDOObject object, InternalCDORevision revision)
  {
    sync.run(() -> {
      object.cdoInternalSetView(this);
      object.cdoInternalSetRevision(revision);

      // Before setting the state to CLEAN (that can trigger a duplicate loading and instantiation of the current
      // object) we make sure that object is registered - without throwing exception if it is already the case
      registerObjectIfNotRegistered(object);

      object.cdoInternalSetState(CDOState.CLEAN);
      object.cdoInternalPostLoad();
    });
  }

  @Override
  public CDOID provideCDOID(Object idOrObject)
  {
    return sync.supply(() -> {
      Object shouldBeCDOID = convertObjectToID(idOrObject);
      if (shouldBeCDOID instanceof CDOID)
      {
        CDOID id = (CDOID)shouldBeCDOID;
        if (TRACER.isEnabled() && id != idOrObject)
        {
          TRACER.format("Converted object to CDOID: {0} --> {1}", idOrObject, id); //$NON-NLS-1$
        }

        return id;
      }

      if (idOrObject instanceof InternalEObject)
      {
        InternalEObject eObject = (InternalEObject)idOrObject;
        if (eObject instanceof InternalCDOObject)
        {
          InternalCDOObject object = (InternalCDOObject)idOrObject;
          if (object.cdoView() != null && FSMUtil.isNew(object))
          {
            if (object.cdoID().isTemporary())
            {
              String uri = EcoreUtil.getURI(object.cdoInternalInstance()).toString();
              return CDOIDUtil.createTempObjectExternal(uri);
            }
          }
        }

        Resource eResource = eObject.eResource();
        if (eResource != null)
        {
          // Check if eObject is contained by a deleted resource
          if (!(eResource instanceof CDOResource) || ((CDOResource)eResource).cdoState() != CDOState.TRANSIENT)
          {
            String uri = EcoreUtil.getURI(CDOUtil.getEObject(eObject)).toString();
            return CDOIDUtil.createExternal(uri);
          }
        }
        else
        {
          URI proxyURI = eObject.eProxyURI();
          if (proxyURI != null)
          {
            ResourceSet resourceSet = getResourceSet();
            EObject resolvedObject = EcoreUtil.resolve(eObject, resourceSet);
            if (resolvedObject == eObject)
            {
              // The proxy could NOT be resolved.
              return CDOIDUtil.createExternal(proxyURI.toString());
            }

            // The proxy was resolved. Call this method again with the resolved object.
            return provideCDOID(resolvedObject);
          }
        }

        throw new DanglingReferenceException(eObject);
      }

      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.16"), idOrObject)); //$NON-NLS-1$
    });
  }

  @Override
  public Object convertObjectToID(Object potentialObject)
  {
    return convertObjectToID(potentialObject, false);
  }

  /**
   * @since 2.0
   */
  @Override
  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID)
  {
    if (potentialObject instanceof CDOID)
    {
      return potentialObject;
    }

    return sync.supply(() -> {
      if (potentialObject instanceof InternalEObject)
      {
        if (potentialObject instanceof InternalCDOObject)
        {
          InternalCDOObject object = (InternalCDOObject)potentialObject;
          CDOID id = getID(object, onlyPersistedID);
          if (id != null)
          {
            return id;
          }
        }
        else
        {
          InternalCDOObject object = (InternalCDOObject)EcoreUtil.getAdapter(((InternalEObject)potentialObject).eAdapters(), CDOLegacyAdapter.class);
          if (object != null)
          {
            CDOID id = getID(object, onlyPersistedID);
            if (id != null)
            {
              return id;
            }

            return object;
          }
        }
      }

      return potentialObject;
    });
  }

  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    return sync.supply(() -> {
      if (onlyPersistedID)
      {
        if (FSMUtil.isTransient(object) || FSMUtil.isNew(object))
        {
          return null;
        }
      }

      CDOView view = object.cdoView();
      if (view == this)
      {
        return object.cdoID();
      }

      if (view != null && view.getSession() == getSession())
      {
        boolean sameTarget = view.getBranch() == getBranch() && view.getTimeStamp() == getTimeStamp();
        if (sameTarget)
        {
          return object.cdoID();
        }

        throw new IllegalArgumentException("Object " + object + " is managed by a view with different target: " + view);
      }

      return null;
    });
  }

  @Override
  public Object convertIDToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      return sync.supply(() -> {
        CDOID id = (CDOID)potentialID;
        if (id.isExternal())
        {
          ResourceSet resourceSet = getResourceSet();
          URI uri = URI.createURI(id.toURIFragment());
          return resourceSet.getEObject(uri, true);
        }

        InternalCDOObject result = getObject(id, true);
        if (result == null)
        {
          throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.17"), id)); //$NON-NLS-1$
        }

        return result.cdoInternalInstance();
      });
    }

    return potentialID;
  }

  /**
   * @since 2.0
   */
  @Override
  public void attachResource(CDOResourceImpl resource)
  {
    sync.run(() -> {
      if (!resource.isExisting())
      {
        throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.18"), this)); //$NON-NLS-1$
      }

      // ResourceSet.getResource(uri, true) was called!!
      resource.cdoInternalSetView(this);
      resource.cdoInternalSetState(CDOState.PROXY);
      registerProxyResource2(resource);
    });
  }

  private void registerProxyResource2(CDOResourceImpl resource)
  {
    sync.run(() -> {
      URI uri = resource.getURI();
      String path = CDOURIUtil.extractResourcePath(uri);
      boolean isRoot = "/".equals(path); //$NON-NLS-1$

      try
      {
        CDOID id;
        if (isRoot)
        {
          id = rootResourceID;
        }
        else
        {
          id = getResourceNodeID(path);
        }

        resource.cdoInternalSetID(id);
        registerObject(resource);
        if (isRoot)
        {
          resource.setRoot(true);
          getResourceSet();
          rootResource = resource;
        }
      }
      catch (LifecycleException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new InvalidURIException(uri, ex);
      }
    });
  }

  /**
   * @deprecated No longer supported.
   */
  @Override
  @Deprecated
  public void registerProxyResource(CDOResourceImpl resource)
  {
    registerProxyResource2(resource);
  }

  /**
   * Does the same as {@link AbstractCDOView#registerObject(InternalCDOObject)}, but without
   * throwing any exception if object is already registered (in that case it will simply do nothing).
   *
   * @param object the object to register
   */
  private void registerObjectIfNotRegistered(InternalCDOObject object)
  {
    if (CDOModelUtil.isResource(object.eClass()))
    {
      return;
    }

    if (objects.containsKey(object.cdoID()))
    {
      return;
    }

    registerObject(object);
  }

  @Override
  public void registerObject(InternalCDOObject object)
  {
    if (LEGACY_MODELS != null && CDOUtil.isLegacyObject(object))
    {
      String nsURI = object.eClass().getEPackage().getNsURI();
      if (LEGACY_MODELS.add(nsURI))
      {
        OM.LOG.info("Legacy model detected: " + nsURI);
      }
    }

    sync.run(() -> {
      if (TRACER.isEnabled())
      {
        TRACER.format("Registering {0}", object); //$NON-NLS-1$
      }

      InternalCDOObject old = objects.put(object.cdoID(), object);
      if (old != null)
      {
        if (old != object)
        {
          throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.30"), object.cdoID())); //$NON-NLS-1$
        }

        if (TRACER.isEnabled())
        {
          TRACER.format(Messages.getString("CDOViewImpl.20"), old); //$NON-NLS-1$
        }
      }

      objectRegistered(object);
    });
  }

  @Override
  public void deregisterObject(InternalCDOObject object)
  {
    sync.run(() -> {
      if (TRACER.isEnabled())
      {
        TRACER.format("Deregistering {0}", object); //$NON-NLS-1$
      }

      removeObject(object.cdoID());
    });
  }

  protected void objectRegistered(InternalCDOObject object)
  {
    if (bypassRegistrationHandlers)
    {
      return;
    }

    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      sync.run(() -> {
        for (int i = 0; i < handlers.length; i++)
        {
          CDORegistrationHandler handler = handlers[i];
          handler.objectRegistered(this, object);
        }
      });
    }
  }

  protected void objectDeregistered(InternalCDOObject object)
  {
    if (bypassRegistrationHandlers)
    {
      return;
    }

    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      sync.run(() -> {
        for (int i = 0; i < handlers.length; i++)
        {
          CDORegistrationHandler handler = handlers[i];
          handler.objectDeregistered(this, object);
        }
      });
    }
  }

  protected void objectCollected(CDOID id)
  {
    if (bypassRegistrationHandlers)
    {
      return;
    }

    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      sync.run(() -> {
        for (int i = 0; i < handlers.length; i++)
        {
          CDORegistrationHandler handler = handlers[i];
          handler.objectCollected(this, id);
        }
      });
    }
  }

  @Override
  public final void remapObject(CDOID oldID)
  {
    sync.run(() -> remapObjectUnsynced(oldID));
  }

  protected InternalCDOObject remapObjectUnsynced(CDOID oldID)
  {
    InternalCDOObject object = objects.remove(oldID);
    CDOID newID = object.cdoID();

    objects.put(newID, object);

    if (lastLookupID == oldID)
    {
      lastLookupID = newID;
      lastLookupObject = object;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping {0} --> {1}", oldID, newID); //$NON-NLS-1$
    }

    return object;
  }

  @Override
  public void addObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.add(handler);
  }

  @Override
  public void removeObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.remove(handler);
  }

  @Override
  public CDOObjectHandler[] getObjectHandlers()
  {
    return objectHandlers.get();
  }

  @Override
  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState)
  {
    CDOObjectHandler[] handlers = getObjectHandlers();
    if (handlers.length != 0)
    {
      sync.run(() -> {
        for (int i = 0; i < handlers.length; i++)
        {
          CDOObjectHandler handler = handlers[i];
          handler.objectStateChanged(this, object, oldState, newState);
        }
      });
    }
  }

  @Override
  public void addRegistrationHandler(CDORegistrationHandler handler)
  {
    registrationHandlers.add(handler);
  }

  @Override
  public void removeRegistrationHandler(CDORegistrationHandler handler)
  {
    registrationHandlers.remove(handler);
  }

  @Override
  public CDORegistrationHandler[] getRegistrationHandlers()
  {
    return registrationHandlers.get();
  }

  protected final void bypassRegistrationHandlers(Runnable runnable)
  {
    bypassRegistrationHandlers = true;

    try
    {
      runnable.run();
    }
    finally
    {
      bypassRegistrationHandlers = false;
    }
  }

  /*
   * Synchronized through CDOViewImpl.ViewInvalidation.run().
   */
  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate( //
      List<CDORevisionKey> allChangedObjects, //
      List<CDOIDAndVersion> allDetachedObjects, //
      List<CDORevisionDelta> deltas, //
      Map<CDOObject, CDORevisionDelta> revisionDeltas, //
      Set<CDOObject> detachedObjects, //
      Map<CDOID, InternalCDORevision> oldRevisions)
  {
    boolean hasConflictResolvers = this instanceof CDOTransaction && ((CDOTransaction)this).options().getConflictResolvers().length != 0;
    Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = null;

    // Bug 363355: manage detached objects before changed objects to avoid issue on eContainer
    for (CDOIDAndVersion key : allDetachedObjects)
    {
      CDOID id = key.getID();

      InternalCDOObject detachedObject = removeObject(id);
      if (detachedObject != null)
      {
        CDORevision oldRevision = detachedObject.cdoRevision();
        oldRevisions.put(id, (InternalCDORevision)oldRevision);

        CDOStateMachine.INSTANCE.detachRemote(detachedObject);

        detachedObjects.add(detachedObject);
        if (detachedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<>();
          }

          conflicts.put(detachedObject, Pair.create(oldRevision, CDORevisionDelta.DETACHED));
        }
      }
    }

    Map<String, CDOResourceNode> newResourceNodes = null;
    for (CDORevisionKey key : allChangedObjects)
    {
      CDORevisionDelta delta = null;
      if (key instanceof CDORevisionDelta)
      {
        delta = (CDORevisionDelta)key;

        // Copy the revision delta so that conflict resolvers can modify it.
        if (hasConflictResolvers)
        {
          delta = new CDORevisionDeltaImpl(delta, true);
        }

        deltas.add(delta);
      }

      CDOID id = key.getID();

      CDOObject changedObject = objects.get(id);
      if (changedObject != null)
      {
        CDORevision oldRevision = changedObject.cdoRevision();
        oldRevisions.put(id, (InternalCDORevision)oldRevision);

        CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)changedObject, key);

        if (changedObject instanceof CDOResourceNodeImpl)
        {
          if (delta == null || isResourceNodeContainerOrNameChanged(delta))
          {
            if (!isReadOnly())
            {
              if (newResourceNodes == null)
              {
                newResourceNodes = collectNewResourceNodes();
              }

              CDOResourceNode changedNode = (CDOResourceNode)changedObject;
              String path = changedNode.getPath();

              CDOResourceNode newResourceNode = newResourceNodes.get(path);
              if (newResourceNode != null)
              {
                preventURIClash(newResourceNode);
                String oldName = newResourceNode.getBasename();
                newResourceNode.setBasename(oldName + SAFE_RENAME);
              }
            }

            ((CDOResourceNodeImpl)changedObject).recacheURIs();
          }
        }

        revisionDeltas.put(changedObject, delta);
        if (changedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<>();
          }

          conflicts.put(changedObject, Pair.create(oldRevision, delta));
        }
      }
    }

    return conflicts;
  }

  private boolean isResourceNodeContainerOrNameChanged(CDORevisionDelta delta)
  {
    if (delta.getFeatureDelta(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME) != null)
    {
      return true;
    }

    if (delta.getFeatureDelta(CDOContainerFeatureDelta.CONTAINER_FEATURE) != null)
    {
      return true;
    }

    return false;
  }

  protected Map<String, CDOResourceNode> collectNewResourceNodes()
  {
    return Collections.emptyMap();
  }

  /**
   * Overridden by {@link CDOTransactionImpl#handleConflicts(long, Map, List)}.
   */
  protected void handleConflicts(long lastUpdateTime, Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts, List<CDORevisionDelta> deltas)
  {
    // Do nothing
  }

  public void fireAdaptersNotifiedEvent(long timeStamp)
  {
    fireEvent(new AdaptersNotifiedEvent(timeStamp));
  }

  /**
   * TODO For this method to be useable locks must be cached locally!
   */
  @SuppressWarnings("unused")
  private boolean isLocked(InternalCDOObject object)
  {
    if (object.cdoWriteLock().isLocked())
    {
      return true;
    }

    if (object.cdoReadLock().isLocked())
    {
      return true;
    }

    return false;
  }

  @Override
  @Deprecated
  public int reload(CDOObject... objects)
  {
    return sync.supply(() -> {
      Collection<InternalCDOObject> internalObjects;
      if (objects != null && objects.length != 0)
      {
        internalObjects = new ArrayList<>(objects.length);
        for (CDOObject object : objects)
        {
          if (object instanceof InternalCDOObject)
          {
            internalObjects.add((InternalCDOObject)object);
          }
        }
      }
      else
      {
        internalObjects = new ArrayList<>(this.objects.values());
      }

      int result = internalObjects.size();
      if (result != 0)
      {
        CDOStateMachine.INSTANCE.reload(internalObjects.toArray(new InternalCDOObject[result]));
      }

      return result;
    });
  }

  @Override
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isClosed()
  {
    return !isActive();
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
    StringBuilder builder = new StringBuilder();
    if (isReadOnly())
    {
      builder.append("View");
    }
    else
    {
      builder.append("Transaction");
    }

    builder.append(" "); //$NON-NLS-1$
    builder.append(getViewID());

    if (!isActive())
    {
      builder.append(" [closed]");
    }
    else
    {
      CDOBranchPoint bp = branchPoint;
      if (bp != null)
      {
        boolean brackets = false;
        if (getSession().getRepositoryInfo().isSupportingBranches())
        {
          brackets = true;
          builder.append(" ["); //$NON-NLS-1$
          builder.append(bp.getBranch().getPathName()); // Do not synchronize on this view!
        }

        long timeStamp = bp.getTimeStamp(); // Do not synchronize on this view!
        if (timeStamp != CDOView.UNSPECIFIED_DATE)
        {
          if (brackets)
          {
            builder.append(", "); //$NON-NLS-1$
          }
          else
          {
            builder.append(" ["); //$NON-NLS-1$
            brackets = true;
          }

          builder.append(CDOCommonUtil.formatTimeStamp(timeStamp));
        }

        if (brackets)
        {
          builder.append("]"); //$NON-NLS-1$
        }
      }
    }

    return builder.toString();
  }

  protected String getClassName()
  {
    return "CDOView"; //$NON-NLS-1$
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return getResourceSet();
  }

  @Override
  public void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions)
  {
    sync.run(() -> {
      for (InternalCDOObject object : objects.values())
      {
        CDOState state = object.cdoState();
        if (state != CDOState.CLEAN && state != CDOState.DIRTY && state != CDOState.CONFLICT)
        {
          continue;
        }

        CDOID id = object.cdoID();
        if (revisions.containsKey(id))
        {
          continue;
        }

        InternalCDORevision revision = getViewedRevision(object);
        if (revision == null)
        {
          continue;
        }

        revisions.put(id, revision);
      }
    });
  }

  protected InternalCDORevision getViewedRevision(InternalCDOObject object)
  {
    return CDOStateMachine.INSTANCE.readNoLoad(object);
  }

  @Override
  public CDOChangeSetData compareRevisions(CDOBranchPoint source)
  {
    return sync.supply(() -> {
      CDOSession session = getSession();
      return session.compareRevisions(source, this);
    });
  }

  @Override
  public CDOCommitHistory getHistory()
  {
    CDOBranch branch = getBranch();
    CDOCommitInfoManager commitInfoManager = getSession().getCommitInfoManager();
    return commitInfoManager.getHistory(branch);
  }

  @Override
  protected CDOCommitHistory createHistory(CDOObject key)
  {
    return new CDOObjectHistoryImpl(key);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (sync instanceof LockedCriticalSection)
    {
      LifecycleUtil.activate(((LockedCriticalSection)sync).getLock());
    }

    CDOBranchPoint bp = branchPoint;
    if (bp != null)
    {
      basicSetBranchPoint(bp);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (viewSet != null)
    {
      ResourceSet resourceSet = viewSet.getResourceSet();
      if (resourceSet != null)
      {
        viewSet.getResourceSet().getURIConverter().getURIHandlers().remove(getURIHandler());
      }
    }

    if (sync instanceof LockedCriticalSection)
    {
      LifecycleUtil.deactivate(((LockedCriticalSection)sync).getLock());
    }

    viewSet = null;
    objects = null;
    store = null;
    resourcePathCache = null;
    lastLookupID = null;
    lastLookupObject = null;
    super.doDeactivate();
  }

  public static void setNextViewLock(Lock viewLock)
  {
    if (viewLock != null)
    {
      NEXT_VIEW_LOCK.set(viewLock);
    }
    else
    {
      NEXT_VIEW_LOCK.remove();
    }
  }

  public static boolean canHaveResourcePathImpact(CDORevisionDelta delta, CDOID rootResourceID)
  {
    EClass eClass = delta.getEClass();
    if (EresourcePackage.Literals.CDO_RESOURCE_NODE.isSuperTypeOf(eClass))
    {
      if (delta.getFeatureDelta(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME) != null)
      {
        return true;
      }
    }

    if (eClass == EresourcePackage.Literals.CDO_RESOURCE_FOLDER)
    {
      CDOListFeatureDelta featureDelta = (CDOListFeatureDelta)delta.getFeatureDelta(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES);
      if (canHaveResourcePathImpact(featureDelta))
      {
        return true;
      }
    }

    if (eClass == EresourcePackage.Literals.CDO_RESOURCE)
    {
      if (rootResourceID == delta.getID())
      {
        CDOListFeatureDelta featureDelta = (CDOListFeatureDelta)delta.getFeatureDelta(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
        if (canHaveResourcePathImpact(featureDelta))
        {
          return true;
        }
      }
    }

    return false;
  }

  private static boolean canHaveResourcePathImpact(CDOListFeatureDelta featureDelta)
  {
    if (featureDelta != null)
    {
      for (CDOFeatureDelta listChange : featureDelta.getListChanges())
      {
        CDOFeatureDelta.Type type = listChange.getType();
        switch (type)
        {
        case REMOVE:
        case CLEAR:
        case SET:
        case UNSET:
          return true;
        }
      }
    }

    return false;
  }

  private static void preventURIClash(CDOResourceNode node)
  {
    String oldName = node.getName();

    // We have no other option than to change the name of the local resource.
    String oldBasename = node.getBasename();
    node.setBasename(oldBasename + SAFE_RENAME);

    OM.LOG.warn("URI clash: resource being instantiated had same URI as a resource already present locally; local resource was renamed from " //
        + oldName + " to " + node.getName());
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class Event extends org.eclipse.net4j.util.event.Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    public Event()
    {
      super(AbstractCDOView.this);
    }

    @Override
    public AbstractCDOView getSource()
    {
      return (AbstractCDOView)super.getSource();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdaptersNotifiedEvent extends Event implements CDOViewAdaptersNotifiedEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    public AdaptersNotifiedEvent(long timeStamp)
    {
      this.timeStamp = timeStamp;
    }

    @Override
    public long getTimeStamp()
    {
      return timeStamp;
    }

    @Override
    protected String formatEventName()
    {
      return "CDOViewAdaptersNotifiedEvent";
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "timeStamp=" + timeStamp;
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class ViewTargetChangedEvent extends Event implements CDOViewTargetChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOBranchPoint oldBranchPoint;

    private final CDOBranchPoint branchPoint;

    public ViewTargetChangedEvent(CDOBranchPoint oldBranchPoint, CDOBranchPoint branchPoint)
    {
      this.oldBranchPoint = CDOBranchUtil.copyBranchPoint(oldBranchPoint);
      this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
    }

    @Override
    public CDOBranchPoint getOldBranchPoint()
    {
      return oldBranchPoint;
    }

    @Override
    public CDOBranchPoint getBranchPoint()
    {
      return branchPoint;
    }

    @Override
    protected String formatEventName()
    {
      return "CDOViewTargetChangedEvent";
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "oldBranchPoint=" + oldBranchPoint + ", branchPoint=" + branchPoint;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ViewProviderChangedEvent extends Event implements CDOViewProviderChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOViewProvider oldProvider;

    private final CDOViewProvider provider;

    public ViewProviderChangedEvent(CDOViewProvider oldProvider, CDOViewProvider provider)
    {
      this.oldProvider = oldProvider;
      this.provider = provider;
    }

    @Override
    public CDOViewProvider getOldProvider()
    {
      return oldProvider;
    }

    @Override
    public CDOViewProvider getProvider()
    {
      return provider;
    }

    @Override
    protected String formatEventName()
    {
      return "CDOViewProviderChangedEvent";
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "oldProvider=" + oldProvider + ", provider=" + provider;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ContainerAdapter extends AdapterImpl
  {
    public AbstractCDOView getView()
    {
      return AbstractCDOView.this;
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      if (msg.isTouch())
      {
        return;
      }

      if (msg.getFeature() != EresourcePackage.Literals.CDO_RESOURCE__CONTENTS)
      {
        return;
      }

      IListener[] listeners = getListeners();
      if (listeners.length == 0)
      {
        return;
      }

      IContainerEvent<CDOResourceNode> event = null;
      int eventType = msg.getEventType();
      switch (eventType)
      {
      case Notification.ADD:
        event = new SingleDeltaContainerEvent<>(AbstractCDOView.this, (CDOResourceNode)msg.getNewValue(), IContainerDelta.Kind.ADDED);
        break;

      case Notification.ADD_MANY:
        // TODO
        break;

      case Notification.REMOVE:
        event = new SingleDeltaContainerEvent<>(AbstractCDOView.this, (CDOResourceNode)msg.getOldValue(), IContainerDelta.Kind.REMOVED);
        break;

      case Notification.REMOVE_MANY:
        // TODO
        break;

      case Notification.UNSET:
        // TODO
        break;

      default:
        break;
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }
  }

  /**
   * For better debugging.
   *
   * @author Eike Stepper
   */
  private static final class BranchPointLock
  {
  }

  /**
   * For better debugging.
   *
   * @author Eike Stepper
   */
  private static final class NOOPMonitor
  {
  }
}
