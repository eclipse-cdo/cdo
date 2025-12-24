/*
 * Copyright (c) 2007-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

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

  public void processCDOObjects(final TreeViewer viewer, final Set<? extends CDOObject> ids)
  {
    try
    {
      viewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        @Override
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

  private void processItems(TreeViewer viewer, Set<? extends CDOObject> ids, TreeItem[] items)
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

  private void processObject(TreeViewer viewer, Set<? extends CDOObject> ids, Object object)
  {
    InternalCDOObject cdoObject = getCDOObject(object);
    if (ids.contains(cdoObject))
    {
      processCDOObject(viewer, cdoObject);
    }
  }
}
