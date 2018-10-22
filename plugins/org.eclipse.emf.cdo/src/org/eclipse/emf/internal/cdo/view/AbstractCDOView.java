/*
 * Copyright (c) 2010-2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
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
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.SelfAttachingContainerListener.DoNotDescend;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
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
import org.eclipse.emf.ecore.EAttribute;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

  private static final ThreadLocal<Lock> NEXT_VIEW_LOCK = new ThreadLocal<Lock>();

  private final ViewAndState[] viewAndStates = ViewAndState.create(this);

  private final CDOURIHandler uriHandler = new CDOURIHandler(this);

  protected final Lock viewLock;

  protected final Condition viewLockCondition;

  private CDOBranchPoint branchPoint;

  private CDOBranchPoint normalizedBranchPoint;

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

  private final IRegistry<String, Object> properties = new HashMapRegistry<String, Object>()
  {
    @Override
    public void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  };

  @ExcludeFromDump
  private transient Map<String, CDOID> resourcePathCache = new HashMap<String, CDOID>();

  @ExcludeFromDump
  private transient CDOID lastLookupID;

  @ExcludeFromDump
  private transient InternalCDOObject lastLookupObject;

  public AbstractCDOView(CDOSession session, CDOBranchPoint branchPoint)
  {
    this(session);
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

    viewLock = lock;
    viewLockCondition = viewLock != null ? viewLock.newCondition() : null;

    initObjectsMap(ReferenceType.SOFT);
  }

  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  public String getRepositoryName()
  {
    Object repositoryName = properties.get(REPOSITORY_NAME_KEY);
    if (repositoryName instanceof String)
    {
      return (String)repositoryName;
    }

    return getSession().getRepositoryInfo().getName();
  }

  public void setRepositoryName(String repositoryName)
  {
    properties.put(REPOSITORY_NAME_KEY, repositoryName);
  }

  public boolean isReadOnly()
  {
    return true;
  }

  public boolean isHistorical()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Deprecated
  public boolean isLegacyModeEnabled()
  {
    return true;
  }

  protected final Map<CDOID, InternalCDOObject> getModifiableObjects()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return objects;
      }
      finally
      {
        unlockView();
      }
    }
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

  public Map<CDOID, InternalCDOObject> getObjects()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (objects == null)
        {
          return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(objects);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected final void setObjects(Map<CDOID, InternalCDOObject> objects)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        this.objects = objects;
      }
      finally
      {
        unlockView();
      }
    }
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
      for (Entry<CDOID, InternalCDOObject> entry : objects.entrySet())
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

  public ViewAndState getViewAndState(CDOState state)
  {
    return viewAndStates[state.ordinal()];
  }

  public CDOStore getStore()
  {
    checkActive();
    return store;
  }

  public ResourceSet getResourceSet()
  {
    return getViewSet().getResourceSet();
  }

  /**
   * @since 2.0
   */
  public InternalCDOViewSet getViewSet()
  {
    return viewSet;
  }

  /**
   * @since 2.0
   */
  public void setViewSet(InternalCDOViewSet viewSet)
  {
    this.viewSet = viewSet;
    if (viewSet != null)
    {
      viewSet.getResourceSet().getURIConverter().getURIHandlers().add(0, getURIHandler());
    }
  }

  public final Object getViewMonitor()
  {
    if (viewLock != null)
    {
      return new NOOPMonitor();
    }

    return this;
  }

  public final Lock getViewLock()
  {
    return viewLock;
  }

  public final void lockView()
  {
    if (viewLock != null)
    {
      viewLock.lock();
    }
  }

  public final void unlockView()
  {
    if (viewLock != null)
    {
      viewLock.unlock();
    }
  }

  public void syncExec(Runnable runnable)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        runnable.run();
      }
      finally
      {
        unlockView();
      }
    }
  }

  public <V> V syncExec(Callable<V> callable) throws Exception
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return callable.call();
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOViewProvider getProvider()
  {
    return provider;
  }

  public void setProvider(CDOViewProvider provider)
  {
    this.provider = provider;

    if (viewSet != null)
    {
      viewSet.remapView(this);
    }
  }

  public void setSession(InternalCDOSession session)
  {
    rootResourceID = session.getRepositoryInfo().getRootResourceID();
    if (rootResourceID == null || rootResourceID.isNull())
    {
      throw new IllegalStateException("RootResourceID is null; is the repository not yet initialized?");
    }
  }

  public CDOResourceImpl getRootResource()
  {
    checkActive();

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (rootResource == null)
        {
          getObject(rootResourceID);
          CheckUtil.checkState(rootResource, "rootResource");
        }

        return rootResource;
      }
      finally
      {
        unlockView();
      }
    }
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

  public boolean isEmpty()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOResource rootResource = getRootResource();
        if (rootResource.cdoPermission() == CDOPermission.NONE)
        {
          return true;
        }

        boolean empty = rootResource.getContents().isEmpty();
        ensureContainerAdapter(rootResource);
        return empty;
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOResourceNode[] getElements()
  {
    List<CDOResourceNode> elements = new ArrayList<CDOResourceNode>();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }

      return elements.toArray(new CDOResourceNode[elements.size()]);
    }
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

  public CDOURIHandler getURIHandler()
  {
    return uriHandler;
  }

  protected CDOBranchPoint getBranchPoint()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return branchPoint;
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOBranchPoint getNormalizedBranchPoint()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return normalizedBranchPoint;
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean setBranch(CDOBranch branch)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return setBranchPoint(branch, getTimeStamp(), null);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean setBranch(CDOBranch branch, IProgressMonitor monitor)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return setBranchPoint(branch, getTimeStamp(), monitor);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean setTimeStamp(long timeStamp)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return setBranchPoint(getBranch(), timeStamp, null);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean setTimeStamp(long timeStamp, IProgressMonitor monitor)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return setBranchPoint(getBranch(), timeStamp, monitor);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return setBranchPoint(branch, timeStamp, null);
  }

  public boolean setBranchPoint(CDOBranch branch, long timeStamp, IProgressMonitor monitor)
  {
    CDOBranchPoint branchPoint = branch.getPoint(timeStamp);
    return setBranchPoint(branchPoint, monitor);
  }

  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    return setBranchPoint(branchPoint, null);
  }

  protected CDOBranchPoint basicSetBranchPoint(CDOBranchPoint branchPoint)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        this.branchPoint = adjustBranchPoint(branchPoint);
        normalizedBranchPoint = CDOBranchUtil.normalizeBranchPoint(this.branchPoint);
        return this.branchPoint;
      }
      finally
      {
        unlockView();
      }
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

  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  public CDOBranch getBranch()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return branchPoint.getBranch();
      }
      finally
      {
        unlockView();
      }
    }
  }

  public long getTimeStamp()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return branchPoint.getTimeStamp();
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected void fireViewTargetChangedEvent(CDOBranchPoint oldBranchPoint, IListener[] listeners)
  {
    fireEvent(new ViewTargetChangedEvent(oldBranchPoint, branchPoint), listeners);
  }

  public boolean isDirty()
  {
    return false;
  }

  public boolean hasConflict()
  {
    return false;
  }

  public boolean hasResource(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

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
      finally
      {
        unlockView();
      }
    }
  }

  public CDOQueryImpl createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null);
  }

  public CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return new CDOQueryImpl(this, language, queryString, context);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOResourceNode getResourceNode(String path)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  private CDOID getCachedResourceNodeID(String path)
  {
    if (resourcePathCache != null)
    {
      return resourcePathCache.get(path);
    }

    return null;
  }

  private void setCachedResourceNodeID(String path, CDOID id)
  {
    if (resourcePathCache != null)
    {
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

  public void setResourcePathCache(Map<String, CDOID> resourcePathCache)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        this.resourcePathCache = resourcePathCache;
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * If <code>delta == null</code> the cache is cleared unconditionally.
   * If <code>delta != null</code> the cache is cleared only if the delta can have an impact on the resource tree structure.
   */
  public void clearResourcePathCacheIfNecessary(CDORevisionDelta delta)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @return never <code>null</code>
   */
  public CDOID getResourceNodeID(String path)
  {
    if (StringUtil.isEmpty(path))
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.1")); //$NON-NLS-1$
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOID id = getCachedResourceNodeID(path);
        if (id == null)
        {
          if (CDOURIUtil.SEGMENT_SEPARATOR.equals(path))
          {
            id = getResourceNodeIDChecked(null, null);
            setCachedResourceNodeID(path, id);
          }
          else
          {
            List<String> names = CDOURIUtil.analyzePath(path);
            path = "";

            for (String name : names)
            {
              path = path.length() == 0 ? name : path + "/" + name;

              CDOID cached = getCachedResourceNodeID(path);
              if (cached != null)
              {
                id = cached;
              }
              else
              {
                id = getResourceNodeIDChecked(id, name);
                setCachedResourceNodeID(path, id);
              }
            }
          }
        }

        return id;
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @return never <code>null</code>
   */
  private CDOID getResourceNodeIDChecked(CDOID folderID, String name)
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
  protected CDOResourceNode getResourceNode(CDOID folderID, String name)
  {
    synchronized (getViewMonitor())
    {
      lockView();

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
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOID getResourceNodeID(CDOID folderID, String name) throws CDOResourceNodeNotFoundException
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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

        EReference nodesFeature = EresourcePackage.eINSTANCE.getCDOResourceFolder_Nodes();
        EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

        CDOList list;
        boolean bypassPermissionChecks = folderRevision.bypassPermissionChecks(true);

        try
        {
          list = folderRevision.getListOrNull(nodesFeature);
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
          value = store.resolveProxy(folderRevision, nodesFeature, i, value);

          CDOID childID = (CDOID)convertObjectToID(value);
          InternalCDORevision childRevision = getLocalRevision(childID);
          String childName = (String)childRevision.get(nameFeature, 0);
          if (name.equals(childName))
          {
            return childID;
          }
        }

        throw new CDOResourceNodeNotFoundException(MessageFormat.format(Messages.getString("CDOViewImpl.5"), name)); //$NON-NLS-1$
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    if (name == null)
    {
      return rootResourceID;
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOQuery resourceQuery = createResourcesQuery(null, name, true);
        resourceQuery.setMaxResults(1);

        List<CDOID> ids = resourceQuery.getResult(CDOID.class);
        if (ids.isEmpty())
        {
          throw new CDOResourceNodeNotFoundException(MessageFormat.format(Messages.getString("CDOViewImpl.7"), name)); //$NON-NLS-1$
        }

        return ids.get(0);
      }
      finally
      {
        unlockView();
      }
    }
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

  public CDOLockState[] getLockStatesOfObjects(Collection<? extends CDOObject> objects)
  {
    List<CDOID> ids = new ArrayList<CDOID>();
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

  public List<InternalCDOObject> getObjectsList()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
        for (InternalCDOObject value : objects.values())
        {
          if (value != null)
          {
            result.add(value);
          }
        }

        return result;
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOResource getResource(String path)
  {
    return getResource(path, true);
  }

  public CDOResource getResource(String path, boolean loadOnDemand)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOTextResource getTextResource(String path)
  {
    return (CDOTextResource)getResourceNode(path);
  }

  public CDOBinaryResource getBinaryResource(String path)
  {
    return (CDOBinaryResource)getResourceNode(path);
  }

  public CDOResourceFolder getResourceFolder(String path)
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

  public final List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CloseableIterator<CDOResourceNode> it = queryResourcesUnsynced(folder, name, exactMatch);

        try
        {
          List<CDOResourceNode> result = new ArrayList<CDOResourceNode>();

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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public final CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return queryResourcesUnsynced(folder, name, exactMatch);
      }
      finally
      {
        unlockView();
      }
    }
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

  public final <T extends EObject> List<T> queryInstances(EClass type)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CloseableIterator<T> it = queryInstancesUnsynced(type, false);

        try
        {
          List<T> result = new ArrayList<T>();

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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public final <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type)
  {
    return queryInstancesAsync(type, false);
  }

  public final <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type, boolean exact)
  {
    if (exact && (type.isInterface() || type.isAbstract()))
    {
      return AbstractCloseableIterator.emptyCloseable();
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return queryInstancesUnsynced(type, exact);
      }
      finally
      {
        unlockView();
      }
    }
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

  public final List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences)
  {
    return queryXRefs(Collections.singleton(targetObject), sourceReferences);
  }

  public final List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CloseableIterator<CDOObjectReference> it = queryXRefsUnsynced(targetObjects, sourceReferences);

        try
        {
          List<CDOObjectReference> result = new ArrayList<CDOObjectReference>();

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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public final CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return queryXRefsUnsynced(targetObjects, sourceReferences);
      }
      finally
      {
        unlockView();
      }
    }
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
      if (builder.length() != 0)
      {
        builder.append("|");
      }

      CDOClassifierRef classifierRef = new CDOClassifierRef(sourceReference.getEContainingClass());
      builder.append(classifierRef.getURI());
      builder.append("|");
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
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        EObject eObject = EcoreUtil.create(eClass);
        return FSMUtil.adapt(eObject, this);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    return getRevision(id, true);
  }

  public InternalCDOObject getObject(CDOID id)
  {
    return getObject(id, true);
  }

  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return getObjectUnsynced(id, loadOnDemand);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected InternalCDOObject getObjectUnsynced(CDOID id, boolean loadOnDemand)
  {
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
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (isObjectNew(id))
        {
          throw new ObjectNotFoundException(id, this);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean isObjectNew(CDOID id)
  {
    return id.isTemporary();
  }

  /**
   * @since 2.0
   */
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean isObjectRegistered(CDOID id)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return false;
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return objects.containsKey(id);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public InternalCDOObject removeObject(CDOID id)
  {
    if (id == null)
    {
      return null;
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
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
    }

    return object;
  }

  private CDOResource newResourceInstance(InternalCDORevision revision)
  {
    String path = getResourcePath(revision);
    URI uri = CDOURIUtil.createResourceURI(this, path);
    ResourceSet resourceSet = getResourceSet();

    // Bug 334995: Check if locally there is already a resource with the same URI
    CDOResource existingResource = (CDOResource)resourceSet.getResource(uri, false);
    if (existingResource != null && !isReadOnly())
    {
      // We have no other option than to change the name of the local resource
      String oldName = existingResource.getName();
      existingResource.setName(oldName + ".renamed");

      OM.LOG.warn("URI clash: resource being instantiated had same URI as a resource already present " + "locally; local resource was renamed from " + oldName
          + " to " + existingResource.getName());
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

      return name;
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
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        object.cdoInternalSetView(this);
        object.cdoInternalSetRevision(revision);

        // Before setting the state to CLEAN (that can trigger a duplicate loading and instantiation of the current
        // object)
        // we make sure that object is registered - without throwing exception if it is already the case
        registerObjectIfNotRegistered(object);

        object.cdoInternalSetState(CDOState.CLEAN);
        object.cdoInternalPostLoad();
      }
      finally
      {
        unlockView();
      }
    }
  }

  public CDOID provideCDOID(Object idOrObject)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
              String uri = EcoreUtil.getURI(object.cdoInternalInstance()).toString();
              if (object.cdoID().isTemporary())
              {
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

          throw new DanglingReferenceException(eObject);
        }

        throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.16"), idOrObject)); //$NON-NLS-1$
      }
      finally
      {
        unlockView();
      }
    }
  }

  public Object convertObjectToID(Object potentialObject)
  {
    return convertObjectToID(potentialObject, false);
  }

  /**
   * @since 2.0
   */
  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID)
  {
    if (potentialObject instanceof CDOID)
    {
      return potentialObject;
    }

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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

              potentialObject = object;
            }
          }
        }

        return potentialObject;
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public Object convertIDToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          CDOID id = (CDOID)potentialID;
          if (id.isExternal())
          {
            return getResourceSet().getEObject(URI.createURI(id.toURIFragment()), true);
          }

          InternalCDOObject result = getObject(id, true);
          if (result == null)
          {
            throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.17"), id)); //$NON-NLS-1$
          }

          return result.cdoInternalInstance();
        }
        finally
        {
          unlockView();
        }
      }
    }

    return potentialID;
  }

  /**
   * @since 2.0
   */
  public void attachResource(CDOResourceImpl resource)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (!resource.isExisting())
        {
          throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.18"), this)); //$NON-NLS-1$
        }

        // ResourceSet.getResource(uri, true) was called!!
        resource.cdoInternalSetView(this);
        resource.cdoInternalSetState(CDOState.PROXY);
        registerProxyResource2(resource);
      }
      finally
      {
        unlockView();
      }
    }
  }

  private void registerProxyResource2(CDOResourceImpl resource)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @deprecated No longer supported.
   */
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

  public void registerObject(InternalCDOObject object)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  public void deregisterObject(InternalCDOObject object)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Deregistering {0}", object); //$NON-NLS-1$
        }

        removeObject(object.cdoID());
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected void objectRegistered(InternalCDOObject object)
  {
    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (int i = 0; i < handlers.length; i++)
          {
            CDORegistrationHandler handler = handlers[i];
            handler.objectRegistered(this, object);
          }
        }
        finally
        {
          unlockView();
        }
      }
    }
  }

  protected void objectDeregistered(InternalCDOObject object)
  {
    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (int i = 0; i < handlers.length; i++)
          {
            CDORegistrationHandler handler = handlers[i];
            handler.objectDeregistered(this, object);
          }
        }
        finally
        {
          unlockView();
        }
      }
    }
  }

  protected void objectCollected(CDOID id)
  {
    CDORegistrationHandler[] handlers = getRegistrationHandlers();
    if (handlers.length != 0)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (int i = 0; i < handlers.length; i++)
          {
            CDORegistrationHandler handler = handlers[i];
            handler.objectCollected(this, id);
          }
        }
        finally
        {
          unlockView();
        }
      }
    }
  }

  public void remapObject(CDOID oldID)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOID newID;
        InternalCDOObject object = objects.remove(oldID);
        newID = object.cdoID();

        objects.put(newID, object);

        if (lastLookupID == oldID)
        {
          lastLookupID = null;
          lastLookupObject = null;
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Remapping {0} --> {1}", oldID, newID); //$NON-NLS-1$
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.add(handler);
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.remove(handler);
  }

  public CDOObjectHandler[] getObjectHandlers()
  {
    return objectHandlers.get();
  }

  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState)
  {
    CDOObjectHandler[] handlers = getObjectHandlers();
    if (handlers.length != 0)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          for (int i = 0; i < handlers.length; i++)
          {
            CDOObjectHandler handler = handlers[i];
            handler.objectStateChanged(this, object, oldState, newState);
          }
        }
        finally
        {
          unlockView();
        }
      }
    }
  }

  public void addRegistrationHandler(CDORegistrationHandler handler)
  {
    registrationHandlers.add(handler);
  }

  public void removeRegistrationHandler(CDORegistrationHandler handler)
  {
    registrationHandlers.remove(handler);
  }

  public CDORegistrationHandler[] getRegistrationHandlers()
  {
    return registrationHandlers.get();
  }

  /*
   * Synchronized through InvalidationRunner.run()
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
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
          }

          conflicts.put(detachedObject, Pair.create(oldRevision, CDORevisionDelta.DETACHED));
        }
      }
    }

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
            ((CDOResourceNodeImpl)changedObject).recacheURIs();
          }
        }

        revisionDeltas.put(changedObject, delta);
        if (changedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
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

  @Deprecated
  public int reload(CDOObject... objects)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        Collection<InternalCDOObject> internalObjects;
        if (objects != null && objects.length != 0)
        {
          internalObjects = new ArrayList<InternalCDOObject>(objects.length);
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
          internalObjects = new ArrayList<InternalCDOObject>(this.objects.values());
        }

        int result = internalObjects.size();
        if (result != 0)
        {
          CDOStateMachine.INSTANCE.reload(internalObjects.toArray(new InternalCDOObject[result]));
        }

        return result;
      }
      finally
      {
        unlockView();
      }
    }
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public String toString()
  {
    if (!isActive())
    {
      return super.toString();
    }

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

    if (branchPoint != null)
    {
      boolean brackets = false;
      if (getSession().getRepositoryInfo().isSupportingBranches())
      {
        brackets = true;
        builder.append(" ["); //$NON-NLS-1$
        builder.append(branchPoint.getBranch().getPathName()); // Do not synchronize on this view!
      }

      long timeStamp = branchPoint.getTimeStamp(); // Do not synchronize on this view!
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

  public void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
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
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected InternalCDORevision getViewedRevision(InternalCDOObject object)
  {
    return CDOStateMachine.INSTANCE.readNoLoad(object);
  }

  public CDOChangeSetData compareRevisions(CDOBranchPoint source)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOSession session = getSession();
        return session.compareRevisions(source, this);
      }
      finally
      {
        unlockView();
      }
    }
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

    LifecycleUtil.activate(viewLock);

    if (branchPoint != null)
    {
      basicSetBranchPoint(branchPoint);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (viewSet != null && viewSet.getResourceSet() != null)
    {
      viewSet.getResourceSet().getURIConverter().getURIHandlers().remove(getURIHandler());
    }

    LifecycleUtil.deactivate(viewLock);

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

    public CDOBranchPoint getOldBranchPoint()
    {
      return oldBranchPoint;
    }

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
        event = new SingleDeltaContainerEvent<CDOResourceNode>(AbstractCDOView.this, (CDOResourceNode)msg.getNewValue(), IContainerDelta.Kind.ADDED);
        break;

      case Notification.ADD_MANY:
        // TODO
        break;

      case Notification.REMOVE:
        event = new SingleDeltaContainerEvent<CDOResourceNode>(AbstractCDOView.this, (CDOResourceNode)msg.getOldValue(), IContainerDelta.Kind.REMOVED);
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
  private static final class NOOPMonitor
  {
  }
}
