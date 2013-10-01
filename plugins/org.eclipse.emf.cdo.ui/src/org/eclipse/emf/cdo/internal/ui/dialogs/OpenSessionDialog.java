/*
 * Copyright (c) 2007-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class OpenSessionDialog extends TitleAreaDialog
{
  public static final String TITLE = Messages.getString("OpenSessionDialog.0"); //$NON-NLS-1$

  private IWorkbenchPage page;

  private SessionComposite sessionComposite;

  public OpenSessionDialog(IWorkbenchPage page)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public SessionComposite getSessionComposite()
  {
    return sessionComposite;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
    newShell.setSize(380, 240);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_OPEN_SESSION));
    setMessage(Messages.getString("OpenSessionDialog.1"));

    sessionComposite = new SessionComposite(parent, SWT.NONE);
    return sessionComposite;
  }

  @Override
  protected void okPressed()
  {
    sessionComposite.rememberSettings();
    super.okPressed();
  }
}
