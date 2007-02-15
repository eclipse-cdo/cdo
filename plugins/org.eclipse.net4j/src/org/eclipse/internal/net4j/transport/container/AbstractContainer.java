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
package org.eclipse.internal.net4j.transport.container;

import static org.eclipse.net4j.util.registry.IRegistryDelta.Kind.DEREGISTERED;
import static org.eclipse.net4j.util.registry.IRegistryDelta.Kind.REGISTERED;

import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.AcceptorFactory;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.transport.container.Container;
import org.eclipse.net4j.transport.container.ContainerAdapter;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleImpl;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryDelta;
import org.eclipse.net4j.util.registry.IRegistryEvent;
import org.eclipse.net4j.util.registry.IRegistryListener;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.AbstractAcceptor;
import org.eclipse.internal.net4j.transport.AbstractConnector;
import org.eclipse.internal.net4j.transport.DescriptionUtil;
import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public abstract class AbstractContainer extends LifecycleImpl implements Container
{
  private IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry;

  private IRegistry<String, ContainerAdapter> adapters = new HashMapRegistry();

  private LifecycleListener acceptorLifecycleListener = new LifecycleListener()
  {
    public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleActivated(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
    {
      deregisterAcceptor((Acceptor)notifier);
    }
  };

  private LifecycleListener connectorLifecycleListener = new LifecycleListener()
  {
    public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleActivated(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
    {
      deregisterConnector((Connector)notifier);
    }
  };

  private IRegistryListener adapterFactoryRegistryListener = new IRegistryListener<String, ContainerAdapterFactory>()
  {
    public void notifyRegistryEvent(IRegistryEvent<String, ContainerAdapterFactory> event)
    {
      IRegistryDelta<String, ContainerAdapterFactory>[] deltas = event.getDeltas();
      for (IRegistryDelta<String, ContainerAdapterFactory> delta : deltas)
      {
        try
        {
          ContainerAdapterFactory factory = delta.getValue();
          switch (delta.getKind())
          {
          case REGISTERED:
            addAdapter(factory.getType());
            break;

          case DEREGISTERED:
            // TODO Implement method .notifyRegistryEvent()
            throw new UnsupportedOperationException("Not yet implemented");
          }
        }
        catch (Exception ex)
        {
          Net4j.LOG.error(ex);
        }
      }
    }
  };

  protected AbstractContainer(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry)
  {
    this.adapterFactoryRegistry = adapterFactoryRegistry;
  }

  public IRegistry<String, ContainerAdapterFactory> getAdapterFactoryRegistry()
  {
    return adapterFactoryRegistry;
  }

  public IRegistry<String, ContainerAdapter> getAdapters()
  {
    return adapters;
  }

  public ContainerAdapter getAdapter(String type)
  {
    return adapters.get(type);
  }

  public Acceptor getAcceptor(String description)
  {
    IRegistry<String, Acceptor> registry = getAcceptorRegistry();
    Acceptor acceptor = registry.get(description);
    if (acceptor == null)
    {
      acceptor = createAcceptor(description);
      if (acceptor != null)
      {
        LifecycleUtil.addListener(acceptor, acceptorLifecycleListener);
        registry.put(description, acceptor);
      }
    }

    return acceptor;
  }

  public Connector getConnector(String description)
  {
    IRegistry<String, Connector> registry = getConnectorRegistry();
    Connector connector = registry.get(description);
    if (connector == null)
    {
      connector = createConnector(description);
      if (connector != null)
      {
        LifecycleUtil.addListener(connector, connectorLifecycleListener);
        registry.put(description, connector);
      }
    }

    return connector;
  }

  public void register(ContainerAdapterFactory factory)
  {
    IRegistry<String, ContainerAdapterFactory> registry = getAdapterFactoryRegistry();
    registry.put(factory.getType(), factory);
  }

  public void deregister(ContainerAdapterFactory factory)
  {
    IRegistry<String, ContainerAdapterFactory> registry = getAdapterFactoryRegistry();
    registry.remove(factory.getType());
  }

  public void register(AcceptorFactory factory)
  {
    IRegistry<String, AcceptorFactory> registry = getAcceptorFactoryRegistry();
    registry.put(factory.getType(), factory);
  }

  public void deregister(AcceptorFactory factory)
  {
    IRegistry<String, AcceptorFactory> registry = getAcceptorFactoryRegistry();
    registry.remove(factory.getType());
  }

  public void register(ConnectorFactory factory)
  {
    IRegistry<String, ConnectorFactory> registry = getConnectorFactoryRegistry();
    registry.put(factory.getType(), factory);
  }

  public void deregister(ConnectorFactory factory)
  {
    IRegistry<String, ConnectorFactory> registry = getConnectorFactoryRegistry();
    registry.remove(factory.getType());
  }

  public void register(ProtocolFactory factory)
  {
    IRegistry<ProtocolFactoryID, ProtocolFactory> registry = getProtocolFactoryRegistry();
    for (ConnectorLocation location : factory.getLocations())
    {
      ProtocolFactoryID id = factory.getID(location);
      registry.put(id, factory);
    }
  }

  public void deregister(ProtocolFactory factory)
  {
    IRegistry<ProtocolFactoryID, ProtocolFactory> registry = getProtocolFactoryRegistry();
    for (ConnectorLocation location : factory.getLocations())
    {
      ProtocolFactoryID id = factory.getID(location);
      registry.remove(id);
    }
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (adapterFactoryRegistry == null)
    {
      throw new IllegalStateException("adapterFactoryRegistry == null");
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    IRegistry<String, ContainerAdapterFactory> registry = getAdapterFactoryRegistry();
    for (ContainerAdapterFactory factory : registry.values())
    {
      addAdapter(factory.getType());
    }

    registry.addRegistryListener(adapterFactoryRegistryListener);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    getAdapterFactoryRegistry().removeRegistryListener(adapterFactoryRegistryListener);
    for (ContainerAdapter adapter : adapters.values())
    {
      LifecycleUtil.deactivateNoisy(adapter);
    }

    for (Connector connector : getConnectorRegistry().values())
    {
      LifecycleUtil.deactivateNoisy(connector);
    }

    for (Acceptor acceptor : getAcceptorRegistry().values())
    {
      LifecycleUtil.deactivateNoisy(acceptor);
    }

    super.onDeactivate();
  }

  private void deregisterAcceptor(Acceptor acceptor)
  {
    getAcceptorRegistry().remove(acceptor.getDescription());
    LifecycleUtil.removeListener(acceptor, acceptorLifecycleListener);
  }

  private void deregisterConnector(Connector connector)
  {
    getConnectorRegistry().remove(connector.getDescription());
    LifecycleUtil.removeListener(connector, connectorLifecycleListener);
  }

  private Acceptor createAcceptor(String description)
  {
    IRegistry<String, AcceptorFactory> registry = getAcceptorFactoryRegistry();
    if (registry == null)
    {
      return null;
    }

    String type = DescriptionUtil.getType(description);
    AcceptorFactory factory = registry.get(type);
    if (factory == null)
    {
      return null;
    }

    AbstractAcceptor acceptor = (AbstractAcceptor)factory.createAcceptor();
    acceptor.setReceiveExecutor(getExecutorService());
    acceptor.setBufferProvider(getBufferProvider());
    acceptor.setDescription(description);
    acceptor.setProtocolFactoryRegistry(getProtocolFactoryRegistry());

    for (ContainerAdapter adapter : adapters.values())
    {
      adapter.initAcceptor(acceptor);
    }

    try
    {
      LifecycleUtil.activate(acceptor);
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
      acceptor = null;
    }

    return acceptor;
  }

  private Connector createConnector(String description)
  {
    IRegistry<String, ConnectorFactory> registry = getConnectorFactoryRegistry();
    if (registry == null)
    {
      return null;
    }

    String type = DescriptionUtil.getType(description);
    ConnectorFactory factory = registry.get(type);
    if (factory == null)
    {
      return null;
    }

    AbstractConnector connector = (AbstractConnector)factory.createConnector();
    connector.setReceiveExecutor(getExecutorService());
    connector.setBufferProvider(getBufferProvider());
    connector.setDescription(description);
    connector.setProtocolFactoryRegistry(getProtocolFactoryRegistry());

    for (ContainerAdapter adapter : adapters.values())
    {
      adapter.initConnector(connector);
    }

    try
    {
      LifecycleUtil.activate(connector);
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
      connector = null;
    }

    return connector;
  }

  private ContainerAdapter addAdapter(String type)
  {
    ContainerAdapterFactory factory = adapterFactoryRegistry.get(type);
    if (factory == null)
    {
      return null;
    }

    ContainerAdapter adapter = createAdapter(factory);
    if (adapter != null)
    {
      adapters.put(type, adapter);
    }

    return adapter;
  }

  private ContainerAdapter createAdapter(ContainerAdapterFactory factory)
  {
    try
    {
      ContainerAdapter adapter = factory.createAdapter(this);
      LifecycleUtil.activate(adapter);
      return adapter;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}
