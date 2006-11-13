/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.transport;

import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * One endpoint of a physical connection of arbitrary nature between two
 * communicating parties. A {@link Connector} encapsulates the process of
 * establishing and closing such connections and has a {@link Type} of
 * {@link Type#CLIENT FOR_CLIENTS} or {@link Type#SERVER FOR_SERVERS} with
 * respect to this process. Once a connection is established either party can
 * use its {@link Connector} to open multiple {@link Channel}s to
 * asynchronously exchange {@link Buffer}s.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface Connector
{
  public static final IRegistry<Integer, Connector> REGISTRY = new HashMapRegistry();

  public Type getType();

  public boolean isClient();

  public boolean isServer();

  public ConnectorCredentials getCredentials();

  public void setCredentials(ConnectorCredentials credentials);

  public State getState();

  /**
   * Same as <code>{@link #getState()} == {@link State#CONNECTED}</code>.
   * <p>
   */
  public boolean isConnected();

  /**
   * Asynchronous connect. May leave this {@link Connector} in a state where
   * <code>{@link #isConnected()} == false</code>.
   * <p>
   */
  public void connectAsync() throws ConnectorException;

  /**
   * Blocks until <code>{@link #isConnected()} == true</code>.
   * <p>
   * 
   * @throws ConnectorException
   */
  public boolean waitForConnection(long timeout) throws ConnectorException;

  /**
   * Synchronous connect. Blocks until <code>{@link #isConnected()} ==
   * true</code>.
   * <p>
   */
  public boolean connect(long timeout) throws ConnectorException;

  public ConnectorException disconnect();

  public Channel[] getChannels();

  /**
   * Synchronous request to open a new {@link Channel} with an undefined channel
   * protocol. Since the peer connector can't lookup a {@link ProtocolFactory}
   * without a protocol identifier the {@link BufferHandler} of the peer
   * {@link Channel} can only be provided by external {@link ChannelListener}s.
   * <p>
   * 
   * @see #openChannel(String)
   */
  public Channel openChannel() throws ConnectorException;

  /**
   * Synchronous request to open a new {@link Channel} with a channel protocol
   * defined by a given protocol identifier. The peer connector will lookup a
   * {@link ProtocolFactory} with the protocol identifier, create a
   * {@link BufferHandler} and inject it into the peer {@link Channel}.
   * <p>
   * 
   * @see #openChannel()
   */
  public Channel openChannel(String protocolID) throws ConnectorException;

  public Channel openChannel(String protocolID, Object protocolData) throws ConnectorException;

  /**
   * Returns the {@link IRegistry} of {@link ProtocolFactory}s associated with
   * this connector.
   * <p>
   * 
   * @see #setProtocolFactoryRegistry(IRegistry)
   * @return The registry or <code>null</code>.
   */
  public IRegistry<String, ProtocolFactory> getProtocolFactoryRegistry();

  /**
   * Sets the {@link IRegistry} of {@link ProtocolFactory}s for this connector.
   * <p>
   * 
   * @param protocolFactoryRegistry
   *          The registry or <code>null</code>.
   */
  public void setProtocolFactoryRegistry(IRegistry<String, ProtocolFactory> protocolFactoryRegistry);

  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);

  public void addStateListener(StateListener listener);

  public void removeStateListener(StateListener listener);

  public void addChannelListener(ChannelListener listener);

  public void removeChannelListener(ChannelListener listener);

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    CLIENT, SERVER
  }

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    DISCONNECTED, CONNECTING, NEGOTIATING, CONNECTED
  }

  /**
   * @author Eike Stepper
   */
  public interface StateListener
  {
    public void notifyStateChanged(Connector connector, State newState, State oldState);
  }

  /**
   * @author Eike Stepper
   */
  public interface ChannelListener
  {
    public void notifyChannelAboutToOpen(Channel channel);

    public void notifyChannelOpened(Channel channel);

    public void notifyChannelClosing(Channel channel);
  }
}
