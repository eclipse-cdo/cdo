/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.handlers;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreationContext;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.CreateBranchDialog;

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CreateBranchHandler extends AbstractBaseHandler<CDOBranchCreationContext>
{
  private CDOBranchPoint base;

  private String name;

  public CreateBranchHandler()
  {
    super(CDOBranchCreationContext.class, false);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    if (elements.size() == 1)
    {
      CDOBranchCreationContext context = elements.get(0);
      base = context.getBase();
      name = getValidChildName(base.getBranch());

      Shell shell = HandlerUtil.getActiveShell(event);
      CreateBranchDialog dialog = new CreateBranchDialog(shell, base, name);
      if (dialog.open() == CreateBranchDialog.OK)
      {
        base = dialog.getBranchPoint();
        name = dialog.getName();
        return;
      }
    }

    base = null;
    name = null;
    cancel();
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    try
    {
      CDOBranch branch = base.getBranch();
      branch.createBranch(name, base.getTimeStamp());
    }
    finally
    {
      base = null;
      name = null;
    }
  }

  @Override
  protected String getErrorMessage(Exception ex)
  {
    return "Branch " + name + " could not be created.";
  }

  public static String getValidChildName(CDOBranch branch)
  {
    Set<String> names = new HashSet<>();
    for (CDOBranch child : branch.getBranches())
    {
      names.add(child.getName());
    }

    for (int i = 1; i < Integer.MAX_VALUE; i++)
    {
      String name = "branch" + i;
      if (!names.contains(name))
      {
        return name;
      }
    }

    throw new IllegalStateException("Too many sub branches: " + branch);
  }

  /**
   * @author Eike Stepper
   */
  public static class TagHandler extends CreateBranchHandler
  {
  }
}
