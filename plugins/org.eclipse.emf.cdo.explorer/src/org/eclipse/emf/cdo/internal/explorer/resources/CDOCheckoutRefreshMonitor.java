/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.resources;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutRefreshMonitor implements IRefreshMonitor, IListener
{
  private final Set<IResource> resources = new HashSet<>();

  private final IRefreshResult workspace;

  private final String checkoutID;

  private CDOCheckout checkout;

  public CDOCheckoutRefreshMonitor(String checkoutID, IRefreshResult workspace)
  {
    this.workspace = workspace;
    this.checkoutID = checkoutID;
  }

  public CDOCheckout getCheckout()
  {
    return checkout;
  }

  public void setCheckout(CDOCheckout checkout)
  {
    CDOCheckout oldCheckout = this.checkout;
    this.checkout = checkout;

    if (checkout != null)
    {
      if (oldCheckout == null)
      {
        checkoutAdded(checkout);
      }
    }
    else
    {
      if (oldCheckout != null)
      {
        checkoutRemoved(oldCheckout);
      }
    }
  }

  public boolean isIdle()
  {
    synchronized (resources)
    {
      return resources.isEmpty();
    }
  }

  public void monitor(IResource resource)
  {
    synchronized (resources)
    {
      resources.add(resource);
    }

    if (checkout == null)
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckoutManager().getCheckout(checkoutID);
      if (checkout != null)
      {
        this.checkout = checkout;
        checkoutAdded(checkout);
      }
    }
  }

  @Override
  public void unmonitor(IResource resource)
  {
    synchronized (resources)
    {
      if (resource == null)
      {
        resources.clear();
      }
      else
      {
        resources.remove(resource);
      }
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewInvalidationEvent)
    {
      refreshResources();
    }
  }

  private void checkoutAdded(CDOCheckout checkout)
  {
    refreshResources();

    CDOView view = checkout.getView();
    if (view != null)
    {
      view.addListener(this);
    }
  }

  private void checkoutRemoved(CDOCheckout checkout)
  {
    CDOView view = checkout.getView();
    if (view != null)
    {
      view.removeListener(this);
    }

    refreshResources();
  }

  private void refreshResources()
  {
    synchronized (resources)
    {
      for (IResource resource : resources)
      {
        workspace.refresh(resource);
      }
    }
  }
}
