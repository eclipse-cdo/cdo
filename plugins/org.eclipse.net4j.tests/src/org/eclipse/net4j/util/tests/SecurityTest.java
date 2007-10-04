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
import org.eclipse.net4j.internal.util.security.PasswordCredentials;
import org.eclipse.net4j.internal.util.security.Randomizer;
import org.eclipse.net4j.internal.util.security.ResponseNegotiator;
import org.eclipse.net4j.internal.util.security.UserManager;
import org.eclipse.net4j.util.security.ICredentials;
import org.eclipse.net4j.util.security.ICredentialsProvider;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class SecurityTest extends AbstractOMTest
{
  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD = "eike2007".toCharArray();

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD);

  private ICredentialsProvider credentialsProvider = new ICredentialsProvider()
  {
    public ICredentials getCredentials()
    {
      return CREDENTIALS;
    }
  };

  private Randomizer randomizer = new Randomizer();

  private UserManager userManager = new UserManager();

  private NegotiationContext challengeContext = new NegotiationContext()
  {
    public void transmitBuffer(ByteBuffer buffer)
    {
      buffer.flip();
      responseContext.getBufferReceiver().receiveBuffer(responseContext, buffer);
    }
  };

  private NegotiationContext responseContext = new NegotiationContext()
  {
    public void transmitBuffer(ByteBuffer buffer)
    {
      buffer.flip();
      challengeContext.getBufferReceiver().receiveBuffer(challengeContext, buffer);
    }
  };

  public void testNegotiation() throws Exception
  {
    randomizer.activate();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    ResponseNegotiator client = new ResponseNegotiator();
    client.setCredentialsProvider(credentialsProvider);
    client.activate();

    new Thread()
    {
      @Override
      public void run()
      {
        ChallengeNegotiator server = new ChallengeNegotiator();
        server.setRandomizer(randomizer);
        server.setUserManager(userManager);
        server.setTokenLength(1024);

        try
        {
          server.activate();
          server.startNegotiation(challengeContext);
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
          server.deactivate();
        }
      }
    }.start();
  }
}
