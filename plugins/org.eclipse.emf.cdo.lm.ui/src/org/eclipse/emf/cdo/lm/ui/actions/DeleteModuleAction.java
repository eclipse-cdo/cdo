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

import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class DeleteModuleAction extends LMAction<Module>
{
  private ISystemDescriptor systemDescriptor;

  public DeleteModuleAction(IWorkbenchPage page, Module module)
  {
    super(page, //
        "Delete Module" + INTERACTIVE, // //$NON-NLS-1$
        "Delete the Module '" + module.getName() + "'", //$NON-NLS-1$ //$NON-NLS-2$
        OM.getImageDescriptor("icons/Delete.gif"), //$NON-NLS-1$
        "Delete the Module '" + module.getName() + "'.", //$NON-NLS-1$ //$NON-NLS-2$
        "icons/DeleteModule.gif", //$NON-NLS-1$
        module);
  }

  @Override
  protected void preRun() throws Exception
  {
    Module module = getContext();
    module.getName();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(module);
    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Module module = getContext();

    Label oldLabel = new Label(parent, SWT.NONE);
    oldLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    oldLabel.setText("Confirm the deletion of module '" + module.getName() //$NON-NLS-1$
        + "'.\nNote that checkouts will be removed, too. "); //$NON-NLS-1$

    validateDialog();
  }

  @Override
  protected void doRun(Module module, IProgressMonitor monitor) throws Exception
  {
    systemDescriptor.deleteModule(module, monitor);
  }
}
