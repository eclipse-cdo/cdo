/*
 * Copyright (c) 2007-2012, 2015, 2019, 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 *    Simon McDuff - bug 246705
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates default {@link CDOResource} instances.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CDOResourceFactoryImpl implements CDOResourceFactory
{
  /**
   * @since 4.0
   */
  public static final CDOResourceFactory INSTANCE = new CDOResourceFactoryImpl();

  private static final Set<String> RESOURCE_SET_CLASS_NAMES = initResourceSetClassNames();

  public CDOResourceFactoryImpl()
  {
  }

  /**
   * @since 4.2
   */
  public Set<String> getResourceSetClassNames()
  {
    return RESOURCE_SET_CLASS_NAMES;
  }

  @Override
  public Resource createResource(URI uri)
  {
    CDOResourceImpl resource = createCDOResource(uri);

    boolean existing = isGetResource();
    resource.setExisting(existing);

    return resource;
  }

  /**
   * @since 4.0
   */
  protected CDOResourceImpl createCDOResource(URI uri)
  {
    return new CDOResourceImpl(uri);
  }

  /**
   * TODO Add TCs to ensure that Ecore internally doesn't change the way the stack is used!!!
   *
   * @since 3.0
   */
  protected boolean isGetResource()
  {
    boolean inResourceSet = false;
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 3; i < elements.length; i++)
    {
      StackTraceElement element = elements[i];
      if (RESOURCE_SET_CLASS_NAMES.contains(element.getClassName()))
      {
        inResourceSet = true;
      }
      else
      {
        if (inResourceSet)
        {
          break;
        }
      }

      if (inResourceSet && "getResource".equals(element.getMethodName())) //$NON-NLS-1$
      {
        return true;
      }
    }

    return false;
  }

  private static Set<String> initResourceSetClassNames()
  {
    Set<String> set = new HashSet<>();
    set.add(ResourceSetImpl.class.getName());
    set.add("org.eclipse.xtext.resource.XtextResourceSet");
    set.add("org.eclipse.xtext.resource.ResourceSetReferencingResourceSetImpl");
    set.add("org.eclipse.xtext.resource.SynchronizedXtextResourceSet");
    ResourceSetClassNameProvider.Factory.fillClassNames(set);
    return set;
  }

  /**
   * @author Eike Stepper
   * @since 4.17
   */
  public interface ResourceSetClassNameProvider
  {
    public String[] getResourceSetClassNames();

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.resourceSetClassNameProviders";

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract ResourceSetClassNameProvider create(String description) throws ProductCreationException;

      private static void fillClassNames(Set<String> classNames)
      {
        for (String type : IPluginContainer.INSTANCE.getFactoryTypes(PRODUCT_GROUP))
        {
          ResourceSetClassNameProvider provider = IPluginContainer.INSTANCE.getElementOrNull(PRODUCT_GROUP, type);
          if (provider != null)
          {
            try
            {
              for (String className : provider.getResourceSetClassNames())
              {
                CollectionUtil.addNotNull(classNames, className);
              }
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }
      }
    }
  }
}
