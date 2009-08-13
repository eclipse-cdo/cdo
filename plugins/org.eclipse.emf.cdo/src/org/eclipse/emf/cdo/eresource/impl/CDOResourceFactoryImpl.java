/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Simon McDuff - http://bugs.eclipse.org/246705
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceFactoryImpl implements Resource.Factory, CDOResourceFactory
{
  private static final String RESOURCE_SET_CLASS_NAME = ResourceSetImpl.class.getName();

  /**
   * @since 2.0
   */
  public static final CDOResourceFactory eINSTANCE = new CDOResourceFactoryImpl();

  public Resource createResource(URI uri)
  {
    String path = CDOURIUtil.extractResourcePath(uri);
    CDOResourceImpl resource = new CDOResourceImpl(uri);
    resource.setRoot(CDOURIUtil.SEGMENT_SEPARATOR.equals(path));
    resource.setExisting(isGetResource());
    return resource;
  }

  /**
   * TODO Add TCs to ensure that Ecore internally doesn't change the way the stack is used!!!
   */
  private boolean isGetResource()
  {
    boolean inResourceSet = false;
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 3; i < elements.length; i++)
    {
      StackTraceElement element = elements[i];
      if (RESOURCE_SET_CLASS_NAME.equals(element.getClassName()))
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
