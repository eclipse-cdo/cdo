/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.ui.dnd.BuddiesTransfer;
import org.eclipse.net4j.util.ui.dnd.DNDDragListener;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class BuddiesDragListener extends DNDDragListener<IBuddy[]>
{
  private static final Transfer[] TRANSFERS = new Transfer[] { BuddiesTransfer.INSTANCE };

  protected BuddiesDragListener(StructuredViewer viewer)
  {
    super(TRANSFERS, viewer);
  }

  @Override
  protected IBuddy[] getObject(IStructuredSelection selection)
  {
    Collection<IBuddy> buddies = new ArrayList<IBuddy>();
    for (Iterator<?> it = selection.iterator(); it.hasNext();)
    {
      Object element = it.next();
      if (element instanceof IBuddy)
      {
        IBuddy buddy = (IBuddy)element;
        buddies.add(buddy);
      }
    }

    if (buddies.isEmpty())
    {
      return null;
    }

    return buddies.toArray(new IBuddy[buddies.size()]);
  }

  public static void support(StructuredViewer viewer)
  {
    viewer.addDragSupport(DND.DROP_MOVE, TRANSFERS, new BuddiesDragListener(viewer));
  }
}
