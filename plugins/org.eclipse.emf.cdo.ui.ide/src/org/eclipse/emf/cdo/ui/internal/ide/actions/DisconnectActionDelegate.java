/*
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
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.internal.team.RepositoryManager;
import org.eclipse.emf.cdo.internal.team.RepositoryTeamProvider;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public class DisconnectActionDelegate implements IObjectActionDelegate
{
  private IProject project;

  public DisconnectActionDelegate()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    project = UIUtil.getElement(selection, IProject.class);
  }

  public void run(IAction action)
  {
    try
    {
      RepositoryManager.INSTANCE.removeElement(project);
      RepositoryTeamProvider.unmapProject(project);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }
}
