/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.wingit;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class GitAction implements IObjectActionDelegate
{
  private IWorkbenchPart targetPart;

  private Repository repository;

  public GitAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.targetPart = targetPart;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    repository = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      if (element instanceof IAdaptable)
      {
        IAdaptable adaptable = (IAdaptable)element;
        repository = (Repository)adaptable.getAdapter(Repository.class);
      }
    }
  }

  public void run(IAction action)
  {
    if (repository != null)
    {
      String gitBash = WinGit.getGitBash(targetPart.getSite().getShell());

      try
      {
        File workTree = repository.getWorkTree();
        Runtime.getRuntime().exec(
            "cmd /c cd \"" + workTree.getAbsolutePath() + "\" && start cmd.exe /c \"" + gitBash + "\" --login -i");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
