/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Lothar Werzinger - support for configuring user managers
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryFactory;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IUserManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class RepositoryConfigurator
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, RepositoryConfigurator.class);

  private IManagedContainer container;

  public RepositoryConfigurator()
  {
    this(null);
  }

  public RepositoryConfigurator(IManagedContainer container)
  {
    this.container = container;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public IRepository[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException,
      CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring CDO server from " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    List<IRepository> repositories = new ArrayList<IRepository>();
    Document document = getDocument(configFile);
    NodeList elements = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element repositoryConfig = (Element)elements.item(i);
      IRepository repository = getRepository(repositoryConfig);
      repositories.add(repository);

      if (container != null)
      {
        CDOServerUtil.addRepository(container, repository);
      }
    }

    return repositories.toArray(new IRepository[repositories.size()]);
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected IRepositoryFactory getRepositoryFactory(String type) throws CoreException
  {
    IRepositoryFactory factory = (IRepositoryFactory)createExecutableExtension("repositoryFactories", //$NON-NLS-1$
        "repositoryFactory", "repositoryType", type); //$NON-NLS-1$ //$NON-NLS-2$
    if (factory == null)
    {
      throw new IllegalStateException("CDORepositoryInfo factory not found: " + type); //$NON-NLS-1$
    }

    return factory;
  }

  protected IRepository getRepository(Element repositoryConfig) throws CoreException
  {
    String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new IllegalArgumentException("CDORepositoryInfo name is missing or empty"); //$NON-NLS-1$
    }

    String repositoryType = repositoryConfig.getAttribute("type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(repositoryType))
    {
      repositoryType = RepositoryFactory.TYPE;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Configuring repository {0} (type={1})", repositoryName, repositoryType); //$NON-NLS-1$
    }

    Map<String, String> properties = getProperties(repositoryConfig, 1);

    Element storeConfig = getStoreConfig(repositoryConfig);
    IStore store = createStore(repositoryName, properties, storeConfig);

    InternalRepository repository = (InternalRepository)getRepository(repositoryType);
    repository.setName(repositoryName);
    repository.setStore((InternalStore)store);
    repository.setProperties(properties);

    Element userManagerConfig = getUserManagerConfig(repositoryConfig);
    if (userManagerConfig != null)
    {
      IUserManager userManager = getUserManager(userManagerConfig);
      if (userManager != null)
      {
        InternalSessionManager sessionManager = repository.getSessionManager();
        if (sessionManager == null)
        {
          sessionManager = new SessionManager();
          repository.setSessionManager(sessionManager);
        }

        sessionManager.setUserManager(userManager);
      }
    }

    return repository;
  }

  protected IRepository getRepository(String repositoryType) throws CoreException
  {
    IRepositoryFactory factory = getRepositoryFactory(repositoryType);
    return factory.createRepository();
  }

  protected Element getUserManagerConfig(Element repositoryConfig)
  {
    NodeList userManagerConfig = repositoryConfig.getElementsByTagName("userManager"); //$NON-NLS-1$
    if (userManagerConfig.getLength() > 1)
    {
      String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
      throw new IllegalStateException("At most one user manager must be configured for repository " + repositoryName); //$NON-NLS-1$
    }

    return (Element)(userManagerConfig.getLength() > 0 ? userManagerConfig.item(0) : null);
  }

  protected IUserManager getUserManager(Element userManagerConfig) throws CoreException
  {
    String type = userManagerConfig.getAttribute("type"); //$NON-NLS-1$
    String description = userManagerConfig.getAttribute("description"); //$NON-NLS-1$
    return getUserManager(type, description);
  }

  protected IUserManager getUserManager(String type, String description) throws CoreException
  {
    IUserManager userManager = (IUserManager)container.getElement("org.eclipse.net4j.userManagers", type, description); //$NON-NLS-1$
    if (userManager == null)
    {
      throw new IllegalStateException("UserManager factory not found: " + type); //$NON-NLS-1$
    }

    return userManager;
  }

  protected Element getStoreConfig(Element repositoryConfig)
  {
    NodeList storeConfigs = repositoryConfig.getElementsByTagName("store"); //$NON-NLS-1$
    // if (storeConfigs.getLength() != 1)
    // {
    //      String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
    //      throw new IllegalStateException("Exactly one store must be configured for repository " + repositoryName); //$NON-NLS-1$
    // }

    return (Element)storeConfigs.item(0);
  }

  protected IStoreFactory getStoreFactory(String type) throws CoreException
  {
    IStoreFactory factory = (IStoreFactory)createExecutableExtension("storeFactories", "storeFactory", "storeType", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        type);
    if (factory == null)
    {
      throw new IllegalStateException("Store factory not found: " + type); //$NON-NLS-1$
    }

    return factory;
  }

  protected IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
      throws CoreException
  {
    String type = storeConfig.getAttribute("type"); //$NON-NLS-1$
    IStoreFactory storeFactory = getStoreFactory(type);
    return storeFactory.createStore(repositoryName, repositoryProperties, storeConfig);
  }

  public static Map<String, String> getProperties(Element element, int levels)
  {
    Map<String, String> properties = new HashMap<String, String>();
    collectProperties(element, "", properties, levels); //$NON-NLS-1$
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties, int levels)
  {
    if ("property".equals(element.getNodeName())) //$NON-NLS-1$
    {
      String name = element.getAttribute("name"); //$NON-NLS-1$
      String value = element.getAttribute("value"); //$NON-NLS-1$
      properties.put(prefix + name, value);
      prefix += name + "."; //$NON-NLS-1$
    }

    if (levels > 0)
    {
      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if (childNode instanceof Element)
        {
          collectProperties((Element)childNode, prefix, properties, levels - 1);
        }
      }
    }
  }

  private static Object createExecutableExtension(String extPointName, String elementName, String attributeName,
      String type) throws CoreException
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, extPointName);
    for (IConfigurationElement element : elements)
    {
      if (ObjectUtil.equals(element.getName(), elementName))
      {
        String storeType = element.getAttribute(attributeName);
        if (ObjectUtil.equals(storeType, type))
        {
          return element.createExecutableExtension("class"); //$NON-NLS-1$
        }
      }
    }

    return null;
  }
}
