/*
 * Copyright (c) 2007-2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
    Collection<IBuddy> buddies = new ArrayList<>();
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
