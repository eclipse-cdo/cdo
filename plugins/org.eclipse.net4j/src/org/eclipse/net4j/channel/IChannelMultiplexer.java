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
package org.eclipse.net4j.channel;

import org.eclipse.net4j.ILocationAware;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IChannelMultiplexer extends ILocationAware, IContainer<IChannel>
{
  /**
   * @since 2.0
   */
  public static final long NO_CHANNEL_TIMEOUT = Long.MAX_VALUE;

  /**
   * Indicates to use the timeout that is configured via debug property <code>channel.timeout</code> (see .options file)
   * which has a default of 10 seconds.
   * 
   * @since 2.0
   */
  public static final long DEFAULT_CHANNEL_TIMEOUT = -1L;

  /**
   * Returns a list of currently open channels. Note that the resulting list does not contain <code>null</code> values.
   * Generally the {@link IChannel#getIndex() index} of a channel <b>must not</b> be used as an index into this list.
   * Each call to this method creates a new copy of the internal channels array, so it can safely be modified bz the
   * caller.
   * <p>
   * 
   * @since 2.0
   */
  public List<IChannel> getChannels();

  /**
   * Synchronous request to open a new {@link IChannel} with an undefined channel protocol. Since the peer connector
   * can't lookup a protocol {@link IFactory factory} without a protocol identifier the {@link IBufferHandler} of the
   * peer {@link IChannel} can only be provided by externally provided channel {@link ILifecycle lifecycle}
   * {@link IListener listeners}.
   * <p>
   * 
   * @see #openChannel(String, Object)
   * @see #openChannel(IProtocol)
   * @since 2.0
   */
  public IChannel openChannel() throws ConnectorException;

  /**
   * Synchronous request to open a new {@link IChannel} with a channel protocol defined by a given protocol identifier.
   * The peer connector will lookup a protocol {@link IFactory factory} with the protocol identifier, create a
   * {@link IBufferHandler} and inject it into the peer {@link IChannel}.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(IProtocol)
   * @since 2.0
   */
  public IChannel openChannel(String protocolID, Object infraStructure) throws ConnectorException;

  /**
   * Synchronous request to open a new {@link IChannel} with the given channel protocol . The peer connector will lookup
   * a protocol {@link IFactory factory} with the protocol identifier, create a {@link IBufferHandler} and inject it
   * into the peer channel.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(String, Object)
   * @since 2.0
   */
  public IChannel openChannel(IProtocol<?> protocol) throws ConnectorException;

  /**
   * @since 2.0
   */
  public long getChannelTimeout();

  /**
   * @since 2.0
   */
  public void setChannelTimeout(long channelTimeout);
}
