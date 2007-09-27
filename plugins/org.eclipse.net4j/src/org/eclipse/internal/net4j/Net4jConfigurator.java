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
package org.eclipse.internal.net4j;

import org.eclipse.net4j.IAcceptor;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.internal.net4j.bundle.OM;

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
public class Net4jConfigurator
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Net4jConfigurator.class);

  private IManagedContainer container;

  public Net4jConfigurator(IManagedContainer container)
  {
    this.container = container;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public IAcceptor[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException,
      CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring Net4j server from " + configFile.getAbsolutePath());
    }

    List<IAcceptor> acceptors = new ArrayList<IAcceptor>();
    Document document = getDocument(configFile);
    NodeList elements = document.getElementsByTagName("acceptor");
    for (int i = 0; i < elements.getLength(); i++)
    {
      Element acceptorConfig = (Element)elements.item(i);
      String type = acceptorConfig.getAttribute("type");
      String listenAddr = acceptorConfig.getAttribute("listenAddr");
      String port = acceptorConfig.getAttribute("port");
      String description = (listenAddr == null ? "" : listenAddr) + (port == null ? "" : ":" + port);

      IAcceptor acceptor = (IAcceptor)container.getElement(AcceptorFactory.PRODUCT_GROUP, type, description);
      acceptors.add(acceptor);
    }

    return acceptors.toArray(new IAcceptor[acceptors.size()]);
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
}