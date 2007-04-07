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
package org.eclipse.net4j.internal.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class ItemProvider<INPUT> extends LabelProvider implements IStructuredContentProvider,
    ITreeContentProvider
{
  protected static final Object[] NO_CHILDREN = new Object[0];

  private StructuredViewer viewer;

  private INPUT input;

  public ItemProvider()
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

  public StructuredViewer getViewer()
  {
    return viewer;
  }

  public INPUT getInput()
  {
    return input;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    if (viewer instanceof StructuredViewer)
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
  }

  public Object[] getElements(Object parent)
  {
    return getChildren(parent);
  }

  public boolean hasChildren(Object parent)
  {
    return getChildren(parent).length != 0;
  }

  public String getText(Object obj)
  {
    return obj.toString();
  }

  public Image getImage(Object obj)
  {
    String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
    return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
  }

  protected void connectInput(INPUT input)
  {
  }

  protected void disconnectInput(INPUT input)
  {
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
            ignore.printStackTrace();
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