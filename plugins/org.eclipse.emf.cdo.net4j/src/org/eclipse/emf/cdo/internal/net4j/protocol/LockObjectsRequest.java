/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LockObjectsRequest extends RefreshSessionRequest
{
  private int viewID;

  private IRWLockManager.LockType lockType;

  private long timeout;

  public LockObjectsRequest(CDOClientProtocol protocol, long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int viewID, IRWLockManager.LockType lockType,
      long timeout)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS, lastUpdateTime, viewedRevisions, CDORevision.UNCHUNKED,
        false);
    this.viewID = viewID;
    this.lockType = lockType;
    this.timeout = timeout;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    super.requesting(out);
    out.writeInt(viewID);
    out.writeCDOLockType(lockType);
    out.writeLong(timeout);
  }
}
