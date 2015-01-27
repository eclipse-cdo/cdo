/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 * @since 4.3
 */
public class CDOCheckoutContentProvider extends AdapterFactoryContentProvider
{
  private static final CDOCheckoutManager MANAGER = CDOExplorerUtil.getCheckoutManager();

  private final ComposedAdapterFactory adapterFactory;

  private Object input;

  public CDOCheckoutContentProvider()
  {
    super(null);

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    setAdapterFactory(adapterFactory);
  }

  @Override
  public void dispose()
  {
    super.dispose();
    adapterFactory.dispose();
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    super.inputChanged(viewer, oldInput, newInput);
    input = newInput;
  }

  @Override
  public Object[] getElements(Object object)
  {
    Object[] children = getRootContent(object);
    if (children != null)
    {
      return children;
    }

    return super.getElements(object);
  }

  @Override
  public Object[] getChildren(Object object)
  {
    Object[] children = getRootContent(object);
    if (children != null)
    {
      return children;
    }

    return super.getChildren(object);
  }

  @Override
  public boolean hasChildren(Object object)
  {
    if (object == input)
    {
      return !MANAGER.isEmpty();
    }

    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      EObject rootObject = checkout.getRootObject();
      return super.hasChildren(rootObject);
    }

    return super.hasChildren(object);
  }

  @Override
  public Object getParent(Object object)
  {
    if (object == input)
    {
      return null;
    }

    if (object instanceof CDOCheckout)
    {
      return input;
    }

    return super.getParent(object);
  }

  private Object[] getRootContent(Object object)
  {
    if (object == input)
    {
      return MANAGER.getCheckouts();
    }

    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      return super.getChildren(checkout.getRootObject());
    }

    return null;
  }
}
