/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public final class CDOExplorerUtil
{
  public static CDOCheckoutManager getCheckoutManager()
  {
    return OM.getCheckoutManager();
  }

  public static CDOCheckout getCheckout(EObject object)
  {
    while (object != null)
    {
      Adapter adapter = EcoreUtil.getAdapter(object.eAdapters(), CDOCheckout.class);
      if (adapter != null)
      {
        return (CDOCheckout)adapter;
      }

      EObject container = object.eContainer();
      if (container != null)
      {
        object = container;
      }
      else
      {
        Resource resource = object.eResource();
        if (resource instanceof CDOResource)
        {
          object = (CDOResource)resource;
        }
        else
        {
          break;
        }
      }
    }

    return null;
  }

  public static CDORepositoryManager getRepositoryManager()
  {
    return OM.getRepositoryManager();
  }
}
