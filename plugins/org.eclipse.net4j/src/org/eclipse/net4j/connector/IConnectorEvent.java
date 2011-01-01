/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.connector;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;

/**
 * An event that is fired by an {@link IConnector}.
 * 
 * @author Eike Stepper
 */
public interface IConnectorEvent extends IEvent
{
  /**
   * The {@link IConnector} that sent this event.
   * 
   * @see IContainerEvent#getSource()
   * @since 3.0
   */
  public IConnector getSource();
}
