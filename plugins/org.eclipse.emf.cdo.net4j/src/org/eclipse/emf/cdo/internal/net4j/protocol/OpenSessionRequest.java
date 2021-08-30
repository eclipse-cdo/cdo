/*
 * Copyright (c) 2009-2013, 2015-2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends CDOClientRequestWithMonitoring<OpenSessionResult>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  private int sessionID;

  private String userID;

  private boolean passiveUpdateEnabled;

  private PassiveUpdateMode passiveUpdateMode;

  private LockNotificationMode lockNotificationMode;

  private boolean subscribed;

  public OpenSessionRequest(CDOClientProtocol protocol, String repositoryName, int sessionID, String userID, boolean passiveUpdateEnabled,
      PassiveUpdateMode passiveUpdateMode, LockNotificationMode lockNotificationMode, boolean subscribed)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_SESSION);
    this.repositoryName = repositoryName;
    this.sessionID = sessionID;
    this.userID = userID;
    this.passiveUpdateEnabled = passiveUpdateEnabled;
    this.passiveUpdateMode = passiveUpdateMode;
    this.lockNotificationMode = lockNotificationMode;
    this.subscribed = subscribed;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing repositoryName: {0}", repositoryName); //$NON-NLS-1$
    }

    out.writeString(repositoryName);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing sessionID: {0}", sessionID); //$NON-NLS-1$
    }

    out.writeXInt(sessionID);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing userID: {0}", userID); //$NON-NLS-1$
    }

    out.writeString(userID);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing passiveUpdateEnabled: {0}", passiveUpdateEnabled); //$NON-NLS-1$
    }

    out.writeBoolean(passiveUpdateEnabled);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing passiveUpdateMode: {0}", passiveUpdateMode); //$NON-NLS-1$
    }

    out.writeEnum(passiveUpdateMode);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing lockNotificationMode: {0}", lockNotificationMode); //$NON-NLS-1$
    }

    out.writeEnum(lockNotificationMode);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing subscribed: {0}", subscribed); //$NON-NLS-1$
    }

    out.writeBoolean(subscribed);
  }

  @Override
  protected OpenSessionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    int sessionID = in.readXInt();
    if (sessionID == 0)
    {
      // The user has canceled the authentication
      return null;
    }

    return new OpenSessionResult(in, sessionID);
  }
}
