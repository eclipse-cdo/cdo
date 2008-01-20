package org.eclipse.net4j.util.collection;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface MoveableList<E> extends List<E>
{
  public E move(int targetIndex, int sourceIndex);
}
