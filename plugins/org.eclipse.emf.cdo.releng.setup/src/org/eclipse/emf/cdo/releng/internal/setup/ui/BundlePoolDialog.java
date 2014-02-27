/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public final class BundlePoolDialog extends AbstractSetupDialog
{
  private BundlePoolDialog(Shell parentShell)
  {
    super(parentShell, "Bundle Pool Management", 750, 750);
    setShellStyle(SWT.TITLE | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.APPLICATION_MODAL);
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Manage your bundle pools, delete unused artifacts and repair damaged archives.";
  }

  @Override
  protected void createUI(Composite parent)
  {
    BundlePoolComposite bundlePoolComposite = new BundlePoolComposite(parent, SWT.NONE);
    bundlePoolComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
  }

  public static void open(Shell shell)
  {
    BundlePoolDialog dialog = new BundlePoolDialog(shell);
    dialog.open();
  }
}
