/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class SecurityExtension implements IAppExtension
{
  public static final String DEFAULT_REALM_PATH = "security";

  public SecurityExtension()
  {
  }

  public void start(File configFile) throws Exception
  {
    OM.LOG.info("Security extension starting"); //$NON-NLS-1$

    Document document = getDocument(configFile);
    NodeList repositoryConfigs = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < repositoryConfigs.getLength(); i++)
    {
      Element repositoryConfig = (Element)repositoryConfigs.item(i);
      configureRepository(repositoryConfig);
    }

    OM.LOG.info("Security extension started"); //$NON-NLS-1$
  }

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
      throw new IllegalStateException(
          "A maximum of one security manager can be configured for repository " + repository); //$NON-NLS-1$
    }

    if (securityManagers.getLength() == 1)
    {
      Element securityManagerElement = (Element)securityManagers.item(0);
      String type = securityManagerElement.getAttribute("type");
      if (type == null || type.length() == 0)
      {
        throw new IllegalStateException("Security manager type not specified for repository " + repository); //$NON-NLS-1$
      }

      String realmPath = securityManagerElement.getAttribute("realmPath");
      if (realmPath == null || realmPath.length() == 0)
      {
        realmPath = DEFAULT_REALM_PATH;
      }

      InternalSecurityManager securityManager = (InternalSecurityManager)container.getElement(
          SecurityManagerFactory.PRODUCT_GROUP, type, realmPath);
      securityManager.setRepository(repository);
    }
  }

  public static IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
