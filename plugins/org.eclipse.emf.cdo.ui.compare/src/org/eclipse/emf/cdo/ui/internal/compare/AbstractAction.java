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
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AbstractAction<TARGET> implements IObjectActionDelegate
{
  private final Class<TARGET> targetClass;

  private IWorkbenchPart targetPart;

  private ISelection selection;

  public AbstractAction(Class<TARGET> targetClass)
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

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void run(IAction action)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      List<TARGET> targets = new ArrayList<TARGET>();

      @SuppressWarnings("unchecked")
      Iterator<Object> iterator = ssel.iterator();

      while (iterator.hasNext())
      {
        Object element = iterator.next();
        TARGET target = getAdapter(element, targetClass);
        if (target != null)
        {
          targets.add(target);
        }
      }

      run(action, targets);
    }
  }

  protected abstract void run(IAction action, List<TARGET> targets);

  @SuppressWarnings("unchecked")
  public static <T> T getAdapter(Object adaptable, Class<T> c)
  {
    if (c.isInstance(adaptable))
    {
      return (T)adaptable;
    }

    if (adaptable instanceof IAdaptable)
    {
      IAdaptable a = (IAdaptable)adaptable;
      Object adapter = a.getAdapter(c);
      if (c.isInstance(adapter))
      {
        return (T)adapter;
      }
    }

    return null;
  }
}
