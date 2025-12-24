/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public final class MapIntrospectionProvider extends IntrospectionProvider
{
  public MapIntrospectionProvider()
  {
    super("java.util.Map", "Map");
  }

  @Override
  public void open(Event selectionEvent, Object parent, Object element, Consumer<Object> introspector)
  {
    if ((selectionEvent.stateMask & SWT.CTRL) != 0)
    {
      Map<?, ?> map = (Map<?, ?>)parent;
      NameAndValue nameAndValue = (NameAndValue)element;

      for (Map.Entry<?, ?> entry : map.entrySet())
      {
        if (new NameAndValue(entry).equals(nameAndValue))
        {
          super.open(selectionEvent, parent, new NameAndValue(nameAndValue.getName(), entry.getKey()), introspector);
          return;
        }
      }
    }

    super.open(selectionEvent, parent, element, introspector);
  }

  @Override
  public boolean canHandle(Object object)
  {
    return object instanceof Map<?, ?>;
  }

  @Override
  public void createColumns(TableViewer viewer)
  {
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_8"), 200); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_9"), 400); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_10"), 300); //$NON-NLS-1$
  }

  @Override
  public Object[] getElements(Object parent) throws Exception
  {
    Map<?, ?> map = (Map<?, ?>)parent;

    NameAndValue[] result = new NameAndValue[map.size()];
    int index = 0;

    for (Map.Entry<?, ?> entry : map.entrySet())
    {
      result[index++] = new NameAndValue(entry);
    }

    return result;
  }

  @Override
  public Object getElementByName(Object parent, String name) throws Exception
  {
    Object value = ((Map<?, ?>)parent).get(name);
    if (value != null)
    {
      return new NameAndValue(name, value);
    }

    return null;
  }

  @Override
  public NameAndValue getNameAndValue(Object element) throws Exception
  {
    return (NameAndValue)element;
  }

  @Override
  public String getColumnText(Object element, int index) throws Exception
  {
    NameAndValue nameAndValue = (NameAndValue)element;

    switch (index)
    {
    case 0:
      return nameAndValue.getName();

    case 1:
      return formatValue(nameAndValue.getValue());

    case 2:
      return getClassName(nameAndValue.getValue());

    default:
      return null;
    }
  }

  @Override
  public ViewerComparator getComparator()
  {
    return new ViewerComparator();
  }
}
