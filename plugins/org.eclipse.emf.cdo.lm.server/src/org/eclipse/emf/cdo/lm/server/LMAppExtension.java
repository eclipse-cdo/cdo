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

import org.eclipse.emf.cdo.lm.server.bundle.OM;
import org.eclipse.emf.cdo.spi.server.AppExtension;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.PropertiesFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LMAppExtension extends AppExtension
{
  private final Map<InternalRepository, XMLLifecycleManager> lifecycleManagers = new HashMap<>();

  public LMAppExtension()
  {
  }

  @Override
  public String getName()
  {
    return "Lifecycle management";
  }

  @Override
  protected void start(InternalRepository repository, Element repositoryConfig) throws Exception
  {
    NodeList lmElements = repositoryConfig.getElementsByTagName("lifecycleManager"); //$NON-NLS-1$
    int length = lmElements.getLength();
    if (length > 1)
    {
      throw new IllegalStateException("At most one lifecycle manager must be configured for repository " + repository.getName()); //$NON-NLS-1$
    }

    if (length == 1)
    {
      Element lmElement = (Element)lmElements.item(0);
      configureLifecycleManager(repository, lmElement);
    }
  }

  @Override
  protected void stop(InternalRepository repository) throws Exception
  {
    XMLLifecycleManager lifecycleManager = lifecycleManagers.get(repository);
    if (lifecycleManager != null)
    {
      OM.LOG.info("Deactivating lifecycle manager of repository " + repository.getName());
      lifecycleManager.deactivate();
    }
  }

  private void configureLifecycleManager(InternalRepository repository, Element lmElement)
  {
    String systemName = lmElement.getAttribute("systemName");
    if (systemName == null || systemName.isEmpty())
    {
      throw new IllegalStateException("A systemName must be specified for the lifecycle manager of repository " + repository.getName()); //$NON-NLS-1$
    }

    String moduleDefinitionPath = lmElement.getAttribute("moduleDefinitionPath");
    if (moduleDefinitionPath == null || moduleDefinitionPath.isEmpty())
    {
      moduleDefinitionPath = ".module.md";
    }

    NodeList moduleTemplateElements = lmElement.getElementsByTagName("moduleTemplate"); //$NON-NLS-1$
    int length = moduleTemplateElements.getLength();
    if (length != 1)
    {
      throw new IllegalStateException("Exactly one module template must be configured for the lifecycle manager of repository " //$NON-NLS-1$
          + repository.getName());
    }

    Element moduleTemplateElement = (Element)moduleTemplateElements.item(0);

    XMLLifecycleManager lifecycleManager = createLifecycleManager(repository, lmElement);
    lifecycleManager.setSystemRepository(repository);
    lifecycleManager.setSystemName(systemName);
    lifecycleManager.setModuleDefinitionPath(moduleDefinitionPath);
    lifecycleManager.setModuleTemplateElement(moduleTemplateElement);

    OM.LOG.info("Activating lifecycle manager of repository " + repository.getName());
    lifecycleManager.activate();

    lifecycleManagers.put(repository, lifecycleManager);
  }

  protected XMLLifecycleManager createLifecycleManager(InternalRepository repository, Element lmElement)
  {
    XMLLifecycleManager lifecycleManager = getContainerElement(lmElement, XMLLifecycleManager.Factory.DEFAULT_TYPE, repository.getContainer());
    return lifecycleManager;
  }

  protected Map<String, String> initializeModuleTypesDefinition()
  {
    return Collections.emptyMap();
  }

  protected Map<String, Boolean> initializeDropTypesDefinition()
  {
    Map<String, Boolean> dropTypes = new HashMap<>();
    dropTypes.put("Tag", false);
    dropTypes.put("Milestone", false);
    dropTypes.put("Release", true);

    return dropTypes;
  }

  private <T> T getContainerElement(Element element, String defaultType, IManagedContainer container)
  {
    String type = element.getAttribute("type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(type))
    {
      type = defaultType;
    }

    String description = element.getAttribute("description"); //$NON-NLS-1$
    if (StringUtil.isEmpty(description))
    {
      Map<String, String> properties = RepositoryConfigurator.getProperties(element, 1);
      description = PropertiesFactory.createDescription(properties);
    }

    @SuppressWarnings("unchecked")
    T containerElement = (T)container.getElement(XMLLifecycleManager.Factory.PRODUCT_GROUP, type, description, false);

    return containerElement;
  }
}
