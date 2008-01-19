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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.RepositoryFactory;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocolFactory;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

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

/**
 * @author Eike Stepper
 */
public final class CDOServerUtil
{
  private CDOServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container, IRepositoryProvider repositoryProvider)
  {
    // container.registerFactory(new RepositoryFactory());
    container.registerFactory(new CDOServerProtocolFactory(repositoryProvider));
  }

  public static void addRepository(IManagedContainer container, IRepository repository)
  {
    LifecycleUtil.activate(repository);
    container.putElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE, repository.getName(), repository);
  }

  public static IRepository getRepository(IManagedContainer container, String name)
  {
    return RepositoryFactory.get(container, name);
  }

  public static Element getRepositoryConfig(String repositoryName) throws ParserConfigurationException, SAXException,
      IOException
  {
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo.server.xml");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(configFile);
    NodeList elements = document.getElementsByTagName("repository");
    for (int i = 0; i < elements.getLength(); i++)
    {
      Node node = elements.item(i);
      if (node instanceof Element)
      {
        Element element = (Element)node;
        String name = element.getAttribute("name");
        if (ObjectUtil.equals(name, repositoryName))
        {
          return element;
        }
      }
    }

    throw new IllegalStateException("Repository config not found: " + repositoryName);
  }
}
