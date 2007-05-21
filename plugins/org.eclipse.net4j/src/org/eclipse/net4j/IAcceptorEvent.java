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
package org.eclipse.net4j;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;

/**
 * An event that is fired by an {@link IAcceptor} to indicate that its set of
 * accepted {@link IConnector}s has changed.
 * <p>
 * The information about added and removed connectors is provided by a set of
 * {@link IContainerDelta}s.
 * 
 * @author Eike Stepper
 * @since 0.8.0
 */
public interface IAcceptorEvent extends IContainerEvent<IConnector>
{
  /**
   * The {@link IAcceptor} that sent this event.
   * 
   * @see IContainerEvent#getContainer()
   */
  public IAcceptor getAcceptor();

  /**
   * The {@link IConnector} of the first {@link IContainerDelta} of this event.
   * 
   * @see IContainerEvent#getDeltaElement()
   */
  public IConnector getConnector();
}
