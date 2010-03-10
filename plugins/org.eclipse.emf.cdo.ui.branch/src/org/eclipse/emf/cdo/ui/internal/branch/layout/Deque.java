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
