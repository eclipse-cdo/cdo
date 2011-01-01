/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 */
public abstract class StructuredContentProvider<INPUT> implements IStructuredContentProvider, IListener
{
  private StructuredViewer viewer;

  private INPUT input;

  private Font italicFont;

  public StructuredContentProvider()
  {
  }

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
  }

  public INPUT getInput()
  {
    return input;
  }

  public StructuredViewer getViewer()
  {
    return viewer;
  }

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

  public void notifyEvent(IEvent event)
  {
    refreshViewer(true);
  }

  /**
   * @since 3.0
   */
  public void refreshViewer(boolean updateLabels)
  {
    refreshElement(null, updateLabels);
  }

  /**
   * @since 3.1
   */
  public void refreshElement(final Object element, final boolean updateLabels)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            refreshSynced(element, updateLabels);
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
  public void refreshSynced(final Object element, final boolean updateLabels)
  {
    if (element != null && element != input)
    {
      viewer.refresh(element, updateLabels);
    }
    else
    {
      viewer.refresh(updateLabels);
    }
  }

  /**
   * @since 3.1
   */
  public void updateLabels(final Object element)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.update(element, null);
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
  public void revealElement(final Object element)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
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
      Control control = viewer.getControl();
      FontData data = control.getFont().getFontData()[0];

      italicFont = new Font(control.getDisplay(), data.getName(), data.getHeight(), data.getStyle() | SWT.ITALIC);
    }

    return italicFont;
  }
}
