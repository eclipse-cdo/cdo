/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LockOwnerRemappedNotificationRequest extends CDOServerRequest
{
  private CDOBranch branch;

  private CDOLockOwner oldOwner;

  private CDOLockOwner newOwner;

  public LockOwnerRemappedNotificationRequest(CDOServerProtocol serverProtocol, CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_LOCK_OWNER_REMAPPED_NOTIFICATION);
    this.branch = branch;
    this.oldOwner = oldOwner;
    this.newOwner = newOwner;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOBranch(branch);
    out.writeCDOLockOwner(oldOwner);
    out.writeCDOLockOwner(newOwner);
  }
}
