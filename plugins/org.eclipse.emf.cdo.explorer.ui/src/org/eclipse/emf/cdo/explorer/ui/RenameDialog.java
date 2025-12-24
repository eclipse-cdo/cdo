/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.common.util.CDORenameContext;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class RenameDialog extends TitleAreaDialog
{
  private final CDORenameContext renameContext;

  private String name;

  private Text nameText;

  public RenameDialog(Shell parentShell, CDORenameContext renameContext)
  {
    super(parentShell);
    this.renameContext = renameContext;
    name = renameContext.getName();

    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  public String getName()
  {
    return name;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    String title = "Rename " + renameContext.getType();
    getShell().setText(title);
    setTitle(title);
    setTitleImage(OM.getImage("icons/wiz/rename.gif"));
    setMessage("Enter the new " + getAttributeName() + " of the " + renameContext.getType().toLowerCase() + ".");

    Composite area = (Composite)super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout containerGridLayout = new GridLayout(2, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;
    container.setLayout(containerGridLayout);

    Label oldLabel = new Label(container, SWT.NONE);
    oldLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    oldLabel.setText("Current " + getAttributeName() + ":");

    Text oldNameText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    oldNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    oldNameText.setText(name);

    Label newLabel = new Label(container, SWT.NONE);
    newLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    newLabel.setText("New " + getAttributeName() + ":");

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.setText(name);
    nameText.setFocus();
    nameText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        name = nameText.getText();

        String error = renameContext.validateName(name);
        setErrorMessage(error);

        Button button = getButton(IDialogConstants.OK_ID);
        if (button != null)
        {
          button.setEnabled(error == null);
        }
      }
    });

    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      nameText.setSelection(0, lastDot);
    }
    else
    {
      nameText.selectAll();
    }

    return area;
  }

  protected String getAttributeName()
  {
    return "name";
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 250);
  }
}
