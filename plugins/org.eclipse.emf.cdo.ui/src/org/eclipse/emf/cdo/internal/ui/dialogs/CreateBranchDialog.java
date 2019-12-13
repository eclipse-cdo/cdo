/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class CreateBranchDialog extends AbstractBranchPointDialog
{
  public static final String TITLE = "New Branch";

  private Text nameText;

  private String name;

  public CreateBranchDialog(Shell parentShell, CDOBranchPoint base, String defaultName)
  {
    super(parentShell, true, base);
    name = StringUtil.safe(defaultName);
  }

  public String getName()
  {
    return name;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_BRANCH_SELECTION));
    setMessage("Select a base point and enter the name of the new branch.");

    return super.createDialogArea(parent);
  }

  @Override
  protected void createUI(Composite container)
  {
    super.createUI(container);

    Label newLabel = new Label(container, SWT.NONE);
    newLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    newLabel.setText("Name:");

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.setText(name);
    nameText.selectAll();
    nameText.setFocus();
    nameText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        name = nameText.getText();
        validate();
      }
    });
  }

  @Override
  protected void doValidate() throws Exception
  {
    super.doValidate();

    if (name.length() == 0)
    {
      throw new Exception("Name is empty.");
    }

    if (name.contains("/") || name.contains("\\"))
    {
      throw new Exception("Name contains a path separator.");
    }

    CDOBranch baseBranch = getBranchPoint().getBranch();
    CDOBranch branch = baseBranch.getBranch(name);
    if (branch != null)
    {
      throw new Exception("Name is not unique within " + baseBranch.getPathName() + ".");
    }
  }

  @Override
  protected void doubleClicked()
  {
    // Don't close.
  }
}
