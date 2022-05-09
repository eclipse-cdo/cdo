/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 4.10
 */
public abstract class AppExtension implements IAppExtension3, IAppExtension5
{
  public AppExtension()
  {
  }

  @Override
  public abstract String getName();

  @Override
  public final void start(File configFile) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void stop() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void start(IRepository[] repositories, File configFile) throws Exception
  {
    Document document = getDocument(configFile);
    NodeList repositoryConfigs = document.getElementsByTagName("repository"); //$NON-NLS-1$

    for (int i = 0; i < repositoryConfigs.getLength(); i++)
    {
      Element repositoryConfig = (Element)repositoryConfigs.item(i);
      String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$

      InternalRepository repository = getRepository(repositories, repositoryName);
      if (repository != null)
      {
        try
        {
          start(repository, repositoryConfig);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  @Override
  public void stop(IRepository[] repositories) throws Exception
  {
    for (IRepository repository : repositories)
    {
      try
      {
        stop((InternalRepository)repository);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  protected abstract void start(InternalRepository repository, Element repositoryConfig) throws Exception;

  protected abstract void stop(InternalRepository repository) throws Exception;

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  private InternalRepository getRepository(IRepository[] repositories, String name)
  {
    for (IRepository repository : repositories)
    {
      if (Objects.equals(repository.getName(), name))
      {
        return (InternalRepository)repository;
      }
    }

    return null;
  }
}
