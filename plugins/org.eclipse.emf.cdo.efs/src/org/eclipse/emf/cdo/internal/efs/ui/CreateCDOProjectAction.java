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
package org.eclipse.emf.cdo.internal.efs.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import java.net.URI;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class CreateCDOProjectAction implements IWorkbenchWindowActionDelegate
{
  public CreateCDOProjectAction()
  {
  }

  /**
   * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench UI.
   * 
   * @see IWorkbenchWindowActionDelegate#run
   */
  public void run(IAction action)
  {
    for (int i = 1; i < 20; i++)
    {
      try
      {
        if (CreateCDOProjectAction.createCDOProject(i) != null)
        {
          return;
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        return;
      }
    }
  }

  /**
   * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but this
   * can only happen after the delegate has been created.
   * 
   * @see IWorkbenchWindowActionDelegate#selectionChanged
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  /**
   * We can use this method to dispose of any system resources we previously allocated.
   * 
   * @see IWorkbenchWindowActionDelegate#dispose
   */
  public void dispose()
  {
  }

  /**
   * We will cache window object in order to be able to provide parent shell for the message dialog.
   * 
   * @see IWorkbenchWindowActionDelegate#init
   */
  public void init(IWorkbenchWindow window)
  {
  }

  public static IProject createCDOProject(int suffix) throws CoreException
  {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();

    IProject project = workspace.getRoot().getProject("cdo" + suffix);
    if (project.exists())
    {
      return null;
    }

    IProjectDescription description = workspace.newProjectDescription("cdo" + suffix);
    description.setLocationURI(URI.create("cdo.net4j.tcp://localhost/repo1/MAIN/@"));

    project.create(description, new NullProgressMonitor());
    if (!project.isOpen())
    {
      project.open(new NullProgressMonitor());
    }

    return project;
  }
}
