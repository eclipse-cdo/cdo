/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ref.ReferenceMonitor;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutViewProvider extends AbstractCDOViewProvider
{
  public static final String SCHEME = CDOExplorerUtil.URI_SCHEME;

  // TODO Move to activator and deactivate via stop().
  private static final ReferenceMonitor<ResourceSet> MONITOR = new ReferenceMonitor<ResourceSet>()
  {
    @Override
    protected void work(ResourceSet resourceSet)
    {
      disposeResourceSet(resourceSet);
    }
  };

  public CDOCheckoutViewProvider()
  {
    this(DEFAULT_PRIORITY);
  }

  public CDOCheckoutViewProvider(int priority)
  {
    super("cdo\\.checkout://.*", priority);
  }

  @Override
  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    CDOCheckout checkout = getCheckout(uri);
    if (checkout != null)
    {
      CheckoutViewProviderAdapter adapter = CheckoutViewProviderAdapter.get(resourceSet);
      if (adapter == null)
      {
        adapter = CheckoutViewProviderAdapter.create(resourceSet);

        MONITOR.activate();
        MONITOR.monitor(resourceSet);
      }

      return adapter.getView(checkout);
    }

    return null;
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    CDOCheckout checkout = CDOExplorerUtil.getCheckout(view);
    if (checkout != null)
    {
      return checkout.createResourceURI(path);
    }

    return null;
  }

  @Override
  public URI getViewURI(URI uri)
  {
    if (uri == null)
    {
      return null;
    }

    String scheme = uri.scheme();
    if (scheme == null)
    {
      return null;
    }

    if (!scheme.equals(SCHEME))
    {
      return null;
    }

    return super.getViewURI(uri);
  }

  public static void disposeResourceSet(ResourceSet resourceSet)
  {
    for (Iterator<Adapter> it = resourceSet.eAdapters().iterator(); it.hasNext();)
    {
      Adapter adapter = it.next();
      if (adapter.getClass() == CheckoutViewProviderAdapter.class)
      {
        it.remove();
        break;
      }
    }
  }

  public static CDOCheckout getCheckout(URI uri)
  {
    if (SCHEME.equals(uri.scheme()))
    {
      String checkoutID = uri.authority();
      return CDOExplorerUtil.getCheckoutManager().getCheckout(checkoutID);
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class CheckoutViewProviderAdapter extends AdapterImpl
  {
    private final Map<CDOCheckout, CDOView> views = new HashMap<>();

    private CheckoutViewProviderAdapter()
    {
    }

    public CDOView getView(final CDOCheckout checkout)
    {
      synchronized (views)
      {
        CDOView view = views.get(checkout);
        if (view == null)
        {
          ResourceSet resourceSet = getTarget();
          CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
          if (viewSet != null)
          {
            for (CDOView viewSetView : viewSet.getViews())
            {
              if (checkout == CDOExplorerUtil.getCheckout(viewSetView))
              {
                view = viewSetView;
                break;
              }
            }
          }

          if (view == null)
          {
            view = checkout.openView(resourceSet);
          }

          if (view != null)
          {
            views.put(checkout, view);

            view.addListener(new LifecycleEventAdapter()
            {
              @Override
              protected void onDeactivated(ILifecycle lifecycle)
              {
                synchronized (views)
                {
                  views.remove(checkout);
                }
              }
            });
          }
        }

        return view;
      }
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == CheckoutViewProviderAdapter.class;
    }

    @Override
    public ResourceSet getTarget()
    {
      return (ResourceSet)super.getTarget();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      ResourceSet resourceSet = getTarget();
      if (newTarget == resourceSet)
      {
        return;
      }

      if (newTarget == null)
      {
        CDOView[] array;
        synchronized (views)
        {
          array = views.values().toArray(new CDOView[views.size()]);
          views.clear();
        }

        for (CDOView view : array)
        {
          try
          {
            view.close();
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
      else if (resourceSet != null)
      {
        throw new IllegalStateException("Illegal attempt to retarget CheckoutViewProviderAdapter to " + newTarget);
      }

      super.setTarget(newTarget);
    }

    public static CheckoutViewProviderAdapter get(ResourceSet resourceSet)
    {
      EList<Adapter> adapters = resourceSet.eAdapters();

      CheckoutViewProviderAdapter adapter = (CheckoutViewProviderAdapter)EcoreUtil.getAdapter(adapters, CheckoutViewProviderAdapter.class);
      if (adapter != null && adapter.getTarget() != resourceSet)
      {
        adapters.remove(adapter);
        adapter = null;
      }

      return adapter;
    }

    public static CheckoutViewProviderAdapter create(ResourceSet resourceSet)
    {
      CheckoutViewProviderAdapter adapter = new CheckoutViewProviderAdapter();
      resourceSet.eAdapters().add(adapter);
      return adapter;
    }
  }
}
