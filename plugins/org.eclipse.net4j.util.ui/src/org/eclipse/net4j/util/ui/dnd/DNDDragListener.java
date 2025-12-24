/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Eike Stepper
 */
public abstract class DNDDragListener<TYPE> extends DragSourceAdapter
{
  private Transfer[] transfers;

  private StructuredViewer viewer;

  /**
   * @since 3.0
   */
  protected DNDDragListener(Transfer[] transfers, StructuredViewer viewer)
  {
    this.transfers = transfers;
    this.viewer = viewer;
  }

  /**
   * @since 3.3
   */
  protected DNDDragListener(StructuredViewer viewer)
  {
    this.viewer = viewer;
  }

  /**
   * @since 3.0
   */
  public Transfer[] getTransfers()
  {
    return transfers;
  }

  /**
   * @since 3.3
   */
  public void setTransfers(Transfer[] transfers)
  {
    this.transfers = transfers;
  }

  public StructuredViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void dragSetData(DragSourceEvent event)
  {
    for (Transfer transfer : transfers)
    {
      if (transfer.isSupportedType(event.dataType))
      {
        IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
        event.data = getObject(selection);
        break;
      }
    }
  }

  @Override
  public void dragStart(DragSourceEvent event)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    event.doit = !viewer.getSelection().isEmpty() && getObject(selection) != null;
  }

  protected abstract TYPE getObject(IStructuredSelection selection);
}
