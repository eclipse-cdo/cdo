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
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.State;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutStateEvent;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;
import org.eclipse.core.runtime.IProgressMonitor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutRefreshProvider extends RefreshProvider implements IListener
{
  private final Map<String, CDOCheckoutRefreshMonitor> monitors = new HashMap<>();

  public CDOCheckoutRefreshProvider()
  {
    CDOExplorerUtil.getCheckoutManager().addListener(this);
  }

  @Override
  public IRefreshMonitor installMonitor(IResource resource, IRefreshResult result, IProgressMonitor progressMonitor)
  {
    String checkoutID = getCheckoutID(resource);
    if (checkoutID == null)
    {
      return null;
    }

    CDOCheckoutRefreshMonitor monitor;
    synchronized (monitors)
    {
      monitor = monitors.computeIfAbsent(checkoutID, id -> new CDOCheckoutRefreshMonitor(id, result));
    }

    monitor.monitor(resource);
    return monitor;
  }

  @Override
  public void resetMonitors(IResource resource, IProgressMonitor progressMonitor)
  {
    String checkoutID = getCheckoutID(resource);
    if (checkoutID == null)
    {
      super.resetMonitors(resource, progressMonitor);
      return;
    }

    synchronized (monitors)
    {
      CDOCheckoutRefreshMonitor monitor = monitors.get(checkoutID);
      if (monitor != null)
      {
        monitor.unmonitor(resource);

        if (monitor.isIdle())
        {
          monitors.remove(checkoutID);
          monitor.setCheckout(null);
        }
      }
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CheckoutStateEvent)
    {
      CheckoutStateEvent e = (CheckoutStateEvent)event;
      CDOCheckout checkout = e.getCheckout();

      CDOCheckoutRefreshMonitor monitor;
      synchronized (monitors)
      {
        monitor = monitors.get(checkout.getID());
      }

      if (monitor != null)
      {
        monitor.setCheckout(e.getNewState() == State.Open ? checkout : null);
      }
    }
    else if (event instanceof IContainerEvent)
    {
      @SuppressWarnings("unchecked")
      IContainerEvent<CDOCheckout> e = (IContainerEvent<CDOCheckout>)event;

      e.accept(new IContainerEventVisitor<CDOCheckout>()
      {
        @Override
        public void added(CDOCheckout checkout)
        {
          CDOCheckoutRefreshMonitor monitor;
          synchronized (monitors)
          {
            monitor = monitors.get(checkout.getID());
          }

          if (monitor != null)
          {
            monitor.setCheckout(checkout);
          }
        }

        @Override
        public void removed(CDOCheckout checkout)
        {
          CDOCheckoutRefreshMonitor monitor;
          synchronized (monitors)
          {
            monitor = monitors.get(checkout.getID());
          }

          if (monitor != null)
          {
            monitor.setCheckout(null);
          }
        }
      });
    }
  }

  private String getCheckoutID(IResource resource)
  {
    if (!resource.exists())
    {
      return null;
    }

    URI uri = resource.getLocationURI();
    if (uri == null || !CDOCheckoutFileSystem.SCHEME.equals(uri.getScheme()))
    {
      return null;
    }

    return uri.getAuthority();
  }
}
