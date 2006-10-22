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

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.Channel;

import org.eclipse.internal.net4j.transport.AbstractProtocol;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class SignalProtocol extends AbstractProtocol
{
  private int nextCorrelationID;

  private Queue<Request> requestQueue = new ConcurrentLinkedQueue();

  public SignalProtocol(Channel channel)
  {
    super(channel);
  }

  public void handleBuffer(Buffer buffer)
  {
    // TODO Implement method SignalProtocol

  }

  @Override
  public String toString()
  {
    return "SignalProtocol[" + getProtocolID() + ", " + getChannel() + "]";
  }

  protected abstract Indication createIndication(short signalID);

  Object sendRequest(Request request)
  {
    // TODO Implement method SignalProtocol
    return null;
  }

  int getNextCorrelationID()
  {
    int correlationID = nextCorrelationID;
    if (nextCorrelationID == Integer.MAX_VALUE)
    {
      System.out.println(toString() + ": Correlation wrap around");
      nextCorrelationID = 0;
    }
    else
    {
      ++nextCorrelationID;
    }

    return correlationID;
  }
}
