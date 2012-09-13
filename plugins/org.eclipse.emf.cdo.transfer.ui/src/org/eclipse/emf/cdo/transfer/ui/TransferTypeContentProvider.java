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
package org.eclipse.emf.cdo.transfer.ui;

import org.eclipse.emf.cdo.transfer.CDOTransferType;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 */
public class TransferTypeContentProvider implements IStructuredContentProvider
{
  public static final CDOTransferType[] NO_TANSFER_TYPES = {};

  public TransferTypeContentProvider()
  {
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    // Do nothing
  }

  public void dispose()
  {
    // Do nothing
  }

  public Object[] getElements(Object inputElement)
  {
    if (inputElement instanceof CDOTransferType)
    {
      CDOTransferType transferType = (CDOTransferType)inputElement;
      return new CDOTransferType[] { transferType };
    }

    if (inputElement instanceof CDOTransferType[])
    {
      CDOTransferType[] transferTypes = (CDOTransferType[])inputElement;
      return transferTypes;
    }

    return NO_TANSFER_TYPES;
  }
}
