/***************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChangeViewRequest extends CDOClientRequestWithMonitoring<boolean[]>
{
  private int viewID;

  private CDOBranchPoint branchPoint;

  private List<InternalCDOObject> invalidObjects;

  public ChangeViewRequest(CDOClientProtocol protocol, int viewID, CDOBranchPoint branchPoint,
      List<InternalCDOObject> invalidObjects)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_VIEW);
    this.viewID = viewID;
    this.branchPoint = branchPoint;
    this.invalidObjects = invalidObjects;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeInt(viewID);
    out.writeCDOBranchPoint(branchPoint);

    out.writeInt(invalidObjects.size());
    for (InternalCDOObject object : invalidObjects)
    {
      out.writeCDOID(object.cdoID());
    }
  }

  @Override
  protected boolean[] confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    int size = in.readInt();
    boolean[] existanceFlags = new boolean[size];
    for (int i = 0; i < size; i++)
    {
      boolean existanceFlag = in.readBoolean();
      existanceFlags[i] = existanceFlag;
    }

    return existanceFlags;
  }
}
