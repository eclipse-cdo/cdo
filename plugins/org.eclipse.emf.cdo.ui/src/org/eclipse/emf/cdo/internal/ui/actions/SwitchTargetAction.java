/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation 
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectBranchPointDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
    CDOView view = getView();
    SelectBranchPointDialog dialog = new SelectBranchPointDialog(getPage(), view.getSession(), view, view.isReadOnly())
    {
      @Override
      protected Control createDialogArea(Composite parent)
      {
        getShell().setText(TITLE);
        setTitle(TITLE);
        setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TARGET_SELECTION));
        setMessage("Compose a valid target point or select one from commits, tags or views.");
        return super.createDialogArea(parent);
      }

      @Override
      protected String getComposeTabTitle()
      {
        return "Target Point";
      }
    };

    if (dialog.open() == Dialog.OK)
    {
      target = dialog.getBranchPoint();
      if (target == null)
      {
        cancel();
      }
    }
    else
    {
      cancel();
    }

    super.preRun();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOView view = getView();
    view.setBranchPoint(target);
  }
}
