/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface ISessionManager extends INotifier
{
  public static final ISessionManager INSTANCE = org.eclipse.net4j.internal.buddies.SessionManager.INSTANCE;

  public IBuddySession getSession();

  public State getState();

  public boolean isConnecting();

  public void connect();

  public void disconnect();

  public void flashMe();

  public boolean isFlashing();

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    CONNECTING, CONNECTED, DISCONNECTED
  }
}
