/*
 * Copyright (c) 2010-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * @author Eike Stepper
 */
public class OpenViewRequest extends CDOClientRequest<CDOBranchPoint>
{
  private final int viewID;

  private final boolean readOnly;

  private final CDOBranchPoint branchPoint;

  private final String durableLockingID;

  private final BiConsumer<CDOID, LockGrade> consumer;

  public OpenViewRequest(CDOClientProtocol protocol, int viewID, boolean readOnly, CDOBranchPoint branchPoint)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.viewID = viewID;
    this.readOnly = readOnly;
    this.branchPoint = branchPoint;
    durableLockingID = null;
    consumer = null;
  }

  public OpenViewRequest(CDOClientProtocol protocol, int viewID, boolean readOnly, String durableLockingID, BiConsumer<CDOID, LockGrade> consumer)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.viewID = viewID;
    this.readOnly = readOnly;
    this.durableLockingID = durableLockingID;
    this.consumer = consumer;
    branchPoint = null;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(viewID);
    out.writeBoolean(readOnly);

    if (branchPoint != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(branchPoint);
    }
    else
    {
      out.writeBoolean(false);
      out.writeString(durableLockingID);
      out.writeBoolean(consumer != null);
    }
  }

  @Override
  protected CDOBranchPoint confirming(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      CDOBranchPoint branchPoint = in.readCDOBranchPoint();

      if (consumer != null)
      {
        for (;;)
        {
          CDOID id = in.readCDOID();
          if (CDOIDUtil.isNull(id))
          {
            break;
          }

          LockGrade lockGrade = in.readEnum(LockGrade.class);
          consumer.accept(id, lockGrade);
        }
      }

      return branchPoint;
    }

    if (durableLockingID != null)
    {
      String message = in.readString();
      if (message != null)
      {
        throw new IllegalStateException(message);
      }

      throw new LockAreaNotFoundException(durableLockingID);
    }

    return null;
  }

  @Override
  protected String getAdditionalInfo()
  {
    String info = "readOnly=" + readOnly + ", branchPoint=" + branchPoint;

    if (durableLockingID != null)
    {
      info += ", durableLockingID=" + durableLockingID;
    }

    return info;
  }
}
