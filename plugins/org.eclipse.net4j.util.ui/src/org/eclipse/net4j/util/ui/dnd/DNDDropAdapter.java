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
package org.eclipse.net4j.util.ui.dnd;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Eike Stepper
 */
public abstract class DNDDropAdapter<TYPE> extends ViewerDropAdapter
{
  private Transfer transfer;

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
  public boolean validateDrop(Object target, int op, TransferData type)
  {
    return transfer.isSupportedType(type) && validateTarget(target);
  }

  protected abstract boolean performDrop(TYPE data, Object target);

  protected abstract boolean validateTarget(Object target);
}