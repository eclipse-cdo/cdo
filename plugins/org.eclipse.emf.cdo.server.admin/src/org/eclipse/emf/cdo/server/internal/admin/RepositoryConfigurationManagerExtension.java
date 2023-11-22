/*
 * Copyright (c) 2013, 2015, 2018, 2019, 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.server.admin.CDORepositoryConfigurationManager;
import org.eclipse.emf.cdo.server.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

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
 * An app extension that starts the {@link CDORepositoryConfigurationManager}
 * (if any) configured in the administrative repository in the XML configuration.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class RepositoryConfigurationManagerExtension implements IAppExtension5
{
  private static final String DEFAULT_CATALOG_PATH = "/catalog";

  private CDORepositoryConfigurationManager repositoryConfigurationManager;

  public RepositoryConfigurationManagerExtension()
  {
  }

  @Override
  public String getName()
  {
    return "Repository configuration manager";
  }

  @Override
  public boolean startBeforeRepositories()
  {
    return false;
  }

  @Override
  public void start(File configFile) throws Exception
  {
    IManagedContainer container = IPluginContainer.INSTANCE;
    Document document = getDocument(configFile);

    NodeList children = document.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE)
      {
        Element childElement = (Element)child;
        if (childElement.getNodeName().equalsIgnoreCase("repository")) //$NON-NLS-1$
        {
          CDORepositoryConfigurationManager repositoryConfigurationManager = configureAdminRepository(container, childElement);
          if (repositoryConfigurationManager != null)
          {
            this.repositoryConfigurationManager = repositoryConfigurationManager;
            break;
          }
        }
      }
    }
  }

  @Override
  public void stop() throws Exception
  {
    LifecycleUtil.deactivate(repositoryConfigurationManager);
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected CDORepositoryConfigurationManager configureAdminRepository(IManagedContainer container, Element repositoryConfig)
  {
    String name = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
    InternalRepository repository = (InternalRepository)RepositoryFactory.get(container, name);
    if (repository == null)
    {
      OM.LOG.warn("Repository not registered with container: " + name); //$NON-NLS-1$
      return null;
    }

    NodeList adminRepositories = repositoryConfig.getElementsByTagName("adminRepository"); //$NON-NLS-1$
    if (adminRepositories.getLength() > 1)
    {
      OM.LOG.warn("A maximum of one administration catalog can be configured in repository " + repository); //$NON-NLS-1$
      return null;
    }

    if (adminRepositories.getLength() == 1)
    {
      Element adminRepositoryElement = (Element)adminRepositories.item(0);
      String type = adminRepositoryElement.getAttribute("configurationManager");
      if (type == null || type.length() == 0)
      {
        OM.LOG.warn("Repository configuration manager type not specified for repository " + repository); //$NON-NLS-1$
        return null;
      }

      String description = adminRepositoryElement.getAttribute("description"); //$NON-NLS-1$
      if (StringUtil.isEmpty(description))
      {
        description = adminRepositoryElement.getAttribute("catalogPath"); //$NON-NLS-1$
      }

      if (StringUtil.isEmpty(description))
      {
        description = DEFAULT_CATALOG_PATH;
      }

      // Create the repository configuration manager
      InternalCDORepositoryConfigurationManager repoManager = (InternalCDORepositoryConfigurationManager)container
          .getElement(CDORepositoryConfigurationManager.Factory.PRODUCT_GROUP, type, description);
      repoManager.setAdminRepository(repository);

      OM.LOG.info("Admin repository: " + repository.getName());
      return repoManager;
    }

    return null;
  }
}
