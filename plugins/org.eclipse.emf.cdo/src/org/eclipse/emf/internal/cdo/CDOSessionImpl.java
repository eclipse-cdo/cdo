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

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionAdaptersEvent;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.internal.cdo.bundle.CDO;
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
  private static final ContextTracer TRACER = new ContextTracer(CDO.DEBUG_SESSION, CDOSessionImpl.class);

  private int sessionID;

  private IChannel channel;

  private String repositoryName;

  private String repositoryUUID;

  private CDORevisionManagerImpl revisionManager;

  private Map<ResourceSet, CDOViewImpl> adapters = new HashMap();

  private IListener channelListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  public CDOSessionImpl()
  {
    revisionManager = new CDORevisionManagerImpl(this);
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public IConnector getConnector()
  {
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
    return attach(resourceSet, new CDOViewImpl(this, readOnly));
  }

  public CDOViewImpl openView(ResourceSet resourceSet, long timeStamp)
  {
    prepare(resourceSet);
    return attach(resourceSet, new CDOViewImpl(this, timeStamp));
  }

  public CDOViewImpl[] getViews()
  {
    Collection<CDOViewImpl> values;
    synchronized (adapters)
    {
      values = adapters.values();
    }
    return values.toArray(new CDOViewImpl[values.size()]);
  }

  public CDOView[] getElements()
  {
    return getViews();
  }

  public boolean isEmpty()
  {
    return adapters.isEmpty();
  }

  public void adapterDetached(CDOViewImpl adapter)
  {
    synchronized (adapters)
    {
      adapters.remove(adapter.getResourceSet());
      fireEvent(new CDOSessionAdaptersEventImpl(this, adapter, IContainerDelta.Kind.REMOVED));
    }
  }

  public void notifyInvalidation(long timeStamp, Set<CDOID> dirtyOIDs, CDOViewImpl excludedAdapter)
  {
    CDOViewImpl[] values;
    synchronized (adapters)
    {
      values = adapters.values().toArray(new CDOViewImpl[adapters.size()]);
    }

    Set<CDOID> unmodifiableSet = Collections.unmodifiableSet(dirtyOIDs);
    for (CDOViewImpl adapter : values)
    {
      if (adapter != excludedAdapter)
      {
        adapter.notifyInvalidation(timeStamp, unmodifiableSet);
      }
    }
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
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    EventUtil.removeListener(channel, channelListener);
    channel.close();
    super.doDeactivate();
  }

  private void prepare(ResourceSet resourceSet)
  {
    CDOView adapter = CDOUtil.getAdapter(resourceSet);
    if (adapter != null)
    {
      throw new IllegalStateException("CDO adapter already present: " + adapter);
    }

    CDOUtil.addResourceFactory(resourceSet);
  }

  private CDOViewImpl attach(ResourceSet resourceSet, CDOViewImpl adapter)
  {
    synchronized (adapters)
    {
      resourceSet.eAdapters().add(adapter);
      adapters.put(resourceSet, adapter);
      fireEvent(new CDOSessionAdaptersEventImpl(this, adapter, IContainerDelta.Kind.ADDED));
    }

    return adapter;
  }

  /**
   * @author Eike Stepper
   */
  private static class CDOSessionAdaptersEventImpl extends SingleDeltaContainerEvent<CDOView> implements
      CDOSessionAdaptersEvent
  {
    private static final long serialVersionUID = 1L;

    public CDOSessionAdaptersEventImpl(CDOSession session, CDOView adapter, Kind kind)
    {
      super(session, adapter, kind);
    }

    public CDOSession getSession()
    {
      return (CDOSession)getContainer();
    }

    public CDOView getAdapter()
    {
      return getDeltaElement();
    }
  }
}
