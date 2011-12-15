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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public abstract class AbstractContainerAction implements IObjectActionDelegate
{
  private IContainer container;

  public AbstractContainerAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    container = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      if (element instanceof IFile)
      {
        container = ((IFile)element).getParent();
      }
      else if (element instanceof IContainer)
      {
        container = (IContainer)element;
      }
    }
  }

  public void run(IAction action)
  {
    try
    {
      run(container);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  protected abstract void run(IContainer container) throws Exception;
}
