package org.eclipse.internal.net4j.util.container;

import org.eclipse.net4j.util.container.IContainerDelta;

/**
 * @author Eike Stepper
 */
public class ContainerDelta<E> implements IContainerDelta<E>
{
  private E element;

  private Kind kind;

  public ContainerDelta(E element, Kind kind)
  {
    this.element = element;
    this.kind = kind;
  }

  public E getElement()
  {
    return element;
  }

  public E setValue(E value)
  {
    throw new UnsupportedOperationException();
  }

  public Kind getKind()
  {
    return kind;
  }
}
