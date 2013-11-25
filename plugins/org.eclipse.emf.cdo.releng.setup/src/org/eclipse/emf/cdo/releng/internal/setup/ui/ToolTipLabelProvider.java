/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.ui.ImageURIRegistry;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class ToolTipLabelProvider extends DecoratingColumLabelProvider
{
  private AdapterFactoryItemDelegator itemDelegator = new AdapterFactoryItemDelegator(EMFUtil.ADAPTER_FACTORY);

  public ToolTipLabelProvider()
  {
    super(new AdapterFactoryLabelProvider(EMFUtil.ADAPTER_FACTORY), new ILabelDecorator()
    {
      public void removeListener(ILabelProviderListener listener)
      {
      }

      public boolean isLabelProperty(Object element, String property)
      {
        return true;
      }

      public void dispose()
      {
      }

      public void addListener(ILabelProviderListener listener)
      {
      }

      public String decorateText(String text, Object element)
      {
        return text;
      }

      public Image decorateImage(Image image, Object element)
      {
        return image;
      }
    });
  }

  @Override
  public String getToolTipText(Object element)
  {
    return renderHTML(itemDelegator, element);
  }

  public static String renderHTML(AdapterFactoryItemDelegator itemDelegator, Object element)
  {
    StringBuilder result = new StringBuilder();
    List<IItemPropertyDescriptor> propertyDescriptors = itemDelegator.getPropertyDescriptors(element);
    if (propertyDescriptors != null)
    {
      result.append("<table border='1'>");
      for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors)
      {
        result.append("<tr>");

        String displayName = propertyDescriptor.getDisplayName(element);
        result.append("<td>").append(DiagnosticDecorator.escapeContent(displayName)).append("</td>");

        result.append("<td>");
        IItemLabelProvider propertyLabelProvider = propertyDescriptor.getLabelProvider(element);
        Object propertyValue = propertyDescriptor.getPropertyValue(element);
        Object image = propertyLabelProvider.getImage(propertyValue);
        if (image != null)
        {
          result.append(DiagnosticDecorator.enquote("<img src='"
              + ImageURIRegistry.INSTANCE.getImageURI(ExtendedImageRegistry.INSTANCE.getImage(image)) + "'/> "));
        }

        String valueText = propertyLabelProvider.getText(propertyValue);
        if (!StringUtil.isEmpty(valueText))
        {
          result.append(DiagnosticDecorator.escapeContent(valueText));
        }
        else
        {
          result.append("&nbsp;");
        }

        if (valueText == null && image == null)
        {
          result.append("&nbsp;");
        }

        result.append("</td>");
        result.append("</tr>");
      }

      result.append("</table>");
    }

    return result.length() == 0 ? null : result.toString();
  }
}
