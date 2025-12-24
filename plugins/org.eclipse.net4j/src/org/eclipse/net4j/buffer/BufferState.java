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
package org.eclipse.net4j.buffer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Enumerates the internal states of an {@link IBuffer}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public enum BufferState
{
  /**
   * Indicates that the {@link IBuffer} has just been provided by its {@link IBufferProvider} or that is has been used
   * and subsequently {@link IBuffer#clear() cleared}.
   * <p>
   * A transition to {@link #PUTTING} can be triggered by calling {@link IBuffer#startPutting(short)} once. If the
   * buffer is intended to be passed to an {@link org.eclipse.net4j.channel.IChannel IChannel} later the
   * {@link org.eclipse.net4j.channel.IChannel#getID() channel index} of that Channel has to be passed because it is
   * part of the buffer's header. A {@link ByteBuffer} is returned that can be used for putting data.
   * <p>
   * A transition to {@link #GETTING} can be triggered by calling {@link IBuffer#startGetting(SocketChannel)} repeatedly
   * until it finally returns a {@link ByteBuffer} that can be used for getting data.
   */
  INITIAL,

  /**
   * Indicates that the {@link IBuffer} can provide a {@link ByteBuffer} that can be used for putting data.
   * <p>
   * A transition to {@link #WRITING} can be triggered by calling {@link IBuffer#write(SocketChannel)}.
   * <p>
   * A transition to {@link #GETTING} can be triggered by calling {@link IBuffer#flip()}.
   * <p>
   * A transition to {@link #INITIAL} can be triggered by calling {@link IBuffer#clear()}.
   */
  PUTTING,

  /**
   * Indicates that the {@link IBuffer} is currently writing its data to a {@link SocketChannel}.
   * <p>
   * Self transitions to {@link #WRITING} can be triggered by repeatedly calling {@link IBuffer#write(SocketChannel)}
   * until it returns <code>true</code>.
   * <p>
   * A transition to {@link #INITIAL} can be triggered by calling {@link IBuffer#clear()}.
   */
  WRITING,

  /**
   * Indicates that the {@link IBuffer} is currently reading its header from a {@link SocketChannel}.
   * <p>
   * Transitions to {@link #READING_HEADER}, {@link #READING_BODY} or {@link #GETTING} can be triggered by repeatedly
   * calling {@link IBuffer#startGetting(SocketChannel)} until it returns a {@link ByteBuffer} that can be used for
   * getting data.
   * <p>
   * A transition to {@link #INITIAL} can be triggered by calling {@link IBuffer#clear()}.
   */
  READING_HEADER,

  /**
   * Indicates that the {@link IBuffer} is currently reading its body from a {@link SocketChannel}.
   * <p>
   * Transitions to {@link #READING_BODY} or {@link #GETTING} can be triggered by repeatedly calling
   * {@link IBuffer#startGetting(SocketChannel)} until it returns a {@link ByteBuffer} that can be used for getting
   * data.
   * <p>
   * A transition to {@link #INITIAL} can be triggered by calling {@link IBuffer#clear()}.
   */
  READING_BODY,

  /**
   * Indicates that the {@link IBuffer} can provide a {@link ByteBuffer} that can be used for getting data.
   * <p>
   * A transition to {@link #INITIAL} can be triggered by calling {@link IBuffer#clear()}.
   */
  GETTING,

  /**
   * Indicates that the {@link IBuffer} is owned by its {@link IBufferProvider}.
   *
   * @since 3.0
   */
  RELEASED,

  /**
   * Indicates that the {@link IBuffer} can not be used anymore.
   *
   * @since 3.0
   */
  DISPOSED
}
