/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.ui;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Eike Stepper
 */
public class TableColumnSorter extends ViewerComparator
{
  private final TableViewer viewer;

  private int direction = SWT.UP;

  private TableColumn column;

  private int columnIndex;

  public TableColumnSorter(TableViewer viewer)
  {
    this.viewer = viewer;
    viewer.setComparator(this);

    Table table = viewer.getTable();
    TableColumn[] columns = table.getColumns();

    for (int i = 0; i < columns.length; i++)
    {
      if (column == null)
      {
        column = columns[i];
        table.setSortColumn(column);
        table.setSortDirection(direction);
      }

      columns[i].addSelectionListener(new ColumnListener(i));
    }
  }

  @Override
  public final int compare(Viewer viewer, Object o1, Object o2)
  {
    String str1;
    String str2;

    ILabelProvider labelProvider = (ILabelProvider)this.viewer.getLabelProvider(columnIndex);
    if (labelProvider instanceof DelegatingStyledCellLabelProvider)
    {
      IStyledLabelProvider styledLabelProvider = ((DelegatingStyledCellLabelProvider)labelProvider).getStyledStringProvider();
      str1 = styledLabelProvider.getStyledText(o1).getString();
      str2 = styledLabelProvider.getStyledText(o2).getString();
    }
    else
    {
      str1 = labelProvider.getText(o1);
      str2 = labelProvider.getText(o2);
    }

    if (str1 == null)
    {
      str1 = "";
    }

    if (str2 == null)
    {
      str2 = "";
    }

    int result = compareStrings(str1, str2);
    return direction == SWT.UP ? result : -result;
  }

  protected int compareStrings(String str1, String str2)
  {
    return str1.compareTo(str2);
  }

  /**
   * @author Eike Stepper
   */
  private final class ColumnListener extends SelectionAdapter
  {
    private final int index;

    public ColumnListener(int index)
    {
      this.index = index;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
      TableColumn selectedColumn = (TableColumn)e.widget;
      if (column == selectedColumn)
      {
        direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
      }
      else
      {
        direction = SWT.UP;
        column = selectedColumn;
        columnIndex = index;
      }

      Table table = viewer.getTable();
      table.setSortColumn(selectedColumn);
      table.setSortDirection(direction);

      viewer.refresh();
    }
  }
}
