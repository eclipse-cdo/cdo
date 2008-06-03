/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ItemsProcessor
{
  private CDOView view;

  public ItemsProcessor(CDOView view)
  {
    this.view = view;
  }

  public CDOView getView()
  {
    return view;
  }

  public void processCDOObjects(TreeViewer viewer)
  {
    processCDOObjects(viewer, null);
  }

  public void processCDOObjects(final TreeViewer viewer, final Set<CDOID> ids)
  {
    try
    {
      viewer.getControl().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            processObject(viewer, ids, viewer.getInput());
            processItems(viewer, ids, viewer.getTree().getItems());
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

  protected InternalCDOObject getCDOObject(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object != null && view != null)
    {
      return FSMUtil.adapt(object, view);
    }

    return null;
  }

  protected abstract void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject);

  private void processItems(TreeViewer viewer, Set<CDOID> ids, TreeItem[] items)
  {
    for (TreeItem item : items)
    {
      Object object = item.getData();
      processObject(viewer, ids, object);
      if (item.getItemCount() != 0)
      {
        processItems(viewer, ids, item.getItems());
      }
    }
  }

  private void processObject(TreeViewer viewer, Set<CDOID> ids, Object object)
  {
    InternalCDOObject cdoObject = getCDOObject(object);
    if (cdoObject != null)
    {
      if (ids == null || ids.contains(cdoObject.cdoID()))
      {
        processCDOObject(viewer, cdoObject);
      }
    }
  }
}
