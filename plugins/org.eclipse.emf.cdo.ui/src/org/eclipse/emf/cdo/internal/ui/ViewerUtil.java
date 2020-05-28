/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public final class ViewerUtil
{
  public static final Object[] NO_CHILDREN = {};

  private ViewerUtil()
  {
  }

  public static void update(StructuredViewer viewer, Object element)
  {
    update(viewer, element, true);
  }

  public static void update(final StructuredViewer viewer, final Object element, boolean async)
  {
    if (viewer != null)
    {
      final Control control = viewer.getControl();
      if (!control.isDisposed())
      {
        Runnable runnable = new Runnable()
        {
          @Override
          public void run()
          {
            if (!control.isDisposed())
            {
              if (element instanceof Object[])
              {
                Object[] array = (Object[])element;
                viewer.update(array, null);
              }
              else if (element instanceof Collection)
              {
                Collection<?> collection = (Collection<?>)element;
                viewer.update(collection.toArray(), null);
              }
              else
              {
                viewer.update(element, null);
              }
            }
          }
        };

        Display display = control.getDisplay();
        if (async)
        {
          display.asyncExec(runnable);
        }
        else
        {
          display.syncExec(runnable);
        }
      }
    }
  }

  public static void refresh(StructuredViewer viewer, Object element)
  {
    refresh(viewer, element, true);
  }

  public static void refresh(final StructuredViewer viewer, final Object element, boolean async)
  {
    refresh(viewer, element, async, false);
  }

  public static void refresh(final StructuredViewer viewer, final Object element, boolean async, final boolean setSelectionBack)
  {
    if (viewer != null)
    {
      Control control = viewer.getControl();
      if (!control.isDisposed())
      {
        Runnable runnable = new Runnable()
        {
          @Override
          public void run()
          {
            if (!viewer.getControl().isDisposed())
            {
              ISelection selection = null;
              if (setSelectionBack)
              {
                selection = viewer.getSelection();
              }

              if (element == null)
              {
                viewer.refresh();
              }
              else if (element instanceof Collection)
              {
                Collection<?> collection = (Collection<?>)element;
                for (Object object : collection)
                {
                  viewer.refresh(object);
                }
              }
              else
              {
                viewer.refresh(element);
              }

              if (selection != null)
              {
                viewer.setSelection(selection);
              }
            }
          }
        };

        Display display = control.getDisplay();
        if (async)
        {
          display.asyncExec(runnable);
        }
        else
        {
          display.syncExec(runnable);
        }
      }
    }
  }

  public static void expand(final TreeViewer viewer, final Object element, final boolean expanded)
  {
    if (viewer != null)
    {
      Tree tree = viewer.getTree();
      if (!tree.isDisposed())
      {
        Display display = tree.getDisplay();
        display.asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (!viewer.getControl().isDisposed())
            {
              viewer.setExpandedState(element, expanded);
            }
          }
        });
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Pending
  {
    private final Object parent;

    private final String text;

    public Pending(Object parent, String text)
    {
      this.parent = parent;
      this.text = text;
    }

    public Object getParent()
    {
      return parent;
    }

    public String getText()
    {
      return text;
    }

    @Override
    public String toString()
    {
      return text;
    }
  }
}
