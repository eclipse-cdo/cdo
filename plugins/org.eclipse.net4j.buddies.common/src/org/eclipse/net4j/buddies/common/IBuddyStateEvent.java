/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.common;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface IBuddyStateEvent extends IEvent
{
  /**
   * @since 3.0
   */
  @Override
  public IBuddy getSource();

  public IBuddy.State getOldState();

  public IBuddy.State getNewState();
}
