/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class IntrospectionProvider implements Comparable<IntrospectionProvider>
{
  public static final int DEFAULT_PRIORITY = 100;

  private static final List<ValueFormatter> FORMATTERS = new ArrayList<>();

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

  public void open(Event selectionEvent, Object parent, Object element, Consumer<Object> introspector)
  {
    introspector.accept(element);
  }

  public void attachListener(TableViewer viewer, Object value)
  {
    if (value instanceof INotifier)
    {
      INotifier notifier = (INotifier)value;

      ValueListener listener = new ValueListener(viewer);
      notifier.addListener(listener);
    }
  }

  public void detachListener(TableViewer viewer, Object value)
  {
    EventUtil.removeListeners(value, listener -> listener.getClass() == ValueListener.class);
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

  public abstract Object[] getElements(Object parent) throws Exception;

  public abstract Object getElementByName(Object parent, String name) throws Exception;

  public abstract NameAndValue getNameAndValue(Object element) throws Exception;

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
  public final int compareTo(IntrospectionProvider o)
  {
    return Integer.compare(getPriority(), o.getPriority());
  }

  @Override
  public final String toString()
  {
    return getLabel();
  }

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

  protected static String getClassName(Object value)
  {
    if (value == null)
    {
      return ""; //$NON-NLS-1$
    }

    Class<?> type = value.getClass();
    return getName(type);
  }

  protected static String getName(Class<?> type)
  {
    if (type.isArray())
    {
      Class<?> componentType = type.getComponentType();
      return getName(componentType) + "[]"; //$NON-NLS-1$
    }

    return type.getName();
  }

  protected static String formatValue(Object value)
  {
    for (ValueFormatter formatter : FORMATTERS)
    {
      if (formatter.canHandle(value))
      {
        try
        {
          return formatter.formatValue(value);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    if (value != null)
    {
      Class<? extends Object> valueClass = value.getClass();
      if (valueClass.isArray())
      {
        if (valueClass == byte[].class)
        {
          return Arrays.toString((byte[])value);
        }

        if (valueClass == short[].class)
        {
          return Arrays.toString((short[])value);
        }

        if (valueClass == int[].class)
        {
          return Arrays.toString((int[])value);
        }

        if (valueClass == long[].class)
        {
          return Arrays.toString((long[])value);
        }

        if (valueClass == char[].class)
        {
          return Arrays.toString((char[])value);
        }

        if (valueClass == float[].class)
        {
          return Arrays.toString((float[])value);
        }

        if (valueClass == double[].class)
        {
          return Arrays.toString((double[])value);
        }

        if (valueClass == boolean[].class)
        {
          return Arrays.toString((boolean[])value);
        }

        return Arrays.deepToString((Object[])value);
      }
    }

    return "" + value;
  }

  static
  {
    IPluginContainer.INSTANCE.forEachElement(ValueFormatter.Factory.PRODUCT_GROUP, ValueFormatter.class, FORMATTERS::add);
    FORMATTERS.sort(null);
  }

  /**
   * @author Eike Stepper
   */
  private static final class ValueListener implements IListener
  {
    private final TableViewer viewer;

    public ValueListener(TableViewer viewer)
    {
      this.viewer = viewer;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      UIUtil.refreshViewer(viewer);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.introspectionProviders"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract IntrospectionProvider create(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static class NameAndValue
  {
    private final String name;

    private final Object value;

    public NameAndValue(String name, Object value)
    {
      this.name = name;
      this.value = value;
    }

    public NameAndValue(int index, Object value)
    {
      this(Integer.toString(index), value);
    }

    public NameAndValue(Map.Entry<?, ?> entry)
    {
      this("" + entry.getKey(), entry.getValue());
    }

    public String getName()
    {
      return name;
    }

    public Object getValue()
    {
      return value;
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (!(obj instanceof NameAndValue))
      {
        return false;
      }

      NameAndValue other = (NameAndValue)obj;
      return Objects.equals(name, other.name);
    }

    @Override
    public String toString()
    {
      return name + "=" + value;
    }
  }
}
