/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.manifests.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.BuildAction;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CleanBuildHandler extends AbstractHandler
{
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
    if (window != null)
    {
      IStructuredSelection selection = getSelection(event);

      if (selection != null)
      {
        BuildAction buildAction = new BuildAction(window, IncrementalProjectBuilder.CLEAN_BUILD);
        buildAction.selectionChanged(selection);
        buildAction.run();
      }
    }

    return null;
  }

  private IStructuredSelection getSelection(ExecutionEvent event)
  {
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    if (selection instanceof IStructuredSelection)
    {
      Set<IProject> projects = new HashSet<IProject>();
      for (Iterator<?> it = ((IStructuredSelection)selection).iterator(); it.hasNext();)
      {
        Object element = it.next();
        if (element instanceof IResource)
        {
          IResource resource = (IResource)element;
          IProject project = resource.getProject();
          if (project != null)
          {
            projects.add(project);
          }
        }
      }

      if (projects.isEmpty())
      {
        return null;
      }

      return new StructuredSelection(projects.toArray());
    }

    IEditorInput activeEditorInput = HandlerUtil.getActiveEditorInput(event);
    if (activeEditorInput instanceof FileEditorInput)
    {
      IProject project = ((FileEditorInput)activeEditorInput).getFile().getProject();
      return new StructuredSelection(project);
    }

    return null;
  }
}
