/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.container;

/**
 * @author Eike Stepper
 */
public interface IContainerEventVisitor<E>
{
  public void added(E element);

  public void removed(E element);

  /**
   * @author Eike Stepper
   */
  public interface Filtered<E> extends IContainerEventVisitor<E>
  {
    public boolean filter(E element);
  }
}
