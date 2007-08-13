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

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class Container<E> extends Lifecycle implements IContainer<E>
{
  public Container()
  {
  }

  public boolean isEmpty()
  {
    E[] elements = getElements();
    return elements == null || elements.length == 0;
  }

  public void fireElementAddedEvent(E element)
  {
    fireContainerEvent(element, IContainerDelta.Kind.ADDED);
  }

  public void fireElementRemovedEvent(E element)
  {
    fireContainerEvent(element, IContainerDelta.Kind.REMOVED);
  }

  public void fireContainerEvent(E element, Kind kind)
  {
    fireEvent(newContainerEvent(element, kind));
  }

  public void fireContainerEvent(List<IContainerDelta<E>> deltas)
  {
    fireEvent(new ContainerEvent(this, deltas));
  }

  protected SingleDeltaContainerEvent newContainerEvent(E element, Kind kind)
  {
    return new SingleDeltaContainerEvent(this, element, kind);
  }

  protected ContainerEvent newContainerEvent()
  {
    return new ContainerEvent(this);
  }
}
