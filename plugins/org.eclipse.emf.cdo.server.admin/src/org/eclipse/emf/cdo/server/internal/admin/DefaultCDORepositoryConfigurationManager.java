/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.lob.CDOClobWriter;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.admin.CDORepositoryConfigurationManager;
import org.eclipse.emf.cdo.server.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogFactory;
import org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog;
import org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.ISecurityManager.RealmOperation;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.spi.server.AuthenticationUtil;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension2;
import org.eclipse.emf.cdo.spi.server.IAuthenticationProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IAuthenticator2;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A default implementation of the {@link CDORepositoryConfigurationManager} interface
 * that stores dynamically created repositories' configurations in XML files.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class DefaultCDORepositoryConfigurationManager extends Lifecycle implements InternalCDORepositoryConfigurationManager, ContainerAware
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DefaultCDORepositoryConfigurationManager.class);

  private IManagedContainer container = IPluginContainer.INSTANCE;

  private InternalRepository adminRepository;

  private String catalogPath = DEFAULT_CATALOG_PATH;

  private RepositoryCatalog catalog;

  private ISecurityManager securityManager;

  private final IListener repositoryListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onActivated(ILifecycle lifecycle)
    {
      initializeCatalog();
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      Exception exception = DefaultCDORepositoryConfigurationManager.this.deactivate();
      if (exception != null)
      {
        OM.LOG.error(exception);
      }
    }
  };

  public DefaultCDORepositoryConfigurationManager()
  {
  }

  public IManagedContainer getManagedContainer()
  {
    return container;
  }

  @Override
  public void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public void setAdminRepository(InternalRepository adminRepository)
  {
    this.adminRepository = adminRepository;
    if (isActive())
    {
      initializeCatalog();
    }
  }

  public void setCatalogPath(String catalogPath)
  {
    this.catalogPath = StringUtil.isEmpty(catalogPath) ? DEFAULT_CATALOG_PATH : catalogPath;
  }

  @Override
  public IRepository addRepository(String name, Document configurationXML)
  {
    checkActive();
    IManagedContainer container = requireContainer();

    if (getRepository(container, name) != null)
    {
      throw new IllegalArgumentException("Repository already exists: " + name); //$NON-NLS-1$
    }

    try
    {
      RepositoryConfiguration configuration = createConfiguration(name, configurationXML);
      return startRepository(configuration);
    }
    catch (RuntimeException e)
    {
      OM.LOG.error(e);
      throw e;
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      throw WrappedException.wrap(e);
    }
  }

  @Override
  public void removeRepository(IRepository repository)
  {
    checkActive();
    deleteConfiguration(repository.getName());
  }

  @Override
  public boolean canRemoveRepository(IRepository repository)
  {
    checkActive();

    return isInCatalog(repository.getName());
  }

  @Override
  public Map<String, IRepository> getRepositories()
  {
    checkActive();

    Map<String, IRepository> result = new java.util.HashMap<>();
    if (catalog != null)
    {
      for (RepositoryConfiguration configuration : catalog.getRepositories())
      {
        IRepository repository = getRepository(getManagedContainer(), configuration.getName());
        if (repository != null)
        {
          result.put(repository.getName(), repository);
        }
      }
    }

    return result;
  }

  @Override
  public void authenticateAdministrator()
  {
    requireSecurityManager();

    IAuthenticationProtocol authProtocol = AuthenticationUtil.getAuthenticationProtocol();
    if (authProtocol != null)
    {
      InternalSessionManager sessionManager = adminRepository.getSessionManager();
      String userID = sessionManager.authenticateUser(authProtocol);

      // If the user could authenticate without an ID, then there is no security manager
      if (userID != null)
      {
        IAuthenticator2 auth = ObjectUtil.tryCast(sessionManager.getAuthenticator(), IAuthenticator2.class);
        if (auth != null && !auth.isAdministrator(userID))
        {
          throw new SecurityException("Must be a server administrator to add or delete repositories.");
        }
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Starting dynamically managed repositories.");
    }

    initializeCatalog();
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Stopping dynamically managed repositories.");
    }

    // Stop any repositories that I manage
    for (IRepository repository : getRepositories().values())
    {
      try
      {
        LifecycleUtil.deactivate(repository);
      }
      catch (Exception e)
      {
        OM.LOG.error(e);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    catalog = null;
  }

  protected IManagedContainer requireContainer()
  {
    IManagedContainer result = getManagedContainer();
    if (result == null)
    {
      throw new IllegalStateException("No container."); //$NON-NLS-1$
    }

    return result;
  }

  protected IRepository startRepository(RepositoryConfiguration configuration) throws Exception
  {
    RepositoryConfigurator configurator = new RepositoryConfigurator(requireContainer());
    IRepository[] result = configurator.configure(configuration.getConfigXML().getContents());
    if (result.length == 1)
    {
      startExtensions(result[0], configuration);
    }

    return result.length == 0 ? null : result[0];
  }

  protected final ISecurityManager requireSecurityManager()
  {
    if (securityManager == null)
    {
      throw new IllegalStateException("The administrative repository does not have a security manager.");
    }

    return securityManager;
  }

  protected <T> T modify(CatalogOperation<T> operation)
  {
    return modify(operation, false);
  }

  protected <T> T modify(final CatalogOperation<T> operation, boolean waitUntilReadable)
  {
    checkActive();

    final Object[] result = new Object[1];
    requireSecurityManager().modify(new RealmOperation()
    {

      @Override
      public void execute(Realm realm)
      {
        try
        {
          RepositoryCatalog localCatalog = realm.cdoView().getObject(catalog);
          result[0] = operation.execute(localCatalog);
        }
        catch (Exception e)
        {
          throw WrappedException.wrap(e);
        }
      }
    }, waitUntilReadable);

    // This cast is known to be safe according to the operation signature
    @SuppressWarnings("unchecked")
    T resultAsT = (T)result[0];
    return resultAsT;
  }

  private IRepository getRepository(IManagedContainer container, String name)
  {
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof IRepository)
      {
        IRepository repository = (IRepository)element;
        if (repository.getName().equals(name))
        {
          return repository;
        }
      }
    }

    return null;
  }

  private void startExtensions(IRepository repository, RepositoryConfiguration configuration)
  {
    final List<IAppExtension2> extensions = new ArrayList<>(3);

    IExtensionRegistry registry = Platform.getExtensionRegistry();
    @SuppressWarnings("restriction")
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(org.eclipse.emf.cdo.internal.server.bundle.OM.BUNDLE_ID, IAppExtension.EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("appExtension".equals(element.getName())) //$NON-NLS-1$
      {
        try
        {
          IAppExtension extension = (IAppExtension)element.createExecutableExtension("class"); //$NON-NLS-1$
          if (extension instanceof IAppExtension2)
          {
            IAppExtension2 extension2 = (IAppExtension2)extension;
            extension2.startDynamic(configuration.getConfigXML().getContents());
            extensions.add(extension2);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    if (!extensions.isEmpty())
    {
      // I have added some extensions, so stop them if and when the repository is shut down
      repository.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          for (IAppExtension extension : extensions)
          {
            try
            {
              extension.stop();
            }
            catch (Exception e)
            {
              OM.LOG.error(e);
            }
          }
        }
      });
    }
  }

  private RepositoryConfiguration createConfiguration(final String repositoryName, final Document configuration) throws Exception
  {
    modify(new CatalogOperation<RepositoryConfiguration>()
    {
      @Override
      public RepositoryConfiguration execute(RepositoryCatalog catalog) throws Exception
      {
        RepositoryConfiguration result = CatalogFactory.eINSTANCE.createRepositoryConfiguration();
        result.setName(repositoryName);
        CDOClobWriter writer = CDOLobUtil.createClobWriter();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        StreamResult streamResult = new StreamResult(writer);
        DOMSource source = new DOMSource(configuration);
        transformer.transform(source, streamResult);
        writer.close();
        result.setConfigXML(writer.getClob());
        catalog.getRepositories().add(result);

        return result;
      }
    }, true);

    return catalog.getRepository(repositoryName);
  }

  private void deleteConfiguration(final String repositoryName)
  {
    modify(new CatalogOperation<Void>()
    {
      @Override
      public Void execute(RepositoryCatalog catalog) throws Exception
      {
        RepositoryConfiguration configuration = catalog.getRepository(repositoryName);

        if (configuration != null)
        {
          EcoreUtil.remove(configuration);
        }

        return null;
      }
    });
  }

  private boolean isInCatalog(String repositoryName)
  {
    return catalog.getRepository(repositoryName) != null;
  }

  private void initializeCatalog()
  {
    if (catalog != null)
    {
      // Already initialized
      return;
    }

    if (adminRepository == null)
    {
      // Cannot initialize
      return;
    }

    adminRepository.addListener(repositoryListener);
    if (!LifecycleUtil.isActive(adminRepository))
    {
      // Cannot initialize now
      return;
    }

    securityManager = SecurityManagerUtil.getSecurityManager(adminRepository);
    if (securityManager == null)
    {
      // We are initializing ahead of the Security Manager. Wait for it
      getManagedContainer().addListener(new ContainerEventAdapter<Object>()
      {
        @Override
        protected void onAdded(IContainer<Object> container, Object element)
        {
          if (element instanceof ISecurityManager)
          {
            ISecurityManager securityManager = (ISecurityManager)element;
            if (securityManager.getRepository() == adminRepository)
            {
              // This is my repository's security manager. Finish initialization
              try
              {
                initializeCatalog();
              }
              finally
              {
                // Don't need any further events
                container.removeListener(this);
              }
            }
          }
        }
      });

      // Double-check
      securityManager = SecurityManagerUtil.getSecurityManager(adminRepository);
      if (securityManager == null)
      {
        return;
      }
    }

    requireSecurityManager().modify(new RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        CDOTransaction initialTransaction = (CDOTransaction)realm.cdoView();
        boolean firstTime = !initialTransaction.hasResource(catalogPath);
        if (firstTime)
        {
          CDOResource resource = initialTransaction.createResource(catalogPath);
          catalog = createCatalog(resource, realm);
          OM.LOG.info("Repository catalog created in " + catalogPath);
        }
        else
        {
          CDOResource resource = initialTransaction.getResource(catalogPath);
          catalog = (RepositoryCatalog)resource.getContents().get(0);
          OM.LOG.info("Repository catalog loaded from " + catalogPath);
        }
      }
    });

    // Get the read-only view of the catalog now from the security manager's view
    requireSecurityManager().read(new RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        catalog = realm.cdoView().getObject(catalog);
      }
    });

    // And load our repositories
    if (catalog != null)
    {
      OM.LOG.info("Starting managed repositories.");

      for (RepositoryConfiguration configuration : catalog.getRepositories())
      {
        try
        {
          startRepository(configuration);
        }
        catch (Exception e)
        {
          OM.LOG.error(e);
        }
      }

      OM.LOG.info("Managed repositories started.");
    }
  }

  private RepositoryCatalog createCatalog(CDOResource resource, Realm realm)
  {
    RepositoryCatalog result = CatalogFactory.eINSTANCE.createRepositoryCatalog();
    resource.getContents().add(result);

    // Give the Administrator read access to the catalog
    Role serverAdmin = realm.addRole("Server Administration"); //$NON-NLS-1$
    FilterPermission catalogAccess = SecurityFactory.eINSTANCE.createFilterPermission(Access.READ,
        SecurityFactory.eINSTANCE.createResourceFilter(resource.getPath()));
    serverAdmin.getPermissions().add(catalogAccess);
    realm.getUser(User.ADMINISTRATOR).getRoles().add(serverAdmin);

    return result;
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static class Factory extends CDORepositoryConfigurationManager.Factory
  {
    public static final String TYPE = "default"; //$NON-NLS-1$

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public CDORepositoryConfigurationManager create(String description) throws ProductCreationException
    {
      DefaultCDORepositoryConfigurationManager result = new DefaultCDORepositoryConfigurationManager();
      result.setCatalogPath(description);
      return result;
    }
  }

  protected static interface CatalogOperation<T>
  {
    public T execute(RepositoryCatalog catalog) throws Exception;
  }
}
