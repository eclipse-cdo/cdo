/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.server.internal.net4j.protocol.RequestChangeCredentialsIndication.Operation;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;

/**
 * Server-initiated request to change the user's password.  It incorporates and extends the
 * authentication challenge (the current credentials must be verified in the same operation).
 * 
 * @since 4.3
 */
public class ChangeCredentialsRequest extends RequestWithMonitoring<Response>
{
  private Challenge challenge;

  private Operation operation;

  private String userID;

  public ChangeCredentialsRequest(CDOServerProtocol protocol, Challenge challenge, String userID, boolean isReset)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_CREDENTIALS);
    this.challenge = challenge;
    operation = isReset ? Operation.RESET_PASSWORD : Operation.CHANGE_PASSWORD;
    this.userID = userID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    out.writeEnum(operation);
    out.writeString(userID);
    challenge.write(out);
  }

  @Override
  protected Response confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    Response result = null;

    try
    {
      if (in.readBoolean())
      {
        result = new Response(in);
      }
    }
    catch (RemoteException e)
    {
      // user may have cancelled the change-password operation
      if (!(e.getCause() instanceof NotAuthenticatedException))
      {
        throw e;
      }
    }
    catch (Exception e)
    {
      // user may have cancelled the change-password operation
      if (!(e instanceof NotAuthenticatedException))
      {
        throw e;
      }
    }

    return result;
  }
}
