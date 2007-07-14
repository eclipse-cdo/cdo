/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends RequestWithConfirmation<OpenSessionResult>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(CDOProtocol.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  public OpenSessionRequest(IChannel channel, String repositoryName)
  {
    super(channel);
    this.repositoryName = repositoryName;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.OPEN_SESSION_SIGNAL;
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

    Set<String> packageURIs = new HashSet();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      String packageURI = in.readString();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read package URI: {0}", packageURI);
      }

      packageURIs.add(packageURI);
    }

    return new OpenSessionResult(sessionID, repositoryUUID, packageURIs);
  }
}
