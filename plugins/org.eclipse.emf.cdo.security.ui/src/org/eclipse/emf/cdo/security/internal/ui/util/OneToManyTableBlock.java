/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;

/**
 * A specialization of the encapsulated UI for one-to-many reference
 * editing that presents a table with multiple columns and in-line
 * editing support.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class OneToManyTableBlock extends OneToManyBlock
{
  public OneToManyTableBlock(DataBindingContext context, EditingDomain domain, AdapterFactory adapterFactory, ITableConfiguration tableConfig)
  {
    super(context, domain, adapterFactory, tableConfig);
  }

  @Override
  protected ITableConfiguration getConfiguration()
  {
    return (ITableConfiguration)super.getConfiguration();
  }

  @Override
  protected boolean isTable()
  {
    return true;
  }

  @Override
  protected void configureColumns(final TableViewer viewer, TableColumnLayout layout)
  {
    super.configureColumns(viewer, layout);

    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);

    final ITableConfiguration tableConfig = getConfiguration();

    String[] columnTitles = tableConfig.getColumnTitles();

    for (int i = 0; i < columnTitles.length; i++)
    {
      TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
      column.getColumn().setText(columnTitles[i]);
      layout.setColumnData(column.getColumn(),
          new ColumnWeightData(tableConfig.getColumnWeight(i), tableConfig.getColumnMinimumSize(i), tableConfig.isColumnResizable(i)));

      final int columnIndex = i;

      column.setLabelProvider(tableConfig.getLabelProvider(viewer, columnIndex));

      column.setEditingSupport(new EditingSupport(viewer)
      {
        @Override
        protected void setValue(Object element, Object value)
        {
          tableConfig.setValue(viewer, element, columnIndex, value);
        }

        @Override
        protected Object getValue(Object element)
        {
          return tableConfig.getValue(viewer, element, columnIndex);
        }

        @Override
        protected boolean canEdit(Object element)
        {
          return tableConfig.canEdit(viewer, element, columnIndex);
        }

        @Override
        protected CellEditor getCellEditor(Object element)
        {
          return tableConfig.getCellEditor(viewer, columnIndex);
        }
      });
    }
  }

  /**
   * Specialized one-to-many configuration that describes a multiple
   * column presentation with in-line editing support.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static interface ITableConfiguration extends IOneToManyConfiguration
  {
    public String[] getColumnTitles();

    public int getColumnWeight(int index);

    public int getColumnMinimumSize(int index);

    public boolean isColumnResizable(int index);

    public CellLabelProvider getLabelProvider(TableViewer viewer, int columnIndex);

    public boolean canEdit(TableViewer viewer, Object element, int columnIndex);

    public void setValue(TableViewer viewer, Object element, int columnIndex, Object value);

    public Object getValue(TableViewer viewer, Object element, int columnIndex);

    public CellEditor getCellEditor(TableViewer viewer, int columnIndex);
  }

  /**
   * Partial implementation of the table configuration interface.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static abstract class TableConfiguration extends OneToManyConfiguration implements ITableConfiguration
  {
    public TableConfiguration(IManagedForm managedForm, EReference reference, EClass itemType, IFilter filter)
    {
      super(managedForm, reference, itemType, filter);
    }

    public TableConfiguration(IManagedForm managedForm, EReference reference, EClass itemType)
    {
      super(managedForm, reference, itemType);
    }

    public TableConfiguration(IManagedForm managedForm, EReference reference, IFilter filter)
    {
      super(managedForm, reference, filter);
    }

    public TableConfiguration(IManagedForm managedForm, EReference reference)
    {
      super(managedForm, reference);
    }
  }
}
