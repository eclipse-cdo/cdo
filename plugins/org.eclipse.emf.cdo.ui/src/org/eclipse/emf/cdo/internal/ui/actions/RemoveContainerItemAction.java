/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
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
    this.selectionProvider = selectionProvider;
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
      container.removeAllElements(useTargets);
    }
  }
}
