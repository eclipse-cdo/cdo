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
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelProvider extends AdapterFactoryLabelProvider implements IColorProvider
{
  private static final Image CHECKOUT_IMAGE = OM.getImage("icons/checkout.gif");

  private static final Image CHECKOUT_CLOSED_IMAGE = OM.getImage("icons/checkout_closed.gif");

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages()
      .getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private final ComposedAdapterFactory adapterFactory;

  public CDOCheckoutLabelProvider()
  {
    super(null);

    adapterFactory = CDOEditor.createAdapterFactory(true);
    setAdapterFactory(adapterFactory);
  }

  @Override
  public void dispose()
  {
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
      ViewerUtil.Pending pending = (ViewerUtil.Pending)element;
      return pending.getText();
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
        return CHECKOUT_IMAGE;
      }

      return CHECKOUT_CLOSED_IMAGE;
    }

    if (element instanceof ViewerUtil.Pending)
    {
      return ContainerItemProvider.PENDING_IMAGE;
    }

    try
    {
      return super.getImage(element);
    }
    catch (Exception ex)
    {
      return ERROR_IMAGE;
    }
  }

  @Override
  public Color getForeground(Object object)
  {
    if (object instanceof ViewerUtil.Pending)
    {
      return ContainerItemProvider.PENDING_COLOR;
    }

    return super.getForeground(object);
  }
}
