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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * Handles the request from a client to initiate the change-credentials protocol.
 */
public class RequestChangeCredentialsIndication extends CDOServerIndicationWithMonitoring
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      RequestChangeCredentialsIndication.class);

  private Operation operation;

  private String userID;

  public RequestChangeCredentialsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REQUEST_CHANGE_CREDENTIALS);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    operation = in.readEnum(Operation.class);
    userID = in.readString();

    if (TRACER.isEnabled())
    {
      TRACER.format("Initiating %s of user credentials", operation); //$NON-NLS-1$
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      try
      {
        InternalSessionManager sessionManager = getRepository().getSessionManager();

        switch (operation)
        {
        case CHANGE_PASSWORD:
          sessionManager.changeUserCredentials(getProtocol());
          break;
        case RESET_PASSWORD:
          sessionManager.resetUserCredentials(getProtocol(), userID);
          break;
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Credentials %s processed.", operation); //$NON-NLS-1$
        }
        out.writeBoolean(true);
      }
      catch (NotAuthenticatedException ex)
      {
        // user has cancelled the authentication
        out.writeBoolean(false);
        return;
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  //
  // Nested types
  //

  public static enum Operation
  {
    CHANGE_PASSWORD, RESET_PASSWORD;

    @Override
    public String toString()
    {
      switch (this)
      {
      case CHANGE_PASSWORD:
        return "change"; //$NON-NLS-1$
      case RESET_PASSWORD:
        return "reset"; //$NON-NLS-1$
      }

      return super.toString();
    }
  }
}
