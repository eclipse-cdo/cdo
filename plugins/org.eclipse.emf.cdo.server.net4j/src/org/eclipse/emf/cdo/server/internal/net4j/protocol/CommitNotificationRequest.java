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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CommitNotificationRequest extends CDOServerRequest
{
  private CDOCommitInfo commitInfo;

  private boolean clearResourcePathCache;

  private Set<CDOID> readOnly;

  public CommitNotificationRequest(CDOServerProtocol serverProtocol, CDOCommitInfo commitInfo,
      boolean clearResourcePathCache, Set<CDOID> readOnly)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
    this.commitInfo = commitInfo;
    this.clearResourcePathCache = clearResourcePathCache;
    this.readOnly = readOnly;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOCommitInfo(commitInfo);
    out.writeBoolean(clearResourcePathCache);

    if (readOnly != null)
    {
      out.writeInt(readOnly.size());
      for (CDOID id : readOnly)
      {
        out.writeCDOID(id);
      }
    }
    else
    {
      out.writeInt(0);
    }
  }
}
