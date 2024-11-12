/*
 * Copyright (c) 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.server;

import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Process;
import org.eclipse.emf.cdo.lm.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.AppExtension;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.XMLUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.PropertiesFactory;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class LMAppExtension extends AppExtension
{
  private static final String DEFAULT_LIFECYCLE_MANAGER_TYPE = OMPlatform.INSTANCE.getProperty( //
      "org.eclipse.emf.cdo.lm.server.LMAppExtension.DEFAULT_LIFECYCLE_MANAGER_TYPE", //
      XMLLifecycleManager.Factory.DEFAULT_TYPE);

  private static final Map<InternalRepository, XMLLifecycleManager> LIFECYCLE_MANAGERS = Collections.synchronizedMap(new HashMap<>());

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
    XMLLifecycleManager lifecycleManager = LIFECYCLE_MANAGERS.remove(repository);
    if (lifecycleManager != null)
    {
      OM.LOG.info("Deactivating lifecycle manager of repository " + repository.getName());
      lifecycleManager.deactivate();
    }
  }

  private void configureLifecycleManager(InternalRepository repository, Element lmElement)
  {
    String systemName = getAttribute(lmElement, "systemName");
    if (StringUtil.isEmpty(systemName))
    {
      throw new IllegalStateException("A systemName must be specified for the lifecycle manager of repository " + repository.getName()); //$NON-NLS-1$
    }

    String moduleDefinitionPath = getAttribute(lmElement, "moduleDefinitionPath");
    if (StringUtil.isEmpty(moduleDefinitionPath))
    {
      moduleDefinitionPath = "module.md";
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

    IPasswordCredentials credentials = createCredentials(lmElement);
    lifecycleManager.setCredentials(credentials);

    Consumer<Process> processInitializer = createProcessInitializater(lmElement);
    lifecycleManager.setProcessInitializer(processInitializer);

    OM.LOG.info("Activating lifecycle manager of repository " + repository.getName());
    lifecycleManager.activate();

    LIFECYCLE_MANAGERS.put(repository, lifecycleManager);
  }

  /**
   * @since 1.2
   */
  protected String getDefaultLifecycleManagerType()
  {
    return DEFAULT_LIFECYCLE_MANAGER_TYPE;
  }

  protected XMLLifecycleManager createLifecycleManager(InternalRepository repository, Element lmElement)
  {
    IManagedContainer container = repository.getContainer();
    String lifecycleManagerType = getDefaultLifecycleManagerType();
    return getContainerElement(lmElement, lifecycleManagerType, "systemName", container);
  }

  /**
   * @since 1.3
   */
  protected IPasswordCredentials createCredentials(Element lmElement)
  {
    IPasswordCredentials[] credentials = { null };

    try
    {
      XMLUtil.handleChildElements(lmElement, child -> {
        if ("credentials".equals(child.getTagName()))
        {
          if (credentials[0] == null)
          {
            String userID = getAttribute(child, "userId");
            if (StringUtil.isEmpty(userID))
            {
              throw new IllegalStateException("userId not specified");
            }

            String password = getAttribute(child, "password");
            if (StringUtil.isEmpty(password))
            {
              throw new IllegalStateException("Password not specified");
            }

            credentials[0] = new PasswordCredentials(userID, password);
          }
          else
          {
            OM.LOG.info("Multiple credentials specified. Using first.");
          }
        }
      });
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    return credentials[0];
  }

  /**
   * @since 1.2
   */
  protected Consumer<Process> createProcessInitializater(Element lmElement)
  {
    Collection<ModuleType> moduleTypes = getModuleTypes(lmElement);
    Collection<DropType> dropTypes = getDropTypes(lmElement);

    if (!ObjectUtil.isEmpty(moduleTypes) || !ObjectUtil.isEmpty(dropTypes))
    {
      return process -> {
        process.getModuleTypes().addAll(moduleTypes);
        process.getDropTypes().addAll(dropTypes);
      };
    }

    return null;
  }

  /**
   * @since 1.2
   */
  protected Collection<ModuleType> getModuleTypes(Element lmElement)
  {
    return getNamedChildren(lmElement, "moduleType", (child, name) -> LMFactory.eINSTANCE.createModuleType(name));
  }

  /**
   * @since 1.2
   */
  protected Collection<DropType> getDropTypes(Element lmElement)
  {
    return getNamedChildren(lmElement, "dropType", (child, name) -> {
      boolean release = StringUtil.isTrue(getAttribute(child, "release"));
      return LMFactory.eINSTANCE.createDropType(name, release);
    });
  }

  private <T> Collection<T> getNamedChildren(Element lmElement, String childTagName, BiFunction<Element, String, T> childCreator)
  {
    List<T> result = new ArrayList<>();

    NodeList elements = lmElement.getElementsByTagName(childTagName);
    for (int i = 0, length = elements.getLength(); i < length; i++)
    {
      Element element = (Element)elements.item(i);

      String name = getAttribute(element, "name");
      if (!StringUtil.isEmpty(name))
      {
        T child = childCreator.apply(element, name);
        result.add(child);
      }
    }

    return result;
  }

  private <T> T getContainerElement(Element element, String defaultType, String descriptionAttribute, IManagedContainer container)
  {
    String type = getAttribute(element, "type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(type))
    {
      type = defaultType;
    }

    if (StringUtil.isEmpty(descriptionAttribute))
    {
      descriptionAttribute = "description";//$NON-NLS-1$
    }

    String description = getAttribute(element, descriptionAttribute);
    if (StringUtil.isEmpty(description))
    {
      Map<String, String> properties = RepositoryConfigurator.getProperties(element, 1, null, container);
      description = PropertiesFactory.createDescription(properties);
    }

    @SuppressWarnings("unchecked")
    T containerElement = (T)container.getElement(AbstractLifecycleManager.Factory.PRODUCT_GROUP, type, description, false);

    return containerElement;
  }

  /**
   * @since 1.4
   */
  public static XMLLifecycleManager getLifecycleManager(IRepository repository)
  {
    return LIFECYCLE_MANAGERS.get(repository);
  }

  /**
   * @deprecated As of 1.2 no longer supported.
   */
  @Deprecated
  protected Map<String, String> initializeModuleTypesDefinition()
  {
    return Collections.emptyMap();
  }

  /**
   * @deprecated As of 1.2 no longer supported.
   */
  @Deprecated
  protected Map<String, Boolean> initializeDropTypesDefinition()
  {
    Map<String, Boolean> dropTypes = new HashMap<>();
    dropTypes.put("Tag", false);
    dropTypes.put("Milestone", false);
    dropTypes.put("Release", true);

    return dropTypes;
  }
}
