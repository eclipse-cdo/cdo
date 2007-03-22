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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IProtocol;

import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class Protocol extends Lifecycle implements IProtocol, IBufferProvider
{
  private Channel channel;

  public Protocol()
  {
  }

  public Channel getChannel()
  {
    return channel;
  }

  public void setChannel(IChannel channel)
  {
    this.channel = (Channel)channel;
  }

  public short getBufferCapacity()
  {
    return BufferUtil.getBufferProvider(channel).getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return BufferUtil.getBufferProvider(channel).provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    BufferUtil.getBufferProvider(channel).retainBuffer(buffer);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (channel == null)
    {
      throw new IllegalStateException("channel == null");
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    channel = null;
    super.doDeactivate();
  }
}
