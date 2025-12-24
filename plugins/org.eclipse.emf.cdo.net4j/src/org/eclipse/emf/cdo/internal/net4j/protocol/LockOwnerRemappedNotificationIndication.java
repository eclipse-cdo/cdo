/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LockOwnerRemappedNotificationIndication extends CDOClientIndication
{
  public LockOwnerRemappedNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OWNER_REMAPPED_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDOBranch branch = in.readCDOBranch();
    CDOLockOwner oldOwner = in.readCDOLockOwner();
    CDOLockOwner newOwner = in.readCDOLockOwner();

    CDOLockStateCache cache = getSession().getLockStateCache();
    cache.remapOwner(branch, oldOwner, newOwner);
  }
}
