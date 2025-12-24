/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.container.IContainerEventVisitor.Filtered;
import org.eclipse.net4j.util.event.Event;

import java.text.MessageFormat;

/**
 * A {@link IContainerEvent container event} with a single element {@link IContainerDelta delta}.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SingleDeltaContainerEvent<E> extends Event implements IContainerEvent<E>
{
  private static final long serialVersionUID = 1L;

  private IContainerDelta<E>[] deltas;

  @SuppressWarnings("unchecked")
  public SingleDeltaContainerEvent(IContainer<E> container, E element, IContainerDelta.Kind kind)
  {
    super(container);
    deltas = new IContainerDelta[] { new ContainerDelta<>(element, kind) };
  }

  /**
   * @since 3.0
   */
  @Override
  @SuppressWarnings("unchecked")
  public IContainer<E> getSource()
  {
    return (IContainer<E>)super.getSource();
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  @Override
  public IContainerDelta<E>[] getDeltas()
  {
    return deltas;
  }

  @Override
  public IContainerDelta<E> getDelta() throws IllegalStateException
  {
    return deltas[0];
  }

  @Override
  public E getDeltaElement() throws IllegalStateException
  {
    return deltas[0].getElement();
  }

  @Override
  public IContainerDelta.Kind getDeltaKind() throws IllegalStateException
  {
    return deltas[0].getKind();
  }

  @Override
  public void accept(IContainerEventVisitor<E> visitor)
  {
    E element = deltas[0].getElement();

    boolean filtered = true;
    if (visitor instanceof Filtered<?>)
    {
      filtered = ((Filtered<E>)visitor).filter(element);
    }

    if (filtered)
    {
      switch (deltas[0].getKind())
      {
      case ADDED:
        visitor.added(element);
        break;
      case REMOVED:
        visitor.removed(element);
        break;
      }
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("ContainerEvent[source={0}, {1}={2}]", getSource(), getDeltaKind(), getDeltaElement()); //$NON-NLS-1$
  }
}
