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
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LockObjectsRequest extends CDOClientRequest<Boolean>
{
  private int viewID;

  private IRWLockManager.LockType lockType;

  private long timeout;

  private List<InternalCDORevision> viewedRevisions;

  /**
   * The branch being viewed
   */
  private CDOBranch viewedBranch;

  public LockObjectsRequest(CDOClientProtocol protocol, List<InternalCDORevision> viewedRevisions, int viewID,
      CDOBranch viewedBranch, LockType lockType, long timeout)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);

    this.viewID = viewID;
    this.lockType = lockType;
    this.timeout = timeout;
    this.viewedRevisions = viewedRevisions;
    this.viewedBranch = viewedBranch;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    out.writeCDOLockType(lockType);
    out.writeLong(timeout);
    out.writeCDOBranch(viewedBranch);

    out.writeInt(viewedRevisions.size());
    for (CDORevision revision : viewedRevisions)
    {
      out.writeCDORevisionKey(revision);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
