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

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionRequest;
import org.eclipse.emf.internal.cdo.protocol.OpenSessionResult;

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
  @SuppressWarnings("unused")
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

  public CDOViewImpl openView(ResourceSet resourceSet)
  {
    return openView(resourceSet, false);
  }

  public CDOViewImpl openView(ResourceSet resourceSet, boolean readOnly)
  {
    prepare(resourceSet);
    return attach(resourceSet, new CDOViewImpl(++lastViewID, this, readOnly));
  }

  public CDOViewImpl openView(ResourceSet resourceSet, long timeStamp)
  {
    prepare(resourceSet);
    return attach(resourceSet, new CDOViewImpl(++lastViewID, this, timeStamp));
  }

  public CDOView openView(boolean readOnly)
  {
    return openView(createResourceSet(), readOnly);
  }

  public CDOView openView(long timeStamp)
  {
    return openView(createResourceSet(), timeStamp);
  }

  public CDOView openView()
  {
    return openView(createResourceSet());
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
    for (TreeIterator<EObject> it = ePackage.eAllContents(); it.hasNext();)
    {
      InternalEObject metaInstance = (InternalEObject)it.next();
      registerMetaInstance(metaInstance);
    }

    return CDOIDRangeImpl.create(lowerBound, nextTemporaryID + 2);
  }

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
    packageManager.addPackageProxies(result.getPackageURIs());
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

  private void prepare(ResourceSet resourceSet)
  {
    CDOView view = CDOUtil.getView(resourceSet);
    if (view != null)
    {
      throw new IllegalStateException("CDO view already open: " + view);
    }

    resourceSet.setPackageRegistry(new EPackageRegistryImpl(packageRegistry));
    CDOUtil.addResourceFactory(resourceSet);
  }

  private CDOViewImpl attach(ResourceSet resourceSet, CDOViewImpl view)
  {
    synchronized (views)
    {
      resourceSet.eAdapters().add(view);
      views.put(resourceSet, view);
      fireEvent(new ViewsEvent(view, IContainerDelta.Kind.ADDED));
    }

    return view;
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
}
