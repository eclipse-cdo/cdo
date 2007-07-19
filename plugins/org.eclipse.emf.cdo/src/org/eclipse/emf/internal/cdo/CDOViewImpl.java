/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewCommittedEvent;
import org.eclipse.emf.cdo.CDOViewDirtyEvent;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.CDOViewResourcesEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOReferenceConverter;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.ResourcePathRequest;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends org.eclipse.net4j.internal.util.event.Notifier implements CDOView, Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int id;

  private CDOSessionImpl session;

  private ResourceSet resourceSet;

  private long timeStamp;

  private CDOTransactionImpl transaction;

  private Map<CDOID, InternalCDOObject> objects = new HashMap();

  private CDOID lastLookupID;

  private InternalCDOObject lastLookupObject;

  private CDOReferenceConverter referenceConverter;

  public CDOViewImpl(int id, CDOSessionImpl session, boolean readOnly)
  {
    this.id = id;
    this.session = session;
    timeStamp = UNSPECIFIED_DATE;
    if (!readOnly)
    {
      transaction = new CDOTransactionImpl(this);
    }

    referenceConverter = new CDOReferenceConverter()
    {
      public CDOID convertToID(Object object)
      {
        try
        {
          CDOID id = (CDOID)CDOViewImpl.this.convertToID(object);
          if (TRACER.isEnabled())
          {
            TRACER.format("Converted dangling reference: {0} --> {1}", object, id);
          }

          return id;
        }
        catch (ClassCastException ex)
        {
          ex.printStackTrace();
          throw new IllegalStateException("Dangling reference: " + object);
        }
      }
    };
  }

  public CDOViewImpl(int id, CDOSessionImpl session, long timeStamp)
  {
    this.id = id;
    this.session = session;
    this.timeStamp = timeStamp;
  }

  public int getID()
  {
    return id;
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public CDOTransactionImpl getTransaction()
  {
    return transaction;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isHistorical()
  {
    return timeStamp != CDOView.UNSPECIFIED_DATE;
  }

  public boolean isReadWrite()
  {
    return transaction != null;
  }

  public boolean isReadOnly()
  {
    return transaction == null;
  }

  public CDOResource createResource(String path)
  {
    URI createURI = CDOUtil.createURI(path);
    return (CDOResource)getResourceSet().createResource(createURI);
  }

  public CDOResource getResource(String path)
  {
    URI uri = CDOUtil.createURI(path);
    return (CDOResource)getResourceSet().getResource(uri, true);
  }

  public CDOResourceImpl getResource(CDOID resourceID)
  {
    if (resourceID == null || resourceID == CDOID.NULL)
    {
      throw new ImplementationError("resourceID == null || resourceID == CDOID.NULL");
    }

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
      ResourcePathRequest signal = new ResourcePathRequest(getSession().getChannel(), resourceID);
      String path = signal.send();

      CDOResourceImpl resource = (CDOResourceImpl)EresourceFactory.eINSTANCE.createCDOResource();
      resource.setResourceSet(resourceSet);
      resource.setPath(path);

      InternalCDOObject resourceObject = resource;
      resourceObject.cdoInternalSetID(resourceID);
      resourceObject.cdoInternalSetView(this);
      resourceObject.cdoInternalSetResource(resource);
      resourceObject.cdoInternalSetState(CDOState.PROXY);

      return resource;
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

  public InternalCDOObject newInstance(EClass eClass)
  {
    return (InternalCDOObject)EcoreUtil.create(eClass);
  }

  public InternalCDOObject newInstance(CDOClass cdoClass)
  {
    EClass eClass = ModelUtil.getEClass((CDOClassImpl)cdoClass, session.getPackageRegistry());
    if (eClass == null)
    {
      throw new IllegalStateException("No EClass for " + cdoClass);
    }

    return newInstance(eClass);
  }

  public CDORevisionImpl getRevision(CDOID id)
  {
    CDORevisionResolver revisionManager = session.getRevisionManager();
    if (isReadWrite())
    {
      return (CDORevisionImpl)revisionManager.getRevision(id);
    }

    return (CDORevisionImpl)revisionManager.getRevision(id, timeStamp);
  }

  public CDOObject lookupObject(CDOID id)
  {
    if (id.equals(lastLookupID))
    {
      return lastLookupObject;
    }

    lastLookupID = id;
    lastLookupObject = objects.get(id);
    if (lastLookupObject == null)
    {
      lastLookupObject = createObject(id);
      registerObject(lastLookupObject);
    }

    return lastLookupObject;
  }

  public void registerObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering object {0}", object);
    }

    InternalCDOObject old = objects.put(object.cdoID(), object);
    if (old != null)
    {
      throw new IllegalStateException("Duplicate ID: " + object);
    }
  }

  public void deregisterObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering object {0}", object);
    }

    InternalCDOObject old = objects.remove(object.cdoID());
    if (old == null)
    {
      throw new IllegalStateException("Unknown ID: " + object);
    }
  }

  public void remapObject(CDOID oldID)
  {
    InternalCDOObject object = objects.remove(oldID);
    CDOID newID = object.cdoID();
    objects.put(newID, object);
    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping object: {0} --> {1}", oldID, newID);
    }
  }

  public CDOReferenceConverter getReferenceConverter()
  {
    return referenceConverter;
  }

  // public final class HistoryEntryImpl implements HistoryEntry, Comparable
  // {
  // private String resourcePath;
  //
  // private HistoryEntryImpl(String resourcePath)
  // {
  // this.resourcePath = resourcePath;
  // }
  //
  // public CDOView getView()
  // {
  // return CDOViewImpl.this;
  // }
  //
  // public String getResourcePath()
  // {
  // return resourcePath;
  // }
  //
  // public int compareTo(Object o)
  // {
  // HistoryEntry that = (HistoryEntry)o;
  // return resourcePath.compareTo(that.getResourcePath());
  // }
  //
  // @Override
  // public String toString()
  // {
  // return resourcePath;
  // }
  // }

  // public final class HistoryEntryImpl implements HistoryEntry, Comparable
  // {
  // private String resourcePath;
  //
  // private HistoryEntryImpl(String resourcePath)
  // {
  // this.resourcePath = resourcePath;
  // }
  //
  // public CDOView getView()
  // {
  // return CDOViewImpl.this;
  // }
  //
  // public String getResourcePath()
  // {
  // return resourcePath;
  // }
  //
  // public int compareTo(Object o)
  // {
  // HistoryEntry that = (HistoryEntry)o;
  // return resourcePath.compareTo(that.getResourcePath());
  // }
  //
  // @Override
  // public String toString()
  // {
  // return resourcePath;
  // }
  // }

  public Object convertToID(Object potentialObject)
  {
    if (potentialObject instanceof CDOObject)
    {
      CDOObject object = (CDOObject)potentialObject;
      if (object.cdoView() == this)
      {
        return object.cdoID();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Dangling reference: {0}", potentialObject);
    }

    return potentialObject;
  }

  public Object convertToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      CDOID id = (CDOID)potentialID;
      CDOObject result = lookupObject(id);
      if (result == null)
      {
        throw new ImplementationError("ID not registered: " + id);
      }

      return result;
    }

    return potentialID;
  }

  /**
   * Turns registered objects into proxies and synchronously delivers
   * invalidation events to registered event listeners.
   * <p>
   * <b>Implementation note:</b> This implementation guarantees that exceptions
   * from listener code don't propagate up to the caller of this method. Runtime
   * exceptions from the implementation of the {@link CDOStateMachine} are
   * propagated to the caller of this method but this should not happen in the
   * absence of implementation errors.
   * 
   * @param timeStamp
   *          The point in time when the newly committed revision have been
   *          created.
   * @param dirtyOIDs
   *          A set of the object IDs to be invalidated. <b>Implementation note:</b>
   *          This implementation expects the dirtyOIDs set to be unmodifiable.
   *          It does not wrap the set (again).
   */
  public void notifyInvalidation(long timeStamp, Set<CDOID> dirtyOIDs)
  {
    for (CDOID dirtyOID : dirtyOIDs)
    {
      InternalCDOObject object = objects.get(dirtyOID);
      if (object != null)
      {
        CDOStateMachine.INSTANCE.invalidate(object, timeStamp);
      }
    }
  }

  public boolean isDirty()
  {
    return transaction == null ? false : transaction.isDirty();
  }

  public void commit()
  {
    checkWritable();
    CommitTransactionResult result = transaction.commit();
    if (result != null)
    {
      Map<CDOID, CDOID> idMappings = result.getIdMappings();
      fireEvent(new CommittedEvent(idMappings));
    }
  }

  public void rollback()
  {
    checkWritable();
    transaction.rollback();
  }

  public void close()
  {
    session.viewDetached(this);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOView({0})", isHistorical() ? new Date(timeStamp) : isReadOnly() ? "readOnly"
        : "readWrite");
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
      TRACER.trace("Attaching CDO adapter to " + resourceSet);
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
      TRACER.trace("Detaching CDO adapter from " + resourceSet);
    }

    if (resourceSet == oldTarget)
    {
      setTarget(null);
    }
  }

  public void fireDirtyEvent()
  {
    fireEvent(new DirtyEvent());
  }

  public void notifyChanged(Notification msg)
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

  private void notifyAdd(Notification msg)
  {
    if (msg.getNewValue() instanceof CDOResourceImpl)
    {
      notifyAdd((CDOResourceImpl)msg.getNewValue());
    }
  }

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
          // TODO Optimize event notification with IContainerEvent
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
    CDOStateMachine.INSTANCE.detach(cdoResource, cdoResource, this);
    fireEvent(new ResourcesEvent(cdoResource.getPath(), ResourcesEvent.Kind.REMOVED));
  }

  private InternalCDOObject createObject(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Creating object from view: ID={0}", id);
    }

    CDORevisionImpl revision = getRevision(id);
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOID resourceID = revision.getResourceID();

    InternalCDOObject object = newInstance(cdoClass);
    if (object instanceof CDOResourceImpl)
    {
      object.cdoInternalSetResource((CDOResourceImpl)object);
    }
    else
    {
      CDOResourceImpl resource = getResource(resourceID);
      object.cdoInternalSetResource(resource);
    }

    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetState(CDOState.CLEAN);
    return object;
  }

  private void checkWritable()
  {
    if (isReadOnly())
    {
      throw new IllegalStateException("CDO view is read only");
    }
  }

  // public final class HistoryEntryImpl implements HistoryEntry, Comparable
  // {
  // private String resourcePath;
  //
  // private HistoryEntryImpl(String resourcePath)
  // {
  // this.resourcePath = resourcePath;
  // }
  //
  // public CDOView getView()
  // {
  // return CDOViewImpl.this;
  // }
  //
  // public String getResourcePath()
  // {
  // return resourcePath;
  // }
  //
  // public int compareTo(Object o)
  // {
  // HistoryEntry that = (HistoryEntry)o;
  // return resourcePath.compareTo(that.getResourcePath());
  // }
  //
  // @Override
  // public String toString()
  // {
  // return resourcePath;
  // }
  // }

  /**
   * @author Eike Stepper
   */
  private abstract class Event extends org.eclipse.net4j.internal.util.event.Event implements CDOViewEvent
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
      return MessageFormat.format("CDOViewResourcesEvent[{0}, {1}, {2}]", getView(), resourcePath, kind);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DirtyEvent extends Event implements CDOViewDirtyEvent
  {
    private static final long serialVersionUID = 1L;

    private DirtyEvent()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommittedEvent extends Event implements CDOViewCommittedEvent
  {
    private static final long serialVersionUID = 1L;

    private Map<CDOID, CDOID> idMappings;

    private CommittedEvent(Map<CDOID, CDOID> idMappings)
    {
      this.idMappings = idMappings;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }
  }
}
