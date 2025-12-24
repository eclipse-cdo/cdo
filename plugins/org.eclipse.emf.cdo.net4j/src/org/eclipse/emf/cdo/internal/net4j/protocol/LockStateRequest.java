/*
 * Copyright (c) 2011, 2012, 2015, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author Caspar De Groot
 */
public class LockStateRequest extends CDOClientRequest<List<CDOLockState>>
{
  private int branchID;

  private Collection<CDOID> ids;

  private int prefetchDepth;

  public LockStateRequest(CDOClientProtocol protocol, int branchID, Collection<CDOID> ids, int prefetchDepth)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
    this.branchID = branchID;
    this.ids = ids;
    this.prefetchDepth = prefetchDepth;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(branchID);

    if (prefetchDepth == CDOLockState.DEPTH_NONE)
    {
      out.writeXInt(ids.size());
    }
    else
    {
      out.writeXInt(-ids.size());
      out.writeXInt(prefetchDepth);
    }

    for (CDOID id : ids)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected List<CDOLockState> confirming(CDODataInput in) throws IOException
  {
    return in.readCDOLockStates();
  }

  @Override
  protected String getAdditionalInfo()
  {
    return MessageFormat.format("branchID={0}, ids={1}, prefetchDepth={2}", branchID, ids, prefetchDepth);
  }
}
