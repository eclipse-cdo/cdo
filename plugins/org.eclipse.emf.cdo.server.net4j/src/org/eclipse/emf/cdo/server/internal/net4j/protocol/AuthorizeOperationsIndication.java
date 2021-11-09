/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class AuthorizeOperationsIndication extends CDOServerReadIndication
{
  private AuthorizableOperation[] operations;

  public AuthorizeOperationsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_AUTHORIZE_OPERATIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int length = in.readXInt();
    operations = new AuthorizableOperation[length];

    for (int i = 0; i < length; i++)
    {
      operations[i] = AuthorizableOperation.read(in);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession session = getSession();
    String[] result = session.authorizeOperations(operations);

    for (int i = 0; i < result.length; i++)
    {
      out.writeString(result[i]);
    }
  }
}
