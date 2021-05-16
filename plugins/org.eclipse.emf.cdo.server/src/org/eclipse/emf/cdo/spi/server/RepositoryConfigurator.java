/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Lothar Werzinger - support for configuring user managers
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryFactory;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.IStoreFactory.ParameterAware;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.PropertiesFactory;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.AuthenticatorFactory;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.UserManagerFactory;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class RepositoryConfigurator implements IManagedContainerProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, RepositoryConfigurator.class);

  private final IManagedContainer container;

  private final Map<String, IRepositoryFactory> repositoryFactories = new HashMap<>();

  private final Map<String, IStoreFactory> storeFactories = new HashMap<>();

  private final Map<String, String> parameters = new HashMap<>();

  public RepositoryConfigurator()
  {
    this(null);
  }

  public RepositoryConfigurator(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  public Map<String, IRepositoryFactory> getRepositoryFactories()
  {
    return repositoryFactories;
  }

  public Map<String, IStoreFactory> getStoreFactories()
  {
    return storeFactories;
  }

  /**
   * @since 4.10
   */
  public String getParameter(String key)
  {
    return parameters.get(key);
  }

  /**
   * @since 4.10
   */
  public String setParameter(String key, String value)
  {
    if (value == null)
    {
      return parameters.remove(key);
    }

    return parameters.put(key, value);
  }

  public IRepository[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException, CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring CDO server from " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    return configure(getDocument(configFile));
  }

  /**
   * @since 4.3
   */
  public IRepository[] configure(Reader configReader) throws ParserConfigurationException, SAXException, IOException, CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring CDO server from dynamic configuration"); //$NON-NLS-1$
    }

    return configure(getDocument(configReader));
  }

  /**
   * @since 4.3
   */
  protected IRepository[] configure(Document document) throws ParserConfigurationException, SAXException, IOException, CoreException
  {
    List<IRepository> repositories = new ArrayList<>();
    NodeList elements = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element repositoryConfig = (Element)elements.item(i);
      IRepository repository = getRepository(repositoryConfig);
      repositories.add(repository);

      if (container != null)
      {
        CDOServerUtil.addRepository(container, repository);
        OM.LOG.info("CDO repository " + repository.getName() + " started");
      }
      else
      {
        OM.LOG.info("CDO repository " + repository.getName() + " added");
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

  /**
   * @since 4.3
   */
  protected Document getDocument(Reader configReader) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(configReader));
  }

  protected IRepositoryFactory getRepositoryFactory(String type) throws CoreException
  {
    IRepositoryFactory factory = repositoryFactories.get(type);
    if (factory == null)
    {
      factory = createExecutableExtension("repositoryFactories", "repositoryFactory", //$NON-NLS-1$ //$NON-NLS-2$
          "repositoryType", type); //$NON-NLS-1$
    }

    if (factory == null)
    {
      throw new IllegalStateException("Repository factory not found: " + type); //$NON-NLS-1$
    }

    return factory;
  }

  protected IRepository getRepository(Element repositoryConfig) throws CoreException
  {
    String repositoryName = getAttribute(repositoryConfig, "name"); //$NON-NLS-1$
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new IllegalArgumentException("Repository name is missing or empty"); //$NON-NLS-1$
    }

    String repositoryType = getAttribute(repositoryConfig, "type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(repositoryType))
    {
      repositoryType = RepositoryFactory.TYPE;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Configuring repository {0} (type={1})", repositoryName, repositoryType); //$NON-NLS-1$
    }

    Map<String, String> properties = getProperties(repositoryConfig, 1, parameters);

    Element storeConfig = getStoreConfig(repositoryConfig);
    IStore store = createStore(repositoryName, properties, storeConfig);

    InternalRepository repository = (InternalRepository)getRepository(repositoryType);
    repository.setName(repositoryName);
    repository.setStore((InternalStore)store);
    repository.setProperties(properties);

    setUserManager(repository, repositoryConfig);
    setAuthenticator(repository, repositoryConfig);
    setActivityLog(repository, repositoryConfig);

    EPackage[] initialPackages = getInitialPackages(repositoryConfig);
    if (initialPackages.length != 0)
    {
      repository.setInitialPackages(initialPackages);
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
    return getChildElement(repositoryConfig, "userManager"); //$NON-NLS-1$
  }

  protected IUserManager getUserManager(Element userManagerConfig) throws CoreException
  {
    String type = getAttribute(userManagerConfig, "type"); //$NON-NLS-1$
    String description = getAttribute(userManagerConfig, "description"); //$NON-NLS-1$
    return getUserManager(type, description);
  }

  protected IUserManager getUserManager(String type, String description) throws CoreException
  {
    IUserManager userManager = (IUserManager)container.getElement(UserManagerFactory.PRODUCT_GROUP, type, description);
    if (userManager == null)
    {
      throw new IllegalStateException("UserManager factory not found: " + type); //$NON-NLS-1$
    }

    return userManager;
  }

  /**
   * @since 4.2
   */
  @SuppressWarnings("deprecation")
  protected void setUserManager(InternalRepository repository, Element repositoryConfig) throws CoreException
  {
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
  }

  /**
   * @since 4.2
   */
  protected Element getAuthenticatorConfig(Element repositoryConfig)
  {
    return getChildElement(repositoryConfig, "authenticator"); //$NON-NLS-1$
  }

  /**
   * @since 4.2
   */
  protected IAuthenticator getAuthenticator(Element authenticatorConfig) throws CoreException
  {
    String type = getAttribute(authenticatorConfig, "type"); //$NON-NLS-1$
    String description = getAttribute(authenticatorConfig, "description"); //$NON-NLS-1$
    return getAuthenticator(type, description);
  }

  /**
   * @since 4.2
   */
  protected IAuthenticator getAuthenticator(String type, String description) throws CoreException
  {
    IAuthenticator authenticator = (IAuthenticator)container.getElement(AuthenticatorFactory.PRODUCT_GROUP, type, description);
    if (authenticator == null)
    {
      throw new IllegalStateException("Authenticator factory not found: " + type); //$NON-NLS-1$
    }

    return authenticator;
  }

  /**
   * @since 4.2
   */
  protected void setAuthenticator(InternalRepository repository, Element repositoryConfig) throws CoreException
  {
    Element authenticatorConfig = getAuthenticatorConfig(repositoryConfig);
    if (authenticatorConfig != null)
    {
      IAuthenticator authenticator = getAuthenticator(authenticatorConfig);
      if (authenticator != null)
      {
        InternalSessionManager sessionManager = repository.getSessionManager();
        if (sessionManager == null)
        {
          sessionManager = new SessionManager();
          repository.setSessionManager(sessionManager);
        }

        sessionManager.setAuthenticator(authenticator);
      }
    }
  }

  /**
   * @since 4.7
   */
  protected void setActivityLog(InternalRepository repository, Element repositoryConfig)
  {
    Element activityLogElement = getChildElement(repositoryConfig, "activityLog"); //$NON-NLS-1$
    if (activityLogElement != null)
    {
      RepositoryActivityLog activityLog = getContainerElement(activityLogElement, RepositoryActivityLog.Rolling.Factory.TYPE);
      activityLog.setRepository(repository);
    }
  }

  protected EPackage[] getInitialPackages(Element repositoryConfig)
  {
    List<EPackage> result = new ArrayList<>();

    NodeList children = repositoryConfig.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE)
      {
        Element childElement = (Element)child;
        if (childElement.getNodeName().equalsIgnoreCase("initialPackage"))//$NON-NLS-1$
        {
          String nsURI = getAttribute(childElement, "nsURI"); //$NON-NLS-1$
          if (nsURI == null)
          {
            throw new IllegalStateException("nsURI missing for initialPackage element"); //$NON-NLS-1$
          }

          EPackage initialPackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
          if (initialPackage == null)
          {
            throw new IllegalStateException("Initial package not found in global package registry: " + nsURI); //$NON-NLS-1$
          }

          result.add(initialPackage);
        }
      }
    }

    return result.toArray(new EPackage[result.size()]);
  }

  protected Element getStoreConfig(Element repositoryConfig)
  {
    Element storeElement = getChildElement(repositoryConfig, "store"); //$NON-NLS-1$
    if (storeElement == null)
    {
      String repositoryName = getAttribute(repositoryConfig, "name"); //$NON-NLS-1$
      throw new IllegalStateException("A store must be configured for repository " + repositoryName); //$NON-NLS-1$
    }

    return storeElement;
  }

  protected IStoreFactory getStoreFactory(String type) throws CoreException
  {
    IStoreFactory factory = storeFactories.get(type);
    if (factory == null)
    {
      factory = createExecutableExtension("storeFactories", "storeFactory", "storeType", type); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    if (factory == null)
    {
      throw new IllegalStateException("Store factory not found: " + type); //$NON-NLS-1$
    }

    return factory;
  }

  protected IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig) throws CoreException
  {
    String type = getAttribute(storeConfig, "type"); //$NON-NLS-1$
    IStoreFactory storeFactory = getStoreFactory(type);

    if (storeFactory instanceof ParameterAware)
    {
      ((ParameterAware)storeFactory).setParameters(parameters);
    }

    return storeFactory.createStore(repositoryName, repositoryProperties, storeConfig);
  }

  /**
   * @since 4.7
   */
  protected <T> T getContainerElement(Element element, String defaultType)
  {
    String type = getAttribute(element, "type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(type))
    {
      type = defaultType;
    }

    String description = getAttribute(element, "description"); //$NON-NLS-1$
    if (StringUtil.isEmpty(description))
    {
      Map<String, String> properties = getProperties(element, 1, parameters);
      description = PropertiesFactory.createDescription(properties);
    }

    @SuppressWarnings("unchecked")
    T containerElement = (T)container.getElement(RepositoryActivityLog.Factory.PRODUCT_GROUP, type, description);

    return containerElement;
  }

  /**
   * @since 4.10
   */
  protected Element getChildElement(Element element, String name)
  {
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE)
      {
        Element childElement = (Element)child;
        if (childElement.getNodeName().equalsIgnoreCase(name))
        {
          return childElement;
        }
      }
    }

    return null;
  }

  /**
   * @since 4.10
   */
  protected String getAttribute(Element element, String name)
  {
    String value = element.getAttribute(name);
    value = substituteParameters(value, parameters);
    return value;
  }

  /**
   * @since 4.10
   */
  public static String substituteParameters(String value, Map<String, String> parameters)
  {
    if (value != null && parameters != null)
    {
      for (Entry<String, String> entry : parameters.entrySet())
      {
        value = value.replace(entry.getKey(), entry.getValue());
      }
    }

    return value;
  }

  public static Map<String, String> getProperties(Element element, int levels)
  {
    return getProperties(element, levels, null);
  }

  /**
   * @since 4.10
   */
  public static Map<String, String> getProperties(Element element, int levels, Map<String, String> parameters)
  {
    Map<String, String> properties = new HashMap<>();
    collectProperties(element, "", properties, levels, parameters); //$NON-NLS-1$
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties, int levels, Map<String, String> parameters)
  {
    if ("property".equals(element.getNodeName())) //$NON-NLS-1$
    {
      String name = element.getAttribute("name"); //$NON-NLS-1$
      String value = element.getAttribute("value"); //$NON-NLS-1$

      value = substituteParameters(value, parameters);

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
          collectProperties((Element)childNode, prefix, properties, levels - 1, parameters);
        }
      }
    }
  }

  private static <T> T createExecutableExtension(String extPointName, String elementName, String attributeName, String type) throws CoreException
  {
    if (OMPlatform.INSTANCE.isExtensionRegistryAvailable())
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
            @SuppressWarnings("unchecked")
            T result = (T)element.createExecutableExtension("class"); //$NON-NLS-1$
            return result;
          }
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   * @since 4.7
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory implements ContainerAware
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryConfigurators"; //$NON-NLS-1$

    private IManagedContainer container;

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }

    @Override
    public final RepositoryConfigurator create(String description) throws ProductCreationException
    {
      return create(container, description);
    }

    public abstract RepositoryConfigurator create(IManagedContainer container, String description) throws ProductCreationException;

    /**
     * @author Eike Stepper
     */
    public static final class Default extends Factory
    {
      public static final String TYPE = "default"; //$NON-NLS-1$

      public Default()
      {
        super(TYPE);
      }

      @Override
      public RepositoryConfigurator create(IManagedContainer container, String description) throws ProductCreationException
      {
        return new RepositoryConfigurator(container);
      }
    }
  }
}
