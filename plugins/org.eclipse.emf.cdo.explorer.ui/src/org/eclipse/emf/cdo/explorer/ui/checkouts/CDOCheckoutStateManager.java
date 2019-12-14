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
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutState.ContentProvider;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutState.LabelProvider;

import org.eclipse.net4j.util.lifecycle.LifecycleException;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOCheckoutStateManager
{
  private final ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

  private final Map<CDOCheckout, CDOCheckoutState> states = new HashMap<>();

  private final CDOCheckoutContentProvider mainContentProvider;

  private CDOCheckoutViewerRefresh viewerRefresh;

  public CDOCheckoutStateManager(CDOCheckoutContentProvider mainContentProvider)
  {
    this.mainContentProvider = mainContentProvider;
  }

  public CDOCheckoutContentProvider getMainContentProvider()
  {
    return mainContentProvider;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public CDOCheckoutViewerRefresh getViewerRefresh()
  {
    return viewerRefresh;
  }

  public CDOCheckoutState[] getStates()
  {
    synchronized (states)
    {
      return states.values().toArray(new CDOCheckoutState[states.size()]);
    }
  }

  public CDOCheckoutState getState(Object object)
  {
    try
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
      if (checkout != null)
      {
        synchronized (states)
        {
          CDOCheckoutState state = states.get(checkout);
          if (state == null)
          {
            state = new CDOCheckoutState(this, checkout);
            states.put(checkout, state);
          }

          return state;
        }
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  public void inputChanged(TreeViewer newTreeViewer, Object oldInput, Object newInput)
  {
    viewerRefresh = new CDOCheckoutViewerRefresh(newTreeViewer);

    for (CDOCheckoutState state : getStates())
    {
      state.inputChanged(newTreeViewer, oldInput, newInput);
    }
  }

  public Object adapt(Object target, Object type)
  {
    CDOCheckoutState state = getState(target);
    if (state != null)
    {
      return state.getAdapterFactory().adapt(target, type);
    }

    return null;
  }

  public ComposedAdapterFactory getAdapterFactory(Object object)
  {
    CDOCheckoutState state = getState(object);
    if (state != null)
    {
      return state.getAdapterFactory();
    }

    return null;
  }

  public ContentProvider getContentProvider(Object object)
  {
    CDOCheckoutState state = getState(object);
    if (state != null)
    {
      return state.getContentProvider();
    }

    return null;
  }

  public LabelProvider getLabelProvider(Object object)
  {
    CDOCheckoutState state = getState(object);
    if (state != null)
    {
      return state.getLabelProvider();
    }

    return null;
  }

  public void dispose()
  {
    resourceManager.dispose();
  }
}
