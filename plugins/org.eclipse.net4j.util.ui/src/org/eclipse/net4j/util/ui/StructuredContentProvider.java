/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public abstract class StructuredContentProvider<INPUT> extends org.eclipse.jface.viewers.ViewerSorter implements IStructuredContentProvider, IListener
{
  private StructuredViewer viewer;

  private INPUT input;

  private Font italicFont;

  private Font boldFont;

  public StructuredContentProvider()
  {
  }

  @Override
  public void dispose()
  {
    if (input != null)
    {
      disconnectInput(input);
      input = null;
    }

    if (italicFont != null)
    {
      italicFont.dispose();
      italicFont = null;
    }

    if (boldFont != null)
    {
      boldFont.dispose();
      boldFont = null;
    }
  }

  public INPUT getInput()
  {
    return input;
  }

  public StructuredViewer getViewer()
  {
    return viewer;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    this.viewer = (StructuredViewer)viewer;
    if (newInput != input)
    {
      if (input != null)
      {
        disconnectInput(input);
      }

      try
      {
        input = (INPUT)newInput;
        if (input != null)
        {
          connectInput(input);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        input = null;
      }
    }
  }

  protected void connectInput(INPUT input)
  {
  }

  protected void disconnectInput(INPUT input)
  {
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    refreshViewer(true);
  }

  /**
   * @since 3.0
   */
  public void refreshViewer(boolean updateLabels)
  {
    UIUtil.refreshElement(viewer, null, updateLabels);
  }

  /**
   * @since 3.1
   */
  public void refreshElement(Object element, boolean updateLabels)
  {
    UIUtil.refreshElement(viewer, element, updateLabels);
  }

  /**
   * @since 3.1
   * @deprecated Use {@link #refreshElement(Object, boolean)}
   */
  @Deprecated
  public void refreshSynced(Object element, boolean updateLabels)
  {
    refreshElement(element, updateLabels);
  }

  /**
   * @since 3.1
   */
  public void updateLabels(Object element)
  {
    UIUtil.updateElements(viewer, element);
  }

  /**
   * @since 3.1
   */
  public void revealElement(final Object element)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            viewer.reveal(element);
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 3.1
   */
  public void selectElement(final Object element, final boolean reveal)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            viewer.setSelection(new StructuredSelection(element), reveal);
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 3.3
   */
  public void expandElement(final Object element, final int level)
  {
    if (element != null)
    {
      if (getViewer() instanceof TreeViewer)
      {
        final TreeViewer viewer = (TreeViewer)getViewer();

        try
        {
          getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              try
              {
                viewer.expandToLevel(element, level);
              }
              catch (RuntimeException ignore)
              {
              }
            }
          });
        }
        catch (RuntimeException ignore)
        {
        }
      }
    }
  }

  protected Display getDisplay()
  {
    Display display = viewer.getControl().getDisplay();
    if (display == null)
    {
      display = UIUtil.getDisplay();
    }

    return display;
  }

  /**
   * @since 3.1
   */
  protected synchronized Font getItalicFont()
  {
    if (italicFont == null && viewer != null)
    {
      italicFont = UIUtil.getItalicFont(viewer.getControl());
    }

    return italicFont;
  }

  /**
   * @since 3.3
   */
  protected synchronized Font getBoldFont()
  {
    if (boldFont == null && viewer != null)
    {
      boldFont = UIUtil.getBoldFont(viewer.getControl());
    }

    return boldFont;
  }
}
