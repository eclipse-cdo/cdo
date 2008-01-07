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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ReflectUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class SignalActor<RESULT> extends Signal
{
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  private boolean terminated;

  private RESULT result;

  protected SignalActor(IChannel channel)
  {
    SignalProtocol protocol = extractSignalProtocol(channel);
    setProtocol(protocol);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  public RESULT send() throws Exception
  {
    return send(NO_TIMEOUT);
  }

  public RESULT send(long timeout) throws Exception
  {
    if (terminated)
    {
      throw new IllegalStateException("Terminated"); //$NON-NLS-1$
    }

    getProtocol().startSignal(this, timeout);
    terminated = true;
    return result;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}, {2}, correlation={3} {4}]", ReflectUtil.getSimpleName(getClass()),
        getSignalID(), getProtocol(), getCorrelationID(), terminated ? "SENT" : "UNSENT");
  }

  protected void setResult(RESULT result)
  {
    this.result = result;
  }

  private static SignalProtocol extractSignalProtocol(IChannel channel)
  {
    IBufferHandler receiveHandler = channel.getReceiveHandler();
    if (receiveHandler == null)
    {
      throw new IllegalArgumentException("Channel has no protocol"); //$NON-NLS-1$
    }

    if (receiveHandler instanceof SignalProtocol)
    {
      return (SignalProtocol)receiveHandler;
    }

    throw new IllegalArgumentException("Channel has no signal protocol"); //$NON-NLS-1$
  }
}
