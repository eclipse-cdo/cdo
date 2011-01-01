/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.properties;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("rawtypes")
public class CDOSessionAdapterFactory implements IAdapterFactory
{
  public static final Class[] CLASSES = { IPropertySourceProvider.class };

  public CDOSessionAdapterFactory()
  {
  }

  public Object getAdapter(Object adaptableObject, Class adapterType)
  {
    if (adaptableObject instanceof CDOSession)
    {
      if (adapterType == CLASSES[0])
      {
        return new IPropertySourceProvider()
        {
          public IPropertySource getPropertySource(Object object)
          {
            return new CDOSessionPropertySource((CDOSession)object);
          }
        };
      }
    }

    return null;
  }

  public Class[] getAdapterList()
  {
    return CLASSES;
  }
}
