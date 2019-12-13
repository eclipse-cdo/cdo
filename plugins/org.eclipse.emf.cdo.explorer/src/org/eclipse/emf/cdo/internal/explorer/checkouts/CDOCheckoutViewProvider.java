/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ref.ReferenceMonitor;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutViewProvider extends AbstractCDOViewProvider
{
  public static final String SCHEME = "cdo.checkout";

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

    return super.getResourceURI(view, path);
  }

  public static void disposeResourceSet(ResourceSet resourceSet)
  {
    CheckoutViewProviderAdapter adapter = CheckoutViewProviderAdapter.get(resourceSet);
    if (adapter != null)
    {
      adapter.dispose();
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
    private final Map<CDOCheckout, CDOView> views = new HashMap<CDOCheckout, CDOView>();

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

    public void dispose()
    {
      CDOView[] array;
      synchronized (views)
      {
        array = views.values().toArray(new CDOView[views.size()]);
        views.clear();
      }

      for (CDOView view : array)
      {
        view.close();
      }

      ResourceSet resourceSet = getTarget();
      resourceSet.eAdapters().remove(this);
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
