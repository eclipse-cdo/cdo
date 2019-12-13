/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.ui.swt;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A vertical {@link SashForm sash} that contains a {@link TransferTreeComposite} and a {@link TransferDetailsComposite}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferComposite extends SashForm implements ISelectionChangedListener
{
  private TransferTreeComposite transferTreeComposite;

  private TransferDetailsComposite transferDetailsComposite;

  public TransferComposite(Composite parent, CDOTransfer transfer)
  {
    super(parent, SWT.SMOOTH | SWT.VERTICAL);
    setLayout(new FillLayout());

    transferTreeComposite = new TransferTreeComposite(this, SWT.NONE, transfer);
    transferTreeComposite.getViewer().addSelectionChangedListener(this);

    transferDetailsComposite = new TransferDetailsComposite(this, SWT.NONE, transfer);
    selectionChanged(null);

    setWeights(new int[] { 2, 1 });
  }

  public CDOTransfer getTransfer()
  {
    if (transferTreeComposite != null)
    {
      CDOTransfer transfer = transferTreeComposite.getTransfer();
      if (transfer != null)
      {
        return transfer;
      }
    }

    if (transferDetailsComposite != null)
    {
      CDOTransfer transfer = transferDetailsComposite.getTransfer();
      if (transfer != null)
      {
        return transfer;
      }
    }

    return null;
  }

  public TransferTreeComposite getTransferTreeComposite()
  {
    return transferTreeComposite;
  }

  public TransferDetailsComposite getTransferDetailsComposite()
  {
    return transferDetailsComposite;
  }

  @Override
  public void selectionChanged(SelectionChangedEvent event)
  {
    CDOTransferMapping mapping = transferTreeComposite.getSelectedMapping();
    transferDetailsComposite.setMapping(mapping);
  }

  @Override
  public boolean setFocus()
  {
    return transferTreeComposite.setFocus();
  }
}
