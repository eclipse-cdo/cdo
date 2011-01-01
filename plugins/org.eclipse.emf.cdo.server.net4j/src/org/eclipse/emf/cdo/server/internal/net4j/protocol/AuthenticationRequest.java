/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class AuthenticationRequest extends RequestWithConfirmation<CDOAuthenticationResult>
{
  private byte[] randomToken;

  public AuthenticationRequest(CDOServerProtocol protocol, byte[] randomToken)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_AUTHENTICATION);
    this.randomToken = randomToken;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeByteArray(randomToken);
  }

  @Override
  protected CDOAuthenticationResult confirming(ExtendedDataInputStream in) throws Exception
  {
    boolean authenticated = in.readBoolean();
    if (!authenticated)
    {
      return null;
    }

    return new CDOAuthenticationResult(in);
  }
}
