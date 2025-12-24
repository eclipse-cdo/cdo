/*
 * Copyright (c) 2009-2012, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ObjectLockedIndication extends CDOServerReadIndication
{
  private boolean isLocked;

  public ObjectLockedIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OBJECT_LOCKED);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readXInt();
    IView view = getView(viewID);
    InternalLockManager lockManager = getRepository().getLockingManager();

    LockType lockType = in.readCDOLockType();
    CDOID id = in.readCDOID();
    Object key = getRepository().isSupportingBranches() ? CDOIDUtil.createIDAndBranch(id, view.getBranch()) : id;

    boolean byOthers = in.readBoolean();
    if (byOthers)
    {
      isLocked = lockManager.hasLockByOthers(lockType, view, key);
    }
    else
    {
      isLocked = lockManager.hasLock(lockType, view, key);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(isLocked);
  }
}
