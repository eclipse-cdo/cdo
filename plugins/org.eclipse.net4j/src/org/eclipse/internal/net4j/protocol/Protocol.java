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
package org.eclipse.internal.net4j.protocol;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.protocol.IProtocol;

import org.eclipse.internal.net4j.channel.Channel;

/**
 * @author Eike Stepper
 */
public abstract class Protocol extends Lifecycle implements IProtocol, IBufferProvider
{
  private Channel channel;

  private Object infraStructure;

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

  public Object getInfraStructure()
  {
    return infraStructure;
  }

  public void setInfraStructure(Object infraStructure)
  {
    this.infraStructure = infraStructure;
  }

  public short getBufferCapacity()
  {
    return Net4jUtil.getBufferProvider(channel).getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return Net4jUtil.getBufferProvider(channel).provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    Net4jUtil.getBufferProvider(channel).retainBuffer(buffer);
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
