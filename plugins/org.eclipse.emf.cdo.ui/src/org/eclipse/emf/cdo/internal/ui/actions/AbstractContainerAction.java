/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

/**
 * @author Victor Roldan Betancort
 */
public abstract class AbstractContainerAction<E> extends LongRunningAction
{
  private IContainer.Modifiable<E> container;

  public AbstractContainerAction(IContainer.Modifiable<E> container)
  {
    setContainer(container);
  }

  protected IContainer.Modifiable<E> getContainer()
  {
    return container;
  }

  protected void setContainer(IContainer.Modifiable<E> container)
  {
    this.container = container;
  }
}
