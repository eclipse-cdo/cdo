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
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class PropertiesViewer extends TableViewer
{
  public static final String[] EXPERT_FILTER = { "org.eclipse.ui.views.properties.expert" };

  public PropertiesViewer(Composite parent, int style)
  {
    super(parent, style);
    setLabelProvider(new PropertiesLabelProvider());
    setContentProvider(new PropertiesContentProvider());

    Table table = getTable();
    UIUtil.applyGridData(table).heightHint = 64;

    TableColumn propertyColumn = new TableColumn(table, SWT.NONE);
    propertyColumn.setText("Property");
    propertyColumn.setWidth(200);

    TableColumn valueColumn = new TableColumn(table, SWT.NONE);
    valueColumn.setText("Value");
    valueColumn.setWidth(400);

    table.setHeaderVisible(true);
    table.setLinesVisible(true);
  }

  /**
   * @author Eike Stepper
   */
  private final class PropertiesLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider
  {
    public String getColumnText(Object element, int columnIndex)
    {
      return (String)((Object[])element)[columnIndex];
    }

    public Image getColumnImage(Object element, int columnIndex)
    {
      if (columnIndex == 1)
      {
        return (Image)((Object[])element)[2];
      }

      return null;
    }

    public Color getForeground(Object element)
    {
      boolean expert = (Boolean)((Object[])element)[3];
      if (expert)
      {
        return getTable().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
      }

      return null;
    }

    public Color getBackground(Object element)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class PropertiesContentProvider implements IStructuredContentProvider
  {
    private final AdapterFactoryItemDelegator itemDelegator = new AdapterFactoryItemDelegator(EMFUtil.ADAPTER_FACTORY);

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public Object[] getElements(Object element)
    {
      List<Object[]> properties = new ArrayList<Object[]>();
      List<Object[]> expertProperties = new ArrayList<Object[]>();

      List<IItemPropertyDescriptor> propertyDescriptors = itemDelegator.getPropertyDescriptors(element);
      if (propertyDescriptors != null)
      {
        for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors)
        {
          String displayName = propertyDescriptor.getDisplayName(element);

          IItemLabelProvider propertyLabelProvider = propertyDescriptor.getLabelProvider(element);
          Object propertyValue = propertyDescriptor.getPropertyValue(element);
          Object imageURL = propertyLabelProvider.getImage(propertyValue);
          Image image = imageURL == null ? null : ExtendedImageRegistry.INSTANCE.getImage(imageURL);

          String valueText = propertyLabelProvider.getText(propertyValue);
          if (StringUtil.isEmpty(valueText))
          {
            valueText = "";
          }

          if (isExpertProperty(propertyDescriptor, element))
          {
            expertProperties.add(new Object[] { displayName, valueText, image, true });
          }
          else
          {
            properties.add(new Object[] { displayName, valueText, image, false });
          }
        }
      }

      properties.addAll(expertProperties);
      return properties.toArray();
    }

    private boolean isExpertProperty(IItemPropertyDescriptor propertyDescriptor, Object element)
    {
      String[] filterFlags = propertyDescriptor.getFilterFlags(element);
      if (filterFlags != null)
      {
        for (String filterFlag : filterFlags)
        {
          if (EXPERT_FILTER[0].equals(filterFlag))
          {
            return true;
          }
        }
      }

      return false;
    }
  }
}
