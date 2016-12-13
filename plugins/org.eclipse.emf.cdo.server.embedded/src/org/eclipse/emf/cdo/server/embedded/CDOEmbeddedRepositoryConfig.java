package org.eclipse.emf.cdo.server.embedded;

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.embedded.EmbeddedRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public abstract class CDOEmbeddedRepositoryConfig extends Lifecycle
{
  private final String repositoryName;

  private EmbeddedRepository repository;

  public CDOEmbeddedRepositoryConfig(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public final CDOSession openClientSession()
  {
    return repository.openClientSession();
  }

  /**
   * Subclasses may override.
   */
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  /**
   * Subclasses may override.
   */
  public void initPackages(IRepository repository, List<EPackage> packages)
  {
    for (String nsURI : new HashSet<String>(EPackage.Registry.INSTANCE.keySet()))
    {
      if (isInitialPackage(repository, nsURI))
      {
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
        packages.add(ePackage);
      }
    }
  }

  /**
   * Subclasses may override.
   */
  public boolean isInitialPackage(IRepository repository, String nsURI)
  {
    return false;
  }

  /**
   * Subclasses may override.
   */
  public void afterFirstStart(IRepository repository)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  public void afterReStart(IRepository repository)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  public void modifySessionConfiguration(IRepository repository, CDONet4jSessionConfiguration config)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  public void modifySession(IRepository repository, CDONet4jSession session)
  {
    // Do nothing.
  }

  public abstract IStore createStore();

  public abstract void initProperties(Map<String, String> properties);

  @Override
  protected void doActivate() throws Exception
  {
    // Initialize container.
    IManagedContainer container = getContainer();

    // Initialize store.
    IStore store = createStore();

    // Initialize properties.
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(IRepository.Props.OVERRIDE_UUID, "");
    initProperties(properties);

    repository = new EmbeddedRepository(this);
    ((InternalRepository)repository).setContainer(container);
    ((InternalRepository)repository).setName(repositoryName);
    ((InternalRepository)repository).setStore((InternalStore)store);
    ((InternalRepository)repository).setProperties(properties);
    LifecycleUtil.activate(repository);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(repository);
    repository = null;
  }
}
