/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.security.IUserAware;

/**
 * One endpoint of a physical connection of arbitrary nature between two communicating parties. A {@link IConnector}
 * encapsulates the process of establishing and closing such connections and has a
 * {@link org.eclipse.net4j.ILocationAware.Location location} of
 * {@link org.eclipse.net4j.ILocationAware.Location#CLIENT CLIENT} or
 * {@link org.eclipse.net4j.ILocationAware.Location#SERVER SERVER} with respect to this process. Once a connection is
 * established either party can use its connector to open multiple {@link IChannel}s to asynchronously exchange
 * {@link IBuffer}s.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients. Providers of connectors for new physical
 * connection types have to implement org.eclipse.internal.net4j.connector.InternalConnector.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConnector extends IChannelMultiplexer, IUserAware, Closeable
{
  /**
   * @since 2.0
   */
  public static final long NO_TIMEOUT = -1;

  public String getURL();

  /**
   * Returns the current state of this connector.
   */
  public ConnectorState getState();

  /**
   * Same as <code>{@link #getState()} == {@link ConnectorState#CONNECTED}</code>.
   */
  public boolean isConnected();

  /**
   * Synchronous connect with infinite timeout value. Same as {@link #connect() connect(NO_TIMEOUT)}.
   *
   * @throws ConnectorException
   * @since 4.0
   */
  public void connect() throws ConnectorException;

  /**
   * Synchronous connect. Blocks until <code>{@link #isConnected()} == true</code> or the given timeout expired.
   *
   * @param timeout
   *          The maximum number of milli seconds to block or {@link #NO_TIMEOUT} to block indefinetely in case no
   *          connection occurs.
   * @throws ConnectorException
   * @since 4.0
   */
  public void connect(long timeout) throws ConnectorException;

  /**
   * Asynchronous connect. May leave this {@link IConnector} in a state where <code>{@link #isConnected()} == false
   * </code>.
   *
   * @throws ConnectorException
   * @see #waitForConnection(long)
   * @see #connect(long)
   */
  public void connectAsync() throws ConnectorException;

  /**
   * Blocks until <code>{@link #isConnected()} == true</code> or the given timeout expired.
   *
   * @param timeout
   *          The maximum number of milli seconds to block or {@link #NO_TIMEOUT} to block indefinetely in case no
   *          connection occurs.
   * @throws ConnectorException
   * @since 4.0
   */
  public void waitForConnection(long timeout) throws ConnectorException;
}
