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

import org.eclipse.net4j.util.internal.ui.messages.Messages;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class RowIntrospectionProvider extends IntrospectionProvider
{
  public static final int DEFAULT_CATEGORY = 0;

  protected RowIntrospectionProvider(String id, String label)
  {
    super(id, label);
  }

  @Override
  public final void createColumns(TableViewer viewer)
  {
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_4"), 200); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_5"), 400); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_6"), 300); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_7"), 300); //$NON-NLS-1$
  }

  @Override
  public final Object[] getElements(Object parent) throws Exception
  {
    List<Row> rows = new ArrayList<>();
    fillRows(parent, rows);
    return rows.toArray();
  }

  protected abstract void fillRows(Object parent, List<Row> rows) throws Exception;

  @Override
  public final Object getObject(Object element) throws Exception
  {
    return ((Row)element).getValue();
  }

  @Override
  public final String getColumnText(Object element, int index) throws Exception
  {
    return ((Row)element).getColumn(index);
  }

  @Override
  public final Color getForeground(Object element)
  {
    return ((Row)element).getForeground();
  }

  @Override
  public final Color getBackground(Object element)
  {
    return ((Row)element).getBackground();
  }

  @Override
  public final ViewerComparator getComparator()
  {
    return new ViewerComparator()
    {
      @Override
      public int category(Object element)
      {
        return ((Row)element).getCategory();
      }
    };
  }

  /**
   * @author Eike Stepper
   */
  public static final class Row
  {
    private final String name;

    private final Object value;

    private final String declaredType;

    private final String concreteType;

    private final int category;

    private final Color foreground;

    private final Color background;

    public Row(String name, Object value, String declaredType, String concreteType, int category, Color foreground, Color background)
    {
      this.name = name;
      this.value = value;
      this.declaredType = declaredType;
      this.concreteType = concreteType;
      this.category = category;
      this.foreground = foreground;
      this.background = background;
    }

    public Row(String name, Object value, String declaredType, String concreteType)
    {
      this(name, value, declaredType, concreteType, DEFAULT_CATEGORY, null, null);
    }

    public String getName()
    {
      return name;
    }

    public Object getValue()
    {
      return value;
    }

    public String getDeclaredType()
    {
      return declaredType;
    }

    public String getConcreteType()
    {
      return concreteType;
    }

    public int getCategory()
    {
      return category;
    }

    public Color getForeground()
    {
      return foreground;
    }

    public Color getBackground()
    {
      return background;
    }

    public String getColumn(int index)
    {
      switch (index)
      {
      case 0:
        return name;

      case 1:
        return "" + value;

      case 2:
        return declaredType;

      case 3:
        return concreteType;

      default:
        return null;
      }
    }

    @Override
    public String toString()
    {
      return name + "=" + value;
    }
  }
}
