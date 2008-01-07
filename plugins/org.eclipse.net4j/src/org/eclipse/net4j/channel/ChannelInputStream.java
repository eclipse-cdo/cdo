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

import org.eclipse.net4j.buffer.BufferInputStream;

/**
 * @author Eike Stepper
 */
public class ChannelInputStream extends BufferInputStream
{
  private IChannel channel;

  private long millisBeforeTimeout = DEFAULT_MILLIS_BEFORE_TIMEOUT;

  private long millisInterruptCheck = DEFAULT_MILLIS_INTERRUPT_CHECK;

  public ChannelInputStream(IChannel channel)
  {
    this(channel, DEFAULT_MILLIS_BEFORE_TIMEOUT);
  }

  public ChannelInputStream(IChannel channel, long millisBeforeTimeout)
  {
    this.channel = channel;
    channel.setReceiveHandler(this);
    this.millisBeforeTimeout = millisBeforeTimeout;
    millisInterruptCheck = DEFAULT_MILLIS_INTERRUPT_CHECK;
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public long getMillisBeforeTimeout()
  {
    return millisBeforeTimeout;
  }

  public void setMillisBeforeTimeout(long millisBeforeTimeout)
  {
    this.millisBeforeTimeout = millisBeforeTimeout;
  }

  public long getMillisInterruptCheck()
  {
    return millisInterruptCheck;
  }

  public void setMillisInterruptCheck(long millisInterruptCheck)
  {
    this.millisInterruptCheck = millisInterruptCheck;
  }

  @Override
  public String toString()
  {
    return "ChannelInputStream[" + channel + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
