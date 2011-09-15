/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.ui.actions;

import org.eclipse.emf.cdo.dawn.codegen.creators.Creator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.ui.creators.EMFFragmentCreator;
import org.eclipse.emf.cdo.dawn.codegen.messages.Messages;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Martin Fluegge
 * @since 0.3
 */
public class GenerateEMFFragmentAction implements IObjectActionDelegate
{
  private IResource selectedElement;

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void run(IAction action)
  {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    try
    {
      window.run(true, true, new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          monitor.beginTask(Messages.GenerateEMFFragmentAction_0, 1000);
          ArrayList<Creator> creators = new ArrayList<Creator>();
          creators.add(new EMFFragmentCreator(selectedElement));

          for (Creator creator : creators)
          {
            SubProgressMonitor monitor2 = new SubProgressMonitor(monitor, 1000 / creators.size());
            creator.create(monitor2);
            monitor2.done();
          }
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException ex)
    {
      throw new RuntimeException(ex);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      Object sel = ((IStructuredSelection)selection).getFirstElement();
      if (sel instanceof IResource)
      {
        selectedElement = (IResource)sel;
      }
    }
  }
}
