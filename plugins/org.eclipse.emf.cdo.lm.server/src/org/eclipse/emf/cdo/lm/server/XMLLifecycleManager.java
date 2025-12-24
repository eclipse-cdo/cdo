/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
    checkState(moduleTemplateElement, "moduleTemplateElement"); //$NON-NLS-1$
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
  public static class Factory extends AbstractLifecycleManager.Factory
  {
    /**
     * @deprecated As of 1.2 use {@link AbstractLifecycleManager.Factory#PRODUCT_GROUP}
     */
    @Deprecated
    public static final String PRODUCT_GROUP = AbstractLifecycleManager.Factory.PRODUCT_GROUP;

    public static final String DEFAULT_TYPE = "default"; //$NON-NLS-1$

    public Factory()
    {
      this(DEFAULT_TYPE);
    }

    protected Factory(String type)
    {
      super(type);
    }

    @Override
    public XMLLifecycleManager create(String description) throws ProductCreationException
    {
      return new XMLLifecycleManager();
    }
  }
}
