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
import org.eclipse.emf.cdo.CDOSessionViewsEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOSessionImpl extends Lifecycle implements CDOSession
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
    this.packageRegistry = new CDOPackageRegistryImpl(this, delegate);
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

  public boolean isEmpty()
  {
    return views.isEmpty();
  }

  public void viewDetached(CDOViewImpl view)
  {
    synchronized (views)
    {
      views.remove(view.getResourceSet());
      fireEvent(new ViewsEvent(view, IContainerDelta.Kind.REMOVED));
    }
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
    return idToMetaInstanceMap.get(id);
  }

  public CDOID lookupMetaInstanceID(InternalEObject metaInstance)
  {
    return metaInstanceToIDMap.get(metaInstance);
  }

  public CDOIDRange registerEPackage(EPackage ePackage)
  {
    long lowerBound = nextTemporaryID;
    registerMetaInstance((InternalEObject)ePackage);
    return CDOIDRangeImpl.create(lowerBound, nextTemporaryID + 2);
  }

  /**
   * TODO synchronize on nextTemporaryID
   */
  private void registerMetaInstance(InternalEObject metaInstance)
  {
    CDOID id = CDOIDImpl.create(nextTemporaryID);
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance);
    }

    idToMetaInstanceMap.put(id, metaInstance);
    metaInstanceToIDMap.put(metaInstance, id);
    --nextTemporaryID;
    --nextTemporaryID;

    // for (EReference reference : metaInstance.eClass().getEAllReferences())
    // {
    // metaInstance.eGet(reference);
    // }

    for (EObject content : metaInstance.eContents())
    {
      registerMetaInstance((InternalEObject)content);
    }
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
      throw new ImplementationError("Not a " + ResourceSetImpl.class.getName());
    }

    synchronized (views)
    {
      resourceSet.eAdapters().add(view);
      views.put(resourceSet, view);
      fireEvent(new ViewsEvent(view, IContainerDelta.Kind.ADDED));
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ViewsEvent extends SingleDeltaContainerEvent<CDOView> implements CDOSessionViewsEvent
  {
    private static final long serialVersionUID = 1L;

    public ViewsEvent(CDOView view, Kind kind)
    {
      super(CDOSessionImpl.this, view, kind);
    }

    public CDOSession getSession()
    {
      return CDOSessionImpl.this;
    }

    public CDOView getView()
    {
      return getDeltaElement();
    }
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

  /**
   * @author Eike Stepper
   */
  private static final class ProxyResolverURIResourceMap implements Map<URI, Resource>
  {
    private Map<URI, Resource> delegate;

    private Resource proxyResolverResource;

    public ProxyResolverURIResourceMap(CDOViewImpl view, Map<URI, Resource> delegate)
    {
      if (delegate == null)
      {
        delegate = new HashMap(); // TODO Cleanup of this lookup cache?
      }

      this.delegate = delegate;
      proxyResolverResource = new ProxyResolverResource(view);
    }

    public Resource get(Object key)
    {
      if (key instanceof URI)
      {
        URI uri = (URI)key;
        String scheme = uri.scheme();
        if ("cdo".equals(scheme))
        {
          String opaquePart = uri.opaquePart();
          if ("proxy".equals(opaquePart))
          {
            return proxyResolverResource;
          }
        }
      }

      return delegate.get(key);
    }

    public void clear()
    {
      delegate.clear();
    }

    public boolean containsKey(Object key)
    {
      return delegate.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
      return delegate.containsValue(value);
    }

    public Set<Entry<URI, Resource>> entrySet()
    {
      return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o)
    {
      return delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
      return delegate.hashCode();
    }

    public boolean isEmpty()
    {
      return delegate.isEmpty();
    }

    public Set<URI> keySet()
    {
      return delegate.keySet();
    }

    public Resource put(URI key, Resource value)
    {
      return delegate.put(key, value);
    }

    public void putAll(Map<? extends URI, ? extends Resource> t)
    {
      delegate.putAll(t);
    }

    public Resource remove(Object key)
    {
      return delegate.remove(key);
    }

    public int size()
    {
      return delegate.size();
    }

    public Collection<Resource> values()
    {
      return delegate.values();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ProxyResolverResource implements Resource
  {
    private CDOViewImpl view;

    public ProxyResolverResource(CDOViewImpl view)
    {
      this.view = view;
    }

    /*
     * @ADDED Called by {@link ResourceSetImpl#getResource(URI, boolean)}
     */
    public boolean isLoaded()
    {
      return true;
    }

    /*
     * @ADDED Called by {@link ResourceSetImpl#getEObject(URI, boolean)}
     */
    public EObject getEObject(String uriFragment)
    {
      CDOID id = CDOIDImpl.create(Long.parseLong(uriFragment));
      InternalCDOObject object = view.lookupInstance(id);
      if (object instanceof CDOAdapterImpl)
      {
        CDOAdapterImpl adapter = (CDOAdapterImpl)object;
        adapter.cdoInternalResolveRevision();
        return adapter.getTarget();
      }

      // throw new ImplementationError("Can't resolve " + uriFragment);
      return object;
    }

    public TreeIterator<EObject> getAllContents()
    {
      throw new UnsupportedOperationException();
    }

    public EList<EObject> getContents()
    {
      throw new UnsupportedOperationException();
    }

    public EList<Diagnostic> getErrors()
    {
      throw new UnsupportedOperationException();
    }

    public ResourceSet getResourceSet()
    {
      throw new UnsupportedOperationException();
    }

    public URI getURI()
    {
      throw new UnsupportedOperationException();
    }

    public String getURIFragment(EObject object)
    {
      throw new UnsupportedOperationException();
    }

    public EList<Diagnostic> getWarnings()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isModified()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isTrackingModification()
    {
      throw new UnsupportedOperationException();
    }

    public void load(Map<?, ?> options) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public void load(InputStream inputStream, Map<?, ?> options) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public void save(Map<?, ?> options) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public void save(OutputStream outputStream, Map<?, ?> options) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public void setModified(boolean isModified)
    {
      throw new UnsupportedOperationException();
    }

    public void setTrackingModification(boolean isTrackingModification)
    {
      throw new UnsupportedOperationException();
    }

    public void setURI(URI uri)
    {
      throw new UnsupportedOperationException();
    }

    public void unload()
    {
      throw new UnsupportedOperationException();
    }

    public EList<Adapter> eAdapters()
    {
      throw new UnsupportedOperationException();
    }

    public boolean eDeliver()
    {
      throw new UnsupportedOperationException();
    }

    public void eNotify(Notification notification)
    {
      throw new UnsupportedOperationException();
    }

    public void eSetDeliver(boolean deliver)
    {
      throw new UnsupportedOperationException();
    }
  }
}
