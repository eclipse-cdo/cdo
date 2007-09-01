/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RepositoryConfiguratorNew implements IElementProcessor
{
  private File configFile;

  private Document document;

  public RepositoryConfiguratorNew()
  {
    configFile = getConfigFile();
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof Repository)
    {
      element = process(container, factoryType, description, (Repository)element);
    }

    return element;
  }

  protected Repository process(IManagedContainer container, String factoryType, String name, Repository repository)
  {
    try
    {
      Element repositoryConfig = getRepositoryConfig(name);
      if (repositoryConfig != null)
      {
        Element storeConfig = getStoreConfig(repositoryConfig);
        IStore store = createStore(storeConfig);
        store.setRepository(repository);

        Map<String, String> properties = getProperties(repositoryConfig, 1);
        repository.setProperties(properties);
      }
      else
      {
        OM.LOG.warn("No configuration found for repository " + name);
      }

      return repository;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected Element getRepositoryConfig(String name) throws ParserConfigurationException, SAXException, IOException
  {
    Document document = getDocument();
    NodeList elements = document.getElementsByTagName("repository");
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element repositoryConfig = (Element)elements.item(i);
      String repositoryName = repositoryConfig.getAttribute("name");
      if (ObjectUtil.equals(repositoryName, name))
      {
        return repositoryConfig;
      }
    }

    return null;
  }

  protected File getConfigFile()
  {
    return OMPlatform.INSTANCE.getConfigFile("cdo.server.xml");
  }

  protected Document getDocument() throws ParserConfigurationException, SAXException, IOException
  {
    if (document == null)
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(configFile);
    }

    return document;
  }

  protected IStore createStore(Element storeConfig) throws CoreException
  {
    String type = storeConfig.getAttribute("type");
    IStoreFactory storeFactory = getStoreFactory(type);
    return storeFactory.createStore(storeConfig);
  }

  protected IStoreFactory getStoreFactory(String type) throws CoreException
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, "storeFactories");
    for (IConfigurationElement element : elements)
    {
      if (ObjectUtil.equals(element.getName(), "storeFactory"))
      {
        String storeType = element.getAttribute("storeType");
        if (ObjectUtil.equals(storeType, type))
        {
          return (IStoreFactory)element.createExecutableExtension("class");
        }
      }
    }

    throw new IllegalStateException("Store factory not found: " + type);
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
}
