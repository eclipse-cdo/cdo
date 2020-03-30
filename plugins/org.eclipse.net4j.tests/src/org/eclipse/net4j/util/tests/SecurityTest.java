/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.IChallengeResponse;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class SecurityTest extends AbstractOMTest
{
  private static final int TIMEOUT = 1000;

  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final char[] PASSWORD1 = "eike2007".toCharArray(); //$NON-NLS-1$

  private static final char[] PASSWORD2 = "invalid".toCharArray(); //$NON-NLS-1$

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD1);

  private IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);

  public void testRandomizerAcceptsSeedsBeforeActivation()
  {
    Randomizer randomizer = new Randomizer();
    randomizer.setSeed(2l);
  }

  public void testSuccess() throws Exception
  {
    // Prepare randomizer
    Randomizer randomizer = new Randomizer();
    randomizer.activate();

    // Prepare user manager
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    // Create negotiation contexts
    PeerNegotiationContext challengeContext = new PeerNegotiationContext();
    PeerNegotiationContext responseContext = new PeerNegotiationContext();

    challengeContext.setPeer(responseContext);
    responseContext.setPeer(challengeContext);

    // Start negotiation threads
    Thread challengeThread = new Thread(challengeContext, "challengeThread"); //$NON-NLS-1$
    challengeThread.start();

    Thread responseThread = new Thread(responseContext, "responseThread"); //$NON-NLS-1$
    responseThread.start();

    // Prepare response negotiator
    ResponseNegotiator responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();
    responseNegotiator.negotiate(responseContext);

    // Prepare challenge negotiator
    ChallengeNegotiator challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();
    challengeNegotiator.negotiate(challengeContext);

    Enum<?> responseState = responseContext.waitUntilFinished(TIMEOUT);
    assertEquals(IChallengeResponse.State.SUCCESS, responseState);

    Enum<?> challengeState = challengeContext.waitUntilFinished(TIMEOUT);
    assertEquals(IChallengeResponse.State.SUCCESS, challengeState);

    challengeContext.deactivate();
    responseContext.deactivate();
    challengeNegotiator.deactivate();
    responseNegotiator.deactivate();
    userManager.deactivate();
    randomizer.deactivate();
  }

  public void testFailure() throws Exception
  {
    // Prepare randomizer
    Randomizer randomizer = new Randomizer();
    randomizer.activate();

    // Prepare user manager
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD2);

    // Create negotiation contexts
    PeerNegotiationContext challengeContext = new PeerNegotiationContext();
    PeerNegotiationContext responseContext = new PeerNegotiationContext();

    challengeContext.setPeer(responseContext);
    responseContext.setPeer(challengeContext);

    // Start negotiation threads
    Thread challengeThread = new Thread(challengeContext, "challengeThread"); //$NON-NLS-1$
    challengeThread.start();

    Thread responseThread = new Thread(responseContext, "responseThread"); //$NON-NLS-1$
    responseThread.start();

    // Prepare response negotiator
    ResponseNegotiator responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();
    responseNegotiator.negotiate(responseContext);

    // Prepare challenge negotiator
    ChallengeNegotiator challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();
    challengeNegotiator.negotiate(challengeContext);

    Enum<?> responseState = responseContext.waitUntilFinished(TIMEOUT);
    assertEquals(IChallengeResponse.State.FAILURE, responseState);

    Enum<?> challengeState = challengeContext.waitUntilFinished(TIMEOUT);
    assertEquals(IChallengeResponse.State.FAILURE, challengeState);

    challengeContext.deactivate();
    responseContext.deactivate();
    challengeNegotiator.deactivate();
    responseNegotiator.deactivate();
    userManager.deactivate();
    randomizer.deactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class PeerNegotiationContext extends NegotiationContext implements Runnable
  {
    private final BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<>();

    private volatile PeerNegotiationContext peer;

    private volatile String userID;

    private volatile boolean running;

    public PeerNegotiationContext()
    {
    }

    @SuppressWarnings("unused")
    public PeerNegotiationContext getPeer()
    {
      return peer;
    }

    public void setPeer(PeerNegotiationContext peer)
    {
      this.peer = peer;
    }

    @SuppressWarnings("unused")
    public String getUserID()
    {
      return userID;
    }

    @Override
    public void setUserID(String userID)
    {
      this.userID = userID;
    }

    @Override
    public ByteBuffer getBuffer()
    {
      return ByteBuffer.allocateDirect(4096);
    }

    @Override
    public void transmitBuffer(ByteBuffer buffer)
    {
      buffer.flip();
      queue.add(buffer);
    }

    public void deactivate()
    {
      running = false;
    }

    @Override
    public void run()
    {
      running = true;
      while (running)
      {
        if (peer != null)
        {
          Receiver receiver = peer.getReceiver();
          if (receiver != null)
          {
            ByteBuffer buffer = null;

            try
            {
              buffer = queue.poll(50, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException ex)
            {
              throw WrappedException.wrap(ex);
            }

            if (buffer != null)
            {
              receiver.receiveBuffer(peer, buffer);
            }
          }
        }
      }
    }
  }
}
