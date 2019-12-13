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
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.server.internal.admin.protocol.CDOAdminServerProtocol;
import org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler;
import org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler2;
import org.eclipse.emf.cdo.spi.common.admin.AbstractCDOAdmin;
import org.eclipse.emf.cdo.spi.server.AuthenticationUtil;
import org.eclipse.emf.cdo.spi.server.IAuthenticationProtocol;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.jvm.IJVMChannel;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.spi.net4j.Protocol;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminServer extends AbstractCDOAdmin
{
  private static Class<?> IJVMCHANNEL_CLASS;

  private final IManagedContainer container;

  private final IListener containerListener = new ContainerEventAdapter<Object>(true)
  {
    @Override
    protected void onAdded(IContainer<Object> container, Object element)
    {
      if (element instanceof IRepository)
      {
        IRepository repository = (IRepository)element;
        repositoryAdded(repository);
      }
    }

    @Override
    protected void onRemoved(IContainer<Object> container, Object element)
    {
      if (element instanceof IRepository)
      {
        IRepository repository = (IRepository)element;
        repositoryRemoved(repository);
      }
    }
  };

  private final Set<CDOAdminServerProtocol> protocols = new HashSet<CDOAdminServerProtocol>();

  public CDOAdminServer(IManagedContainer container, long timeout)
  {
    super(timeout);
    this.container = container;
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  public void registerProtocol(final CDOAdminServerProtocol protocol)
  {
    protocol.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        protocol.deactivate();
      }
    });

    synchronized (protocols)
    {
      protocols.add(protocol);
    }
  }

  public void deregisterProtocol(CDOAdminServerProtocol protocol)
  {
    synchronized (protocols)
    {
      protocols.remove(protocol);
    }
  }

  @Override
  protected boolean doCreateRepository(String name, String type, Map<String, Object> properties)
  {
    CDOAdminHandler handler = getAdminHandler(type);
    if (handler instanceof CDOAdminHandler2)
    {
      // Must provide administrator credentials to create a repository
      ((CDOAdminHandler2)handler).authenticateAdministrator();
    }

    IRepository delegate = handler.createRepository(name, properties);
    CDOServerUtil.addRepository(container, delegate);
    return true;
  }

  @Override
  protected boolean doDeleteRepository(String name, String type)
  {
    CDOAdminHandler handler = getAdminHandler(type);
    CDOAdminServerRepository repository = (CDOAdminServerRepository)getRepository(name);
    if (repository == null)
    {
      return false;
    }

    IRepository delegate = repository.getDelegate();

    // Can this handler delete the repository?
    if (!canDelete(delegate, handler))
    {
      throw new IllegalStateException("The repository is permanently configured.");
    }

    if (handler instanceof CDOAdminHandler2)
    {
      // Must provide administrator credentials to delete a repository
      ((CDOAdminHandler2)handler).authenticateAdministrator();
    }

    // Do we have any connected users? If so, prompt for confirmation
    if (hasConnections(delegate) && !confirmDeletion(delegate))
    {
      return false;
    }

    LifecycleUtil.deactivate(delegate);
    handler.deleteRepository(delegate);
    return true;
  }

  protected boolean canDelete(IRepository repository, CDOAdminHandler handler)
  {
    return !(handler instanceof CDOAdminHandler2) || ((CDOAdminHandler2)handler).canDelete(repository);
  }

  protected boolean hasConnections(IRepository delegate)
  {
    ISession[] sessions = delegate.getSessionManager().getSessions();
    if (sessions != null)
    {
      for (int i = 0; i < sessions.length; i++)
      {
        ISessionProtocol protocol = sessions[i].getProtocol();
        if (protocol instanceof Protocol<?>)
        {
          // Connections from within this JVM do not count
          IChannel channel = ((Protocol<?>)protocol).getChannel();
          if (channel != null && !isJVMChannel(channel))
          {
            return true;
          }
        }
      }
    }

    return false;
  }

  protected boolean confirmDeletion(IRepository delegate)
  {
    IAuthenticationProtocol authProtocol = AuthenticationUtil.getAuthenticationProtocol();
    if (authProtocol instanceof CDOAdminServerProtocol)
    {
      CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)authProtocol;
      String message = MessageFormat.format("The repository \"{0}\" has connected users. Proceed with deletion anyways?", delegate.getName());
      try
      {
        if (protocol.sendConfirmationRequest("Repository In Use", message, Confirmation.NO, Confirmation.YES, Confirmation.NO) != Confirmation.YES)
        {
          return false;
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return false;
      }
    }

    return true;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof IRepository)
      {
        IRepository delegate = (IRepository)element;
        CDOAdminServerRepository repository = new CDOAdminServerRepository(this, delegate);
        addElement(repository);
      }
    }

    container.addListener(containerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    container.removeListener(containerListener);
    super.doDeactivate();
  }

  protected void repositoryAdded(IRepository delegate)
  {
    CDOAdminServerRepository repository = new CDOAdminServerRepository(this, delegate);
    if (addElement(repository))
    {
      for (CDOAdminServerProtocol protocol : getProtocols())
      {
        try
        {
          protocol.sendRepositoryAdded(repository);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(protocol, ex);
        }
      }
    }
  }

  protected void repositoryRemoved(IRepository delegate)
  {
    String name = delegate.getName();
    CDOAdminServerRepository repository = (CDOAdminServerRepository)getRepository(name);

    if (removeElement(repository))
    {
      repository.dispose();
      for (CDOAdminServerProtocol protocol : getProtocols())
      {
        try
        {
          protocol.sendRepositoryRemoved(name);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(protocol, ex);
        }
      }
    }
  }

  protected void repositoryTypeChanged(String name, Type oldType, Type newType)
  {
    for (CDOAdminServerProtocol protocol : getProtocols())
    {
      try
      {
        protocol.sendRepositoryTypeChanged(name, oldType, newType);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(protocol, ex);
      }
    }
  }

  protected void repositoryStateChanged(String name, State oldState, State newState)
  {
    for (CDOAdminServerProtocol protocol : getProtocols())
    {
      try
      {
        protocol.sendRepositoryStateChanged(name, oldState, newState);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(protocol, ex);
      }
    }
  }

  protected void repositoryReplicationProgressed(String name, double totalWork, double work)
  {
    for (CDOAdminServerProtocol protocol : getProtocols())
    {
      try
      {
        protocol.sendRepositoryReplicationProgressed(name, totalWork, work);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(protocol, ex);
      }
    }
  }

  protected void handleNotificationProblem(CDOAdminServerProtocol protocol, Exception ex)
  {
    OM.LOG.warn("A problem occured while notifying client " + protocol, ex);
  }

  protected CDOAdminHandler getAdminHandler(String type)
  {
    return (CDOAdminHandler)container.getElement(CDOAdminHandler.Factory.PRODUCT_GROUP, type, null);
  }

  private CDOAdminServerProtocol[] getProtocols()
  {
    synchronized (protocols)
    {
      return protocols.toArray(new CDOAdminServerProtocol[protocols.size()]);
    }
  }

  private static boolean isJVMChannel(IChannel channel)
  {
    return getIJVMChannelClass().isInstance(channel);
  }

  private static Class<?> getIJVMChannelClass()
  {
    if (IJVMCHANNEL_CLASS == null)
    {
      // Try to load the class, but the plug-in may not be installed
      try
      {
        new Runnable()
        {
          @Override
          public void run()
          {
            IJVMCHANNEL_CLASS = IJVMChannel.class;
          }
        }.run();
      }
      catch (LinkageError er)
      {
        // The class is not available, so no channel can be an IJVMChannel
        IJVMCHANNEL_CLASS = Void.class;
      }
    }

    return IJVMCHANNEL_CLASS;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.admin.adminServers";

    public static final String TYPE = "default";

    private final IManagedContainer container;

    private final long timeout;

    public Factory(IManagedContainer container)
    {
      this(container, ISignalProtocol.DEFAULT_TIMEOUT);
    }

    public Factory(IManagedContainer container, long timeout)
    {
      super(PRODUCT_GROUP, TYPE);
      this.container = container;
      this.timeout = timeout;
    }

    public final IManagedContainer getContainer()
    {
      return container;
    }

    public final long getTimeout()
    {
      return timeout;
    }

    @Override
    public CDOAdminServer create(String description)
    {
      return new CDOAdminServer(container, timeout);
    }

    public static CDOAdminServer get(IManagedContainer container, String description)
    {
      return (CDOAdminServer)container.getElement(PRODUCT_GROUP, TYPE, description);
    }

    /**
     * @author Eike Stepper
     */
    public static final class Plugin extends Factory
    {
      public Plugin()
      {
        super(IPluginContainer.INSTANCE);
      }
    }
  }

}
