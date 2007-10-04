/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.security;

import org.eclipse.net4j.util.security.IBufferReceiver;
import org.eclipse.net4j.util.security.INegotiationContext;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class NegotiationContext implements INegotiationContext
{
  private IBufferReceiver bufferReceiver;

  private State state = State.ONGOING;

  private CountDownLatch ongoingLatch = new CountDownLatch(1);

  public NegotiationContext()
  {
  }

  public IBufferReceiver getBufferReceiver()
  {
    return bufferReceiver;
  }

  public void setBufferReceiver(IBufferReceiver bufferReceiver)
  {
    this.bufferReceiver = bufferReceiver;
  }

  public State getState()
  {
    return state;
  }

  public ByteBuffer getBuffer()
  {
    return ByteBuffer.allocateDirect(4096);
  }

  public void negotiationSuccess()
  {
    state = State.SUCCESS;
    ongoingLatch.countDown();
  }

  public void negotiationFailure()
  {
    state = State.FAILURE;
    ongoingLatch.countDown();
  }

  public State waitForResult(long timeout)
  {
    try
    {
      if (timeout == -1)
      {
        ongoingLatch.await();
      }
      else
      {
        ongoingLatch.await(timeout, TimeUnit.MILLISECONDS);
      }
    }
    catch (InterruptedException ex)
    {
      state = State.INTERRUPTED;
    }

    return state;
  }

  /**
   * @author Eike Stepper
   */
  public static enum State
  {
    ONGOING, SUCCESS, FAILURE, INTERRUPTED;
  }
}
