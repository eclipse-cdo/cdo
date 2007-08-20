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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.eclipse.net4j.util.ObjectUtil;

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
  public RepositoryConfigurator()
  {
  }

  public IRepository[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException,
      CoreException
  {
    List<IRepository> repositories = new ArrayList();
    Document document = getDocument(configFile);
    NodeList elements = document.getElementsByTagName("repository");
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element repositoryConfig = (Element)elements.item(i);
      IRepository repository = configureRepository(repositoryConfig);
      repositories.add(repository);
    }

    return repositories.toArray(new IRepository[repositories.size()]);
  }

  protected IRepository configureRepository(Element repositoryConfig) throws CoreException
  {
    String repositoryName = repositoryConfig.getAttribute("name");
    NodeList storeConfigs = repositoryConfig.getElementsByTagName("store");
    if (storeConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one store must be configured for repository " + repositoryName);
    }

    Element storeConfig = (Element)storeConfigs.item(0);
    IStore store = configureStore(storeConfig);
    Repository repository = new Repository(repositoryName, store);
    store.setRepository(repository);
    return repository;
  }

  protected IStore configureStore(Element storeConfig) throws CoreException
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

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(configFile);
    return document;
  }

  public static Map<String, String> getProperties(Element element)
  {
    Map<String, String> properties = new HashMap();
    collectProperties(element, "", properties);
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties)
  {
    if ("property".equals(element.getNodeName()))
    {
      String name = element.getAttribute("name");
      String value = element.getAttribute("value");
      properties.put(prefix + name, value);
      prefix += name + ".";
    }

    NodeList childNodes = element.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++)
    {
      Node childNode = childNodes.item(i);
      if (childNode instanceof Element)
      {
        collectProperties((Element)childNode, prefix, properties);
      }
    }
  }
}
