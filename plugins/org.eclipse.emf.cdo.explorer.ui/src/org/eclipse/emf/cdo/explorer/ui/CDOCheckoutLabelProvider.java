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

import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelProvider extends AdapterFactoryLabelProvider
{
  private final ComposedAdapterFactory adapterFactory;

  private final Image checkoutImage;

  private final Image checkoutClosedImage;

  private final Image errorImage;

  public CDOCheckoutLabelProvider()
  {
    super(null);

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    setAdapterFactory(adapterFactory);

    checkoutImage = OM.getImageDescriptor("icons/checkout.gif").createImage();
    checkoutClosedImage = OM.getImageDescriptor("icons/checkout_closed.gif").createImage();
    errorImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
  }

  @Override
  public void dispose()
  {
    checkoutClosedImage.dispose();
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

      try
      {
        return resourceNode.getName();
      }
      catch (Exception ex)
      {
        return ex.getMessage();
      }
    }

    if (element instanceof ViewerUtil.Pending)
    {
      return "Loading...";
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;
      if (checkout.isOpen())
      {
        return checkoutImage;
      }

      return checkoutClosedImage;
    }

    if (element instanceof ViewerUtil.Pending)
    {
      return ContainerItemProvider.IMAGE_PENDING;
    }

    try
    {
      return super.getImage(element);
    }
    catch (Exception ex)
    {
      return errorImage;
    }
  }
}
