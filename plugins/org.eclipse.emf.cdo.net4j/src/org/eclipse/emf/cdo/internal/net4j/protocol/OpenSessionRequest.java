/*
 * Copyright (c) 2009-2013, 2015-2017, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.security.LoginPeekException;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends CDOClientRequestWithMonitoring<OpenSessionResult>
{
  private final String repositoryName;

  private final int sessionID;

  private final String userID;

  private final byte[] oneTimeLoginToken;

  private final boolean loginPeek;

  private final boolean passiveUpdateEnabled;

  private final PassiveUpdateMode passiveUpdateMode;

  private final LockNotificationMode lockNotificationMode;

  private final boolean subscribed;

  private final AuthorizableOperation[] operations;

  public OpenSessionRequest(CDOClientProtocol protocol, String repositoryName, int sessionID, String userID, byte[] oneTimeLoginToken, boolean loginPeek,
      boolean passiveUpdateEnabled, PassiveUpdateMode passiveUpdateMode, LockNotificationMode lockNotificationMode, boolean subscribed,
      AuthorizableOperation[] operations)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_SESSION);
    this.repositoryName = repositoryName;
    this.sessionID = sessionID;
    this.userID = userID;
    this.oneTimeLoginToken = oneTimeLoginToken;
    this.loginPeek = loginPeek;
    this.passiveUpdateEnabled = passiveUpdateEnabled;
    this.passiveUpdateMode = passiveUpdateMode;
    this.lockNotificationMode = lockNotificationMode;
    this.subscribed = subscribed;
    this.operations = operations;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeString(repositoryName);
    out.writeXInt(sessionID);
    out.writeString(userID);
    out.writeByteArray(oneTimeLoginToken);
    out.writeBoolean(loginPeek);
    out.writeBoolean(passiveUpdateEnabled);
    out.writeEnum(passiveUpdateMode);
    out.writeEnum(lockNotificationMode);
    out.writeBoolean(subscribed);

    int size = operations == null ? 0 : operations.length;
    out.writeXInt(size);

    for (int i = 0; i < operations.length; i++)
    {
      AuthorizableOperation operation = operations[i];
      operation.write(out);
    }
  }

  @Override
  protected OpenSessionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    boolean loginPeekFailure = in.readBoolean();
    if (loginPeekFailure)
    {
      throw new LoginPeekException();
    }

    int sessionID = in.readXInt();
    if (sessionID == 0)
    {
      // The user has canceled the authentication
      return null;
    }

    return new OpenSessionResult(in, sessionID);
  }

  @Override
  protected String getAdditionalInfo()
  {
    String info = "repository=" + repositoryName;

    if (userID != null)
    {
      info += ", userID=" + userID;
    }

    info += ", passiveUpdates=";
    if (passiveUpdateEnabled)
    {
      info += passiveUpdateMode;
    }
    else
    {
      info += "OFF";
    }

    info += ", lockNotifications=" + lockNotificationMode;
    info += ", subscribed=" + subscribed;
    return info;
  }
}
