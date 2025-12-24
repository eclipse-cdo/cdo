/*
 * Copyright (c) 2011-2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.AbstractBranchPointDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Victor Roldan Betancort
 */
public class SwitchTargetAction extends AbstractViewAction
{
  public static final String ID = "switch-target"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("SwitchTargetAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("SwitchTargetAction.1"); //$NON-NLS-1$

  private CDOBranchPoint target;

  public SwitchTargetAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    Shell shell = getShell();
    CDOView view = getView();

    target = AbstractBranchPointDialog.select(shell, true, view);
    if (target == null)
    {
      cancel();
      return;
    }

    super.preRun();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOView view = getView();
    view.setBranchPoint(target, progressMonitor);
  }
}
