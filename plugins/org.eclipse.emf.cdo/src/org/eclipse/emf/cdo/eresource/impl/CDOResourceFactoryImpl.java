/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceFactoryImpl implements Resource.Factory, CDOResourceFactory
{
  // @Singleton
  public static final CDOResourceFactoryImpl INSTANCE = new CDOResourceFactoryImpl();

  private static final String RESOURCE_SET_CLASS_NAME = ResourceSetImpl.class.getName();

  public CDOResourceFactoryImpl()
  {
  }

  public Resource createResource(URI uri)
  {
    CDOResourceImpl resource = (CDOResourceImpl)EresourceFactory.eINSTANCE.createCDOResource();
    resource.setURI(uri);
    resource.setExisting(isExistingResource());
    return resource;
  }

  private boolean isExistingResource()
  {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    if (elements.length >= 6)
    {
      if (isResourceSetMethod(elements[6], "getResource"))
      {
        return true;
      }

      if (isResourceSetMethod(elements[4], "createResource"))
      {
        return false;
      }
    }

    throw new ImplementationError("Call stack is in unexpected state");
  }

  private boolean isResourceSetMethod(StackTraceElement element, String methodName)
  {
    return methodName.equals(element.getMethodName()) && RESOURCE_SET_CLASS_NAME.equals(element.getClassName());
  }
}
