/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.admin;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.internal.admin.protocol.CDOAdminClientProtocol;
import org.eclipse.emf.cdo.spi.common.admin.AbstractCDOAdmin;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class CDOAdminClientImpl extends AbstractCDOAdmin implements CDOAdminClient
{
  private static final String URL_SEPARATOR = "://";

  private final CDOAdminClientManagerImpl manager;

  private final String url;

  private final IManagedContainer container;

  private final ExecutorService executorService;

  private boolean connected;

  private CDOAdminClientProtocol protocol;

  public CDOAdminClientImpl(String url, long timeout, IManagedContainer container)
  {
    this(url, timeout, container, null);
  }

  protected CDOAdminClientImpl(String url, long timeout, CDOAdminClientManagerImpl manager)
  {
    this(url, timeout, manager.getContainer(), manager);
  }

  protected CDOAdminClientImpl(String url, long timeout, IManagedContainer container, CDOAdminClientManagerImpl manager)
  {
    super(timeout);
    this.url = url;
    this.container = container;
    this.manager = manager;

    executorService = ExecutorServiceFactory.get(container);
    activate();
  }

  public final String getURL()
  {
    return url;
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  public CDOAdminClientManagerImpl getManager()
  {
    return manager;
  }

  public boolean isConnected()
  {
    return connected;
  }

  public void repositoryTypeChanged(String name, Type oldType, Type newType)
  {
    CDOAdminClientRepository repository = (CDOAdminClientRepository)getRepository(name);
    if (repository != null)
    {
      repository.typeChanged(oldType, newType);
    }
  }

  public void repositoryStateChanged(String name, State oldState, State newState)
  {
    CDOAdminClientRepository repository = (CDOAdminClientRepository)getRepository(name);
    if (repository != null)
    {
      repository.stateChanged(oldState, newState);
    }
  }

  public void repositoryReplicationProgressed(String name, double totalWork, double work)
  {
    CDOAdminClientRepository repository = (CDOAdminClientRepository)getRepository(name);
    if (repository != null)
    {
      repository.replicationProgressed(totalWork, work);
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (url == null ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (!(obj instanceof CDOAdminClient))
    {
      return false;
    }

    CDOAdminClient other = (CDOAdminClient)obj;
    if (url == null)
    {
      if (other.getURL() != null)
      {
        return false;
      }
    }
    else if (!url.equals(other.getURL()))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return url;
  }

  @Override
  protected boolean doCreateRepository(String name, String type, Map<String, Object> properties)
  {
    return protocol.createRepository(name, type, properties);
  }

  @Override
  protected boolean doDeleteRepository(String name, String type)
  {
    return protocol.deleteRepository(name, type);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    setConnected(false);

    protocol.close();
    protocol = null;

    super.doDeactivate();
  }

  protected void connect()
  {
    executorService.submit(new ConnectRunnable());
  }

  protected void setConnected(final boolean on)
  {
    connected = on;
    if (!on)
    {
      clear();
    }

    fireEvent(new ConnectionStateChangedEvent()
    {
      public CDOAdminClient getSource()
      {
        return CDOAdminClientImpl.this;
      }

      public boolean isConnected()
      {
        return on;
      }

      @Override
      public String toString()
      {
        return "ConnectionStateChangedEvent[connected=" + on + "]";
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  protected class ConnectRunnable implements Runnable
  {
    public void run()
    {
      try
      {
        int pos = url.indexOf(URL_SEPARATOR);
        String type = url.substring(0, pos);
        String description = url.substring(pos + URL_SEPARATOR.length());

        IConnector connector = (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, type, description);

        protocol = new CDOAdminClientProtocol(CDOAdminClientImpl.this);
        protocol.open(connector);
        protocol.addListener(new LifecycleEventAdapter()
        {
          @Override
          protected void onDeactivated(ILifecycle lifecycle)
          {
            setConnected(false);
            protocol = null;
            connect();
          }
        });

        Set<CDOAdminRepository> repositories = protocol.queryRepositories();
        for (CDOAdminRepository repository : repositories)
        {
          addElement(repository);
        }

        setConnected(true);
      }
      catch (Exception ex)
      {
        if (protocol != null)
        {
          LifecycleUtil.deactivate(protocol.getChannel());
          protocol = null;
          ConcurrencyUtil.sleep(getTimeout());
        }

        connect();
      }
      finally
      {
      }
    }
  }
}
