package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class ContainerRepositoryProvider implements IRepositoryProvider
{
  private IManagedContainer container;

  public ContainerRepositoryProvider(IManagedContainer container)
  {
    this.container = container;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public IRepository getRepository(String name)
  {
    return RepositoryFactory.get(container, name);
  }
}