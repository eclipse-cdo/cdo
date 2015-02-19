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
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutElement;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.LinkedList;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public final class CDOExplorerUtil
{
  public static CDORepositoryManager getRepositoryManager()
  {
    return OM.getRepositoryManager();
  }

  public static CDOCheckoutManager getCheckoutManager()
  {
    return OM.getCheckoutManager();
  }

  public static CDOCheckout getCheckout(Object object)
  {
    return walkUp(object, null);
  }

  public static Object getParent(Object object)
  {
    CDOCheckoutElement contentProvider = AdapterUtil.adapt(object, CDOCheckoutElement.class);
    if (contentProvider != null)
    {
      return contentProvider.getParent();
    }

    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      CDOCheckoutElement element = (CDOCheckoutElement)EcoreUtil.getExistingAdapter(eObject, CDOCheckoutElement.class);
      if (element != null)
      {
        return element;
      }

      return getParentOfEObject(eObject);
    }

    return null;
  }

  public static EObject getParentOfEObject(EObject eObject)
  {
    EObject container = eObject.eContainer();
    if (container != null)
    {
      return container;
    }

    if (eObject instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)eObject;
      if (resource.isRoot())
      {
        return null;
      }
    }

    Resource resource = eObject.eResource();
    if (resource instanceof CDOResource)
    {
      return (CDOResource)resource;
    }

    if (eObject instanceof CDOResourceNode)
    {
      CDOView view = ((CDOResourceNode)eObject).cdoView();
      if (view != null)
      {
        return view.getRootResource();
      }
    }

    return null;
  }

  public static LinkedList<Object> getPath(Object object)
  {
    LinkedList<Object> path = new LinkedList<Object>();
    if (walkUp(object, path) != null)
    {
      return path;
    }

    return null;
  }

  public static CDOCheckout walkUp(Object object, LinkedList<Object> path)
  {
    while (object != null)
    {
      if (path != null)
      {
        path.addFirst(object);
      }

      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;

        Adapter adapter = EcoreUtil.getAdapter(eObject.eAdapters(), CDOCheckout.class);
        if (adapter != null)
        {
          return (CDOCheckout)adapter;
        }
      }

      object = getParent(object);
    }

    return null;
  }
}
