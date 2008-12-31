/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
public class RemoveContainerItemAction<E> extends AbstractContainerAction<E>
{
  private ISelectionProvider selectionProvider;

  private transient List<E> targets;

  public RemoveContainerItemAction(IContainer.Modifiable<E> container, ISelectionProvider selectionProvider)
  {
    super(container);
  }

  @Override
  protected void preRun() throws Exception
  {
    ISelection selection = selectionProvider.getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (!ssel.isEmpty())
      {
        @SuppressWarnings("unchecked")
        List<E> cast = ssel.toList();
        targets = cast;
        return;
      }
    }

    cancel();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (targets != null)
    {
      List<E> useTargets = targets;
      targets = null;

      IContainer.Modifiable<E> container = getContainer();
      for (E target : useTargets)
      {
        container.removeElement(target);
      }
    }
  }
}
