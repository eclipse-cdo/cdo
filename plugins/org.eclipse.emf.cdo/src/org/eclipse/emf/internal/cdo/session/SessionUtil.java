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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.view.CDOViewSetImpl;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

/**
 * @author Eike Stepper
 */
public final class SessionUtil
{
  private SessionUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static InternalCDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = null;
    synchronized (resourceSet)
    {
      viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(resourceSet);
      if (viewSet == null)
      {
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }

    return viewSet;
  }
}
