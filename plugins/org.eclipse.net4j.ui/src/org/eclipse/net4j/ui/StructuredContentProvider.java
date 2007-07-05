/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 */
public abstract class StructuredContentProvider<INPUT> implements IStructuredContentProvider, IListener
{
  private StructuredViewer viewer;

  private INPUT input;

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
  }

  public INPUT getInput()
  {
    return input;
  }

  public StructuredViewer getViewer()
  {
    return viewer;
  }

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
        connectInput(input);
      }
      catch (Exception ex)
      {
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

  protected void refreshViewer(boolean updateLabels)
  {
    refreshElement(null, updateLabels);
  }

  protected void refreshElement(final Object element, final boolean updateLabels)
  {
    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            if (element != null)
            {
              viewer.refresh(element, updateLabels);
            }
            else
            {
              viewer.refresh(updateLabels);
            }
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  protected void revealElement(final Object element)
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
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  protected Display getDisplay()
  {
    Display display = viewer.getControl().getDisplay();
    if (display == null)
    {
      display = Display.getCurrent();
    }

    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      throw new IllegalStateException("display == null");
    }

    return display;
  }
}
