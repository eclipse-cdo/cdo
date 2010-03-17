/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.layout;

import java.util.LinkedList;

/**
 * @author Eike Stepper
 */
public final class Deque<E> extends LinkedList<E>
{
  private static final long serialVersionUID = 1L;

  public E peekFirst()
  {
    if (isEmpty())
    {
      return null;
    }

    return getFirst();
  }

  public E peekLast()
  {
    if (isEmpty())
    {
      return null;
    }

    return getLast();
  }
}
