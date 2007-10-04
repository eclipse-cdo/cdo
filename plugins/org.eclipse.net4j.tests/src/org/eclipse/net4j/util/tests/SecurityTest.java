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
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.security.ChallengeNegotiator;
import org.eclipse.net4j.internal.util.security.NegotiationContext;
import org.eclipse.net4j.internal.util.security.Randomizer;
import org.eclipse.net4j.internal.util.security.ResponseNegotiator;
import org.eclipse.net4j.internal.util.security.UserManager;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class SecurityTest extends AbstractOMTest
{
  private Randomizer randomizer = new Randomizer();

  private UserManager userManager = new UserManager();

  private NegotiationContext challengeContext = new NegotiationContext()
  {
    public void transmitBuffer(ByteBuffer buffer)
    {
      buffer.flip();
      responseContext.getBufferReceiver().receiveBuffer(buffer);
    }
  };

  private NegotiationContext responseContext = new NegotiationContext()
  {
    public void transmitBuffer(ByteBuffer buffer)
    {
      buffer.flip();
      challengeContext.getBufferReceiver().receiveBuffer(buffer);
    }
  };

  public void testNegotiation() throws Exception
  {
    randomizer.activate();
    userManager.activate();
    userManager.addUser("stepper", "eike2007".toCharArray());

    ResponseNegotiator responseNegotiator = new ResponseNegotiator();
    new Thread()
    {
      @Override
      public void run()
      {
        ChallengeNegotiator negotiator = new ChallengeNegotiator();
        negotiator.setRandomizer(randomizer);
        negotiator.setUserManager(userManager);
        negotiator.setTokenLength(1024);

        try
        {
          negotiator.activate();
          negotiator.startNegotiation(challengeContext);
          NegotiationContext.State result = challengeContext.waitForResult(2000);
          System.out.println(result);
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          fail(ex.getMessage());
        }
        finally
        {
          negotiator.deactivate();
        }
      }
    }.start();
  }
}
