/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.channel;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * An {@link InputStream input stream} that provides the {@link IBuffer buffers} which arrive at a {@link IChannel
 * channel} as a continuous byte sequence.
 *
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
    this.millisBeforeTimeout = millisBeforeTimeout;

    channel.setReceiveHandler(this);
  }

  public IChannel getChannel()
  {
    return channel;
  }

  @Override
  public long getMillisBeforeTimeout()
  {
    return millisBeforeTimeout;
  }

  public void setMillisBeforeTimeout(long millisBeforeTimeout)
  {
    this.millisBeforeTimeout = millisBeforeTimeout;
  }

  @Override
  public long getMillisInterruptCheck()
  {
    return millisInterruptCheck;
  }

  public void setMillisInterruptCheck(long millisInterruptCheck)
  {
    this.millisInterruptCheck = millisInterruptCheck;
  }

  @Override
  public int read() throws IOException
  {
    if (isCCAM())
    {
      ExecutorService executorService = ConcurrencyUtil.getExecutorService(channel);
      executorService.submit(new Runnable()
      {
        public void run()
        {
          ConcurrencyUtil.sleep(500);
          channel.close();
        }
      });
    }

    return super.read();
  }

  @Override
  public String toString()
  {
    return "ChannelInputStream[" + channel + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
