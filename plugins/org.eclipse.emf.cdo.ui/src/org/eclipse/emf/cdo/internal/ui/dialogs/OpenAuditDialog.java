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

import org.eclipse.emf.cdo.internal.ui.SharedIcons;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Eike Stepper
 */
public class OpenAuditDialog extends TitleAreaDialog
{
  public static final String TITLE = "Open Audit";

  private IWorkbenchPage page;

  private DateTime dateControl;

  private DateTime timeControl;

  private long timeStamp;

  public OpenAuditDialog(IWorkbenchPage page)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(3, false));

    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    new Label(composite, SWT.NONE).setText("Target Time:");
    dateControl = new DateTime(composite, SWT.DATE);
    timeControl = new DateTime(composite, SWT.TIME);

    return composite;
  }

  @Override
  protected void okPressed()
  {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, dateControl.getYear());
    calendar.set(Calendar.MONTH, dateControl.getMonth());
    calendar.set(Calendar.DATE, dateControl.getDay());
    calendar.set(Calendar.HOUR_OF_DAY, timeControl.getHours());
    calendar.set(Calendar.MINUTE, timeControl.getMinutes());
    calendar.set(Calendar.SECOND, timeControl.getSeconds());

    timeStamp = calendar.getTimeInMillis();
    super.okPressed();
  }
}
