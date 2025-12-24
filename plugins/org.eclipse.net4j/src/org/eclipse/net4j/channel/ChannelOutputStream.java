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
package org.eclipse.net4j.channel;

import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;

import java.io.OutputStream;

/**
 * An {@link OutputStream output stream} that fragments the written byte sequence into fixed-sized {@link IBuffer
 * buffers} and passes them to configured {@link IChannel channel}.
 *
 * @author Eike Stepper
 */
public class ChannelOutputStream extends BufferOutputStream
{
  public ChannelOutputStream(IChannel channel)
  {
    super(channel, channel.getID());
  }

  public ChannelOutputStream(IChannel channel, IBufferProvider bufferProvider)
  {
    super(channel, bufferProvider, channel == null ? IBuffer.NO_CHANNEL : channel.getID());
  }
}
