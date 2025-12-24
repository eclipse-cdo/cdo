/*
 * Copyright (c) 2007-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.swt.graphics.Rectangle;
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

  private static final int WIDTH = 380;

  private static final int HEIGHT = 240;

  private IWorkbenchPage page;

  private SessionComposite sessionComposite;

  public OpenSessionDialog(IWorkbenchPage page)
  {
    super(page.getWorkbenchWindow().getShell());
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

    Rectangle bounds = page.getWorkbenchWindow().getShell().getBounds();
    int x = bounds.x + (bounds.width - WIDTH) / 2;
    int y = bounds.y + (bounds.height - HEIGHT) / 2;
    newShell.setBounds(x, y, WIDTH, HEIGHT);
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
