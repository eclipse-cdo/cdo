/*
 * Copyright (c) 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.util.LMOperations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class DeleteChangeAction extends LMAction<Change>
{
  private ISystemDescriptor systemDescriptor;

  public DeleteChangeAction(IWorkbenchPage page, Change change)
  {
    super(page, //
        "Delete Change" + INTERACTIVE, //
        "Delete the change '" + change.getLabel() + "'", //
        OM.getImageDescriptor("icons/Delete.gif"), //
        "Delete the change '" + change.getLabel() + "'.", //
        "icons/wizban/Delete.png", //
        change);
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return LMOperations.DELETE_CHANGE;
  }

  @Override
  protected void preRun() throws Exception
  {
    Change change = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(change.getSystem());

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Change change = getContext();

    Label oldLabel = new Label(parent, SWT.NONE);
    oldLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    oldLabel.setText("Confirm the deletion of change '" + change.getLabel() + "'.\nNote that checkouts will be removed, too. ");

    validateDialog();
  }

  @Override
  protected void doRun(Change change, IProgressMonitor monitor) throws Exception
  {
    IAssemblyManager assemblyManager = IAssemblyManager.INSTANCE;

    IAssemblyDescriptor[] descriptors = assemblyManager.getDescriptors(change);

    for (IAssemblyDescriptor assemblyDescriptor : descriptors)
    {
      CDOCheckout checkout = assemblyDescriptor.getCheckout();
      checkout.delete(true);
    }

    systemDescriptor.deleteChange(change, monitor);
  }
}
