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
package org.eclipse.net4j.util.ui.dnd;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Eike Stepper
 */
public abstract class DNDDropAdapter<TYPE> extends ViewerDropAdapter
{
  private Transfer transfer;

  private boolean dropBetweenEnabled;

  protected DNDDropAdapter(Transfer transfer, StructuredViewer viewer)
  {
    super(viewer);
    this.transfer = transfer;
  }

  public Transfer getTransfer()
  {
    return transfer;
  }

  @Override
  public StructuredViewer getViewer()
  {
    return (StructuredViewer)super.getViewer();
  }

  public boolean isDropBetweenEnabled()
  {
    return dropBetweenEnabled;
  }

  public void setDropBetweenEnabled(boolean dropBetweenEnabled)
  {
    this.dropBetweenEnabled = dropBetweenEnabled;
  }

  @Override
  protected int determineLocation(DropTargetEvent event)
  {
    int location = super.determineLocation(event);
    if (location == LOCATION_BEFORE || location == LOCATION_AFTER)
    {
      if (!dropBetweenEnabled)
      {
        location = LOCATION_ON;
      }
    }

    return location;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean performDrop(Object data)
  {
    Object target = getCurrentTarget();
    if (target == null)
    {
      target = getViewer().getInput();
    }

    return performDrop((TYPE)data, target);
  }

  @Override
  public boolean validateDrop(Object target, int operation, TransferData type)
  {
    return transfer.isSupportedType(type) && validateTarget(target, operation);
  }

  protected abstract boolean performDrop(TYPE data, Object target);

  protected abstract boolean validateTarget(Object target, int operation);
}
