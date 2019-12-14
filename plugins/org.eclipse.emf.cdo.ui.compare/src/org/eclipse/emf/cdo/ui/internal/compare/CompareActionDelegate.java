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
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CompareActionDelegate<TARGET> extends LongRunningActionDelegate
{
  private final Class<TARGET> targetClass;

  private IWorkbenchPart targetPart;

  private ISelection selection;

  public CompareActionDelegate(Class<TARGET> targetClass)
  {
    this.targetClass = targetClass;
  }

  public IWorkbenchPart getTargetPart()
  {
    return targetPart;
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.targetPart = targetPart;
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      List<TARGET> targets = new ArrayList<>();

      @SuppressWarnings("unchecked")
      Iterator<Object> iterator = ssel.iterator();

      while (iterator.hasNext())
      {
        Object element = iterator.next();
        TARGET target = AdapterUtil.adapt(element, targetClass);
        if (target != null)
        {
          targets.add(target);
        }
      }

      run(targets, progressMonitor);
    }
  }

  protected abstract void run(List<TARGET> targets, IProgressMonitor progressMonitor);
}
