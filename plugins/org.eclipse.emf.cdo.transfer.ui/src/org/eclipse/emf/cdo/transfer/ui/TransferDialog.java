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
import org.eclipse.emf.cdo.transfer.ui.swt.TransferComposite;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class TransferDialog extends TitleAreaDialog
{
  private final CDOTransfer transfer;

  private TransferComposite transferComposite;

  private InitializationState initializationState = InitializationState.MAPPING;

  private Button okButton;

  public TransferDialog(Shell parentShell, CDOTransfer transfer)
  {
    super(parentShell);
    setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.transfer = transfer;
  }

  public final CDOTransfer getTransfer()
  {
    return transfer;
  }

  public final TransferComposite getTransferComposite()
  {
    return transferComposite;
  }

  public InitializationState getInitializationState()
  {
    return initializationState;
  }

  public void setInitializationState(InitializationState initializationState)
  {
    this.initializationState = initializationState;

    if (okButton != null)
    {
      okButton.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          updateOkButtonEnabledment();
        }
      });
    }
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Transfer from " + transfer.getSourceSystem() + " to " + transfer.getTargetSystem());
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TRANSFER));

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
    okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    updateOkButtonEnabledment();
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  protected void updateOkButtonEnabledment()
  {
    okButton.setEnabled(initializationState == InitializationState.MAPPED);
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(1000, 800);
  }

  /**
   * @author Eike Stepper
   */
  public static enum InitializationState
  {
    MAPPING, MAPPED, FAILED, CANCELED
  }
}
