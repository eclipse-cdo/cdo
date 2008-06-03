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
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.fsm.ITransition;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public abstract class ChallengeResponseNegotiator extends
    Negotiator<IChallengeResponse.State, IChallengeResponse.Event> implements IChallengeResponse
{
  public static final String DEFAULT_ALGORITHM_NAME = SecurityUtil.PBE_WITH_MD5_AND_DES;

  private String algorithmName = DEFAULT_ALGORITHM_NAME;

  public ChallengeResponseNegotiator(boolean initiator)
  {
    super(State.class, Event.class, State.INITIAL, State.SUCCESS, State.FAILURE, Event.START, Event.BUFFER, initiator);

    init(State.INITIAL, Event.START, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer NULL)
      {
        // Create and transmit challenge
        ByteBuffer challenge = context.getBuffer();
        createChallenge(context, challenge);
        context.transmitBuffer(challenge);

        // Set context state
        changeState(context, State.CHALLENGE);
      }
    });

    init(State.INITIAL, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer challenge)
      {
        // Handle challenge and transmit response
        ByteBuffer response = context.getBuffer();
        handleChallenge(context, challenge, response);
        context.transmitBuffer(response);

        // Set context state
        changeState(context, State.RESPONSE);
      }
    });

    init(State.CHALLENGE, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer response)
      {
        // Handle response
        boolean success = handleResponse(context, response);

        // Transmit acknowledgement
        ByteBuffer acknowledgement = context.getBuffer();
        acknowledgement.put(success ? ACKNOWLEDGE_SUCCESS : ACKNOWLEDGE_FAILURE);
        context.transmitBuffer(acknowledgement);

        // Set context state
        changeState(context, success ? State.SUCCESS : State.FAILURE);
      }
    });

    init(State.RESPONSE, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer acknowledgement)
      {
        // Handle acknowledgement
        boolean success = acknowledgement.get() == ACKNOWLEDGE_SUCCESS;

        // Set context state
        changeState(context, success ? State.SUCCESS : State.FAILURE);
      }
    });
  }

  public String getAlgorithmName()
  {
    return algorithmName;
  }

  public void setAlgorithmName(String algorithmName)
  {
    this.algorithmName = algorithmName;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (algorithmName == null)
    {
      throw new IllegalStateException("algorithmName == null");
    }
  }

  @Override
  protected State getState(INegotiationContext subject)
  {
    return (State)subject.getState();
  }

  @Override
  protected void setState(INegotiationContext subject, State state)
  {
    subject.setState(state);
  }

  protected abstract void createChallenge(INegotiationContext context, ByteBuffer challenge);

  protected abstract void handleChallenge(INegotiationContext context, ByteBuffer challenge, ByteBuffer response);

  protected abstract boolean handleResponse(INegotiationContext context, ByteBuffer response);

  /**
   * @author Eike Stepper
   */
  protected abstract class Transition implements ITransition<State, Event, INegotiationContext, ByteBuffer>
  {
    public final void execute(INegotiationContext context, State state, Event event, ByteBuffer buffer)
    {
      execute(context, buffer);
    }

    protected abstract void execute(INegotiationContext context, ByteBuffer buffer);
  }
}
