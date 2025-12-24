/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainer.Modifiable;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.ArrayList;

/**
 * @author Victor Roldan Betancort
 */
public class RemoveAllContainerItemAction<E> extends AbstractContainerAction<E>
{
  public RemoveAllContainerItemAction(IContainer.Modifiable<E> container)
  {
    super(container);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    Modifiable<E> container = getContainer();
    ArrayList<E> elementsToRemove = new ArrayList<>();
    for (E element : container.getElements())
    {
      elementsToRemove.add(element);
    }

    container.removeAllElements(elementsToRemove);
  }
}
