/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454: adapted from CDO Server
 */
package org.eclipse.net4j.signal.security;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;
import org.eclipse.net4j.util.security.NotAuthenticatedException;

/**
 * @author Eike Stepper
 *
 * @since 4.3
 */
public class AuthenticationRequest extends RequestWithMonitoring<Response>
{
  private final Challenge challenge;

  public AuthenticationRequest(SignalProtocol<?> protocol, short id, String name, Challenge challenge)
  {
    super(protocol, id, name);
    this.challenge = challenge;
  }

  public AuthenticationRequest(SignalProtocol<?> protocol, short signalID, Challenge challenge)
  {
    super(protocol, signalID);
    this.challenge = challenge;
  }

  public AuthenticationRequest(SignalProtocol<?> protocol, Enum<?> literal, Challenge challenge)
  {
    super(protocol, literal);
    this.challenge = challenge;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    challenge.write(out);
  }

  @Override
  protected Response confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    try
    {
      if (in.readBoolean())
      {
        return new Response(in);
      }
    }
    catch (RemoteException ex)
    {
      if (ex.getCause() instanceof NotAuthenticatedException)
      {
        // Skip silently because user has canceled the authentication
      }
      else
      {
        throw ex;
      }
    }
    catch (Exception ex)
    {
      if (ex instanceof NotAuthenticatedException)
      {
        // Skip silently because user has canceled the authentication
      }
      else
      {
        throw ex;
      }
    }

    return null;
  }
}
