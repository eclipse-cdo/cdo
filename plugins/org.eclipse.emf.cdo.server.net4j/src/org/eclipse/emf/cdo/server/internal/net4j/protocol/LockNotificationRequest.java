/*
 * Copyright (c) 2011, 2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public class LockNotificationRequest extends CDOServerRequest
{
  private CDOLockChangeInfo lockChangeInfo;

  private Set<CDOID> filter;

  public LockNotificationRequest(CDOServerProtocol serverProtocol, CDOLockChangeInfo lockChangeInfo, Set<CDOID> filter)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_LOCK_NOTIFICATION);
    this.lockChangeInfo = lockChangeInfo;
    this.filter = filter;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOLockChangeInfo(lockChangeInfo, filter);
  }
}
