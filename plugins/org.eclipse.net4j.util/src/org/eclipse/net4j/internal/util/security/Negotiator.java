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

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.security.IBufferReceiver;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public abstract class Negotiator extends Lifecycle implements INegotiator, IBufferReceiver
{
  public static final int INITIAL = 0;

  public static final int SUCCESS = -1;

  public static final int FAILURE = -2;

  public static final int NEED_MORE_BUFFERS = -3;

  private int phase = INITIAL;

  private boolean initiator;

  private INegotiationContext context;

  public Negotiator(boolean initiator)
  {
    this.initiator = initiator;
  }

  public int getPhase()
  {
    return phase;
  }

  public boolean isInitiator()
  {
    return initiator;
  }

  public void startNegotiation(INegotiationContext context)
  {
    this.context = context;
    context.setBufferReceiver(this);
    if (initiator)
    {
      doNegotiation(null);
    }
  }

  public void receiveBuffer(ByteBuffer buffer)
  {
    checkContext();

    try
    {
      doNegotiation(buffer);
    }
    catch (SecurityException ex)
    {
      context.negotiationFailure();
    }
  }

  protected void doNegotiation(ByteBuffer buffer)
  {
    int result = negotiate(phase, buffer);
    switch (result)
    {
    case SUCCESS:
      context.negotiationSuccess();
      break;
    case FAILURE:
      context.negotiationSuccess();
      break;
    case NEED_MORE_BUFFERS:
      break;
    default:
      phase = result;
      break;
    }
  }

  protected abstract int negotiate(int phase, ByteBuffer buffer);

  protected ByteBuffer getBuffer()
  {
    checkContext();
    return context.getBuffer();
  }

  protected void transmitBuffer(ByteBuffer buffer)
  {
    checkContext();
    context.transmitBuffer(buffer);
  }

  private void checkContext()
  {
    if (context == null)
    {
      throw new IllegalStateException("context == null");
    }
  }
}
