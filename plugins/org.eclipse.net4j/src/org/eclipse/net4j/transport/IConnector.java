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

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.internal.net4j.transport.Channel;
import org.eclipse.internal.net4j.transport.Connector;

/**
 * One endpoint of a physical connection of arbitrary nature between two
 * communicating parties. A {@link IConnector} encapsulates the process of
 * establishing and closing such connections and has a {@link ConnectorLocation}
 * of {@link ConnectorLocation#CLIENT} or {@link ConnectorLocation#SERVER} with
 * respect to this process. Once a connection is established either party can
 * use its connector to open multiple {@link IChannel}s to asynchronously
 * exchange {@link IBuffer}s.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients.
 * Providers of connectors for new physical connection types have to subclass
 * {@link Connector} (see {@link Channel#setConnector(Connector)}.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface IConnector extends IContainer<IChannel>
{
  public ConnectorLocation getLocation();

  public boolean isClient();

  public boolean isServer();

  public String getUserID();

  public IConnectorCredentials getCredentials();

  public ConnectorState getState();

  /**
   * Same as
   * <code>{@link #getState()} == {@link ConnectorState#CONNECTED}</code>.
   * <p>
   */
  public boolean isConnected();

  /**
   * Asynchronous connect. May leave this {@link IConnector} in a state where
   * <code>{@link #isConnected()} == false</code>.
   * <p>
   */
  public void connectAsync() throws ConnectorException;

  /**
   * Blocks until <code>{@link #isConnected()} == true</code> or the given
   * timeout expired.
   * <p>
   * 
   * @throws ConnectorException
   */
  public boolean waitForConnection(long timeout) throws ConnectorException;

  /**
   * Synchronous connect. Blocks until <code>{@link #isConnected()} ==
   * true</code>
   * or the given timeout expired.
   * <p>
   */
  public boolean connect(long timeout) throws ConnectorException;

  public ConnectorException disconnect();

  public IChannel[] getChannels();

  /**
   * Synchronous request to open a new {@link IChannel} with an undefined
   * channel protocol. Since the peer connector can't lookup a
   * {@link IProtocolFactory} without a protocol identifier the
   * {@link IBufferHandler} of the peer {@link IChannel} can only be provided by
   * external {@link ChannelListener}s.
   * <p>
   * 
   * @see #openChannel(String)
   */
  public IChannel openChannel() throws ConnectorException;

  /**
   * Synchronous request to open a new {@link IChannel} with a channel protocol
   * defined by a given protocol identifier. The peer connector will lookup a
   * {@link IProtocolFactory} with the protocol identifier, create a
   * {@link IBufferHandler} and inject it into the peer {@link IChannel}.
   * <p>
   * 
   * @see #openChannel()
   */
  public IChannel openChannel(String protocolID) throws ConnectorException;
}
