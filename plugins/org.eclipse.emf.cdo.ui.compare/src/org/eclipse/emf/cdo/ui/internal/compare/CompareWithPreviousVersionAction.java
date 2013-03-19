/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public class CompareWithPreviousVersionAction implements IObjectActionDelegate
{
  private CDOCommitInfo commitInfo;

  public CompareWithPreviousVersionAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    // Do nothing
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    commitInfo = null;
    if (selection instanceof IStructuredSelection)
    {
      Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
      if (selectedElement instanceof CDOCommitInfo)
      {
        commitInfo = getAdapter(selectedElement, CDOCommitInfo.class);
      }
    }
  }

  public void run(IAction action)
  {
    if (commitInfo != null)
    {
      CDOCompareEditorUtil.openDialog(commitInfo);
    }
  }

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
