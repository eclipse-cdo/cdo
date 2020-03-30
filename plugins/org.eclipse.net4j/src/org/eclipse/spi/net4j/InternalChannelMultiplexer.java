/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.ITransportConfigAware;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalChannelMultiplexer extends IChannelMultiplexer, IBufferProvider, ITransportConfigAware
{
  /**
   * @since 4.5
   */
  public static final ThreadLocal<InternalChannelMultiplexer> CONTEXT_MULTIPLEXER = new ThreadLocal<>();

  /**
   * @since 4.0
   */
  public static final short RESERVED_CHANNEL = 0;

  /**
   * Called by a {@link IChannel channel} each time a new {@link IBuffer buffer} is available for multiplexing. This or another buffer can be
   * dequeued from the {@link InternalChannel#getSendQueue() send queue} of the channel.
   */
  public void multiplexChannel(InternalChannel channel);

  /**
   * @since 2.0
   */
  public void closeChannel(InternalChannel channel);

  /**
   * @author Eike Stepper
   * @since 4.10
   */
  public interface BufferMultiplexer
  {
    /**
     * Called by a {@link IChannel channel} each time a new {@link IBuffer buffer} is available for multiplexing.
     */
    public void multiplexBuffer(InternalChannel channel, IBuffer buffer);
  }
}
