/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.ui.ide.Node.BranchNode;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Victor Roldan Betancort
 */
public class CreateBranchActionDelegate extends LongRunningActionDelegate
{
  private String name;

  public CreateBranchActionDelegate()
  {
  }

  @Override
  protected void preRun() throws Exception
  {
    InputDialog dialog = new InputDialog(new Shell(), Messages.getString("CreateBranchActionDelegate_0"), //$NON-NLS-1$
        Messages.getString("CreateBranchActionDelegate_1"), "", new BranchNameInputValidator()); //$NON-NLS-1$ //$NON-NLS-2$

    if (dialog.open() == Dialog.OK)
    {
      name = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    BranchNode branchNode = UIUtil.getElement(getSelection(), BranchNode.class);
    branchNode.getBranch().createBranch(name);
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class BranchNameInputValidator implements IInputValidator
  {
    public String isValid(String newText)
    {
      if (StringUtil.isEmpty(newText))
      {
        return Messages.getString("CreateBranchActionDelegate_3"); //$NON-NLS-1$
      }

      if (newText.contains(CDOBranch.PATH_SEPARATOR))
      {
        return Messages.getString("CreateBranchActionDelegate_4"); //$NON-NLS-1$
      }

      BranchNode branchNode = UIUtil.getElement(getSelection(), BranchNode.class);
      CDOBranch branch = branchNode.getBranch();
      if (branch.getBranch(newText) != null)
      {
        return Messages.getString("CreateBranchActionDelegate_5"); //$NON-NLS-1$
      }

      return null;
    }
  }
}
