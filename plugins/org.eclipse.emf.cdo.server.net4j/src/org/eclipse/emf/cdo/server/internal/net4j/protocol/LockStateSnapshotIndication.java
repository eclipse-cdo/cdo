/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.LockingManager;
import org.eclipse.emf.cdo.internal.server.Session;

import java.io.IOException;

/**
 * Returns a lock-state snapshot paired with the session sequence baseline it represents.
 *
 * @author Eike Stepper
 */
public class LockStateSnapshotIndication extends CDOServerReadIndication
{
  private LockingManager.LockStateSnapshot snapshot;

  public LockStateSnapshotIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE_SNAPSHOT);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    snapshot = ((LockingManager)getRepository().getLockingManager()).snapshotLockStates((Session)getSession());
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeXLong(snapshot.getLockModCount());
    out.writeCDOLockStates(snapshot.getLockStates(), null);
  }
}
