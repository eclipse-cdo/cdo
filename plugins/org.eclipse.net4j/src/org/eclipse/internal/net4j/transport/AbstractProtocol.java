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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;

/**
 * @author Eike Stepper
 */
public abstract class AbstractProtocol extends AbstractLifecycle implements Protocol, BufferProvider
{
  private Channel channel;

  public AbstractProtocol(Channel channel)
  {
    this.channel = channel;
  }

  public Channel getChannel()
  {
    return channel;
  }

  public short getBufferCapacity()
  {
    return BufferUtil.getBufferProvider(channel).getBufferCapacity();
  }

  public Buffer provideBuffer()
  {
    return BufferUtil.getBufferProvider(channel).provideBuffer();
  }

  public void retainBuffer(Buffer buffer)
  {
    BufferUtil.getBufferProvider(channel).retainBuffer(buffer);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    channel = null;
    super.onDeactivate();
  }
}
