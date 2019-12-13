/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.ui.swt.TransferComposite;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.om.monitor.SubProgressMonitor;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

/**
 * A dialog that contains a {@link TransferComposite}.
 *
 * @author Eike Stepper
 */
public class TransferDialog extends TitleAreaDialog
{
  private final CDOTransfer transfer;

  private TransferComposite transferComposite;

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

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Transfer from " + transfer.getSourceSystem() + " to " + transfer.getTargetSystem());
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TRANSFER));

    Composite area = (Composite)super.createDialogArea(parent);

    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(new FillLayout());

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
  protected void okPressed()
  {
    UIUtil.runWithProgress(new IRunnableWithProgress()
    {
      @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        try
        {
          transfer.perform(monitor);
        }
        catch (OperationCanceledException ex)
        {
          throw new InterruptedException();
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new InvocationTargetException(ex);
        }
      }
    });

    super.okPressed();
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(1000, 800);
  }

  public static boolean open(Shell shell, final List<CDOTransferElement> sourceElements, final CDOTransferElement targetElement)
  {
    final CDOTransferSystem sourceSystem = sourceElements.get(0).getSystem();
    final CDOTransferSystem targetSystem = targetElement.getSystem();

    final CDOTransfer transfer = new CDOTransfer(sourceSystem, targetSystem);
    transfer.setTargetPath(targetElement.getPath());

    initializeTransfer(transfer, sourceElements);

    TransferDialog dialog = new TransferDialog(shell, transfer);
    return dialog.open() == TransferDialog.OK;
  }

  public static void initializeTransfer(final CDOTransfer transfer, final Collection<CDOTransferElement> sourceElements)
  {
    UIUtil.runWithProgress(new IRunnableWithProgress()
    {
      @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        try
        {
          CDOTransferSystem sourceSystem = transfer.getSourceSystem();
          CDOTransferSystem targetSystem = transfer.getTargetSystem();
          monitor.beginTask("Initialize transfer from " + sourceSystem + " to " + targetSystem, sourceElements.size());

          for (CDOTransferElement sourceElement : sourceElements)
          {
            transfer.map(sourceElement, new SubProgressMonitor(monitor, 1));
          }
        }
        catch (OperationCanceledException ex)
        {
          throw new InterruptedException();
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new InvocationTargetException(ex);
        }
        finally
        {
          monitor.done();
        }
      }
    });
  }
}
