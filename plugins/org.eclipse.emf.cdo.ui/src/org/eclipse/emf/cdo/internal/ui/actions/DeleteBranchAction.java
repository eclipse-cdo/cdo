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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class DeleteBranchAction extends LongRunningAction
{
  public static final String ID = "delete-branch"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("DeleteBranchAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("DeleteBranchAction.1"); //$NON-NLS-1$

  private CDOBranch branch;

  public DeleteBranchAction(IWorkbenchPage page, CDOBranch branch)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null);
    this.branch = branch;
    setId(ID);
  }

  public final CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    branch.delete(new EclipseMonitor(progressMonitor));
  }
}
