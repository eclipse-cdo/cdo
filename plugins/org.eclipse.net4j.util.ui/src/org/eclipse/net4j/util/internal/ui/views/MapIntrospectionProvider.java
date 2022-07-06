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
import org.eclipse.jface.viewers.ViewerComparator;

import java.util.Map;

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
    if (parent instanceof Map<?, ?>)
    {
      Map<?, ?> map = (Map<?, ?>)parent;
      return map.entrySet().toArray();
    }

    return null;
  }

  @Override
  public Object getObject(Object element) throws Exception
  {
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>)element;
    return entry.getValue();
  }

  @Override
  public String getColumnText(Object element, int index) throws Exception
  {
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>)element;

    Object key = entry.getKey();
    Object value = entry.getValue();

    switch (index)
    {
    case 0:
      return key == null ? Messages.getString("Net4jIntrospectorView_27") : key.toString(); //$NON-NLS-1$
    case 1:
      return value == null ? Messages.getString("Net4jIntrospectorView_28") : value.toString(); //$NON-NLS-1$
    case 2:
      return value == null ? Messages.getString("Net4jIntrospectorView_29") : value.getClass().getName(); //$NON-NLS-1$

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
