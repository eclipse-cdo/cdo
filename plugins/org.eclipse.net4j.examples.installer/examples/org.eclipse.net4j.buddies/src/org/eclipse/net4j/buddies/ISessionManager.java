/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
