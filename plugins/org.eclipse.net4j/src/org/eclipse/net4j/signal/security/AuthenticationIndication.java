/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454: adapted from CDO Server
 */
package org.eclipse.net4j.signal.security;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.security.DiffieHellman;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.ByteArrayOutputStream;

/**
 * @author Eike Stepper
 * 
 * @since 4.3
 */
public class AuthenticationIndication extends IndicationWithMonitoring
{
  private Challenge challenge;

  public AuthenticationIndication(AuthenticatingSignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  public AuthenticationIndication(AuthenticatingSignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public AuthenticationIndication(AuthenticatingSignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  public AuthenticatingSignalProtocol<?> getProtocol()
  {
    return (AuthenticatingSignalProtocol<?>)super.getProtocol();
  }

  @Override
  protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    challenge = new Challenge(in);
  }

  protected final Challenge getChallenge()
  {
    return challenge;
  }

  @Override
  protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      IPasswordCredentialsProvider credentialsProvider = getCredentialsProvider();
      if (credentialsProvider == null)
      {
        throw new IllegalStateException("No credentials provider configured"); //$NON-NLS-1$
      }

      IPasswordCredentials credentials = credentialsProvider.getCredentials();
      if (credentials == null)
      {
        throw new IllegalStateException("No credentials provided"); //$NON-NLS-1$
      }

      String userID = credentials.getUserID();
      if (StringUtil.isEmpty(userID))
      {
        throw new IllegalStateException("No userID provided"); //$NON-NLS-1$
      }

      String password = new String(credentials.getPassword());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      @SuppressWarnings("resource")
      ExtendedDataOutputStream stream = new ExtendedDataOutputStream(baos);
      stream.writeString(userID);
      stream.writeString(password);
      stream.flush();
      byte[] clearText = baos.toByteArray();

      DiffieHellman.Client client = new DiffieHellman.Client();
      Response response = client.handleChallenge(challenge, clearText);
      out.writeBoolean(true);
      response.write(out);
    }
    catch (Throwable ex)
    {
      out.writeBoolean(false);
      OM.LOG.error(ex);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  protected IPasswordCredentialsProvider getCredentialsProvider()
  {
    AuthenticatingSignalProtocol<?> protocol = getProtocol();
    return protocol.getCredentialsProvider();
  }
}
