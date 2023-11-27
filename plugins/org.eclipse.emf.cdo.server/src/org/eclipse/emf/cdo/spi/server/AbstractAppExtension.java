/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.ParameterAware;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.container.IManagedContainerProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public abstract class AbstractAppExtension implements IManagedContainerProvider, ContainerAware, ParameterAware, IAppExtension
{
  private IManagedContainer container;

  private Map<String, String> parameters;

  public AbstractAppExtension()
  {
  }

  @Override
  public final IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public final void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }

  public final Map<String, String> getParameters()
  {
    return parameters;
  }

  @Override
  public final void setParameters(Map<String, String> parameters)
  {
    this.parameters = parameters;
  }

  protected final Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected final String getAttribute(Element element, String name)
  {
    String value = element.getAttribute(name);
    value = RepositoryConfigurator.expandValue(value, parameters, container);
    return value;
  }

  protected final InternalRepository getRepository(IRepository[] repositories, String name)
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
