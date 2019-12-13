/*
 * Copyright (c) 2012, 2013, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 420644
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;
import org.eclipse.emf.cdo.spi.server.IAppExtension2;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Eike Stepper
 */
public class SecurityExtension implements IAppExtension2
{
  public static final String DEFAULT_REALM_PATH = "security";

  public SecurityExtension()
  {
  }

  @Override
  public void start(File configFile) throws Exception
  {
    start(getDocument(configFile));
  }

  @Override
  public void startDynamic(Reader xmlConfigReader) throws Exception
  {
    start(getDocument(xmlConfigReader));
  }

  protected void start(Document document) throws Exception
  {
    OM.LOG.info("Security extension starting"); //$NON-NLS-1$

    NodeList repositoryConfigs = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < repositoryConfigs.getLength(); i++)
    {
      Element repositoryConfig = (Element)repositoryConfigs.item(i);
      configureRepository(repositoryConfig);
    }

    OM.LOG.info("Security extension started"); //$NON-NLS-1$
  }

  @Override
  public void stop() throws Exception
  {
    OM.LOG.info("Security extension stopping"); //$NON-NLS-1$

    OM.LOG.info("Security extension stopped"); //$NON-NLS-1$
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected Document getDocument(Reader configReader) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(configReader));
  }

  protected void configureRepository(Element repositoryConfig)
  {
    IManagedContainer container = getContainer();
    String name = repositoryConfig.getAttribute("name");
    InternalRepository repository = (InternalRepository)RepositoryFactory.get(container, name);
    if (repository == null)
    {
      throw new IllegalStateException("Repository not registered with container: " + name); //$NON-NLS-1$
    }

    NodeList securityManagers = repositoryConfig.getElementsByTagName("securityManager"); //$NON-NLS-1$
    if (securityManagers.getLength() > 1)
    {
      throw new IllegalStateException("A maximum of one security manager can be configured for repository " + repository); //$NON-NLS-1$
    }

    if (securityManagers.getLength() == 1)
    {
      Element securityManagerElement = (Element)securityManagers.item(0);
      String type = securityManagerElement.getAttribute("type");
      if (type == null || type.length() == 0)
      {
        throw new IllegalStateException("Security manager type not specified for repository " + repository); //$NON-NLS-1$
      }

      String description = securityManagerElement.getAttribute("description");
      if (description == null || description.length() == 0)
      {
        description = securityManagerElement.getAttribute("realmPath");
      }

      if (description == null || description.length() == 0)
      {
        description = DEFAULT_REALM_PATH;
      }

      String qualifiedDescription = String.format("%s:%s", name, description); //$NON-NLS-1$
      if (container.getElement(SecurityManagerFactory.PRODUCT_GROUP, type, qualifiedDescription) != null)
      {
        OM.LOG.info("Security manager for repository " + repository.getName() + ": " + qualifiedDescription);
      }
    }
  }

  public static IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
