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
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.CDOTransaction;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RollbackTransactionDialog extends TitleAreaDialog
{
  // public static final String TITLE = "Rollback Transaction";

  public static final int REMOTE = CANCEL + 1;

  public static final int LOCAL = CANCEL + 2;

  private static final int REMOTE_ID = IDialogConstants.CLIENT_ID + REMOTE;

  private static final int LOCAL_ID = IDialogConstants.CLIENT_ID + LOCAL;

  private IWorkbenchPage page;

  private String title;

  private String description;

  private CDOTransaction transaction;

  public RollbackTransactionDialog(IWorkbenchPage page, String title, String description, CDOTransaction transaction)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    this.title = title;
    this.description = description;
    this.transaction = transaction;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(title);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(1, false));

    setTitle(description);
    setTitleImage(getShell().getDisplay().getSystemImage(SWT.ICON_QUESTION));

    Label label = new Label(composite, SWT.NONE);
    label.setText(formatMessage());
    label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, REMOTE_ID, "Remote Rollback", true);
    createButton(parent, LOCAL_ID, "Local Rollback", false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == REMOTE_ID)
    {
      setReturnCode(REMOTE);
      close();
    }
    else if (buttonId == LOCAL_ID)
    {
      setReturnCode(LOCAL);
      close();
    }
    else
    {
      super.buttonPressed(buttonId);
    }
  }

  protected String formatMessage()
  {
    int newObjects = transaction.getNewObjects().size();
    int dirtyObjects = transaction.getDirtyObjects().size();
    int count = (newObjects > 0 ? 1 : 0) + (dirtyObjects > 0 ? 1 : 0);

    StringBuilder builder = new StringBuilder();
    builder.append("This transaction contains ");

    if (newObjects > 0)
    {
      builder.append(newObjects);
      builder.append(" new object");
      if (newObjects > 1)
      {
        builder.append("s");
      }
    }

    if (dirtyObjects > 0)
    {
      if (count > 0)
      {
        builder.append(" and ");
      }

      builder.append(dirtyObjects);
      builder.append(" dirty object");
      if (dirtyObjects > 1)
      {
        builder.append("s");
      }
    }

    builder.append(".\n\nBe careful, rolling back to local state can result\n"
        + "in visible state that is different from the remote state!");
    builder.append("\n\nAre you sure to rollback this transaction?");
    return builder.toString();
  }
}
