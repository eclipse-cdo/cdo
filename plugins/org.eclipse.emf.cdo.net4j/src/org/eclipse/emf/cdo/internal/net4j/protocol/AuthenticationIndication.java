/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
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

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.ByteArrayOutputStream;

/**
 * @author Eike Stepper
 */
public class AuthenticationIndication extends IndicationWithMonitoring
{
  private Challenge challenge;

  public AuthenticationIndication(SignalProtocol<?> protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_AUTHENTICATION);
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getSession();
  }

  @Override
  protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    challenge = new Challenge(in);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      IPasswordCredentialsProvider credentialsProvider = getSession().getCredentialsProvider();
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
      if (StringUtil.isEmpty(userID))
      {
        throw new IllegalStateException("No password provided"); //$NON-NLS-1$
      }

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

      // CDOAuthenticator authenticator = getSession().getAuthenticator();
      // if (authenticator == null)
      // {
      //        throw new IllegalStateException("No authenticator configured"); //$NON-NLS-1$
      // }
      //
      // CDOAuthenticationResult result = authenticator.authenticate(challenge);
      // if (result == null)
      // {
      // out.writeBoolean(false);
      // return;
      // }
      //
      // String userID = result.getUserID();
      // if (userID == null)
      // {
      //        throw new SecurityException("No user ID"); //$NON-NLS-1$
      // }
      //
      // byte[] cryptedToken = result.getCryptedToken();
      // if (cryptedToken == null)
      // {
      //        throw new SecurityException("No crypted token"); //$NON-NLS-1$
      // }
      //
      // out.writeBoolean(true);
      // result.write(out);
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
}
