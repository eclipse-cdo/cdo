/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Simon McDuff - http://bugs.eclipse.org/246705
 **************************************************************************/
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.CDOViewSetImpl;

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
  private CDOViewSetImpl viewSet;

  /**
   * @since 2.0
   */
  public CDOResourceFactoryImpl(CDOViewSet viewSet)
  {
    this.viewSet = (CDOViewSetImpl)viewSet;
  }

  public Resource createResource(URI uri)
  {
    // URI can be invalid or incomplete. Extract repo + resource path and build a new URI.
    String repositoryUUID = CDOURIUtil.extractRepositoryUUID(uri);

    // repoUUID can be null but can be null
    CDOViewImpl view = viewSet.resolveView(repositoryUUID);
    String path = CDOURIUtil.extractResourcePath(uri);

    // Build a new URI with the view and the path
    URI newURI = CDOURIUtil.createResourceURI(view, path);

    // Important: Set URI *after* registration with the view!
    CDOResourceImpl resource = new CDOResourceImpl(newURI);
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

      if (inResourceSet && "getResource".equals(element.getMethodName()))
      {
        return true;
      }
    }

    return false;
  }
}
