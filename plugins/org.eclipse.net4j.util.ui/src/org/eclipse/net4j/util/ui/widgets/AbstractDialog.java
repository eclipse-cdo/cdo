/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 3.12
 */
public abstract class AbstractDialog extends TitleAreaDialog
{
  public AbstractDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  public final void enableOKButton(boolean enabled)
  {
    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null)
    {
      okButton.setEnabled(enabled);
    }
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 450);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite area = (Composite)super.createDialogArea(parent);

    GridLayout containerGridLayout = new GridLayout(2, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;
    containerGridLayout.verticalSpacing = 10;

    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(containerGridLayout);

    createUI(container);
    validate();

    return area;
  }

  protected abstract void createUI(Composite container);

  public final boolean validate()
  {
    String error = null;

    try
    {
      doValidate();
    }
    catch (Exception ex)
    {
      error = ex.getMessage();
    }

    setErrorMessage(error);
    enableOKButton(error == null);
    return error == null;
  }

  protected void doValidate() throws Exception
  {
    // Do nothing.
  }
}
