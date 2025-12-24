/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link IContainer container} when its elements have changed.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IContainerEvent<E> extends IEvent
{
  /**
   * @since 3.0
   */
  @Override
  public IContainer<E> getSource();

  public boolean isEmpty();

  public IContainerDelta<E>[] getDeltas();

  public IContainerDelta<E> getDelta() throws IllegalStateException;

  public E getDeltaElement() throws IllegalStateException;

  public Kind getDeltaKind() throws IllegalStateException;

  public void accept(IContainerEventVisitor<E> visitor);
}
