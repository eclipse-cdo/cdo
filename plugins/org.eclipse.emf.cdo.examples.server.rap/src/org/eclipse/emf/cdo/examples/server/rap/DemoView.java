/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.rap;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 */
public class DemoView extends ViewPart
{
  public static final String ID = "org.eclipse.emf.cdo.examples.server.rap.view";

  private TableViewer viewer;

  @Override
  public void createPartControl(Composite parent)
  {
    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    // Provide the input to the ContentProvider
    viewer.setInput(new String[] { "One", "Two", "Three" });
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  /**
   * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
   * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore
   * it and always show the same content (like Task List, for example).
   * 
   * @author Eike Stepper
   */
  class ViewContentProvider implements IStructuredContentProvider
  {
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object parent)
    {
      if (parent instanceof Object[])
      {
        return (Object[])parent;
      }
      return new Object[0];
    }
  }

  /**
   * @author Eike Stepper
   */
  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public String getColumnText(Object obj, int index)
    {
      return getText(obj);
    }

    public Image getColumnImage(Object obj, int index)
    {
      return getImage(obj);
    }

    @Override
    public Image getImage(Object obj)
    {
      return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }
  }
}
