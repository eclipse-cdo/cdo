/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.IContainerEventVisitor.Filtered;

/**
 * @author Eike Stepper
 */
public class SingleDeltaContainerEvent<E> extends Event implements IContainerEvent<E>
{
  private static final long serialVersionUID = 1L;

  private IContainerDelta<E>[] deltas;

  public SingleDeltaContainerEvent(IContainer<E> container, E element, Kind kind)
  {
    super(container);
    deltas = new IContainerDelta[] { new ContainerDelta(element, kind) };
  }

  public IContainer<E> getContainer()
  {
    return (IContainer<E>)getSource();
  }

  public IContainerDelta<E>[] getDeltas()
  {
    return deltas;
  }

  public IContainerDelta<E> getDelta() throws IllegalStateException
  {
    return deltas[0];
  }

  public E getDeltaElement() throws IllegalStateException
  {
    return deltas[0].getElement();
  }

  public Kind getDeltaKind() throws IllegalStateException
  {
    return deltas[0].getKind();
  }

  public void accept(IContainerEventVisitor<E> visitor)
  {
    E element = deltas[0].getElement();

    boolean filtered = true;
    if (visitor instanceof Filtered)
    {
      filtered = ((Filtered)visitor).filter(element);
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
}
