/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.widgets;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class LogDialog extends BaseDialog
{
  private String log;

  private Text text;

  private Font font;

  public LogDialog(Shell parentShell, int shellStyle, String title, String message, String log, IDialogSettings settings)
  {
    super(parentShell, shellStyle, title, message, settings);
    this.log = log;
  }

  public LogDialog(Shell parentShell, String title, String message, String log, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, log, settings);
  }

  @Override
  public boolean close()
  {
    font.dispose();
    return super.close();
  }

  @Override
  protected void createUI(Composite parent)
  {
    GridLayout grid = new GridLayout();
    grid.marginTop = 6;
    grid.marginLeft = 6;
    grid.marginRight = 6;
    grid.marginBottom = 6;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    font = new Font(getShell().getDisplay(), "Courier", 8, SWT.NORMAL);

    text = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    text.setText(log);
    text.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
    text.setFont(font);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
  }
}
