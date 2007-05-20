/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IAcceptorEvent;
import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.container.LifecycleEventConverter;
import org.eclipse.internal.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class Acceptor extends Lifecycle implements IAcceptor
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, Acceptor.class);

  private IBufferProvider bufferProvider;

  private IRegistry<IFactoryKey, IFactory> factoryRegistry;

  private ExecutorService receiveExecutor;

  /**
   * Is registered with each {@link IConnector} of this {@link IAcceptor}.
   * <p>
   */
  private transient IListener lifecycleEventConverter = new LifecycleEventConverter(this)
  {
    @Override
    protected IContainerEvent createContainerEvent(IContainer container, Object element, Kind kind)
    {
      return new AcceptorEvent((IAcceptor)container, (IConnector)element, kind);
    }
  };

  // private transient IListener lifecycleEventConverter = new
  // LifecycleEventConverter(this)
  // {
  // @Override
  // protected void removed(ILifecycleEvent e)
  // {
  // removeConnector((IConnector)e.getLifecycle());
  // super.removed(e);
  // }
  // };

  private Set<IConnector> acceptedConnectors = new HashSet(0);

  public Acceptor()
  {
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<IFactoryKey, IFactory> getFactoryRegistry()
  {
    return factoryRegistry;
  }

  public void setFactoryRegistry(IRegistry<IFactoryKey, IFactory> factoryRegistry)
  {
    this.factoryRegistry = factoryRegistry;
  }

  public IConnector[] getAcceptedConnectors()
  {
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.toArray(new IConnector[acceptedConnectors.size()]);
    }
  }

  public boolean isEmpty()
  {
    return acceptedConnectors.isEmpty();
  }

  public IConnector[] getElements()
  {
    return getAcceptedConnectors();
  }

  protected void addConnector(Connector connector)
  {
    try
    {
      connector.setBufferProvider(bufferProvider);
      connector.setReceiveExecutor(receiveExecutor);
      connector.setFactoryRegistry(factoryRegistry);
      connector.activate();
      connector.addListener(lifecycleEventConverter);

      synchronized (acceptedConnectors)
      {
        acceptedConnectors.add(connector);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Added connector " + connector); //$NON-NLS-1$
      }

      fireEvent(new AcceptorEvent(this, connector, IContainerDelta.Kind.ADDED));
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
    }
  }

  protected void removeConnector(IConnector connector)
  {
    connector.removeListener(lifecycleEventConverter);
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.remove(connector);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }

    fireEvent(new AcceptorEvent(this, connector, IContainerDelta.Kind.REMOVED));
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (factoryRegistry == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("factoryRegistry == null"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("receiveExecutor == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IConnector connector : getAcceptedConnectors())
    {
      LifecycleUtil.deactivate(connector);
    }

    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static class AcceptorEvent extends SingleDeltaContainerEvent<IConnector> implements IAcceptorEvent
  {
    private static final long serialVersionUID = 1L;

    public AcceptorEvent(IAcceptor acceptor, IConnector connector, Kind kind)
    {
      super(acceptor, connector, kind);
    }

    public IAcceptor getAcceptor()
    {
      return (IAcceptor)getContainer();
    }

    public IConnector getConnector()
    {
      return getDeltaElement();
    }
  }
}
