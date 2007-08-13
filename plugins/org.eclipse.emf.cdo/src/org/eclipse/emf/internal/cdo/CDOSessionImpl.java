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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.util.container.Container;
import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionResult;
import org.eclipse.emf.internal.cdo.protocol.ViewsChangedNotification;
import org.eclipse.emf.internal.cdo.util.ModelUtil;
import org.eclipse.emf.internal.cdo.util.ProxyResolverURIResourceMap;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class CDOSessionImpl extends Container<CDOView> implements CDOSession
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private static final long INITIAL_TEMPORARY_ID = -1L;

  private transient long nextTemporaryID = INITIAL_TEMPORARY_ID;

  private int sessionID;

  private IChannel channel;

  private String repositoryName;

  private String repositoryUUID;

  private CDOPackageRegistryImpl packageRegistry;

  private CDOSessionPackageManager packageManager;

  private CDORevisionManagerImpl revisionManager;

  private Map<CDOID, InternalEObject> idToMetaInstanceMap = new HashMap();

  private Map<InternalEObject, CDOID> metaInstanceToIDMap = new HashMap();

  private ConcurrentMap<CDOID, CDOClassImpl> types = new ConcurrentHashMap();

  private Map<ResourceSet, CDOViewImpl> views = new HashMap();

  private transient int lastViewID = 0;

  private IListener channelListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  public CDOSessionImpl(EPackage.Registry delegate)
  {
    packageRegistry = new CDOPackageRegistryImpl(this, delegate);
    packageManager = new CDOSessionPackageManager(this);
    revisionManager = new CDORevisionManagerImpl(this);
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public IConnector getConnector()
  {
    if (channel == null)
    {
      return null;
    }

    return channel.getConnector();
  }

  public void setConnector(IConnector connector) throws ConnectorException
  {
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setSession(this);
    channel = connector.openChannel(protocol);
    EventUtil.addListener(channel, channelListener);
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public void setChannel(IChannel channel)
  {
    this.channel = channel;
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

  public CDOPackageRegistryImpl getPackageRegistry()
  {
    return packageRegistry;
  }

  public CDOSessionPackageManager getPackageManager()
  {
    return packageManager;
  }

  public CDORevisionManagerImpl getRevisionManager()
  {
    return revisionManager;
  }

  public CDOTransactionImpl openTransaction(ResourceSet resourceSet)
  {
    CDOTransactionImpl transaction = new CDOTransactionImpl(++lastViewID, this);
    attach(resourceSet, transaction);
    return transaction;
  }

  public CDOTransactionImpl openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  public CDOViewImpl openView(ResourceSet resourceSet)
  {
    CDOViewImpl view = new CDOViewImpl(++lastViewID, this);
    attach(resourceSet, view);
    return view;
  }

  public CDOViewImpl openView()
  {
    return openView(createResourceSet());
  }

  public CDOAuditImpl openAudit(ResourceSet resourceSet, long timeStamp)
  {
    CDOAuditImpl audit = new CDOAuditImpl(++lastViewID, this, timeStamp);
    attach(resourceSet, audit);
    return audit;
  }

  public CDOAuditImpl openAudit(long timeStamp)
  {
    return openAudit(createResourceSet(), timeStamp);
  }

  public CDOViewImpl[] getViews()
  {
    Collection<CDOViewImpl> values;
    synchronized (views)
    {
      values = views.values();
    }

    return values.toArray(new CDOViewImpl[values.size()]);
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
    ResourceSet resourceSet = view.getResourceSet();
    synchronized (views)
    {
      if (views.remove(resourceSet) == null)
      {
        return;
      }
    }

    try
    {
      new ViewsChangedNotification(channel, view.getViewID(), CDOProtocolConstants.VIEW_CLOSED).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    fireElementRemovedEvent(view);
  }

  public CDOIDRange getTemporaryIDRange(long count)
  {
    long id1 = nextTemporaryID;
    nextTemporaryID -= count + count;
    long id2 = nextTemporaryID + 2;
    return CDOIDRangeImpl.create(id1, id2);
  }

  public InternalEObject lookupMetaInstance(CDOID id)
  {
    InternalEObject metaInstance = idToMetaInstanceMap.get(id);
    if (metaInstance == null)
    {
      CDOPackageImpl[] cdoPackages = packageManager.getPackages();
      for (CDOPackageImpl cdoPackage : cdoPackages)
      {
        CDOIDRange metaIDRange = cdoPackage.getMetaIDRange();
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

  public void registerEPackage(EPackage ePackage, CDOIDRange metaIDRange)
  {
    if (metaIDRange.isTemporary())
    {
      throw new IllegalArgumentException("metaIDRange.isTemporary()");
    }

    if (!metaIDRange.isMeta())
    {
      throw new IllegalArgumentException("!metaIDRange.isMeta()");
    }

    long count = registerMetaInstance((InternalEObject)ePackage, metaIDRange.getLowerBound().getValue());
    if (count != metaIDRange.getCount())
    {
      throw new IllegalStateException("count != metaIDRange.getCount()");
    }
  }

  public CDOIDRange registerEPackage(EPackage ePackage)
  {
    long count = registerMetaInstance((InternalEObject)ePackage, nextTemporaryID);
    long newTempID = nextTemporaryID - 2L * count;
    CDOIDRange range = CDOIDRangeImpl.create(nextTemporaryID, newTempID + 2L);
    nextTemporaryID = newTempID;
    return range;
  }

  /**
   * TODO synchronize
   */
  private long registerMetaInstance(InternalEObject metaInstance, long idValue)
  {
    CDOID id = CDOIDImpl.create(idValue);
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance);
    }

    idToMetaInstanceMap.put(id, metaInstance);
    metaInstanceToIDMap.put(metaInstance, id);

    long step = id.isTemporary() ? -2L : 2L;
    long count = 1L;
    for (EObject content : metaInstance.eContents())
    {
      count += registerMetaInstance((InternalEObject)content, idValue + step * count);
    }

    return count;
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

  public CDOClassImpl getObjectType(CDOID id)
  {
    return types.get(id);
  }

  public CDOClassImpl requestObjectType(CDOID id)
  {
    CDOClassRef typeRef = null; // FIXME
    CDOClassImpl type = packageManager.resolveClass(typeRef);
    registerObjectType(id, type);
    return type;
  }

  public void registerObjectType(CDOID id, CDOClassImpl type)
  {
    types.put(id, type);
  }

  public void notifyInvalidation(long timeStamp, Set<CDOID> dirtyOIDs, CDOViewImpl excludedView)
  {
    dirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
    for (CDOViewImpl view : getViews())
    {
      if (view != excludedView)
      {
        try
        {
          view.notifyInvalidation(timeStamp, dirtyOIDs);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    fireEvent(new InvalidationEvent(excludedView, timeStamp, dirtyOIDs));
  }

  @Override
  public String toString()
  {
    IConnector connector = channel == null ? null : channel.getConnector();
    return MessageFormat.format("CDOSession[{0}/{1}]", connector, repositoryName);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (channel == null)
    {
      throw new IllegalStateException("channel == null");
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
    OpenSessionRequest request = new OpenSessionRequest(channel, repositoryName);
    OpenSessionResult result = request.send();
    sessionID = result.getSessionID();
    repositoryUUID = result.getRepositoryUUID();
    packageManager.addPackageProxies(result.getPackageInfos());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (views)
    {
      for (CDOViewImpl view : getViews())
      {
        view.close();
      }
    }

    EventUtil.removeListener(channel, channelListener);
    channel.close();
    super.doDeactivate();
  }

  private ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  private void attach(ResourceSet resourceSet, CDOViewImpl view)
  {
    if (CDOUtil.getView(resourceSet) != null)
    {
      throw new IllegalStateException("CDO view already open");
    }

    resourceSet.setPackageRegistry(new EPackageRegistryImpl(packageRegistry));
    CDOUtil.prepareResourceSet(resourceSet);

    Map<URI, Resource> resourceMap = null;
    if (resourceSet instanceof ResourceSetImpl)
    {
      ResourceSetImpl rs = (ResourceSetImpl)resourceSet;
      resourceMap = rs.getURIResourceMap();
      rs.setURIResourceMap(new ProxyResolverURIResourceMap(view, resourceMap));
    }
    else
    {
      throw new ImplementationError("Not a " + ResourceSetImpl.class.getName() + ": "
          + resourceSet.getClass().getName());
    }

    synchronized (views)
    {
      views.put(resourceSet, view);
    }

    resourceSet.eAdapters().add(view);
    sendViewsNotification(view);
    fireElementAddedEvent(view);
  }

  private void sendViewsNotification(CDOViewImpl view)
  {
    try
    {
      int id = view.getViewID();
      byte kind = getKind(view);
      new ViewsChangedNotification(channel, id, kind).send();
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

  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOViewImpl view;

    private long timeStamp;

    private Set<CDOID> dirtyOIDs;

    public InvalidationEvent(CDOViewImpl view, long timeStamp, Set<CDOID> dirtyOIDs)
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

    public Set<CDOID> getDirtyOIDs()
    {
      return dirtyOIDs;
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent" + dirtyOIDs;
    }
  }
}
