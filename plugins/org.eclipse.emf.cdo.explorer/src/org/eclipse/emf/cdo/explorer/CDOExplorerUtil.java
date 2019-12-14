/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutViewProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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
    if (object instanceof CDOView)
    {
      CDOView view = (CDOView)object;
      if (view.isClosed())
      {
        return null;
      }

      return (CDOCheckout)view.properties().get(CDOCheckoutImpl.CHECKOUT_KEY);
    }

    if (object instanceof URI)
    {
      URI uri = (URI)object;
      return CDOCheckoutViewProvider.getCheckout(uri);
    }

    if (object instanceof String)
    {
      String id = (String)object;
      return getCheckoutManager().getCheckout(id);
    }

    CDOCheckout checkout = walkUp(object, null);
    if (checkout != null)
    {
      return checkout;
    }

    if (object instanceof Notifier)
    {
      Notifier notifier = (Notifier)object;
      CDOView view = CDOUtil.getView(notifier);
      if (view != null)
      {
        return getCheckout(view);
      }
    }

    return null;
  }

  public static Object getParent(Object object)
  {
    CDOElement cdoElement = AdapterUtil.adapt(object, CDOElement.class);
    if (cdoElement != null)
    {
      return cdoElement.getParent();
    }

    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      CDOElement element = (CDOElement)EcoreUtil.getExistingAdapter(eObject, CDOElement.class);
      if (element != null)
      {
        return element;
      }

      return CDOElement.getParentOf(eObject);
    }

    return null;
  }

  public static String getName(Object object)
  {
    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      return checkout.getLabel();
    }

    if (object instanceof CDOResourceNode)
    {
      CDOResourceNode resourceNode = (CDOResourceNode)object;
      return resourceNode.getName();
    }

    return null;
  }

  public static LinkedList<Object> getPath(Object object)
  {
    LinkedList<Object> path = new LinkedList<>();
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

      if (object instanceof CDOCheckout)
      {
        return (CDOCheckout)object;
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
