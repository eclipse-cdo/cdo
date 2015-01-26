/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 * @since 4.3
 */
public class CDOCheckoutLabelProvider extends AdapterFactoryLabelProvider
{
  private final ComposedAdapterFactory adapterFactory;

  private Image checkoutImage;

  public CDOCheckoutLabelProvider()
  {
    super(null);

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    setAdapterFactory(adapterFactory);

    ImageDescriptor imageDescriptor = OM.getImageDescriptor("icons/checkout.gif");
    checkoutImage = imageDescriptor.createImage();
  }

  @Override
  public void dispose()
  {
    checkoutImage.dispose();
    super.dispose();

    // Must come after super.dispose().
    adapterFactory.dispose();
  }

  @Override
  public String getText(Object element)
  {
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;
      return checkout.getLabel();
    }

    if (element instanceof CDOResourceNode)
    {
      CDOResourceNode resourceNode = (CDOResourceNode)element;
      return resourceNode.getName();
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (element instanceof CDOCheckout)
    {
      return checkoutImage;
    }

    return super.getImage(element);
  }
}
