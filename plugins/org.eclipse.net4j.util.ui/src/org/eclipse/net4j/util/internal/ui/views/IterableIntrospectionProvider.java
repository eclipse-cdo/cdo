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
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_11"), 400); //$NON-NLS-1$
    createColumn(viewer, Messages.getString("Net4jIntrospectorView_12"), 300); //$NON-NLS-1$
  }

  @Override
  public Object[] getElements(Object parent) throws Exception
  {
    if (parent instanceof Iterable<?>)
    {
      Iterable<?> iterable = (Iterable<?>)parent;

      List<Object> result = new ArrayList<>();
      for (Object object : iterable)
      {
        result.add(object);
      }

      return result.toArray();
    }

    return null;
  }

  @Override
  public Object getObject(Object element) throws Exception
  {
    return element;
  }

  @Override
  public String getColumnText(Object element, int index) throws Exception
  {
    switch (index)
    {
    case 0:
      return element == null ? Messages.getString("Net4jIntrospectorView_21") : element.toString(); //$NON-NLS-1$
    case 1:
      return element == null ? Messages.getString("Net4jIntrospectorView_22") : element.getClass().getName(); //$NON-NLS-1$

    default:
      return null;
    }
  }
}
