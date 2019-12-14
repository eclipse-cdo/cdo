/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class TableLabelProvider<T> extends ManagedLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider
{
  private final List<Column<T>> columns = new ArrayList<>();

  private boolean headerVisible = true;

  private boolean linesVisible;

  public TableLabelProvider()
  {
  }

  public TableLabelProvider<T> addColumn(Column<T> column)
  {
    columns.add(column);
    return this;
  }

  public boolean isHeaderVisible()
  {
    return headerVisible;
  }

  public TableLabelProvider<T> setHeaderVisible(boolean headerVisible)
  {
    this.headerVisible = headerVisible;
    return this;
  }

  public boolean isLinesVisible()
  {
    return linesVisible;
  }

  public TableLabelProvider<T> setLinesVisible(boolean linesVisible)
  {
    this.linesVisible = linesVisible;
    return this;
  }

  public Column<T> removeColumn(int columnIndex)
  {
    return columns.remove(columnIndex);
  }

  public Column<T> getColumn(int columnIndex)
  {
    return columns.get(columnIndex);
  }

  @Override
  public String getColumnText(Object element, int columnIndex)
  {
    @SuppressWarnings("unchecked")
    T t = (T)element;
    String text = getColumn(columnIndex).getText(t);
    if (text == null)
    {
      return StringUtil.EMPTY;
    }

    return text;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex)
  {
    @SuppressWarnings("unchecked")
    T t = (T)element;
    return getColumn(columnIndex).getImage(t);
  }

  @Override
  public Color getForeground(Object element, int columnIndex)
  {
    @SuppressWarnings("unchecked")
    T t = (T)element;
    return getColumn(columnIndex).getForeground(t);
  }

  @Override
  public Color getBackground(Object element, int columnIndex)
  {
    @SuppressWarnings("unchecked")
    T t = (T)element;
    return getColumn(columnIndex).getBackground(t);
  }

  @Override
  public Font getFont(Object element, int columnIndex)
  {
    @SuppressWarnings("unchecked")
    T t = (T)element;
    return getColumn(columnIndex).getFont(t);
  }

  public TableLabelProvider<T> support(TableViewer tableViewer)
  {
    for (Column<T> column : columns)
    {
      TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);

      TableColumn tableColumn = tableViewerColumn.getColumn();
      tableColumn.setText(column.getHeader());
      tableColumn.setToolTipText(column.getToolTipText());
      tableColumn.setWidth(column.getWidth());
      tableColumn.setAlignment(column.getAlignment());
      tableColumn.setImage(column.getImage());
      tableColumn.setMoveable(column.isMoveable());
      tableColumn.setResizable(column.isResizable());
    }

    Table table = tableViewer.getTable();
    table.setHeaderVisible(headerVisible);
    table.setLinesVisible(linesVisible);

    tableViewer.setLabelProvider(this);
    return this;
  }

  /**
   * @author Eike Stepper
   */
  public static class Column<T>
  {
    public static final int DEFAULT_WIDTH = 100;

    private String header = StringUtil.EMPTY;

    private String toolTipText = StringUtil.EMPTY;

    private int width = DEFAULT_WIDTH;

    private int alignment = SWT.LEFT;

    private Image image;

    private boolean moveable;

    private boolean resizable = true;

    public Column()
    {
    }

    public Column(String header)
    {
      this.header = header;
    }

    public Column(String header, int width)
    {
      this.header = header;
      this.width = width;
    }

    public Column(String header, int width, int alignment)
    {
      this.header = header;
      this.width = width;
      this.alignment = alignment;
    }

    public String getHeader()
    {
      return header;
    }

    public Column<T> setHeader(String header)
    {
      this.header = header;
      return this;
    }

    public String getToolTipText()
    {
      return toolTipText;
    }

    public Column<T> setToolTipText(String toolTipText)
    {
      this.toolTipText = toolTipText;
      return this;
    }

    public int getWidth()
    {
      return width;
    }

    public Column<T> setWidth(int width)
    {
      this.width = width;
      return this;
    }

    public int getAlignment()
    {
      return alignment;
    }

    public Column<T> setAlignment(int alignment)
    {
      this.alignment = alignment;
      return this;
    }

    public Image getImage()
    {
      return image;
    }

    public Column<T> setImage(Image image)
    {
      this.image = image;
      return this;
    }

    public boolean isMoveable()
    {
      return moveable;
    }

    public Column<T> setMoveable(boolean moveable)
    {
      this.moveable = moveable;
      return this;
    }

    public boolean isResizable()
    {
      return resizable;
    }

    public Column<T> setResizable(boolean resizable)
    {
      this.resizable = resizable;
      return this;
    }

    public String getText(T element)
    {
      if (element == null)
      {
        return StringUtil.EMPTY;
      }

      return element.toString();
    }

    public Image getImage(T element)
    {
      return null;
    }

    public Color getForeground(T element)
    {
      return null;
    }

    public Color getBackground(T element)
    {
      return null;
    }

    public Font getFont(T element)
    {
      return null;
    }
  }
}
