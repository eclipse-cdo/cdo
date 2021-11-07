/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DeleteBranchRequest extends CDOClientRequestWithMonitoring<CDOBranch[]>
{
  private int branchID;

  public DeleteBranchRequest(CDOClientProtocol protocol, int branchID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_DELETE_BRANCH);
    this.branchID = branchID;
  }

  @Override
  protected int getRequestingWorkPercent()
  {
    return 1;
  }

  @Override
  protected int getConfirmingWorkPercent()
  {
    return 1;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeXInt(branchID);
  }

  @Override
  protected CDOBranch[] confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    int size = in.readXInt();
    CDOBranch[] branches = new CDOBranch[size];

    InternalCDOBranchManager branchManager = getSession().getBranchManager();

    for (int i = 0; i < size; i++)
    {
      int id = in.readXInt();
      branches[i] = branchManager.getBranch(id);
    }

    return branches;
  }
}
