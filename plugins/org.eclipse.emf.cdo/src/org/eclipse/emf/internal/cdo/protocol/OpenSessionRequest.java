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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.internal.cdo.bundle.OM;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends RequestWithConfirmation<OpenSessionResult>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  public OpenSessionRequest(IChannel channel, String repositoryName)
  {
    super(channel);
    this.repositoryName = repositoryName;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing repositoryName: {0}", repositoryName);
    }

    out.writeString(repositoryName);
  }

  @Override
  protected OpenSessionResult confirming(ExtendedDataInputStream in) throws IOException
  {
    int sessionID = in.readInt();
    if (sessionID == CDOProtocolConstants.ERROR_REPOSITORY_NOT_FOUND)
    {
      String msg = MessageFormat.format("Repository {0} not found", repositoryName);
      throw new ServerException(msg);
    }

    if (sessionID == CDOProtocolConstants.ERROR_NO_SESSION)
    {
      String msg = MessageFormat.format("Failed to open session for repository {0}", repositoryName);
      throw new ServerException(msg);
    }

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read sessionID: {0}", sessionID);
    }

    String repositoryUUID = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read repositoryUUID: {0}", repositoryUUID);
    }

    OpenSessionResult result = new OpenSessionResult(sessionID, repositoryUUID);

    for (;;)
    {
      String packageURI = in.readString();
      if (packageURI == null)
      {
        break;
      }

      boolean dynamic = in.readBoolean();
      CDOIDRange metaIDRange = CDOIDRangeImpl.read(in);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read package info: uri={0}, dynamic={1}, metaIDRange={2}", packageURI, dynamic, metaIDRange);
      }

      result.addPackageInfo(packageURI, dynamic, metaIDRange);
    }

    return result;
  }
}
