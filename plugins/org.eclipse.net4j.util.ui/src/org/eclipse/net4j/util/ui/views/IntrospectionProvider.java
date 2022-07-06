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
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class IntrospectionProvider implements Comparable<IntrospectionProvider>
{
  public static final int DEFAULT_PRIORITY = 100;

  private final String id;

  private final String label;

  public IntrospectionProvider(String id, String label)
  {
    this.id = id;
    this.label = label;
  }

  public int getPriority()
  {
    return DEFAULT_PRIORITY;
  }

  public final String getId()
  {
    return id;
  }

  public final String getLabel()
  {
    return label;
  }

  public abstract boolean canHandle(Object object);

  public TableViewer createViewer(Composite parent)
  {
    TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setLayoutData(UIUtil.createGridData());
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    return viewer;
  }

  public abstract void createColumns(TableViewer viewer);

  protected static TableColumn createColumn(TableViewer viewer, String name, int width)
  {
    Table table = viewer.getTable();
    int index = table.getColumnCount();

    TableColumn column = new TableColumn(table, SWT.LEFT, index);
    column.setText(name);
    column.setWidth(width);
    column.setMoveable(true);
    column.setResizable(true);
    return column;
  }

  public abstract Object[] getElements(Object parent) throws Exception;

  public abstract Object getObject(Object element) throws Exception;

  public abstract String getColumnText(Object element, int index) throws Exception;

  public Image getColumnImage(Object element, int index) throws Exception
  {
    return null;
  }

  public Color getForeground(Object element)
  {
    return null;
  }

  public Color getBackground(Object element)
  {
    return null;
  }

  public ViewerComparator getComparator()
  {
    return null;
  }

  @Override
  public int compareTo(IntrospectionProvider o)
  {
    return Integer.compare(getPriority(), o.getPriority());
  }

  @Override
  public String toString()
  {
    return getLabel();
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.introspectionProviders";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract IntrospectionProvider create(String description) throws ProductCreationException;
  }
}
