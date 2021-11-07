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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession.AuthorizableOperation;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class AuthorizeOperationsRequest extends CDOClientRequest<String[]>
{
  private AuthorizableOperation[] operations;

  public AuthorizeOperationsRequest(CDOClientProtocol protocol, AuthorizableOperation[] operations)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_AUTHORIZE_OPERATIONS);
    this.operations = operations;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    int length = operations.length;
    out.writeXInt(length);

    for (int i = 0; i < length; i++)
    {
      AuthorizableOperation operation = operations[i];
      operation.write(out);
    }
  }

  @Override
  protected String[] confirming(CDODataInput in) throws IOException
  {
    String[] result = new String[operations.length];

    for (int i = 0; i < result.length; i++)
    {
      result[i] = in.readString();
    }

    return result;
  }
}
