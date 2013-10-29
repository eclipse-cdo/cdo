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
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.NotAuthenticatedException;

/**
 * Handles the request from a client to initiate the change-credentials protocol.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class ChangeCredentialsIndication extends CDOServerIndicationWithMonitoring
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, ChangeCredentialsIndication.class);

  private CredentialsUpdateOperation operation;

  private String userID;

  public ChangeCredentialsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_CREDENTIALS);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    operation = in.readEnum(CredentialsUpdateOperation.class);
    userID = in.readString();

    if (TRACER.isEnabled())
    {
      TRACER.format("Initiating {0} of user credentials", operation); //$NON-NLS-1$
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
          sessionManager.changeUserCredentials(getProtocol(), getSession().getUserID());
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
        // User has cancelled the authentication
        out.writeBoolean(false);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }
}
