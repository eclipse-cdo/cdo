/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager.RepositoryConnectionEvent;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.SystemElement;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.internal.client.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.job.OMJob;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
public final class SystemManager extends LMManager<CDORepository, CDORepositoryManager, ISystemDescriptor> implements ISystemManager
{
  public static final SystemManager INSTANCE = new SystemManager();

  private static final String PROP_SYSTEM_NAME = "name";

  private static final String PROP_OPEN = "open";

  private final Map<String, ISystemDescriptor> descriptorsBySystemName = new HashMap<>();

  private final Set<CDORepository> analyzingRepositories = Collections.synchronizedSet(new HashSet<>());

  private SystemManager()
  {
    super(CDOExplorerUtil.getRepositoryManager(), CDORepository.class);
  }

  @Override
  public void refresh()
  {
    forEachExplorerElement(repository -> {
      ISystemDescriptor descriptor = getDescriptor(repository);
      if (descriptor == SystemDescriptor.NO_SYSTEM)
      {
        deleteStateFolder(repository);
        descriptor = null;
      }

      if (descriptor == null)
      {
        addDescriptor(repository);
      }
    });
  }

  @Override
  public ISystemDescriptor[] getDescriptors()
  {
    ISystemDescriptor[] array = super.getDescriptors();
    Arrays.sort(array);
    return array;
  }

  @Override
  protected boolean filterDescriptor(ISystemDescriptor descriptor)
  {
    return descriptor != SystemDescriptor.NO_SYSTEM;
  }

  @Override
  protected ISystemDescriptor[] newArray(int size)
  {
    return new ISystemDescriptor[size];
  }

  @Override
  public ISystemDescriptor getDescriptor(String systemName)
  {
    synchronized (this)
    {
      return descriptorsBySystemName.get(systemName);
    }
  }

  @Override
  public ISystemDescriptor getDescriptor(String systemName, long timeout) throws TimeoutRuntimeException
  {
    long deadline = java.lang.System.currentTimeMillis() + timeout;

    synchronized (this)
    {
      while (java.lang.System.currentTimeMillis() < deadline)
      {
        ISystemDescriptor descriptor = descriptorsBySystemName.get(systemName);
        if (descriptor != null)
        {
          return descriptor;
        }

        try
        {
          wait(10L);
        }
        catch (InterruptedException ex)
        {
          Thread.interrupted();
          break;
        }
      }

      throw new TimeoutRuntimeException("System " + systemName + " is not available after " + timeout + " milliseconds");
    }
  }

  @Override
  public ISystemDescriptor getDescriptor(EObject object)
  {
    if (object instanceof System)
    {
      System system = (System)object;
      String systemName = system.getName();
      return getDescriptor(systemName);
    }

    if (object instanceof SystemElement)
    {
      SystemElement systemElement = (SystemElement)object;
      System system = systemElement.getSystem();
      String systemName = system.getName();
      return getDescriptor(systemName);
    }

    IAssemblyDescriptor assemblyDescriptor = IAssemblyManager.INSTANCE.getDescriptor(object);
    if (assemblyDescriptor != null)
    {
      return assemblyDescriptor.getSystemDescriptor();
    }

    return getSessionProperty(object, SystemDescriptor::getSystemDescriptor);
  }

  @Override
  public String getModuleName(EObject object)
  {
    return getSessionProperty(object, SystemDescriptor::getModuleName);
  }

  public void fireDescriptorStateEvent(ISystemDescriptor systemDescriptor, System system, boolean open)
  {
    CDORepository repository = systemDescriptor.getSystemRepository();
    Properties properties = loadProperties(repository);
    if (Boolean.parseBoolean(properties.getProperty(PROP_OPEN, "false")) != open)
    {
      properties.put(PROP_OPEN, Boolean.toString(open));
      saveProperties(repository, properties);
    }

    fireEvent(new DescriptorStateEventImpl(this, systemDescriptor, system, open));
  }

  public void fireModuleCreatedEvent(SystemDescriptor systemDescriptor, Module newModule)
  {
    fireEvent(new ModuleCreatedEventImpl(this, systemDescriptor, newModule));
  }

  public void fireModuleDeletedEvent(SystemDescriptor systemDescriptor, CDOID deletedModuleID)
  {
    fireEvent(new ModuleDeletedEventImpl(this, systemDescriptor, deletedModuleID));
  }

  public void fireBaselineCreatedEvent(SystemDescriptor systemDescriptor, Baseline newBaseline)
  {
    fireEvent(new BaselineCreatedEventImpl(this, systemDescriptor, newBaseline));
  }

