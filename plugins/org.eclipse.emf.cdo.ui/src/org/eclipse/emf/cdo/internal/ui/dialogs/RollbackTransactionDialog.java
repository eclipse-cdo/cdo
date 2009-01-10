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

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.ui.UIUtil;

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

  protected String formatMessage()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("This transaction contains:\n");

    append(builder, transaction.getNewResources().size(), "new resource");
    append(builder, transaction.getNewObjects().size(), "new object");
    append(builder, transaction.getDirtyObjects().size(), "dirty object");
    append(builder, transaction.getDetachedObjects().size(), "detached object");

    builder.append("\n\nAre you sure to rollback this transaction?");
    return builder.toString();
  }

  private void append(StringBuilder builder, int count, String label)
  {
    if (count > 0)
    {
      builder.append("\n- ");
      builder.append(count);
      builder.append(" ");
      builder.append(label);
      if (count > 1)
      {
        builder.append("s");
      }
    }
  }
}
