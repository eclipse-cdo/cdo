/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.team.actions;

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
