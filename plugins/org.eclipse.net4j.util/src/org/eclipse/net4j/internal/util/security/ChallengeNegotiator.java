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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IChallengeResponse;
import org.eclipse.net4j.util.security.IRandomizer;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class ChallengeNegotiator extends UserManagerNegotiator implements IChallengeResponse
{
  public static final int DEFAULT_TOKEN_LENGTH = 128;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ChallengeNegotiator.class);

  private int tokenLength = DEFAULT_TOKEN_LENGTH;

  private IRandomizer randomizer;

  private transient byte[] randomToken;

  public ChallengeNegotiator()
  {
    super(true);
  }

  public int getTokenLength()
  {
    return tokenLength;
  }

  public void setTokenLength(int tokenLength)
  {
    this.tokenLength = tokenLength;
  }

  public IRandomizer getRandomizer()
  {
    return randomizer;
  }

  public void setRandomizer(IRandomizer randomizer)
  {
    this.randomizer = randomizer;
  }

  @Override
  protected int negotiate(int phase, ByteBuffer buffer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Negotiating phase {0}", phase);
    }

    switch (phase)
    {
    case INITIAL:
      challenge();
      return PHASE_RESPONSE;

    case PHASE_RESPONSE:
      try
      {
        if (verifyResponse(buffer))
        {
          acknowledge(true);
          return SUCCESS;
        }
        else
        {
          return NEED_MORE_BUFFERS;
        }
      }
      catch (SecurityException ex)
      {
        acknowledge(false);
        return FAILURE;
      }

    case PHASE_ACKNOWLEDGE:
      break;

    default:
      break;
    }
    return 0;
  }

  /**
   * Use {@link #getBuffer()} and {@link #transmitBuffer(ByteBuffer)} to send the challenge.
   */
  protected void challenge()
  {
    if (TRACER.isEnabled()) TRACER.trace("Transmitting token");
    randomToken = createRandomToken();
    ByteBuffer buffer = getBuffer();
    buffer.putInt(randomToken.length);
    buffer.put(randomToken);
    transmitBuffer(buffer);
  }

  /**
   * Use the passed <code>ByteBuffer</code> to authenticate the user.
   * 
   * @return <code>true</code> if authentication was successful, <code>false</code> if more buffers are needed.
   * @throws SecurityException
   *           if authentication was not successful.
   */
  protected boolean verifyResponse(ByteBuffer buffer) throws SecurityException
  {
    if (TRACER.isEnabled()) TRACER.trace("Received cryptedToken");
    int size = buffer.getInt();
    byte[] cryptedTokenFromClient = new byte[size];
    buffer.get(cryptedTokenFromClient);

    if (TRACER.isEnabled()) TRACER.trace("Received userID");
    size = buffer.getInt();
    byte[] userIDBytes = new byte[size];
    buffer.get(userIDBytes);

    String userID = new String(userIDBytes);
    byte[] cryptedToken = encrypt(userID, randomToken);

    if (Arrays.equals(cryptedToken, cryptedTokenFromClient))
    {
      return true;
    }

    throw new SecurityException("User could not be authenticated: " + userID);
  }

  /**
   * Use {@link #getBuffer()} and {@link #transmitBuffer(ByteBuffer)} to send the acknowledgement. The default
   * implementation of this method jsut sends a buffer with <code>(byte)1</code> if <code>success == true</code> or
   * <code>(byte)0</code> if <code>success == false</code>.
   */
  protected void acknowledge(boolean success)
  {
    ByteBuffer buffer = getBuffer();
    buffer.put(success ? ACKNOWLEDGE_SUCCESS : ACKNOWLEDGE_FAILURE);
    transmitBuffer(buffer);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (tokenLength <= 0)
    {
      throw new IllegalStateException("tokenLength must be positive");
    }

    if (randomizer == null)
    {
      throw new IllegalStateException("randomizer == null");
    }
  }

  protected byte[] createRandomToken()
  {
    byte[] token = new byte[tokenLength];
    randomizer.nextBytes(token);
    return token;
  }
}