  public void fireStreamBranchChangedEvent(SystemDescriptor systemDescriptor, Stream stream, CDOBranchRef newBranch)
  {
    fireEvent(new StreamBranchChangedEventImpl(this, systemDescriptor, stream, newBranch));
  }

  public void scheduleOpenSystem(ISystemDescriptor descriptor)
  {
    String systemName = descriptor.getSystemName();
    schedule(new OMJob("Open " + systemName)
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          descriptor.open();
        }
        catch (Exception ex)
        {
          OM.LOG.error("Failed to open system " + systemName, ex);
        }

        return Status.OK_STATUS;
      }
    });
  }

  @Override
  protected void explorerElementAdded(CDORepository repository)
  {
    ISystemDescriptor descriptor = getDescriptor(repository);
    if (descriptor == null)
    {
      addDescriptor(repository);
    }
  }

  @Override
  protected void explorerElementRemoved(CDORepository repository)
  {
    removeDescriptor(repository);
  }

  protected void repositoryConnected(CDORepository repository)
  {
    ISystemDescriptor descriptor = getDescriptor(repository);
    if (descriptor == null)
    {
      addDescriptor(repository);
    }
  }

  protected void repositoryDisconnected(CDORepository repository)
  {
    ISystemDescriptor descriptor = getDescriptor(repository);
    if (descriptor != null && filterDescriptor(descriptor))
    {
      descriptor.close();
    }
  }

  @Override
  protected void notifyExplorerElementEvent(IEvent event)
  {
    if (event instanceof RepositoryConnectionEvent)
    {
      RepositoryConnectionEvent e = (RepositoryConnectionEvent)event;
      if (e.isConnected())
      {
        repositoryConnected(e.getRepository());
      }
      else
      {
        repositoryDisconnected(e.getRepository());
      }
    }
  }

  private void addDescriptor(CDORepository repository)
  {
    Properties properties = loadProperties(repository);
    if (properties == null)
    {
      scheduleAnalyzeRepository(repository);
      return;
    }

    ISystemDescriptor descriptor;
    boolean open;

    String systemName = properties.getProperty(PROP_SYSTEM_NAME);
    if (systemName == null)
    {
      descriptor = SystemDescriptor.NO_SYSTEM;
      open = false;
    }
    else
    {
      descriptor = new SystemDescriptor(repository, systemName);
      open = Boolean.parseBoolean(properties.getProperty(PROP_OPEN, "false"));

      repository.addKeyword("org.eclipse.emf.cdo.lm.System");
    }

    synchronized (this)
    {
      descriptors.put(repository, descriptor);

      if (filterDescriptor(descriptor))
      {
        ++count;
        descriptorsBySystemName.put(systemName, descriptor);
        notifyAll();
      }
    }

    if (filterDescriptor(descriptor))
    {
      fireElementAddedEvent(descriptor);
    }

    if (open)
    {
      scheduleOpenSystem(descriptor);
    }
  }

  private void removeDescriptor(CDORepository repository)
  {
    ISystemDescriptor descriptor;
    synchronized (this)
    {
      descriptor = descriptors.remove(repository);
      if (descriptor != null && filterDescriptor(descriptor))
      {
        --count;
        descriptorsBySystemName.remove(descriptor.getSystemName());
        notifyAll();
      }
    }

    if (descriptor != null && filterDescriptor(descriptor))
    {
      fireElementRemovedEvent(descriptor);
    }
  }

  /**
   * Returns the system name if the repository contains a system, or
   * <code>null</code> otherwise.
   *
   * @throws RepositoryConnectionException
   *             to indicate that a system name (or the
   *             absence of it) could not be determined.
   */
  private String querySystemName(CDORepository repository) throws RepositoryConnectionException
  {
    synchronized (repository)
    {
      CDOSession session = null;

      try
      {
        session = repository.acquireSession();
        CDOView view = session.openView();

        CDOResource resource = view.getResource(System.RESOURCE_PATH);
        EList<EObject> contents = resource.getContents();
        if (!contents.isEmpty())
        {
          EObject root = contents.get(0);
          if (root instanceof System)
          {
            System system = (System)root;
            return system.getName();
          }
        }
      }
      catch (CDOResourceNodeNotFoundException | InvalidURIException | NoPermissionException ex)
      {
        // $FALL-THROUGH$
      }
      catch (Exception ex)
      {
        throw new RepositoryConnectionException(ex);
      }
      finally
      {
        if (session != null)
        {
          repository.releaseSession();
        }
      }

      return null;
    }
  }

  private void scheduleAnalyzeRepository(CDORepository repository)
  {
    if (analyzingRepositories.add(repository))
    {
      schedule(new OMJob("Analyze " + repository.getName())
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          try
          {
            while (!repository.isConnected())
            {
              if (monitor.isCanceled())
              {
                return Status.CANCEL_STATUS;
              }

              ConcurrencyUtil.sleep(100);
            }

            Properties properties = new Properties();

            String systemName = querySystemName(repository);
            if (systemName != null)
            {
              properties.put(PROP_SYSTEM_NAME, systemName);
              properties.put(PROP_OPEN, StringUtil.FALSE);
            }

            saveProperties(repository, properties);
            addDescriptor(repository);
          }
          catch (RepositoryConnectionException ex)
          {
            // Server or network may be down. At this point it can't be determined whether
            // the repository contains a system or not. Do not register a descriptor now.
            OM.LOG.info(ex);
          }
          finally
          {
            analyzingRepositories.remove(repository);
          }

          return Status.OK_STATUS;
        }
      });
    }
  }

  private static void schedule(OMJob job)
  {
    job.setSystem(true);
    job.schedule();
  }

  private static <T> T getSessionProperty(EObject object, Function<CDOSession, T> function)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOView view = cdoObject.cdoView();
      if (view != null)
      {
        CDOSession session = view.getSession();
        if (session != null)
        {
          return function.apply(session);
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class SystemEventImpl extends Event implements SystemEvent
  {
    private static final long serialVersionUID = 1L;

    private final ISystemDescriptor systemDescriptor;

    public SystemEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor)
    {
      super(manager);
      this.systemDescriptor = systemDescriptor;
    }

    @Override
    public final ISystemDescriptor getSystemDescriptor()
    {
      return systemDescriptor;
    }

    @Override
    public System getSystem()
    {
      return systemDescriptor.getSystem();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "system=" + systemDescriptor;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DescriptorStateEventImpl extends SystemEventImpl implements DescriptorStateEvent
  {
    private static final long serialVersionUID = 1L;

    private final System system;

    private final boolean open;

    public DescriptorStateEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor, System system, boolean open)
    {
      super(manager, systemDescriptor);
      this.system = system;
      this.open = open;
    }

    @Override
    public System getSystem()
    {
      return system;
    }

    @Override
    public boolean isOpen()
    {
      return open;
    }

    @Override
    protected String formatEventName()
    {
      return DescriptorStateEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", open=" + open;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ModuleCreatedEventImpl extends SystemEventImpl implements ModuleCreatedEvent
  {
    private static final long serialVersionUID = 1L;

    private final Module newModule;

    public ModuleCreatedEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor, Module newModule)
    {
      super(manager, systemDescriptor);
      this.newModule = newModule;
    }

    @Override
    public Module getNewModule()
    {
      return newModule;
    }

    @Override
    protected String formatEventName()
    {
      return ModuleCreatedEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", module=" + newModule.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ModuleDeletedEventImpl extends SystemEventImpl implements ModuleDeletedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOID deletedModuleID;

    public ModuleDeletedEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor, CDOID deletedModuleID)
    {
      super(manager, systemDescriptor);
      this.deletedModuleID = deletedModuleID;
    }

    @Override
    public CDOID getDeletedModuleID()
    {
      return deletedModuleID;
    }

    @Override
    protected String formatEventName()
    {
      return ModuleDeletedEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", deletedModuleID=" + deletedModuleID;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BaselineCreatedEventImpl extends SystemEventImpl implements BaselineCreatedEvent
  {
    private static final long serialVersionUID = 1L;

    private final Baseline newBaseline;

    public BaselineCreatedEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor, Baseline newBaseline)
    {
      super(manager, systemDescriptor);
      this.newBaseline = newBaseline;
    }

    @Override
    public Baseline getNewBaseline()
    {
      return newBaseline;
    }

    @Override
    protected String formatEventName()
    {
      return BaselineCreatedEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", baseline=" + newBaseline.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class StreamBranchChangedEventImpl extends SystemEventImpl implements StreamBranchChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final Stream stream;

    private final CDOBranchRef newBranch;

    public StreamBranchChangedEventImpl(SystemManager manager, ISystemDescriptor systemDescriptor, Stream stream, CDOBranchRef newBranch)
    {
      super(manager, systemDescriptor);
      this.stream = stream;
      this.newBranch = newBranch;
    }

    @Override
    public Stream getStream()
    {
      return stream;
    }

    @Override
    public CDOBranchRef getNewBranch()
    {
      return newBranch;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", stream=" + stream.getName() + ", newBranch=" + newBranch;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RepositoryConnectionException extends Exception
  {
    private static final long serialVersionUID = 1L;

    public RepositoryConnectionException(Throwable cause)
    {
      super(cause);
    }
  }
}
