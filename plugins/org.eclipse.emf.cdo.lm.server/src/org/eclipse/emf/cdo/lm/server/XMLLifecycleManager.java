/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.server;

import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.core.runtime.CoreException;

import org.w3c.dom.Element;

/**
 * @author Eike Stepper
 */
public class XMLLifecycleManager extends AbstractLifecycleManager
{
  private Element moduleTemplateElement;

  public XMLLifecycleManager()
  {
  }

  public void setModuleTemplateElement(Element moduleTemplateElement)
  {
    checkInactive();
    this.moduleTemplateElement = moduleTemplateElement;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(moduleTemplateElement, "moduleTemplateElement");
  }

  @Override
  protected InternalRepository createModuleRepository(String moduleName) throws CoreException
  {
    IManagedContainer container = getContainer();

    class TemplateRepositoryConfigurator extends RepositoryConfigurator
    {
      public TemplateRepositoryConfigurator()
      {
        super(container);
      }

      public InternalRepository createModuleRepository(String moduleName) throws CoreException
      {
        setParameter("$MODULE$", moduleName);

        InternalRepository repository = (InternalRepository)getRepository(moduleTemplateElement);
        if (repository.getName() == null)
        {
          repository.setName(moduleName);
        }

        return repository;
      }
    }

    TemplateRepositoryConfigurator configurator = new TemplateRepositoryConfigurator();
    InternalRepository moduleRepository = configurator.createModuleRepository(moduleName);
    return moduleRepository;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.lm.server.lifecycleManagers";

    public static final String DEFAULT_TYPE = "default";

    public Factory()
    {
      this(DEFAULT_TYPE);
    }

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public XMLLifecycleManager create(String description) throws ProductCreationException
    {
      return new XMLLifecycleManager();
    }
  }
}
