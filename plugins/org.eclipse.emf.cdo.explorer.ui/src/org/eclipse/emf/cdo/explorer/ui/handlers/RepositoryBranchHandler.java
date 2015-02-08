/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.internal.ui.dialogs.CreateBranchDialog;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RepositoryBranchHandler extends AbstractBaseHandler<CDORepositoryElement>
{
  private String name;

  private CDOBranchPoint branchPoint;

  public RepositoryBranchHandler()
  {
    super(CDORepositoryElement.class, false);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    if (elements.size() == 1)
    {
      CDORepositoryElement repositoryElement = elements.get(0);

      CDORepository repository = repositoryElement.getRepository();
      CDOSession session = repository.getSession();

      CDOBranchManager branchManager = session.getBranchManager();
      CDOBranch branch = branchManager.getBranch(repositoryElement.getBranchID());

      CDOBranchPoint defaultBranchPoint = branch.getHead();
      String defaultName = getValidChildName(branch);

      IWorkbenchPage page = HandlerUtil.getActivePart(event).getSite().getPage();
      CreateBranchDialog dialog = new CreateBranchDialog(page, session, defaultBranchPoint, true, defaultName);
      if (dialog.open() == CreateBranchDialog.OK)
      {
        branchPoint = dialog.getBranchPoint();
        name = dialog.getName();
        return;
      }
    }

    cancel();
  }

  @Override
  protected void doExecute(IProgressMonitor progressMonitor) throws Exception
  {
    CDOBranch branch = branchPoint.getBranch();
    branch.createBranch(name, branchPoint.getTimeStamp());
  }

  private static String getValidChildName(CDOBranch branch)
  {
    Set<String> names = new HashSet<String>();
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
  public static class TagHandler extends RepositoryBranchHandler
  {
  }
}
