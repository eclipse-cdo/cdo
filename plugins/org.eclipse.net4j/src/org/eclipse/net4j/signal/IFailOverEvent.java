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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface IFailOverEvent extends IEvent
{
  /**
   * @return The old channel that was active before the fail over occured. At
   *         the time this event is being sent the old channel is already
   *         closed.
   */
  public IChannel getOldChannel();

  /**
   * @return The new channel that is active after the fail over occured. At the
   *         time this event is being sent the new channel is already active.
   */
  public IChannel getNewChannel();
}
