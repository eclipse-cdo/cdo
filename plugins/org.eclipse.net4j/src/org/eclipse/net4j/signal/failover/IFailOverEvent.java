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
package org.eclipse.net4j.signal.failover;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface IFailOverEvent extends IEvent
{
  /**
   * Returns the old channel that was active before the fail over occured. At the time this event is being sent the old
   * channel is already closed.
   */
  public IChannel getOldChannel();

  /**
   * Returns the new channel that is active after the fail over occured. At the time this event is being sent the new
   * channel is already active.
   */
  public IChannel getNewChannel();

  /**
   * Returns the connector of the new channel.
   */
  public IConnector getNewConnector();
}
