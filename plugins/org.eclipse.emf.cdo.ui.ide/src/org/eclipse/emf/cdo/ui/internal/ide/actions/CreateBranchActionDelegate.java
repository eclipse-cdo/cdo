/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
    InputDialog dialog = new InputDialog(new Shell(), "Create a new branch",
        "Please specify the name of the new branch", "", new BranchNameInputValidator());

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
        return "Branch name cannot be empty";
      }

      BranchNode branchNode = UIUtil.getElement(getSelection(), BranchNode.class);
      CDOBranch branch = branchNode.getBranch();
      if (branch.getBranch(newText) != null)
      {
        return "A branch with that name already exists";
      }

      return null;
    }
  }
}
