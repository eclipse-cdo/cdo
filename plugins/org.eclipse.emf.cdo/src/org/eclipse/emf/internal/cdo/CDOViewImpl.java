/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/201265
 *    Simon McDuff - https://bugs.eclipse.org/201266
 *    Simon McDuff - https://bugs.eclipse.org/201997
 *    Simon McDuff - https://bugs.eclipse.org/202064
 *    Simon McDuff - https://bugs.eclipse.org/230832    
 *    Simon McDuff - https://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOChangeSubscriptionPolicy;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.CDOViewResourcesEvent;
import org.eclipse.emf.cdo.analyzer.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.query.CDOQuery;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
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
public class CDOViewImpl extends org.eclipse.net4j.util.event.Notifier implements CDOView, CDOIDProvider,
    Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private CDOSessionImpl session;

  private ResourceSet resourceSet;

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

  public CDOViewImpl(int id, CDOSessionImpl session)
  {
    viewID = id;
    this.session = session;
    invalidationNotificationsEnabled = OM.PREF_ENABLE_INVALIDATION_NOTIFICATIONS.getValue();
    loadRevisionCollectionChunkSize = OM.PREF_LOAD_REVISION_COLLECTION_CHUNK_SIZE.getValue();
    objects = createObjectsMap();
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
    return resourceSet;
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
    return this.changeSubscriptionPolicy;
  }

  /**
   * @since 2.0
   */
  public void setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy subscriptionPolicy)
  {
    if (this.changeSubscriptionPolicy != subscriptionPolicy)
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
  public Map<CDOID, InternalCDOObject> getObjectsMap()
  {
    return Collections.unmodifiableMap(objects);
  }

  public CDOResource getResource(String path)
  {
    URI uri = CDOUtil.createResourceURI(path);
    return (CDOResource)getResourceSet().getResource(uri, true);
  }

  public CDOResourceImpl getResource(CDOID resourceID)
  {
    if (resourceID == null || resourceID.isNull())
    {
      throw new IllegalArgumentException("resourceID == null || resourceID == CDOID.NULL");
    }

    // TODO What about somply looking in the objects cache of this view as well?
    ResourceSet resourceSet = getResourceSet();
    EList<Resource> resources = resourceSet.getResources();
    for (Resource resource : resources)
    {
      if (resource instanceof CDOResourceImpl)
      {
        CDOResourceImpl cdoResource = (CDOResourceImpl)resource;
        if (resourceID.equals(cdoResource.cdoID()))
        {
          return cdoResource;
        }
      }
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
    CDOResourceImpl resource = (CDOResourceImpl)EresourceFactory.eINSTANCE.createCDOResource();
    resource.setPath(path);

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
    return getObject(id, false);
  }

  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    if (id == null || id.isNull()) return null;

    synchronized (objects)
    {
      if (id.equals(lastLookupID))
      {
        return lastLookupObject;
      }

      lastLookupID = id;
      lastLookupObject = objects.get(id);
      if (lastLookupObject == null)
      {
        if (id.isMeta())
        {
          lastLookupObject = createMetaObject((CDOIDMeta)id);
        }
        else
        {
          if (loadOnDemand)
          {
            lastLookupObject = createObject(id);
          }
          else
          {
            lastLookupObject = createProxy(id);
          }
        }

        registerObject(lastLookupObject);
      }

      return lastLookupObject;
    }
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

    return new CDOMetaImpl(this, metaInstance, id);
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
    return object;
  }

  private InternalCDOObject createProxy(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Creating proxy for " + id);
    }

    CDOClass cdoClass = getObjectType(id);
    InternalCDOObject object = newInstance(cdoClass);
    if (object instanceof CDOResourceImpl)
    {
      object.cdoInternalSetResource((CDOResourceImpl)object);
    }

    object.cdoInternalSetView(this);
    object.cdoInternalSetID(id);
    object.cdoInternalSetState(CDOState.PROXY);
    return object;
  }

  private CDOClass getObjectType(CDOID id)
  {
    CDOClass type = session.getObjectType(id);
    if (type != null)
    {
      return type;
    }

    if (id.isLegacy())
    {
      CDOClassRef typeRef = ((CDOIDObject)id).getClassRef();
      type = typeRef.resolve(session.getPackageManager());
      session.registerObjectType(id, type);
      return type;
    }

    return session.requestObjectType(id);
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

    throw new IllegalStateException("Unable to provideCDOID: " + idOrObject.getClass().getName());
  }

  public Object convertObjectToID(Object potentialObject)
  {
    if (potentialObject instanceof CDOID)
    {
      return potentialObject;
    }

    if (potentialObject instanceof InternalEObject && !(potentialObject instanceof InternalCDOObject))
    {
      InternalEObject eObject = (InternalEObject)potentialObject;
      InternalCDOObject adapter = FSMUtil.adapt(eObject, this);
      if (adapter != null)
      {
        potentialObject = adapter;
      }
    }

    if (potentialObject instanceof InternalCDOObject)
    {
      InternalCDOObject object = (InternalCDOObject)potentialObject;
      if (object.cdoView() == this)
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
      InternalCDOObject result = getObject(id, true);
      if (result == null)
      {
        throw new ImplementationError("ID not registered: " + id);
      }

      return result.cdoInternalInstance();
    }

    return potentialID;
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
  public void handleInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs)
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
          if (notification != null) notification.dispatch();
        }
      }
    }
  }

  public int reload(CDOObject... objects)
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
      synchronized (objects)
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
    return resourceSet;
  }

  public void setTarget(Notifier newTarget)
  {
    ResourceSet resourceSet = (ResourceSet)newTarget;
    if (TRACER.isEnabled())
    {
      TRACER.trace("Attaching CDO view to " + resourceSet);
    }

    this.resourceSet = resourceSet;
    for (Resource resource : resourceSet.getResources())
    {
      if (resource instanceof CDOResourceImpl)
      {
        CDOResourceImpl cdoResource = (CDOResourceImpl)resource;
        notifyAdd(cdoResource);
      }
    }
  }

  public void unsetTarget(Notifier oldTarget)
  {
    ResourceSet resourceSet = (ResourceSet)oldTarget;
    for (Resource resource : resourceSet.getResources())
    {
      if (resource instanceof CDOResourceImpl)
      {
        CDOResourceImpl cdoResource = (CDOResourceImpl)resource;
        notifyRemove(cdoResource);
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Detaching CDO view from " + resourceSet);
    }

    if (resourceSet == oldTarget)
    {
      setTarget(null);
    }
  }

  public void notifyChanged(Notification msg)
  {
    try
    {
      switch (msg.getEventType())
      {
      case Notification.ADD:
        notifyAdd(msg);
        break;

      case Notification.ADD_MANY:
        notifyAddMany(msg);
        break;

      case Notification.REMOVE:
        notifyRemove(msg);
        break;

      case Notification.REMOVE_MANY:
        notifyRemoveMany(msg);
        break;
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void notifyAdd(Notification msg)
  {
    if (msg.getNewValue() instanceof CDOResourceImpl)
    {
      notifyAdd((CDOResourceImpl)msg.getNewValue());
    }
  }

  @SuppressWarnings("unchecked")
  private void notifyAddMany(Notification msg)
  {
    EList<Resource> newResources = (EList<Resource>)msg.getNewValue();
    EList<Resource> oldResources = (EList<Resource>)msg.getOldValue();
    for (Resource newResource : newResources)
    {
      if (newResource instanceof CDOResourceImpl)
      {
        if (!oldResources.contains(newResource))
        {
          notifyAdd((CDOResourceImpl)newResource);
        }
      }
    }
  }

  private void notifyAdd(CDOResourceImpl cdoResource)
  {
    try
    {
      CDOStateMachine.INSTANCE.attach(cdoResource, cdoResource, this);
      fireEvent(new ResourcesEvent(cdoResource.getPath(), ResourcesEvent.Kind.ADDED));
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);

      try
      {
        ((InternalCDOObject)cdoResource).cdoInternalSetState(CDOState.NEW);
        resourceSet.getResources().remove(cdoResource);
      }
      catch (RuntimeException ignore)
      {
      }

      throw ex;
    }
  }

  private void notifyRemove(Notification msg)
  {
    if (msg.getOldValue() instanceof CDOResourceImpl)
    {
      notifyRemove((CDOResourceImpl)msg.getOldValue());
    }
  }

  @SuppressWarnings("unchecked")
  private void notifyRemoveMany(Notification msg)
  {
    EList<Resource> newResources = (EList<Resource>)msg.getNewValue();
    EList<Resource> oldResources = (EList<Resource>)msg.getOldValue();
    for (Resource oldResource : oldResources)
    {
      if (oldResource instanceof CDOResourceImpl)
      {
        if (!newResources.contains(oldResource))
        {
          // TODO Optimize event notification with IContainerEvent
          notifyRemove((CDOResourceImpl)oldResource);
        }
      }
    }
  }

  private void notifyRemove(CDOResourceImpl cdoResource)
  {
    CDOStateMachine.INSTANCE.detach(cdoResource);
    fireEvent(new ResourcesEvent(cdoResource.getPath(), ResourcesEvent.Kind.REMOVED));
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
   * @author Eike Stepper
   */
  private final class ResourcesEvent extends Event implements CDOViewResourcesEvent
  {
    private static final long serialVersionUID = 1L;

    private String resourcePath;

    private Kind kind;

    public ResourcesEvent(String resourcePath, Kind kind)
    {
      this.resourcePath = resourcePath;
      this.kind = kind;
    }

    public String getResourcePath()
    {
      return resourcePath;
    }

    public Kind getKind()
    {
      return kind;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewResourcesEvent[source={0}, {1}={2}]", getSource(), resourcePath, kind);
    }
  }

  /**
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
          /*
           * This is safe since we use ConcurrentHashMap
           */
          for (InternalCDOObject cdoObject : objects.values())
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
          pendingSubscriptions.remove(internalCDOObject);
      }
    }

    protected boolean hasSubscription(InternalCDOObject eObject)
    {
      return (subscriptions.get(eObject) != null);
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
