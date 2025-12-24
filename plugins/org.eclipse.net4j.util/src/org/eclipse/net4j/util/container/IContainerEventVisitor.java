/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

/**
 * A callback interface for visiting {@link IContainerDelta container deltas}.
 *
 * @see IContainerEvent#accept(IContainerEventVisitor)
 * @author Eike Stepper
 */
public interface IContainerEventVisitor<E>
{
  public void added(E element);

  public void removed(E element);

  /**
   * An extension interface for {@link IContainerEventVisitor container event visitors} that can {@link #filter(Object)
   * filter} deltas from being visited.
   *
   * @see IContainerEvent#accept(IContainerEventVisitor)
   * @author Eike Stepper
   */
  public interface Filtered<E> extends IContainerEventVisitor<E>
  {
    public boolean filter(E element);
  }
}
