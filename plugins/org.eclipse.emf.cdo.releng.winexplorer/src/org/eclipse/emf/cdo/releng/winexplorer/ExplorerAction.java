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
package org.eclipse.emf.cdo.releng.winexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ExplorerAction implements IObjectActionDelegate
{
  private IResource resource;

  public ExplorerAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    resource = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      if (element instanceof IResource)
      {
        resource = (IResource)element;
        if (resource instanceof IFile)
        {
          resource = resource.getParent();
        }
      }
    }
  }

  public void run(IAction action)
  {
    try
    {
      String location = resource.getLocation().toString().replace('/', File.separatorChar);
      Runtime.getRuntime().exec("explorer.exe \"" + location + "\"");
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}
