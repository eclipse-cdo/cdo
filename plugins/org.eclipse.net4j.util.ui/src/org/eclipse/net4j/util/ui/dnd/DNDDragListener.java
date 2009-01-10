/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
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
  private Transfer transfer;

  private StructuredViewer viewer;

  public DNDDragListener(Transfer transfer, StructuredViewer viewer)
  {
    this.transfer = transfer;
    this.viewer = viewer;
  }

  public Transfer getTransfer()
  {
    return transfer;
  }

  public StructuredViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void dragSetData(DragSourceEvent event)
  {
    if (transfer.isSupportedType(event.dataType))
    {
      IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
      event.data = getObject(selection);
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
