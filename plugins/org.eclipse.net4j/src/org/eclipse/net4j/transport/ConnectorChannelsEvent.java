/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.transport;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface ConnectorChannelsEvent extends IEvent
{
  public Channel getChannel();

  public Type getType();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    ABOUT_TO_OPEN, OPENED, CLOSING
  }
}
