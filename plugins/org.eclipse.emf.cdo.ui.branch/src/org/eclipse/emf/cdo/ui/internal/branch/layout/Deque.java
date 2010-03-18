/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.branch.layout;

import java.util.LinkedList;

/**
 * A double ended list, that returns <tt>null</tt> if no element is present. Mimics the jdk 1.6 Deque.
 * 
 * @author Eike Stepper
 */
public final class Deque<E> extends LinkedList<E>
{

  private static final long serialVersionUID = 1L;

  /**
   * Returns the first element if present, <tt>null</tt> otherwise.
   * 
   * @return the first element in this list.
   */
  public E peekFirst()
  {
    if (isEmpty())
    {
      return null;
    }

    return getFirst();
  }

  /**
   * Returns the last element if present, <tt>null</tt> otherwise.
   * 
   * @return the e
   */
  public E peekLast()
  {
    if (isEmpty())
    {
      return null;
    }

    return getLast();
  }
}
