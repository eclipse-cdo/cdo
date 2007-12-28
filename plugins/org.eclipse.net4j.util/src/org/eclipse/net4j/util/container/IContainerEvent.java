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

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface IContainerEvent<E> extends IEvent
{
  public IContainer<E> getContainer();

  public boolean isEmpty();

  public IContainerDelta<E>[] getDeltas();

  public IContainerDelta<E> getDelta() throws IllegalStateException;

  public E getDeltaElement() throws IllegalStateException;

  public Kind getDeltaKind() throws IllegalStateException;

  public void accept(IContainerEventVisitor<E> visitor);
}
