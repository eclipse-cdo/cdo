/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryFactory;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

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
 */
public class RepositoryConfigurator
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, RepositoryConfigurator.class);

  private IManagedContainer container;

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
      TRACER.trace("Configuring CDO server from " + configFile.getAbsolutePath());
    }

    List<IRepository> repositories = new ArrayList<IRepository>();
    Document document = getDocument(configFile);
    NodeList elements = document.getElementsByTagName("repository");
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element repositoryConfig = (Element)elements.item(i);
      IRepository repository = configureRepository(repositoryConfig);
      repositories.add(repository);
      CDOServerUtil.addRepository(container, repository);
    }

    return repositories.toArray(new IRepository[repositories.size()]);
  }

  protected IRepository configureRepository(Element repositoryConfig) throws CoreException
  {
    String repositoryName = repositoryConfig.getAttribute("name");
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new IllegalArgumentException("Repository name is missing or empty");
    }

    String repositoryType = repositoryConfig.getAttribute("type");
    if (StringUtil.isEmpty(repositoryType))
    {
      repositoryType = RepositoryFactory.TYPE;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Configuring repository {0} (type={1})", repositoryName, repositoryType);
    }

    IRepository repository = createRepository(repositoryType);
    repository.setName(repositoryName);

    Element storeConfig = getStoreConfig(repositoryConfig);
    IStore store = configureStore(storeConfig);
    store.setRepository(repository);
    repository.setStore(store);

    Map<String, String> properties = getProperties(repositoryConfig, 1);
    repository.setProperties(properties);

    return repository;
  }

  protected IRepository createRepository(String repositoryType) throws CoreException
  {
    IRepositoryFactory factory = getRepositoryFactory(repositoryType);
    return factory.createRepository();
  }

  protected IStore configureStore(Element storeConfig) throws CoreException
  {
    String type = storeConfig.getAttribute("type");
    IStoreFactory storeFactory = getStoreFactory(type);
    return storeFactory.createStore(storeConfig);
  }

  protected IStoreFactory getStoreFactory(String type) throws CoreException
  {
    IStoreFactory factory = (IStoreFactory)createExecutableExtension("storeFactories", "storeFactory", "storeType",
        type);
    if (factory == null)
    {
      throw new IllegalStateException("Store factory not found: " + type);
    }

    return factory;
  }

  protected IRepositoryFactory getRepositoryFactory(String type) throws CoreException
  {
    IRepositoryFactory factory = (IRepositoryFactory)createExecutableExtension("repositoryFactories",
        "repositoryFactory", "repositoryType", type);
    if (factory == null)
    {
      throw new IllegalStateException("Repository factory not found: " + type);
    }

    return factory;
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected Element getStoreConfig(Element repositoryConfig)
  {
    NodeList storeConfigs = repositoryConfig.getElementsByTagName("store");
    if (storeConfigs.getLength() != 1)
    {
      String repositoryName = repositoryConfig.getAttribute("name");
      throw new IllegalStateException("Exactly one store must be configured for repository " + repositoryName);
    }

    return (Element)storeConfigs.item(0);
  }

  public static Map<String, String> getProperties(Element element, int levels)
  {
    Map<String, String> properties = new HashMap<String, String>();
    collectProperties(element, "", properties, levels);
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties, int levels)
  {
    if ("property".equals(element.getNodeName()))
    {
      String name = element.getAttribute("name");
      String value = element.getAttribute("value");
      properties.put(prefix + name, value);
      prefix += name + ".";
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
          return element.createExecutableExtension("class");
        }
      }
    }

    return null;
  }
}
