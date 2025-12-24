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
  private Transfer[] transfers;

  /**
   * Specifies if dropping between two viewer elements is allowed.
   */
  private boolean dropBetweenEnabled;

  /**
   * @since 3.0
   */
  protected DNDDropAdapter(Transfer[] transfers, StructuredViewer viewer)
  {
    super(viewer);
    this.transfers = transfers;
  }

  /**
   * @since 3.3
   */
  protected DNDDropAdapter(StructuredViewer viewer)
  {
    super(viewer);
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
  protected void setTransfers(Transfer[] transfers)
  {
    this.transfers = transfers;
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
    if (target != null && !validateTarget(target, operation))
    {
      return false;
    }

    return validateTransfer(type);
  }

  /**
   * @since 3.3
   */
  protected boolean validateTransfer(TransferData type)
  {
    for (Transfer transfer : getTransfers())
    {
      if (transfer.isSupportedType(type))
      {
        return true;
      }
    }

    return false;
  }

  protected abstract boolean validateTarget(Object target, int operation);

  protected abstract boolean performDrop(TYPE data, Object target);
}
