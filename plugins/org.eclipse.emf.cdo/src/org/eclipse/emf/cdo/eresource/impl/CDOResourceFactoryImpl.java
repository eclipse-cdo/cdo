/*
 * Copyright (c) 2007-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
  private Set<String> resourceSetClassNames = new HashSet<>();

  /**
   * @since 4.0
   */
  public static final CDOResourceFactory INSTANCE = new CDOResourceFactoryImpl();

  public CDOResourceFactoryImpl()
  {
    resourceSetClassNames.add(ResourceSetImpl.class.getName());
    resourceSetClassNames.add("org.eclipse.xtext.resource.XtextResourceSet");
    resourceSetClassNames.add("org.eclipse.xtext.resource.ResourceSetReferencingResourceSetImpl");
    resourceSetClassNames.add("org.eclipse.xtext.resource.SynchronizedXtextResourceSet");
  }

  /**
   * @since 4.2
   */
  public Set<String> getResourceSetClassNames()
  {
    return resourceSetClassNames;
  }

  @Override
  public Resource createResource(URI uri)
  {
    boolean existing = isGetResource();

    CDOResourceImpl resource = createCDOResource(uri);
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
      if (resourceSetClassNames.contains(element.getClassName()))
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
}
