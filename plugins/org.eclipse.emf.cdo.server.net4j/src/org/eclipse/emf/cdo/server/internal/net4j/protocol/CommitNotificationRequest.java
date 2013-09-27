/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends CDOServerRequest
{
  private CommitNotificationInfo info;

  public CommitNotificationRequest(CDOServerProtocol serverProtocol, CommitNotificationInfo info)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
    this.info = info;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    info.write(out);
  }
}
