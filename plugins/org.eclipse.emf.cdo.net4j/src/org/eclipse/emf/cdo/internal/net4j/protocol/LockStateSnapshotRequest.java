/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockStateSnapshotResult;

import java.io.IOException;

/**
 * Requests an authoritative lock-state snapshot for the current session.
 *
 * @author Eike Stepper
 */
public class LockStateSnapshotRequest extends CDOClientRequest<LockStateSnapshotResult>
{
  public LockStateSnapshotRequest(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE_SNAPSHOT);
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
  }

  @Override
  protected LockStateSnapshotResult confirming(CDODataInput in) throws IOException
  {
    long lockModCount = in.readXLong();
    return new LockStateSnapshotResult(lockModCount, in.readCDOLockStates());
  }
}
