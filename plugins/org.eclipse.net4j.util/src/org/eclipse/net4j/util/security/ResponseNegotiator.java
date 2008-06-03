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

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class ResponseNegotiator extends ChallengeResponseNegotiator
{
  private IPasswordCredentialsProvider credentialsProvider;

  public ResponseNegotiator()
  {
    super(false);
  }

  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (credentialsProvider == null)
    {
      throw new IllegalStateException("credentialsProvider == null");
    }
  }

  @Override
  protected void createChallenge(INegotiationContext context, ByteBuffer challenge)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void handleChallenge(INegotiationContext context, ByteBuffer challenge, ByteBuffer response)
  {
    // Get random token from challenge
    int size = challenge.getInt();
    byte[] randomToken = new byte[size];
    challenge.get(randomToken);

    // Get credentials and encrypt token
    IPasswordCredentials credentials = credentialsProvider.getCredentials();
    String userID = credentials.getUserID();
    context.setUserID(userID);

    // Set userID into response
    byte[] userIDBytes = userID.getBytes();
    response.putInt(userIDBytes.length);
    response.put(userIDBytes);

    // Set crypted token into response
    byte[] cryptedToken = encryptToken(credentials.getPassword(), randomToken);
    response.putInt(cryptedToken.length);
    response.put(cryptedToken);
  }

  @Override
  protected boolean handleResponse(INegotiationContext context, ByteBuffer response)
  {
    throw new UnsupportedOperationException();
  }

  protected byte[] encryptToken(char[] password, byte[] token)
  {
    try
    {
      return SecurityUtil.encrypt(token, password, getAlgorithmName());
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new SecurityException(ex);
    }
  }
}
