/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Eike Stepper
 */
public final class ViewerUtil
{
  public static final Object[] NO_CHILDREN = {};

  private ViewerUtil()
  {
  }

  public static void refresh(StructuredViewer viewer, Object element)
  {
    refresh(viewer, element, true);
  }

  public static void refresh(final StructuredViewer viewer, final Object element, boolean async)
  {
    if (viewer != null)
    {
      Control control = viewer.getControl();
      if (!control.isDisposed())
      {
        Runnable runnable = new Runnable()
        {
          public void run()
          {
            if (!viewer.getControl().isDisposed())
            {
              if (element == null)
              {
                viewer.refresh();
              }
              else
              {
                viewer.refresh(element);
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
  }
}
