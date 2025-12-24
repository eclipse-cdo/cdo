/*
 * Copyright (c) 2007-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.connector;

import org.eclipse.net4j.util.security.INegotiator;

/**
 * Enumerates the lifecycle states of an {@link IConnector}.
 *
 * @see IConnector#getState()
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public enum ConnectorState
{
  /**
   * Indicates that the {@link IConnector} has not been connected yet or has been disconnected after being connected
   * previously.
   * <p>
   * A connector is <code>DISCONNECTED</code> if and only if it is not
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. A transition to {@link #CONNECTING}
   * can be triggered by calling {@link IConnector#connect(long)} or {@link IConnector#connectAsync()}.
   *
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  DISCONNECTED,

  /**
   * Indicates that the {@link IConnector} is currently trying to establish an underlying physical connection like a TCP
   * socket connection.
   * <p>
   * A connector can only be <code>CONNECTING</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. As soon as the underlying physical
   * connection is successfully established the state of the connector automatically transitions to {@link #NEGOTIATING}.
   *
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  CONNECTING,

  /**
   * Indicates that the {@link IConnector} has successfully managed to establish the underlying physical connection and
   * has currently delegated control over this connection to an {@link INegotiator}.
   * <p>
   * A connector can only be <code>NEGOTIATING</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active} and a negotiator has been supplied.
   * As soon as the negotiator has successfully negotiated both peers (or a negotiator has not been supplied) the state
   * of the connector automatically transitions to {@link #CONNECTED}.
   * <p>
   * Negotiators can implement arbitrary handshake protocols, challenge-response sequences or other authentication
   * procedures. They can also be used to initially setup connection encryption if the connector implementation is not
   * able to do so.
   *
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  NEGOTIATING,

  /**
   * Indicates that the {@link IConnector} has successfully managed to establish and negotiate the underlying physical
   * connection and is ready now to perform actual communications.
   * <p>
   * A connector can only be <code>CONNECTED</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. A transition to
   * {@link #DISCONNECTED} can be triggered by calling {@link IConnector#close()}.
   *
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  CONNECTED
}
