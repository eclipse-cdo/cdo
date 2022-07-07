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
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider;

import org.eclipse.jface.viewers.TableViewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class IterableIntrospectionProvider extends IntrospectionProvider
{
  public IterableIntrospectionProvider()
  {
    super("java.lang.Iterable", "Iterable");
  }

  @Override
  public boolean canHandle(Object object)
  {
    return object instanceof Iterable<?>;
  }

  @Override
  public void createColumns(TableViewer viewer)
  {
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_13"), 50); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_11"), 400); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_12"), 300); //$NON-NLS-1$
  }

  @Override
  public Object[] getElements(Object parent) throws Exception
  {
    if (parent instanceof Iterable<?>)
    {
      Iterable<?> iterable = (Iterable<?>)parent;

      List<NameAndValue> result = new ArrayList<>();
      int index = 0;

      for (Object object : iterable)
      {
        result.add(new NameAndValue(index++, object));
      }

      return result.toArray();
    }

    return null;
  }

  @Override
  public Object getElementByName(Object parent, String name) throws Exception
  {
    Iterable<?> iterable = (Iterable<?>)parent;
    Iterator<?> iterator = iterable.iterator();

    int index = Integer.parseInt(name);
    Object value = null;

    for (int i = 0; i < index; i++)
    {
      if (!iterator.hasNext())
      {
        return null;
      }

      value = iterator.next();
    }

    return new NameAndValue(index, value);
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
}
