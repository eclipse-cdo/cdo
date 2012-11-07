/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class CreateBranchDialog extends SelectBranchPointDialog
{
  private String name;

  private Text nameText;

  public CreateBranchDialog(IWorkbenchPage page, CDOSession session, CDOBranchPoint branchPoint,
      boolean allowTimeStamp, String name)
  {
    super(page, session, branchPoint, allowTimeStamp);
    this.name = StringUtil.safe(name);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
    validate();
  }

  public Text getNameText()
  {
    return nameText;
  }

  @Override
  protected void createBranchPointArea(Composite parent)
  {
    GridLayout gridLayout = UIUtil.createGridLayout(2);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 5;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData(true, false));
    composite.setLayout(gridLayout);

    Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(UIUtil.createGridData(false, false));
    label.setText("Name:");

    nameText = new Text(composite, SWT.BORDER);
    nameText.setLayoutData(UIUtil.createGridData(true, false));
    nameText.setText(name);
    nameText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        setName(nameText.getText());
      }
    });

    super.createBranchPointArea(parent);
  }
}
