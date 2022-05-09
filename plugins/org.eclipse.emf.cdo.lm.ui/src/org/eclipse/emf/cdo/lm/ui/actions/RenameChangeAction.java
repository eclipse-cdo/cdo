/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.internal.client.LMNamingStrategy;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RenameChangeAction extends LMAction<Change>
{
  private ISystemDescriptor systemDescriptor;

  private Text labelText;

  private String labelString;

  public RenameChangeAction(IWorkbenchPage page, Change change)
  {
    super(page, //
        "Rename Change" + INTERACTIVE, //
        "Rename the change '" + change.getLabel() + "'", //
        OM.getImageDescriptor("icons/Rename.gif"), //
        "Rename the change '" + change.getLabel() + "'.", //
        "icons/RenameChange.gif", //
        change);
  }

  @Override
  protected void preRun() throws Exception
  {
    Change change = getContext();
    System system = change.getSystem();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(system);

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Change change = getContext();
    String label = change.getLabel();

    Label oldLabel = new Label(parent, SWT.NONE);
    oldLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    oldLabel.setText("Current label:");

    Text oldLabelText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
    oldLabelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    oldLabelText.setText(label);

    Label newLabel = new Label(parent, SWT.NONE);
    newLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    newLabel.setText("New label:");

    labelText = new Text(parent, SWT.BORDER);
    labelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    labelText.setText(label);
    labelText.setFocus();
    labelText.addModifyListener(e -> {
      labelString = labelText.getText();
      validateDialog();
    });

    // TODO Make the note conditional (only if checkouts exist).
    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    Label note = new Label(parent, SWT.NONE);
    note.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    note.setText("Note that checkouts will be removed.");
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (StringUtil.isEmpty(labelString))
    {
      return "A new label must be entered.";
    }

    Change change = getContext();
    String moduleName = change.getModule().getName();

    boolean[] branchExists = { false };
    systemDescriptor.withModuleSession(moduleName, session -> {
      CDOBranchPoint branchPoint = change.getBranchPoint().resolve(session.getBranchManager());

      String branchName = LMNamingStrategy.getChangeBranchName(labelString);
      branchExists[0] = branchPoint.getBranch().getBase().getBranch().getBranch(branchName) != null;
    });

    if (branchExists[0])
    {
      return "A change with the same label already exists.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected void doRun(Change change, IProgressMonitor monitor) throws Exception
  {
    systemDescriptor.renameChange(change, labelString, monitor);

    for (IAssemblyDescriptor assemblyDescriptor : IAssemblyManager.INSTANCE.getDescriptors(change))
    {
      CDOCheckout checkout = assemblyDescriptor.getCheckout();
      checkout.delete(true);
    }
  }
}
