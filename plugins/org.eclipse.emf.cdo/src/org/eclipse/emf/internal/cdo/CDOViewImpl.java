/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201265
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/201997
 *    Simon McDuff - http://bugs.eclipse.org/202064
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Simon McDuff - http://bugs.eclipse.org/204890
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOChangeSubscriptionPolicy;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.analyzer.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.query.CDOQuery;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.ReadOnlyException;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.ChangeSubscriptionRequest;
import org.eclipse.emf.internal.cdo.protocol.ResourceIDRequest;
import org.eclipse.emf.internal.cdo.protocol.ResourcePathRequest;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.ReferenceValueMap;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends org.eclipse.net4j.util.event.Notifier implements CDOView, CDOIDProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private CDOSessionImpl session;

  private CDOViewSet viewSet;

  private boolean uniqueResourceContents = true;

  private boolean invalidationNotificationsEnabled;

  private int loadRevisionCollectionChunkSize;

  private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

  private ConcurrentMap<CDOID, InternalCDOObject> objects;

  private CDOStore store = new CDOStore(this);

  private CDOID lastLookupID;

  private InternalCDOObject lastLookupObject;

  /**
   * @since 2.0
   */
  private ChangeSubscriptionManager changeSubscriptionManager = new ChangeSubscriptionManager();

  /**
   * @since 2.0
   */
  private CDOChangeSubscriptionPolicy changeSubscriptionPolicy = CDOChangeSubscriptionPolicy.NONE;

  /**
   * @since 2.0
   */
  public CDOViewImpl(CDOSessionImpl session, int viewID)
  {
    this.session = session;
    this.viewID = viewID;
    invalidationNotificationsEnabled = OM.PREF_ENABLE_INVALIDATION_NOTIFICATIONS.getValue();
    loadRevisionCollectionChunkSize = OM.PREF_LOAD_REVISION_COLLECTION_CHUNK_SIZE.getValue();
    objects = createObjectsMap();
  }

  public int getViewID()
  {
    return viewID;
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
  public CDOViewSet getViewSet()
  {
    return viewSet;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public CDOStore getStore()
  {
    return store;
  }

  public boolean isDirty()
  {
    return false;
  }

  public boolean hasConflict()
  {
    return false;
  }

  public boolean hasUniqueResourceContents()
  {
    return uniqueResourceContents;
  }

  public void setUniqueResourceContents(boolean uniqueResourceContents)
  {
    this.uniqueResourceContents = uniqueResourceContents;
  }

  public boolean isInvalidationNotificationsEnabled()
  {
    return invalidationNotificationsEnabled;
  }

  public void setInvalidationNotificationsEnabled(boolean invalidationNotificationsEnabled)
  {
    this.invalidationNotificationsEnabled = invalidationNotificationsEnabled;
  }

  /**
   * @since 2.0
   */
  public CDOChangeSubscriptionPolicy getChangeSubscriptionPolicy()
  {
    return changeSubscriptionPolicy;
  }

  /**
   * @since 2.0
   */
  public void setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy subscriptionPolicy)
  {
    if (changeSubscriptionPolicy != subscriptionPolicy)
    {
      changeSubscriptionPolicy = subscriptionPolicy;

      changeSubscriptionManager.notifyChangeSubcriptionPolicy();
    }
  }

  public int getLoadRevisionCollectionChunkSize()
  {
    return loadRevisionCollectionChunkSize;
  }

  public void setLoadRevisionCollectionChunkSize(int loadRevisionCollectionChunkSize)
  {
    this.loadRevisionCollectionChunkSize = loadRevisionCollectionChunkSize;
  }

  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return featureAnalyzer;
  }

  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    this.featureAnalyzer = featureAnalyzer == null ? CDOFeatureAnalyzer.NOOP : featureAnalyzer;
  }

  public CDOTransactionImpl toTransaction()
  {
    if (this instanceof CDOTransactionImpl)
    {
      return (CDOTransactionImpl)this;
    }

    throw new ReadOnlyException("CDO view is read only: " + this);
  }

  public boolean hasResource(String path)
  {
    CDOID id = getResourceID(path);
    return id != null && !id.isNull();
  }

  /**
   * @since 2.0
   */
  public CDOQuery createQuery(String language, String queryString)
  {
    return new CDOQueryImpl(this, language, queryString);
  }

  public CDOID getResourceID(String path)
  {
    try
    {
      CDOResource resource = getResource(path, false);
      if (resource != null && resource.cdoID() != null)
      {
        return resource.cdoID();
      }

      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      ResourceIDRequest request = new ResourceIDRequest(session.getChannel(), path);
      return failOverStrategy.send(request);
    }
    catch (Exception ex)
    {
      throw new TransactionException(ex);
    }
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
    URI uri = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().getResource(uri, loadInDemand);
  }

  /**
   * @since 2.0
   */
  public List<CDOResource> queryResources(String pathPrefix)
  {
    CDOQuery resourceQuery = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, pathPrefix);
    return resourceQuery.getResult(CDOResource.class);
  }

  public CDOResourceImpl getResource(CDOID resourceID)
  {
    if (resourceID == null || resourceID.isNull())
    {
      throw new IllegalArgumentException("resourceID == null || resourceID.isNull()");
    }

    CDOResourceImpl resource = (CDOResourceImpl)getObject(resourceID);
    if (resource != null)
    {
      return resource;
    }

    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      ResourcePathRequest request = new ResourcePathRequest(session.getChannel(), resourceID);
      String path = failOverStrategy.send(request);
      return addResource(resourceID, path);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  public CDOResourceImpl addResource(CDOID id, String path)
  {
    URI createURI = CDOURIUtil.createResourceURI(this, path);
    CDOResourceImpl resource = (CDOResourceImpl)viewSet.getResourceFactory().createResource(createURI);
    resource.setURI(createURI);

    InternalCDOObject resourceObject = resource;
    resourceObject.cdoInternalSetID(id);
    resourceObject.cdoInternalSetView(this);
    resourceObject.cdoInternalSetResource(resource);
    resourceObject.cdoInternalSetState(CDOState.PROXY);

    ResourceSet resourceSet = getResourceSet();
    resourceSet.getResources().add(resource);
    return resource;
  }

  public InternalCDOObject newInstance(EClass eClass)
  {
    EObject eObject = EcoreUtil.create(eClass);
    return FSMUtil.adapt(eObject, this);
  }

  public InternalCDOObject newInstance(CDOClass cdoClass)
  {
    EClass eClass = ModelUtil.getEClass(cdoClass, session.getPackageRegistry());
    if (eClass == null)
    {
      throw new IllegalStateException("No EClass for " + cdoClass);
    }

    return newInstance(eClass);
  }

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    CDORevisionResolver revisionManager = session.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevision(id, session.getReferenceChunkSize(), loadOnDemand);
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
    if (id == null || id.isNull())
    {
      return null;
    }

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
            localLookupObject = createObject(id);
          }
          else
          {
            return null;
          }
        }

        registerObject(localLookupObject);
      }

      lastLookupID = id;
      lastLookupObject = localLookupObject;
      return lastLookupObject;
    }
  }

  /**
   * @since 2.0
   */
  @SuppressWarnings("unchecked")
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    if (objectFromDifferentView instanceof CDOObject)
    {
      CDOObject object = (CDOObject)objectFromDifferentView;
      CDOView view = object.cdoView();
      if (view != this)
      {
        if (view.getSession() != session)
        {
          throw new IllegalArgumentException("Object is contaiuned in a different session: " + objectFromDifferentView);
        }

        CDOID id = object.cdoID();
        objectFromDifferentView = (T)getObject(id, true);
      }
    }

    return objectFromDifferentView;
  }

  public boolean isObjectRegistered(CDOID id)
  {
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
      TRACER.trace("Creating meta object for " + id);
    }

    InternalEObject metaInstance = session.lookupMetaInstance(id);
    if (metaInstance == null)
    {
      throw new ImplementationError("No metaInstance for " + id);
    }

    return new CDOMetaWrapper(this, metaInstance, id);
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createObject(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating object for " + id);
    }

    InternalCDORevision revision = getRevision(id, true);
    CDOClass cdoClass = revision.getCDOClass();
    InternalCDOObject object = newInstance(cdoClass);
    cleanObject(object, revision);
    return object;
  }

  /**
   * @since 2.0
   */
  protected void cleanObject(InternalCDOObject object, InternalCDORevision revision)
  {
    if (object instanceof CDOResourceImpl)
    {
      object.cdoInternalSetResource((CDOResourceImpl)object);
    }
    else
    {
      CDOID resourceID = revision.getResourceID();
      if (!resourceID.isNull())
      {
        CDOResourceImpl resource = getResource(resourceID);
        object.cdoInternalSetResource(resource);
      }
    }

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
        TRACER.format("Converted dangling reference: {0} --> {1}", idOrObject, id);
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
      return CDOIDUtil.createExternal(uri);
    }

    throw new IllegalStateException("Unable to provideCDOID: " + idOrObject.getClass().getName());
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

    if (potentialObject instanceof InternalEObject && !(potentialObject instanceof InternalCDOObject))
    {
      try
      {
        InternalEObject eObject = (InternalEObject)potentialObject;
        Object legacyListener = FSMUtil.getLegacyWrapper(eObject);
        if (legacyListener != null)
        {
          potentialObject = legacyListener;
        }
      }
      catch (Throwable ex)
      {
        OM.LOG.warn(ex);
      }
    }

    if (potentialObject instanceof InternalCDOObject)
    {
      InternalCDOObject object = (InternalCDOObject)potentialObject;
      boolean newOrTransient = FSMUtil.isTransient(object) || FSMUtil.isNew(object);
      if (object.cdoView() == this && (!onlyPersistedID || !newOrTransient))
      {
        return object.cdoID();
      }
    }

    return potentialObject;
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
        throw new ImplementationError("ID not registered: " + id);
      }

      return result.cdoInternalInstance();
    }

    return potentialID;
  }

  /**
   * @since 2.0
   */
  public void registerProxyResource(CDOResourceImpl resource)
  {
    resource.cdoInternalSetResource(resource);
    resource.cdoInternalSetView(this);
    resource.cdoInternalSetID(getResourceID(resource.getPath()));
    resource.cdoInternalSetState(CDOState.PROXY);
    registerObject(resource);
  }

  public void registerObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", object);
    }

    InternalCDOObject old;
    synchronized (objects)
    {
      old = objects.put(object.cdoID(), object);
    }

    if (old != null)
    {
      throw new IllegalStateException("Duplicate ID: " + object);
    }
  }

  public void deregisterObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering {0}", object);
    }

    InternalCDOObject old = removeObject(object.cdoID());
    if (old == null)
    {
      throw new IllegalStateException("Unknown ID: " + object);
    }
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
      TRACER.format("Remapping {0} --> {1}", oldID, newID);
    }
  }

  /**
   * Turns registered objects into proxies and synchronously delivers invalidation events to registered event listeners.
   * <p>
   * <b>Implementation note:</b> This implementation guarantees that exceptions from listener code don't propagate up to
   * the caller of this method. Runtime exceptions from the implementation of the {@link CDOStateMachine} are propagated
   * to the caller of this method but this should not happen in the absence of implementation errors.
   * 
   * @param timeStamp
   *          The time stamp of the server transaction if this event was sent as a result of a successfully committed
   *          transaction or <code>LOCAL_ROLLBACK</code> if this event was sent due to a local rollback.
   * @param dirtyOIDs
   *          A set of the object IDs to be invalidated. <b>Implementation note:</b> This implementation expects the
   *          dirtyOIDs set to be unmodifiable. It does not wrap the set (again).
   * @since 2.0
   */
  public void handleInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
  {
    List<InternalCDOObject> dirtyObjects = invalidationNotificationsEnabled ? new ArrayList<InternalCDOObject>() : null;
    for (CDOIDAndVersion dirtyOID : dirtyOIDs)
    {
      InternalCDOObject dirtyObject;
      synchronized (objects)
      {
        dirtyObject = objects.get(dirtyOID.getID());
        if (dirtyObject != null)
        {
          CDOStateMachine.INSTANCE.invalidate(dirtyObject, timeStamp);
        }
      }

      if (dirtyObject != null && dirtyObjects != null && dirtyObject.eNotificationRequired())
      {
        dirtyObjects.add(dirtyObject);
      }
    }

    for (CDOID id : detachedObjects)
    {
      InternalCDOObject cdoObject = removeObject(id);
      if (cdoObject != null)
      {
        CDOStateMachine.INSTANCE.invalidate(cdoObject, true, timeStamp);
        if (dirtyObjects != null && cdoObject.eNotificationRequired())
        {
          dirtyObjects.add(cdoObject);
        }
      }
    }

    if (dirtyObjects != null)
    {
      for (InternalCDOObject dirtyObject : dirtyObjects)
      {
        CDOInvalidationNotificationImpl notification = new CDOInvalidationNotificationImpl(dirtyObject);
        dirtyObject.eNotify(notification);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void handleChangeSubscription(Collection<CDORevisionDelta> deltas)
  {
    if (deltas != null && getChangeSubscriptionPolicy() != CDOChangeSubscriptionPolicy.NONE)
    {
      CDONotificationBuilder builder = new CDONotificationBuilder(getSession().getPackageRegistry());
      for (CDORevisionDelta delta : deltas)
      {
        InternalCDOObject object = objects.get(delta.getID());
        if (object != null && object.eNotificationRequired() && changeSubscriptionManager.hasSubscription(object))
        {
          NotificationImpl notification = builder.buildNotification(object, delta);
          if (notification != null)
          {
            notification.dispatch();
          }
        }
      }
    }
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
  protected ChangeSubscriptionManager getChangeSubscriptionManager()
  {
    return changeSubscriptionManager;
  }

  protected ConcurrentMap<CDOID, InternalCDOObject> createObjectsMap()
  {
    return new ReferenceValueMap.Weak<CDOID, InternalCDOObject>();
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
    session.viewDetached(this);
    objects.clear();
    objects = null;
    store = null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOView({0})", viewID);
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public Notifier getTarget()
  {
    return getResourceSet();
  }

  /**
   * @since 2.0
   */
  public void setViewSet(CDOViewSet viewSet)
  {
    this.viewSet = viewSet;
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
  protected class ChangeSubscriptionManager
  {
    private Map<InternalCDOObject, Integer> subscriptions = new HashMap<InternalCDOObject, Integer>();

    private Map<InternalCDOObject, Integer> pendingSubscriptions = new HashMap<InternalCDOObject, Integer>();

    private boolean isPending(InternalCDOObject internalCDOObject)
    {
      return internalCDOObject.cdoID().isTemporary();
    }

    protected int getNumberOfValidAdapter(InternalCDOObject object)
    {
      int count = 0;
      if (object.eNotificationRequired())
      {
        for (Adapter adapter : object.eAdapters())
        {
          if (changeSubscriptionPolicy.shouldSubscribe(object, adapter))
          {
            count++;
          }
        }

      }

      return count;
    }

    /**
     * Register to the server all objects from the active list
     */
    protected void notifyChangeSubcriptionPolicy()
    {
      synchronized (subscriptions)
      {
        subscriptions.clear();
        pendingSubscriptions.clear();
        List<CDOID> cdoIDs = new ArrayList<CDOID>();
        if (changeSubscriptionPolicy != CDOChangeSubscriptionPolicy.NONE)
        {
          for (InternalCDOObject cdoObject : getObjectsArray())
          {
            int count = getNumberOfValidAdapter(cdoObject);
            if (count > 0)
            {
              cdoIDs.add(cdoObject.cdoID());
              boolean isPending = isPending(cdoObject);
              Map<InternalCDOObject, Integer> subscribersMap = isPending ? pendingSubscriptions : subscriptions;
              subscribersMap.put(cdoObject, count);
            }
          }
        }

        request(cdoIDs, true, true);
      }
    }

    protected void request(List<CDOID> cdoIDs, boolean clear, boolean subscribeMode)
    {
      try
      {
        ChangeSubscriptionRequest request = new ChangeSubscriptionRequest(getSession().getChannel(), getViewID(),
            cdoIDs, subscribeMode, clear);
        session.getFailOverStrategy().send(request);
      }
      catch (Exception ex)
      {
        throw new TransactionException(ex);
      }
    }

    protected void notifyDirtyObjects()
    {
      synchronized (subscriptions)
      {
        List<InternalCDOObject> objectsToRemove = new ArrayList<InternalCDOObject>();
        for (Entry<InternalCDOObject, Integer> entry : pendingSubscriptions.entrySet())
        {
          if (!isPending(entry.getKey()))
          {
            subscribe(entry.getKey(), entry.getValue());
            objectsToRemove.add(entry.getKey());
          }
        }

        for (InternalCDOObject internalCDOObject : objectsToRemove)
        {
          pendingSubscriptions.remove(internalCDOObject);
        }
      }
    }

    protected boolean hasSubscription(InternalCDOObject eObject)
    {
      return subscriptions.get(eObject) != null;
    }

    private void subscribe(EObject eObject, Adapter adapter, int adjust)
    {
      synchronized (subscriptions)
      {
        if (getChangeSubscriptionPolicy().shouldSubscribe(eObject, adapter))
        {
          subscribe(eObject, adjust);
        }
      }
    }

    private void subscribe(EObject eObject, int adjust)
    {
      synchronized (subscriptions)
      {
        InternalCDOObject internalCDOObject = FSMUtil.adapt(eObject, CDOViewImpl.this);
        if (internalCDOObject.cdoView() != CDOViewImpl.this)
        {
          throw new CDOException("Object " + internalCDOObject + " doesn`t belong to this view.");
        }

        boolean isPending = isPending(internalCDOObject);
        Map<InternalCDOObject, Integer> subscribersMap = isPending ? pendingSubscriptions : subscriptions;
        Integer count = subscribersMap.get(internalCDOObject);
        if (count == null)
        {
          // Cannot adjust negative value
          if (adjust < 0)
          {
            throw new IllegalStateException("Object " + internalCDOObject.cdoID() + " cannot be unsubscribe");
          }

          count = 0;

          // Notification need to be enable to send correct value to the server
          if (!isPending && getChangeSubscriptionPolicy() != CDOChangeSubscriptionPolicy.NONE)
          {
            request(Collections.singletonList(internalCDOObject.cdoID()), false, true);
          }
        }

        count += adjust;

        // Look if objects need to be unsubscribe
        if (count <= 0)
        {
          subscribersMap.remove(internalCDOObject);

          // Notification need to be enable to send correct value to the server
          if (!isPending && getChangeSubscriptionPolicy() != CDOChangeSubscriptionPolicy.NONE)
          {
            request(Collections.singletonList(internalCDOObject.cdoID()), false, false);
          }
        }
        else
        {
          subscribersMap.put(internalCDOObject, count);
        }
      }
    }

    public void subscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, 1);
    }

    public void unsubscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, -1);
    }
  }

}
