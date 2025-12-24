/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;

import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Eike Stepper
 */
public class UpdateAssemblyAction extends LongRunningActionDelegate
{
  public UpdateAssemblyAction()
  {
  }

  @Override
  protected void preRun() throws Exception
  {
    IAssemblyDescriptor assemblyDescriptor = getAssemblyDescriptor();
    if (assemblyDescriptor == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    IAssemblyDescriptor assemblyDescriptor = getAssemblyDescriptor();
    assemblyDescriptor.update();
  }

  private IAssemblyDescriptor getAssemblyDescriptor()
  {
    ISelection selection = getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;

      Object element = ssel.getFirstElement();
      if (element instanceof IAssemblyDescriptor)
      {
        return (IAssemblyDescriptor)element;
      }
    }

    return null;
  }
}
