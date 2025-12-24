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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.CreateBranchDialog;
import org.eclipse.emf.cdo.internal.ui.handlers.CreateBranchHandler;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class CreateBranchAction extends LongRunningAction
{
  public static final String ID = "create-branch"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("CreateBranchAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("CreateBranchAction.1"); //$NON-NLS-1$

  private CDOBranchPoint base;

  private String name;

  public CreateBranchAction(IWorkbenchPage page, CDOBranchPoint base)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null);
    this.base = base;
    setId(ID);
  }

  public final CDOBranchPoint getBase()
  {
    return base;
  }

  public final String getName()
  {
    return name;
  }

  @Override
  protected void preRun() throws Exception
  {
    name = CreateBranchHandler.getValidChildName(base.getBranch());

    CreateBranchDialog dialog = new CreateBranchDialog(getShell(), base, name);
    if (dialog.open() == CreateBranchDialog.OK)
    {
      base = dialog.getBranchPoint();
      name = dialog.getName();
      return;
    }

    base = null;
    name = null;
    cancel();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOBranch branch = base.getBranch();
    branch.createBranch(name, base.getTimeStamp());
  }
}
