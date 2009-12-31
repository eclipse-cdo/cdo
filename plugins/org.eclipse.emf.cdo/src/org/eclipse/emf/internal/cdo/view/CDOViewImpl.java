/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.internal.cdo.CDODeltaNotificationImpl;
import org.eclipse.emf.internal.cdo.CDOInvalidationNotificationImpl;
import org.eclipse.emf.internal.cdo.CDOMetaWrapper;
import org.eclipse.emf.internal.cdo.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOStore;
import org.eclipse.emf.internal.cdo.CDOURIHandler;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends Lifecycle implements InternalCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private InternalCDOSession session;

  private InternalCDOViewSet viewSet;

  private CDOURIHandler uriHandler = new CDOURIHandler(this);

  private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

  private ConcurrentMap<CDOID, InternalCDOObject> objects;

  private CDOStore store = new CDOStore(this);

  private ReentrantLock lock = new ReentrantLock(true);

  private ReentrantLock stateLock = new ReentrantLock(true);

  private CDOResourceImpl rootResource;

  private ChangeSubscriptionManager changeSubscriptionManager = createChangeSubscriptionManager();

  private AdapterManager adapterPolicyManager = createAdapterManager();

  /**
   * TODO Optimize by storing an array. See {@link Notifier}.
   */
  private List<CDOObjectHandler> objectHandlers = new ArrayList<CDOObjectHandler>(0);

  private OptionsImpl options;

  @ExcludeFromDump
  private transient CDOID lastLookupID;

  @ExcludeFromDump
  private transient InternalCDOObject lastLookupObject;

  /**
   * @since 2.0
   */
  public CDOViewImpl()
  {
    options = createOptions();
  }

  /**
   * @since 2.0
   */
  public OptionsImpl options()
  {
    return options;
  }

  public int getViewID()
  {
    return viewID;
  }

  /**
   * @since 2.0
   */
  public void setViewID(int viewId)
  {
    viewID = viewId;
  }

  public Type getViewType()
  {
    return Type.READONLY;
  }

  public ResourceSet getResourceSet()
  {
    return viewSet.getResourceSet();
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

  /**
   * @since 2.0
   */
  public InternalCDOSession getSession()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  public void setSession(InternalCDOSession session)
  {
    this.session = session;
  }

  public CDOStore getStore()
  {
    checkActive();
    return store;
  }

  /**
   * @since 2.0
   */
  public synchronized CDOResourceImpl getRootResource()
  {
    checkActive();
    if (rootResource == null)
    {
      rootResource = createRootResource();
    }

    return rootResource;
  }

  /**
   * @return
   * @since 2.0
   */
  protected CDOResourceImpl createRootResource()
  {
    return (CDOResourceImpl)getResource(CDOResourceNode.ROOT_PATH);
  }

  /**
   * @since 2.0
   */
  public CDOURIHandler getURIHandler()
  {
    return uriHandler;
  }

  /**
   * @since 2.0
   */
  public ReentrantLock getLock()
  {
    return lock;
  }

  /**
   * @since 2.0
   */
  public ReentrantLock getStateLock()
  {
    return stateLock;
  }

  /**
   * @throws InterruptedException
   * @since 2.0
   */
  public void lockObjects(Collection<? extends CDOObject> objects, RWLockManager.LockType lockType, long timeout)
      throws InterruptedException
  {
    checkActive();
    Map<CDOID, CDOIDAndVersion> uniqueObjects = new HashMap<CDOID, CDOIDAndVersion>();
    getCDOIDAndVersion(uniqueObjects, objects);
    for (CDOObject object : objects)
    {
      CDOIDAndVersion idAndVersion = uniqueObjects.get(object.cdoID());
      if (idAndVersion == null)
      {
        uniqueObjects
            .put(object.cdoID(), CDOIDUtil.createIDAndVersion(object.cdoID(), CDORevision.UNSPECIFIED_VERSION));
      }
    }

    session.getSessionProtocol().lockObjects(this, uniqueObjects, timeout, lockType);
  }

  /**
   * @since 2.0
   */
  public void unlockObjects(Collection<? extends CDOObject> objects, RWLockManager.LockType lockType)
  {
    checkActive();
    session.getSessionProtocol().unlockObjects(this, objects, lockType);
  }

  /**
   * @since 2.0
   */
  public void unlockObjects()
  {
    unlockObjects(null, null);
  }

  /**
   * @since 2.0
   */
  public boolean isObjectLocked(CDOObject object, RWLockManager.LockType lockType, boolean byOthers)
  {
    checkActive();
    return session.getSessionProtocol().isObjectLocked(this, object, lockType, byOthers);
  }

  /**
   * @since 2.0
   */
  public long getTimeStamp()
  {
    return UNSPECIFIED_DATE;
  }

  public boolean isDirty()
  {
    return false;
  }

  public boolean hasConflict()
  {
    return false;
  }

  /**
   * @since 2.0
   */
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return featureAnalyzer;
  }

  /**
   * @since 2.0
   */
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    this.featureAnalyzer = featureAnalyzer == null ? CDOFeatureAnalyzer.NOOP : featureAnalyzer;
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction toTransaction()
  {
    checkActive();
    if (this instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)this;
    }

    throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.0"), this)); //$NON-NLS-1$
  }

  public boolean hasResource(String path)
  {
    checkActive();

    try
    {
      getResourceNodeID(path);
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  /**
   * @since 2.0
   */
  public CDOQuery createQuery(String language, String queryString)
  {
    checkActive();
    return new CDOQueryImpl(this, language, queryString);
  }

  /**
   * @since 2.0
   */
  public CDOResourceNode getResourceNode(String path)
  {
    CDOID id = getResourceNodeID(path);
    if (id == null)
    {
      return null;
    }

    InternalCDOObject object = getObject(id);
    if (object instanceof CDOResourceNode)
    {
      return (CDOResourceNode)object;
    }

    return null;
  }

  /**
   * @return never <code>null</code>
   * @since 2.0
   */
  public CDOID getResourceNodeID(String path)
  {
    if (StringUtil.isEmpty(path))
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.1")); //$NON-NLS-1$
    }

    CDOID folderID = null;
    if (CDOURIUtil.SEGMENT_SEPARATOR.equals(path))
    {
      folderID = getResourceNodeIDChecked(null, null);
    }
    else
    {
      List<String> names = CDOURIUtil.analyzePath(path);
      for (String name : names)
      {
        folderID = getResourceNodeIDChecked(folderID, name);
      }
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   */
  private CDOID getResourceNodeIDChecked(CDOID folderID, String name)
  {
    folderID = getResourceNodeID(folderID, name);
    if (folderID == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.2"), name)); //$NON-NLS-1$
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   * @since 2.0
   */
  protected CDOResourceNode getResourceNode(CDOID folderID, String name)
  {
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
  }

  /**
   * @since 2.0
   */
  protected CDOID getResourceNodeID(CDOID folderID, String name)
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

    int size = folderRevision.data().size(nodesFeature);
    for (int i = 0; i < size; i++)
    {
      Object value = folderRevision.data().get(nodesFeature, i);
      value = getStore().resolveProxy(folderRevision, nodesFeature, i, value);

      CDORevision childRevision = getLocalRevision((CDOID)convertObjectToID(value, false));
      if (name.equals(childRevision.data().get(nameFeature, 0)))
      {
        return childRevision.getID();
      }
    }

    throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.5"), name)); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  protected CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    CDOQuery resourceQuery = createResourcesQuery(null, name, true);
    resourceQuery.setMaxResults(1);
    List<CDOID> ids = resourceQuery.getResult(CDOID.class);
    if (ids.isEmpty())
    {
      if (name == null)
      {
        throw new CDOException(Messages.getString("CDOViewImpl.6")); //$NON-NLS-1$
      }
      else
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.7"), name)); //$NON-NLS-1$
      }
    }

    if (ids.size() > 1)
    {
      // TODO is this still needed since the is resourceQuery.setMaxResults(1) ??
      throw new ImplementationError(Messages.getString("CDOViewImpl.8")); //$NON-NLS-1$
    }

    return ids.get(0);
  }

  /**
   * @since 2.0
   */
  protected InternalCDORevision getLocalRevision(CDOID id)
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

  /**
   * @since 2.0
   */
  public InternalCDOObject[] getObjectsArray()
  {
    synchronized (objects)
    {
      return objects.values().toArray(new InternalCDOObject[objects.size()]);
    }
  }

  /**
   * TODO Remove me
   * 
   * @since 2.0
   */
  @Deprecated
  public CDOResourceFolder getResourceFolder(String path)
  {
    if (path == null)
    {
      return null;
    }

    CDOResourceFolder folder = null;
    StringTokenizer tokenizer = new StringTokenizer(path, CDOURIUtil.SEGMENT_SEPARATOR);
    while (tokenizer.hasMoreTokens())
    {
      String segment = tokenizer.nextToken();
      if (segment != null)
      {
        if (folder == null)
        {
        }
        else
        {
        }
      }
    }

    return folder;
  }

  /**
   * @since 2.0
   */
  public CDOResource getResource(String path)
  {
    return getResource(path, true);
  }

  /**
   * @since 2.0
   */
  public CDOResource getResource(String path, boolean loadInDemand)
  {
    checkActive();
    URI uri = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().getResource(uri, loadInDemand);
  }

  /**
   * @since 2.0
   */
  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    checkActive();
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResult(CDOResourceNode.class);
  }

  /**
   * @since 2.0
   */
  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch)
  {
    checkActive();
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResultAsync(CDOResourceNode.class);
  }

  private CDOQuery createResourcesQuery(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    CDOQuery query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, name);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID, folder == null ? null : folder.cdoID());
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH, exactMatch);
    return query;
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
    EObject eObject = EcoreUtil.create(eClass);
    return FSMUtil.adapt(eObject, this);
  }

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    CDORevisionResolver revisionManager = session.getRevisionManager();
    int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
    return (InternalCDORevision)revisionManager.getRevision(id, initialChunkSize, loadOnDemand);
  }

  public InternalCDOObject getObject(CDOID id)
  {
    return getObject(id, true);
  }

  /**
   * Support recursivity and concurrency.
   */
  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    // Since getObject could trigger a read (ONLY when we load a resource) we NEED to make sure the state lock is
    // active.
    // Always use in the following order
    // 1- getStateLock().lock();
    // 2- synchronized(objects)
    // DO NOT inverse them otherwise deadlock could occured.
    getStateLock().lock();
    
    try
    {
      synchronized (objects)
      {
        if (id.equals(lastLookupID))
        {
          return lastLookupObject;
        }

        // Needed for recursive call to getObject. (from createObject/cleanObject/getResource/getObject)
        InternalCDOObject localLookupObject = objects.get(id);
        if (localLookupObject == null)
        {
          if (id.isMeta())
          {
            localLookupObject = createMetaObject((CDOIDMeta)id);
          }
          else
          {
            if (loadOnDemand)
            {
              if (id.isTemporary())
              {
                throw new ObjectNotFoundException(id);
              }

              localLookupObject = createObject(id);
            }
            else
            {
              return null;
            }
          }

          // CDOResource have a special way to register to the view.
          if (!CDOModelUtil.isResource(localLookupObject.eClass()))
          {
            registerObject(localLookupObject);
          }
        }

        lastLookupID = id;
        lastLookupObject = localLookupObject;
        return lastLookupObject;
      }
    }
    finally
    {
      getStateLock().unlock();
    }
  }

  /**
   * @since 2.0
   */
  @SuppressWarnings("unchecked")
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    checkActive();
    if (objectFromDifferentView instanceof CDOObject)
    {
      CDOObject object = (CDOObject)objectFromDifferentView;
      CDOView view = object.cdoView();
      if (view != this)
      {
        if (view.getSession() != session)
        {
          throw new IllegalArgumentException(MessageFormat.format(
              Messages.getString("CDOViewImpl.11"), objectFromDifferentView)); //$NON-NLS-1$
        }

        CDOID id = object.cdoID();
        objectFromDifferentView = (T)getObject(id, true);
      }
    }

    return objectFromDifferentView;
  }

  public boolean isObjectRegistered(CDOID id)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return false;
    }

    synchronized (objects)
    {
      return objects.containsKey(id);
    }
  }

  public InternalCDOObject removeObject(CDOID id)
  {
    synchronized (objects)
    {
      if (id.equals(lastLookupID))
      {
        lastLookupID = null;
        lastLookupObject = null;
      }

      return objects.remove(id);
    }
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createMetaObject(CDOIDMeta id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating meta object for " + id); //$NON-NLS-1$
    }

    InternalCDOPackageRegistry packageRegistry = session.getPackageRegistry();
    InternalEObject metaInstance = packageRegistry.getMetaInstanceMapper().lookupMetaInstance(id);
    return new CDOMetaWrapper(this, metaInstance, id);
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

    InternalCDORevision revision = getRevision(id, true);
    FSMUtil.validate(id, revision);

    EClass eClass = revision.getEClass();
    InternalCDOObject object;
    if (CDOModelUtil.isResource(eClass))
    {
      object = (InternalCDOObject)newResourceInstance(revision);
      // object is PROXY
    }
    else
    {
      object = newInstance(eClass);
      // object is TRANSIENT
    }

    cleanObject(object, revision);
    return object;
  }

  private CDOResource newResourceInstance(InternalCDORevision revision)
  {
    String path = getResourcePath(revision);
    return getResource(path, true);
  }

  private String getResourcePath(InternalCDORevision revision)
  {
    EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    CDOID folderID = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(nameFeature, 0);
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
    object.cdoInternalCleanup();

    object.cdoInternalSetView(this);
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetState(CDOState.CLEAN);

    object.cdoInternalPostLoad();
  }

  public CDOID provideCDOID(Object idOrObject)
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
    else if (idOrObject instanceof InternalEObject)
    {
      InternalEObject eObject = (InternalEObject)idOrObject;
      String uri = EcoreUtil.getURI(eObject).toString();
      if (eObject instanceof InternalCDOObject)
      {
        InternalCDOObject object = (InternalCDOObject)idOrObject;
        if (object.cdoView() != null && FSMUtil.isNew(object))
        {
          return CDOIDUtil.createExternalTemp(uri);
        }
      }

      if (eObject.eResource() != null)
      {
        return CDOIDUtil.createExternal(uri);
      }

      throw new DanglingReferenceException(eObject);
    }

    throw new IllegalStateException(MessageFormat.format(
        Messages.getString("CDOViewImpl.16"), idOrObject.getClass().getName())); //$NON-NLS-1$
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
        try
        {
          InternalCDOObject object = FSMUtil.getLegacyAdapter(((InternalEObject)potentialObject).eAdapters());
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
        catch (Throwable ex)
        {
          OM.LOG.warn(ex);
        }
      }
    }

    return potentialObject;
  }

  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    boolean newOrTransient = FSMUtil.isTransient(object) || FSMUtil.isNew(object);

    if (!(onlyPersistedID && newOrTransient))
    {
      CDOView view = object.cdoView();
      if (view == this)
      {
        return object.cdoID();
      }

      if (view != null && view.getSession() == getSession())
      {
        return object.cdoID();
      }
    }

    return null;
  }

  public Object convertIDToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

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

    return potentialID;
  }

  /**
   * @since 2.0
   */
  public void attachResource(CDOResourceImpl resource)
  {
    if (!resource.isExisting())
    {
      throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.18"), this)); //$NON-NLS-1$
    }

    // ResourceSet.getResource(uri, true) was called!!
    resource.cdoInternalSetView(this);
    resource.cdoInternalSetState(CDOState.PROXY);
  }

  /**
   * @since 2.0
   */
  public void registerProxyResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    String path = CDOURIUtil.extractResourcePath(uri);

    try
    {
      CDOID id = getResourceNodeID(path);
      resource.cdoInternalSetID(id);
      registerObject(resource);
    }
    catch (Exception ex)
    {
      throw new InvalidURIException(uri, ex);
    }
  }

  public void registerObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", object); //$NON-NLS-1$
    }

    InternalCDOObject old;
    synchronized (objects)
    {
      old = objects.put(object.cdoID(), object);
    }

    if (old != null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.20"), object)); //$NON-NLS-1$
    }
  }

  public void deregisterObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering {0}", object); //$NON-NLS-1$
    }

    removeObject(object.cdoID());
  }

  public void remapObject(CDOID oldID)
  {
    CDOID newID;
    synchronized (objects)
    {
      InternalCDOObject object = objects.remove(oldID);
      newID = object.cdoID();
      objects.put(newID, object);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping {0} --> {1}", oldID, newID); //$NON-NLS-1$
    }
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    synchronized (objectHandlers)
    {
      if (!objectHandlers.contains(handler))
      {
        objectHandlers.add(handler);
      }
    }
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    synchronized (objectHandlers)
    {
      objectHandlers.remove(handler);
    }
  }

  public CDOObjectHandler[] getObjectHandlers()
  {
    synchronized (objectHandlers)
    {
      return objectHandlers.toArray(new CDOObjectHandler[objectHandlers.size()]);
    }
  }

  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState)
  {
    CDOObjectHandler[] handlers = getObjectHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      CDOObjectHandler handler = handlers[i];
      handler.objectStateChanged(this, object, oldState, newState);
    }
  }

  /**
   * Turns registered objects into proxies and synchronously delivers invalidation events to registered event listeners.
   * <p>
   * <b>Implementation note:</b> This implementation guarantees that exceptions from listener code don't propagate up to
   * the caller of this method. Runtime exceptions from the implementation of the {@link CDOStateMachine} are propagated
   * to the caller of this method but this should not happen in the absence of implementation errors.
   * <p>
   * Note that this method can block for an uncertain amount of time on the reentrant view lock!
   * 
   * @param timeStamp
   *          The time stamp of the server transaction if this event was sent as a result of a successfully committed
   *          transaction or <code>LOCAL_ROLLBACK</code> if this event was sent due to a local rollback.
   * @param dirtyOIDs
   *          A set of the object IDs to be invalidated. <b>Implementation note:</b> This implementation expects the
   *          dirtyOIDs set to be unmodifiable. It does not wrap the set (again).
   * @since 2.0
   */
  public Set<CDOObject> handleInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedOIDs)
  {
    Set<CDOObject> conflicts = null;
    Set<InternalCDOObject> dirtyObjects = new HashSet<InternalCDOObject>();
    Set<InternalCDOObject> detachedObjects = new HashSet<InternalCDOObject>();
    lock.lock();

    try
    {
      conflicts = handleInvalidationWithoutNotification(dirtyOIDs, detachedOIDs, dirtyObjects, detachedObjects);
    }
    finally
    {
      lock.unlock();
    }

    if (options().isInvalidationNotificationEnabled())
    {
      for (InternalCDOObject dirtyObject : dirtyObjects)
      {
        if (dirtyObject.eNotificationRequired())
        {
          CDOInvalidationNotificationImpl notification = new CDOInvalidationNotificationImpl(dirtyObject);
          dirtyObject.eNotify(notification);
        }
      }

      for (InternalCDOObject detachedObject : detachedObjects)
      {
        if (detachedObject.eNotificationRequired())
        {
          CDOInvalidationNotificationImpl notification = new CDOInvalidationNotificationImpl(detachedObject);
          detachedObject.eNotify(notification);
        }
      }
    }

    fireInvalidationEvent(timeStamp, Collections.unmodifiableSet(dirtyObjects), Collections
        .unmodifiableSet(detachedObjects));
    return conflicts;
  }

  public Set<CDOObject> handleInvalidationWithoutNotification(Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedOIDs, Set<InternalCDOObject> dirtyObjects, Set<InternalCDOObject> detachedObjects)
  {
    Set<CDOObject> conflicts = null;
    for (CDOIDAndVersion dirtyOID : dirtyOIDs)
    {
      InternalCDOObject dirtyObject = null;
      // 258831 - Causes deadlock when introduce thread safe mechanisms in State machine.
      synchronized (objects)
      {
        dirtyObject = objects.get(dirtyOID.getID());
      }

      if (dirtyObject != null)
      {
        CDOStateMachine.INSTANCE.invalidate(dirtyObject, dirtyOID.getVersion());
        dirtyObjects.add(dirtyObject);
        if (dirtyObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashSet<CDOObject>();
          }

          conflicts.add(dirtyObject);
        }
      }
    }

    for (CDOID id : detachedOIDs)
    {
      InternalCDOObject detachedObject = removeObject(id);
      if (detachedObject != null)
      {
        CDOStateMachine.INSTANCE.detachRemote(detachedObject);
        detachedObjects.add(detachedObject);
        if (detachedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashSet<CDOObject>();
          }

          conflicts.add(detachedObject);
        }
      }
    }

    return conflicts;
  }

  /**
   * @since 2.0
   */
  private void fireInvalidationEvent(long timeStamp, Set<? extends CDOObject> dirtyObjects,
      Set<? extends CDOObject> detachedObjects)
  {
    if (!dirtyObjects.isEmpty() || !detachedObjects.isEmpty())
    {
      fireEvent(new InvalidationEvent(timeStamp, dirtyObjects, detachedObjects));
    }
  }

  /**
   * @since 2.0
   */
  public void fireAdaptersNotifiedEvent(long timeStamp)
  {
    fireEvent(new AdaptersNotifiedEvent(timeStamp));
  }

  /**
   * @since 2.0
   */
  public void handleChangeSubscription(Collection<CDORevisionDelta> deltas, Collection<CDOID> detachedObjects)
  {
    boolean policiesPresent = options().hasChangeSubscriptionPolicies();
    if (!policiesPresent)
    {
      return;
    }

    if (deltas != null)
    {
      CDONotificationBuilder builder = new CDONotificationBuilder();
      for (CDORevisionDelta delta : deltas)
      {
        InternalCDOObject object = changeSubscriptionManager.getSubcribeObject(delta.getID());
        if (object != null && object.eNotificationRequired())
        {
          NotificationImpl notification = builder.buildNotification(object, delta);
          if (notification != null)
          {
            notification.dispatch();
          }
        }
      }
    }

    if (detachedObjects != null)
    {
      for (CDOID id : detachedObjects)
      {
        InternalCDOObject object = changeSubscriptionManager.getSubcribeObject(id);
        if (object != null && object.eNotificationRequired())
        {
          NotificationImpl notification = new CDODeltaNotificationImpl(object, CDONotification.DETACH_OBJECT,
              Notification.NO_FEATURE_ID, null, null);
          notification.dispatch();
        }
      }

      getChangeSubscriptionManager().handleDetachedObjects(detachedObjects);
    }
  }

  /**
   * @since 2.0
   */
  protected ChangeSubscriptionManager createChangeSubscriptionManager()
  {
    return new ChangeSubscriptionManager();
  }

  /**
   * @since 2.0
   */
  public AdapterManager getAdapterManager()
  {
    return adapterPolicyManager;
  }

  /**
   * @since 2.0
   */
  protected AdapterManager createAdapterManager()
  {
    return new AdapterManager();
  }

  /**
   * @since 2.0
   */
  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      subscribe(eObject, adapter);
    }

    adapterPolicyManager.attachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      unsubscribe(eObject, adapter);
    }

    adapterPolicyManager.detachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void subscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.subscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.unsubscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public boolean hasSubscription(CDOID id)
  {
    return changeSubscriptionManager.getSubcribeObject(id) != null;
  }

  /**
   * @since 2.0
   */
  protected ChangeSubscriptionManager getChangeSubscriptionManager()
  {
    return changeSubscriptionManager;
  }

  /**
   * Needed for {@link CDOAuditImpl#setTimeStamp(long)}.
   * 
   * @since 2.0
   */
  protected List<InternalCDOObject> getInvalidObjects(long timeStamp)
  {
    List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
    synchronized (objects)
    {
      for (InternalCDOObject object : objects.values())
      {
        CDORevision revision = object.cdoRevision();
        if (revision == null)
        {
          revision = getRevision(object.cdoID(), false);
        }

        if (revision == null || !revision.isValid(timeStamp))
        {
          result.add(object);
        }
      }
    }

    return result;
  }

  public int reload(CDOObject... objects)
  {
    Collection<InternalCDOObject> internalObjects;
    // TODO Should objects.length == 0 reload *all* objects, too?
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
      synchronized (this.objects)
      {
        internalObjects = new ArrayList<InternalCDOObject>(this.objects.values());
      }
    }

    int result = internalObjects.size();
    if (result != 0)
    {
      CDOStateMachine.INSTANCE.reload(internalObjects.toArray(new InternalCDOObject[result]));
    }

    return result;
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

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOView({0})", viewID); //$NON-NLS-1$
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return getResourceSet();
  }

  public void getCDOIDAndVersion(Map<CDOID, CDOIDAndVersion> uniqueObjects, Collection<? extends CDOObject> cdoObjects)
  {
    for (CDOObject internalCDOObject : cdoObjects)
    {
      CDORevision cdoRevision = CDOStateMachine.INSTANCE.readNoLoad((InternalCDOObject)internalCDOObject);
      CDOID cdoId = internalCDOObject.cdoID();
      if (cdoRevision != null && !uniqueObjects.containsKey(cdoId))
      {
        int version = cdoRevision.getVersion();
        uniqueObjects.put(cdoId, CDOIDUtil.createIDAndVersion(cdoId, version));
      }
    }
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
  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(session, "session"); //$NON-NLS-1$
    checkState(viewID > 0, "viewID"); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doActivate() throws Exception
  {
    session.getSessionProtocol().openView(getViewID(), getProtocolViewType(), getTimeStamp());
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      session.getSessionProtocol().closeView(getViewID());
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    try
    {
      session.viewDetached(this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    session = null;
    objects = null;
    store = null;
    viewSet = null;
    changeSubscriptionManager = null;
    featureAnalyzer = null;
    lastLookupID = null;
    lastLookupObject = null;
    options = null;
  }

  private byte getProtocolViewType()
  {
    Type viewType = getViewType();
    switch (viewType)
    {
    case READONLY:
      return CDOProtocolConstants.VIEW_READONLY;

    case TRANSACTION:
      return CDOProtocolConstants.VIEW_TRANSACTION;

    case AUDIT:
      return CDOProtocolConstants.VIEW_AUDIT;

    default:
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.26"), viewType)); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class Event extends org.eclipse.net4j.util.event.Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    public Event()
    {
      super(CDOViewImpl.this);
    }

    public CDOViewImpl getView()
    {
      return CDOViewImpl.this;
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected class AdapterManager
  {
    private Set<CDOObject> objects = new HashBag<CDOObject>();

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (options().getStrongReferencePolicy() != CDOAdapterPolicy.NONE)
      {
        for (CDOObject object : commitContext.getNewObjects().values())
        {
          attachObject(object);
        }

        for (CDOObject object : commitContext.getNewResources().values())
        {
          attachObject(object);
        }

        for (CDOObject object : commitContext.getDetachedObjects().values())
        {
          detachObject(object);
        }
      }
    }

    protected synchronized void attachObject(CDOObject object)
    {
      if (((InternalEObject)object).eNotificationRequired())
      {
        CDOAdapterPolicy strongReferencePolicy = options().getStrongReferencePolicy();
        int count = 0;
        for (Adapter adapter : object.eAdapters())
        {
          if (strongReferencePolicy.isValid(object, adapter))
          {
            count++;
          }
        }

        for (int i = 0; i < count; i++)
        {
          objects.add(object);
        }
      }
    }

    protected synchronized void detachObject(CDOObject object)
    {
      while (objects.remove(object))
      {
        // Do nothing
      }
    }

    protected synchronized void attachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.add(object);
      }
    }

    protected synchronized void detachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.remove(object);
      }
    }

    public synchronized void reset()
    {
      // Keep the object in memory
      Set<CDOObject> oldObject = objects;
      objects = new HashBag<CDOObject>();
      if (options().getStrongReferencePolicy() != CDOAdapterPolicy.NONE)
      {
        InternalCDOObject objects[] = getObjectsArray();
        for (int i = 0; i < objects.length; i++)
        {
          InternalCDOObject object = objects[i];
          attachObject(object);
        }
      }

      oldObject.clear();
    }
  };

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected class ChangeSubscriptionManager
  {
    private Map<CDOID, SubscribeEntry> subscriptions = new HashMap<CDOID, SubscribeEntry>()
    {
      private static final long serialVersionUID = 1L;
    };

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      handleNewObjects(commitContext.getNewResources().values());
      handleNewObjects(commitContext.getNewObjects().values());
      handleDetachedObjects(commitContext.getDetachedObjects().keySet());
    }

    public void subscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, 1);
    }

    public void unsubscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, -1);
    }

    /**
     * Register to the server all objects from the active list
     */
    protected void notifyChangeSubcriptionPolicy()
    {
      boolean policiesPresent = options().hasChangeSubscriptionPolicies();
      synchronized (subscriptions)
      {
        subscriptions.clear();
        List<CDOID> cdoIDs = new ArrayList<CDOID>();
        if (policiesPresent)
        {
          for (InternalCDOObject cdoObject : getObjectsArray())
          {
            int count = getNumberOfValidAdapter(cdoObject);
            if (count > 0)
            {
              cdoIDs.add(cdoObject.cdoID());
              addEntry(cdoObject.cdoID(), cdoObject, count);
            }
          }
        }

        request(cdoIDs, true, true);
      }
    }

    protected void handleDetachedObjects(Collection<CDOID> detachedObjects)
    {
      synchronized (subscriptions)
      {
        for (CDOID id : detachedObjects)
        {
          SubscribeEntry entry = subscriptions.get(id);
          if (entry != null)
          {
            detachObject(id);
          }
        }
      }
    }

    protected void handleNewObjects(Collection<? extends CDOObject> newObjects)
    {
      synchronized (subscriptions)
      {
        for (CDOObject object : newObjects)
        {
          InternalCDOObject cdoDetachedObject = (InternalCDOObject)object;
          if (cdoDetachedObject != null)
          {
            int count = getNumberOfValidAdapter(cdoDetachedObject);
            if (count > 0)
            {
              subscribe(cdoDetachedObject.cdoID(), cdoDetachedObject, count);
            }
          }
        }
      }
    }

    protected InternalCDOObject getSubcribeObject(CDOID id)
    {
      synchronized (subscriptions)
      {
        SubscribeEntry entry = subscriptions.get(id);
        if (entry != null)
        {
          return entry.getObject();
        }
      }

      return null;
    }

    protected void request(List<CDOID> cdoIDs, boolean clear, boolean subscribeMode)
    {
      session.getSessionProtocol().changeSubscription(getViewID(), cdoIDs, subscribeMode, clear);
    }

    protected int getNumberOfValidAdapter(InternalCDOObject object)
    {
      int count = 0;
      if (!FSMUtil.isTransient(object) && !FSMUtil.isNew(object))
      {
        if (object.eNotificationRequired())
        {
          for (Adapter adapter : object.eAdapters())
          {
            if (shouldSubscribe(object, adapter))
            {
              count++;
            }
          }
        }
      }

      return count;
    }

    private void subscribe(EObject eObject, Adapter adapter, int adjust)
    {
      synchronized (subscriptions)
      {
        if (shouldSubscribe(eObject, adapter))
        {
          InternalCDOObject internalCDOObject = FSMUtil.adapt(eObject, CDOViewImpl.this);
          if (internalCDOObject.cdoView() != CDOViewImpl.this)
          {
            throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.27"), internalCDOObject)); //$NON-NLS-1$
          }

          subscribe(internalCDOObject.cdoID(), internalCDOObject, adjust);
        }
      }
    }

    private boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      for (CDOAdapterPolicy policy : options().getChangeSubscriptionPolicies())
      {
        if (policy.isValid(eObject, adapter))
        {
          return true;
        }
      }

      return false;
    }

    private void subscribe(CDOID id, InternalCDOObject cdoObject, int adjust)
    {
      boolean policiesPresent = options().hasChangeSubscriptionPolicies();
      synchronized (subscriptions)
      {
        int count = 0;
        SubscribeEntry entry = subscriptions.get(id);
        if (entry == null)
        {
          // Cannot adjust negative value
          if (adjust < 0)
          {
            return;
          }

          // Notification need to be enable to send correct value to the server
          if (policiesPresent)
          {
            request(Collections.singletonList(id), false, true);
          }
        }
        else
        {
          count = entry.getCount();
        }

        count += adjust;

        // Look if objects need to be unsubscribe
        if (count <= 0)
        {
          subscriptions.remove(id);

          // Notification need to be enable to send correct value to the server
          if (policiesPresent)
          {
            request(Collections.singletonList(id), false, false);
          }
        }
        else
        {
          if (entry == null)
          {
            addEntry(id, cdoObject, count);
          }
          else
          {
            entry.setCount(count);
          }
        }
      }
    }

    private void detachObject(CDOID id)
    {
      subscribe(id, null, Integer.MIN_VALUE);
    }

    private void addEntry(CDOID key, InternalCDOObject object, int count)
    {
      subscriptions.put(key, new SubscribeEntry(key, object, count));
    }

    class SubscribeEntry
    {
      private CDOID id;

      private int count = 0;

      private InternalCDOObject object;

      public SubscribeEntry(CDOID id, InternalCDOObject object, int count)
      {
        super();
        this.id = id;
        this.count = count;
        this.object = object;
      }

      public int getCount()
      {
        return count;
      }

      public void setCount(int count)
      {
        this.count = count;
      }

      public CDOID getId()
      {
        return id;
      }

      public InternalCDOObject getObject()
      {
        return object;
      }
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class InvalidationEvent extends Event implements CDOViewInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    private Set<? extends CDOObject> dirtyObjects;

    private Set<? extends CDOObject> detachedObjects;

    public InvalidationEvent(long timeStamp, Set<? extends CDOObject> dirtyOIDs,
        Set<? extends CDOObject> detachedObjects)
    {
      this.timeStamp = timeStamp;
      dirtyObjects = dirtyOIDs;
      this.detachedObjects = detachedObjects;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Set<? extends CDOObject> getDirtyObjects()
    {
      return dirtyObjects;
    }

    public Set<? extends CDOObject> getDetachedObjects()
    {
      return detachedObjects;
    }

    @Override
    public String toString()
    {
      return "CDOViewInvalidationEvent: " + dirtyObjects; //$NON-NLS-1$
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
    public String toString()
    {
      return "CDOViewAdaptersNotifiedEvent: " + timeStamp; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean invalidationNotificationEnabled;

    private CDORevisionPrefetchingPolicy revisionPrefetchingPolicy;

    private HashBag<CDOAdapterPolicy> changeSubscriptionPolicies = new HashBag<CDOAdapterPolicy>();

    private CDOAdapterPolicy adapterReferencePolicy = CDOAdapterPolicy.ALL;

    public OptionsImpl()
    {
      setCacheReferenceType(null);
      invalidationNotificationEnabled = OM.PREF_ENABLE_INVALIDATION_NOTIFICATION.getValue();
      revisionPrefetchingPolicy = CDOUtil.createRevisionPrefetchingPolicy(OM.PREF_REVISION_LOADING_CHUNK_SIZE
          .getValue());
    }

    public CDOViewImpl getContainer()
    {
      return CDOViewImpl.this;
    }

    public boolean isInvalidationNotificationEnabled()
    {
      return invalidationNotificationEnabled;
    }

    public void setInvalidationNotificationEnabled(boolean enabled)
    {
      if (invalidationNotificationEnabled != enabled)
      {
        invalidationNotificationEnabled = enabled;
        fireEvent(new InvalidationNotificationEventImpl());
      }
    }

    public boolean hasChangeSubscriptionPolicies()
    {
      synchronized (changeSubscriptionPolicies)
      {
        return !changeSubscriptionPolicies.isEmpty();
      }
    }

    public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
    {
      synchronized (changeSubscriptionPolicies)
      {
        return changeSubscriptionPolicies.toArray(new CDOAdapterPolicy[changeSubscriptionPolicies.size()]);
      }
    }

    public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      boolean changed = false;
      synchronized (changeSubscriptionPolicies)
      {
        if (changeSubscriptionPolicies.add(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          changed = true;
        }
      }

      if (changed)
      {
        fireEvent(new ChangeSubscriptionPoliciesEventImpl());
      }
    }

    public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      boolean changed = false;
      synchronized (changeSubscriptionPolicies)
      {
        if (changeSubscriptionPolicies.remove(policy) && !changeSubscriptionPolicies.contains(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          changed = true;
        }
      }

      if (changed)
      {
        fireEvent(new ChangeSubscriptionPoliciesEventImpl());
      }
    }

    public CDOAdapterPolicy getStrongReferencePolicy()
    {
      return adapterReferencePolicy;
    }

    public void setStrongReferencePolicy(CDOAdapterPolicy adapterPolicy)
    {
      if (adapterPolicy == null)
      {
        adapterPolicy = CDOAdapterPolicy.ALL;
      }

      if (adapterReferencePolicy != adapterPolicy)
      {
        adapterReferencePolicy = adapterPolicy;
        adapterPolicyManager.reset();
        fireEvent(new ReferencePolicyEventImpl());
      }
    }

    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
    {
      return revisionPrefetchingPolicy;
    }

    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
    {
      if (prefetchingPolicy == null)
      {
        prefetchingPolicy = CDORevisionPrefetchingPolicy.NO_PREFETCHING;
      }

      if (revisionPrefetchingPolicy != prefetchingPolicy)
      {
        revisionPrefetchingPolicy = prefetchingPolicy;
        fireEvent(new RevisionPrefetchingPolicyEventImpl());
      }
    }

    public ReferenceType getCacheReferenceType()
    {
      if (objects instanceof ReferenceValueMap.Strong<?, ?>)
      {
        return ReferenceType.STRONG;
      }

      if (objects instanceof ReferenceValueMap.Soft<?, ?>)
      {
        return ReferenceType.SOFT;
      }

      if (objects instanceof ReferenceValueMap.Weak<?, ?>)
      {
        return ReferenceType.WEAK;
      }

      throw new IllegalStateException(Messages.getString("CDOViewImpl.29")); //$NON-NLS-1$
    }

    public boolean setCacheReferenceType(ReferenceType referenceType)
    {
      if (referenceType == null)
      {
        referenceType = ReferenceType.SOFT;
      }

      ReferenceValueMap<CDOID, InternalCDOObject> newObjects;
      switch (referenceType)
      {
      case STRONG:
        if (objects instanceof ReferenceValueMap.Strong<?, ?>)
        {
          return false;
        }

        newObjects = new ReferenceValueMap.Strong<CDOID, InternalCDOObject>();
        break;

      case SOFT:
        if (objects instanceof ReferenceValueMap.Soft<?, ?>)
        {
          return false;
        }

        newObjects = new ReferenceValueMap.Soft<CDOID, InternalCDOObject>();
        break;

      case WEAK:
        if (objects instanceof ReferenceValueMap.Weak<?, ?>)
        {
          return false;
        }

        newObjects = new ReferenceValueMap.Weak<CDOID, InternalCDOObject>();
        break;

      default:
        throw new IllegalArgumentException(Messages.getString("CDOViewImpl.29")); //$NON-NLS-1$
      }

      if (objects == null)
      {
        objects = newObjects;
        fireEvent(new CacheReferenceTypeEventImpl());
        return true;
      }

      for (Entry<CDOID, InternalCDOObject> entry : objects.entrySet())
      {
        InternalCDOObject object = entry.getValue();
        if (object != null)
        {
          newObjects.put(entry.getKey(), object);
        }
      }

      ConcurrentMap<CDOID, InternalCDOObject> oldObjects = objects;
      synchronized (objects)
      {
        objects = newObjects;
      }

      oldObjects.clear();
      fireEvent(new CacheReferenceTypeEventImpl());
      return true;
    }

    /**
     * @author Eike Stepper
     */
    private final class CacheReferenceTypeEventImpl extends OptionsEvent implements CacheReferenceTypeEvent
    {
      private static final long serialVersionUID = 1L;

      public CacheReferenceTypeEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ChangeSubscriptionPoliciesEventImpl extends OptionsEvent implements
        ChangeSubscriptionPoliciesEvent
    {
      private static final long serialVersionUID = 1L;

      public ChangeSubscriptionPoliciesEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class InvalidationNotificationEventImpl extends OptionsEvent implements InvalidationNotificationEvent
    {
      private static final long serialVersionUID = 1L;

      public InvalidationNotificationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class RevisionPrefetchingPolicyEventImpl extends OptionsEvent implements
        RevisionPrefetchingPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public RevisionPrefetchingPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ReferencePolicyEventImpl extends OptionsEvent implements ReferencePolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public ReferencePolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }
}
