/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectBranchPointDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class CreateBranchAction extends SessionAction
{
  public static final String ID = "create-branch"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("CreateBranchAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("CreateBranchAction.1"); //$NON-NLS-1$

  private String name;

  private CDOBranchPoint base;

  public CreateBranchAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, session);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    SelectBranchPointDialog.WithName dialog = new SelectBranchPointDialog.WithName(getPage(), getSession(), null, null)
    {
      @Override
      protected Control createDialogArea(Composite parent)
      {
        getShell().setText(TITLE);
        setTitle(TITLE);
        setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TARGET_SELECTION));
        setMessage("Enter the name of the new branch and compose a valid base point or select one from commits, tags or views.");
        return super.createDialogArea(parent);
      }
    };

    if (dialog.open() == Dialog.OK)
    {
      name = dialog.getName();
      base = dialog.getBranchPoint();
      if (StringUtil.isEmpty(name) || base == null)
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

  }
}
