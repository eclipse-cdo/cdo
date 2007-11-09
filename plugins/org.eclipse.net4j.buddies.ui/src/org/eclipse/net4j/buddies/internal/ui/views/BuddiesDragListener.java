/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.internal.ui.dnd.BuddiesTransfer;
import org.eclipse.net4j.buddies.protocol.IBuddy;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class BuddiesDragListener extends DragSourceAdapter
{
  private StructuredViewer viewer;

  public BuddiesDragListener(StructuredViewer viewer)
  {
    this.viewer = viewer;
  }

  @Override
  public void dragSetData(DragSourceEvent event)
  {
    if (BuddiesTransfer.INSTANCE.isSupportedType(event.dataType))
    {
      event.data = getBuddies();
    }
  }

  @Override
  public void dragStart(DragSourceEvent event)
  {
    event.doit = !viewer.getSelection().isEmpty();
  }

  protected IBuddy[] getBuddies()
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    List<IBuddy> buddies = new ArrayList<IBuddy>();
    for (Iterator<Object> it = selection.iterator(); it.hasNext();)
    {
      Object element = it.next();
      if (element instanceof IBuddy)
      {
        IBuddy buddy = (IBuddy)element;
        buddies.add(buddy);
      }
    }
  
    return buddies.toArray(new IBuddy[buddies.size()]);
  }

  // @Override
  // public void dragFinished(DragSourceEvent event)
  // {
  // if (!event.doit) return;
  // // if the gadget was moved, remove it from the source viewer
  // if (event.detail == DND.DROP_MOVE)
  // {
  // IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
  // for (Iterator it = selection.iterator(); it.hasNext();)
  // {
  // ((Gadget)it.next()).setParent(null);
  // }
  // viewer.refresh();
  // }
  // }
}