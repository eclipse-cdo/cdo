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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.transport.BufferHandler;
import org.eclipse.net4j.transport.Channel;

/**
 * @author Eike Stepper
 */
public abstract class SignalActor<RESULT> extends Signal
{
  private boolean terminated;

  private RESULT result;

  protected SignalActor(Channel channel)
  {
    SignalProtocol protocol = extractSignalProtocol(channel);
    setProtocol(protocol);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  public RESULT send() throws Exception
  {
    return send(0);
  }

  public RESULT send(long timeout) throws Exception
  {
    if (terminated)
    {
      throw new IllegalStateException("Terminated");
    }

    getProtocol().startSignal(this, timeout);
    terminated = true;
    return result;
  }

  @Override
  public String toString()
  {
    return "SignalActor[" + getSignalID() + ", " + getProtocol() + ", correlation="
        + getCorrelationID() + (terminated ? ", SENT" : "") + "]";
  }

  protected void setResult(RESULT result)
  {
    this.result = result;
  }

  private static SignalProtocol extractSignalProtocol(Channel channel)
  {
    BufferHandler receiveHandler = channel.getReceiveHandler();
    if (receiveHandler == null)
    {
      throw new IllegalArgumentException("Channel has no protocol");
    }

    if (!(receiveHandler instanceof SignalProtocol))
    {
      throw new IllegalArgumentException("Channel has no signal protocol");
    }

    return (SignalProtocol)receiveHandler;
  }
}
