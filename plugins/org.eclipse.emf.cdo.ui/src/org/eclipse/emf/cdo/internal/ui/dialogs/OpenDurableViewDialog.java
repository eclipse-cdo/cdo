/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class OpenDurableViewDialog extends TitleAreaDialog
{
  public static final String TITLE = Messages.getString("OpenDurableViewDialog.0"); //$NON-NLS-1$

  private IWorkbenchPage page;

  private Text idControl;

  private String areaID;

  public OpenDurableViewDialog(IWorkbenchPage page)
  {
    super(page.getWorkbenchWindow().getShell());
    this.page = page;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public String getAreaID()
  {
    return areaID;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TIME_SELECTION));

    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(3, false));

    new Label(composite, SWT.NONE).setText(Messages.getString("OpenDurableViewDialog.1")); //$NON-NLS-1$
    idControl = new Text(composite, SWT.BORDER);
    idControl.setLayoutData(UIUtil.createGridData(true, false));

    return composite;
  }

  @Override
  protected void okPressed()
  {
    areaID = idControl.getText();
    super.okPressed();
  }
}
