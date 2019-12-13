/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.internal.admin;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.internal.admin.protocol.CDOAdminClientProtocol;
import org.eclipse.emf.cdo.spi.common.admin.AbstractCDOAdmin;

import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.confirmation.IConfirmationProvider;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class CDOAdminClientImpl extends AbstractCDOAdmin implements CDOAdminClient, IPasswordCredentialsProvider.Provider, IConfirmationProvider.Provider
{
  private static final String URL_SEPARATOR = "://";

  private final String url;

  private final IManagedContainer container;

  private final ExecutorService executorService;

  private boolean connected;

  private ConnectLock connectLock = new ConnectLock();

  private long connectAttempt;

  private CDOAdminClientProtocol protocol;

  public CDOAdminClientImpl(String url, long timeout, IManagedContainer container)
  {
    super(timeout);
    this.url = url;
    this.container = container;

    executorService = ExecutorServiceFactory.get(container);
  }

  @Override
  public final String getURL()
  {
    return url;
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public boolean isConnected()
  {
    return connected;
  }

  @Override
  public IConnector getConnector()
  {
    if (!connected)
    {
      return null;
    }

    IChannelMultiplexer multiplexer = protocol.getChannel().getMultiplexer();
    if (multiplexer instanceof IConnector)
    {
      return (IConnector)multiplexer;
    }

    return null;
  }

  @Override
  public CDOAdminClientRepository[] getRepositories()
  {
    CDOAdminRepository[] repositories = super.getRepositories();
    CDOAdminClientRepository[] result = new CDOAdminClientRepository[repositories.length];
    for (int i = 0; i < repositories.length; i++)
    {
      result[i] = (CDOAdminClientRepository)repositories[i];
    }

    return result;
  }

  @Override
  public synchronized CDOAdminClientRepository getRepository(String name)
  {
    return (CDOAdminClientRepository)super.getRepository(name);
  }

  @Override
  public CDOAdminClientRepository createRepository(String name, String type, Map<String, Object> properties)
  {
    return (CDOAdminClientRepository)super.createRepository(name, type, properties);
  }

  @Override
  public CDOAdminClientRepository waitForRepository(String name)
  {
    return (CDOAdminClientRepository)super.waitForRepository(name);
  }

  public void repositoryTypeChanged(String name, Type oldType, Type newType)
  {
    CDOAdminClientRepositoryImpl repository = (CDOAdminClientRepositoryImpl)getRepository(name);
    if (repository != null)
    {
      repository.typeChanged(oldType, newType);
    }
  }

  public void repositoryStateChanged(String name, State oldState, State newState)
  {
    CDOAdminClientRepositoryImpl repository = (CDOAdminClientRepositoryImpl)getRepository(name);
    if (repository != null)
    {
      repository.stateChanged(oldState, newState);
    }
  }

  public void repositoryReplicationProgressed(String name, double totalWork, double work)
  {
    CDOAdminClientRepositoryImpl repository = (CDOAdminClientRepositoryImpl)getRepository(name);
    if (repository != null)
    {
      repository.replicationProgressed(totalWork, work);
    }
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    try
    {
      return (IPasswordCredentialsProvider)container.getElement(CredentialsProviderFactory.PRODUCT_GROUP, "interactive", //$NON-NLS-1$
          null);
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @Override
  public IConfirmationProvider getConfirmationProvider()
  {
    try
    {
      return (IConfirmationProvider)container.getElement(IConfirmationProvider.Factory.PRODUCT_GROUP, IConfirmationProvider.Factory.INTERACTIVE_TYPE, null);
    }
    catch (Exception ex)
    {
      return null;
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

  protected void setConnected(final boolean on)
  {
    connected = on;
    if (!on)
    {
      clear();
    }

    fireEvent(new ConnectionStateChangedEvent()
    {
      @Override
      public CDOAdminClient getSource()
      {
        return CDOAdminClientImpl.this;
      }

      @Override
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

  protected void connect()
  {
    if (LifecycleUtil.isActive(executorService))
    {
      synchronized (connectLock)
      {
        executorService.submit(new ConnectRunnable());
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class ConnectRunnable implements Runnable
  {
    @Override
    public void run()
    {
      if (!container.isActive())
      {
        return;
      }

      try
      {
        sleep();

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

            if (isActive())
            {
              connect();
            }
          }
        });

        Set<CDOAdminClientRepository> repositories = protocol.queryRepositories();
        for (CDOAdminClientRepository repository : repositories)
        {
          addElement(repository);
        }

        setConnected(true);
      }
      catch (InterruptedException ex)
      {
        return;
      }
      catch (Throwable ex)
      {
        if (protocol != null)
        {
          LifecycleUtil.deactivate(protocol.getChannel());
          protocol = null;
        }

        connect();
      }
    }

    private void sleep() throws InterruptedException
    {
      long now = System.currentTimeMillis();
      if (connectAttempt != 0)
      {
        long passed = now - connectAttempt;
        long timeout = getTimeout();
        long sleep = Math.max(timeout - passed, timeout);
        Thread.sleep(sleep);
      }

      connectAttempt = now;
    }
  }

  /**
   * A separate class for better monitor debugging.
   *
   * @author Eike Stepper
   */
  private static final class ConnectLock
  {
  }
}
