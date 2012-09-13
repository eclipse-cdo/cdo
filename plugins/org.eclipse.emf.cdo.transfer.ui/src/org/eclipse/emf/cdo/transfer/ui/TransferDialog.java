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

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.transfer.ui.swt.TransferComposite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class TransferDialog extends TitleAreaDialog
{
  private CDOTransfer transfer;

  private TransferComposite transferComposite;

  private Image wizban;

  public TransferDialog(Shell parentShell, CDOTransfer transfer)
  {
    super(parentShell);
    setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.transfer = transfer;
  }

  public TransferComposite getTransferComposite()
  {
    return transferComposite;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    ImageDescriptor descriptor = OM.Activator.INSTANCE.loadImageDescriptor("icons/transfer_wiz.png");
    Display display = parent.getDisplay();
    wizban = descriptor.createImage(display);

    setTitle("Transfer from " + transfer.getSourceSystem() + " to " + transfer.getTargetSystem());
    setTitleImage(wizban);

    Composite area = (Composite)super.createDialogArea(parent);

    Composite container = new Composite(area, SWT.NONE);
    container.setLayout(new FillLayout());
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    transferComposite = new TransferComposite(container, transfer);

    return area;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(1000, 800);
  }

  @Override
  public boolean close()
  {
    if (wizban != null)
    {
      wizban.dispose();
      wizban = null;
    }

    return super.close();
  }
}
