/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;
import org.eclipse.net4j.util.security.NotAuthenticatedException;

/**
 * Server-initiated request to change the user's password.  It incorporates and extends the
 * authentication challenge (the current credentials must be verified in the same operation).
 *
 * @since 4.3
 * @author Christian W. Damus (CEA LIST)
 */
public class CredentialsChallengeRequest extends RequestWithMonitoring<Response>
{
  private Challenge challenge;

  private String userID;

  private CredentialsUpdateOperation operation;

  public CredentialsChallengeRequest(CDOServerProtocol protocol, Challenge challenge, String userID, CredentialsUpdateOperation operation)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREDENTIALS_CHALLENGE);
    this.challenge = challenge;
    this.userID = userID;
    this.operation = operation;
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
    catch (RemoteException ex)
    {
      // User may have cancelled the change-password operation
      if (!(ex.getCause() instanceof NotAuthenticatedException))
      {
        throw ex;
      }
    }
    catch (Exception ex)
    {
      // User may have cancelled the change-password operation
      if (!(ex instanceof NotAuthenticatedException))
      {
        throw ex;
      }
    }

    return result;
  }
}
