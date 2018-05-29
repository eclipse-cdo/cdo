/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

/**
 * @author Eike Stepper
 */
public class TransferDropAdapterAssistant extends CommonDropAdapterAssistant
{
  public TransferDropAdapterAssistant()
  {
    System.out.println("TransferDropAdapterAssistant");
  }

  @Override
  public IStatus validateDrop(Object target, int operation, TransferData transferType)
  {
    return null;
  }

  @Override
  public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget)
  {
    return null;
  }
}
