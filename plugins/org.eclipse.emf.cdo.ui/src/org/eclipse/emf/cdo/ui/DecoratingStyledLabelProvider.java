/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil.Pending;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * An exception-tolerant {@link DecoratingLabelProvider} with support for {@link IStyledLabelProvider styled labels}
 * and {@link Pending lazy loading}.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class DecoratingStyledLabelProvider extends DecoratingLabelProvider implements IStyledLabelProvider
{
  public DecoratingStyledLabelProvider(ILabelProvider provider, ILabelDecorator decorator)
  {
    super(provider, decorator);
  }

  @Override
  public Image getImage(Object element)
  {
    try
    {
      if (element instanceof ViewerUtil.Pending)
      {
        return ContainerItemProvider.pendingImage();
      }

      Image image = super.getImage(element);
      if (image != null)
      {
        return image;
      }
    }
    catch (Exception ex)
    {
      handleException(ex);
    }

    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
  }

  @Override
  public String getText(Object element)
  {
    try
    {
      if (element instanceof ViewerUtil.Pending)
      {
        ViewerUtil.Pending pending = (ViewerUtil.Pending)element;
        return pending.getText();
      }

      String text = super.getText(element);
      if (!StringUtil.isEmpty(text))
      {
        return text;
      }
    }
    catch (Exception ex)
    {
      handleException(ex);
    }

    try
    {
      if (element instanceof EObject)
      {
        EObject eObject = (EObject)element;
        EClass eClass = eObject.eClass();
        String text = getText(eClass);
        if (!StringUtil.isEmpty(text))
        {
          return text;
        }
      }
    }
    catch (Exception ignore)
    {
      //$FALL-THROUGH$
    }

    return element.getClass().getSimpleName();
  }

  @Override
  public StyledString getStyledText(Object element)
  {
    ILabelProvider provider = getLabelProvider();
    if (provider instanceof IStyledLabelProvider)
    {
      return ((IStyledLabelProvider)provider).getStyledText(element);
    }

    return new StyledString(getText(element));
  }

  protected void handleException(Exception ex)
  {
    OM.LOG.error(ex);
  }
}
