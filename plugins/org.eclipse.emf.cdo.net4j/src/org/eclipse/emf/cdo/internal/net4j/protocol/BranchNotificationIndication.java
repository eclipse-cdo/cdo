/*
 * Copyright (c) 2010-2013, 2015, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import org.eclipse.net4j.util.concurrent.OrderedExecution;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class BranchNotificationIndication extends CDOClientIndication implements OrderedExecution
{
  public BranchNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_BRANCH_NOTIFICATION);
  }

  @Override
  public Object getExecutionOrderKey()
  {
    return CDOBranchManager.class;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    ChangeKind changeKind = in.readEnum(ChangeKind.class);
    int size = in.readXInt();

    if (size != 1)
    {
      if (changeKind == ChangeKind.RENAMED)
      {
        throw new IOException("A branch rename notification must contain exactly one branch");
      }

      if (changeKind == ChangeKind.CREATED)
      {
        throw new IOException("A branch creation notification must contain exactly one branch");
      }
    }

    InternalCDOBranchManager branchManager = getSession().getBranchManager();
    InternalCDOBranch firstBranch = null;
    int[] branchIDs = new int[size];

    for (int i = 0; i < size; i++)
    {
      int branchID = in.readXInt();
      branchIDs[i] = branchID;

      if (changeKind == ChangeKind.CREATED)
      {
        String name = in.readString();
        int baseBranchID = in.readXInt();
        long baseTimeStamp = in.readXLong();

        InternalCDOBranch baseBranch = branchManager.getBranch(baseBranchID);
        firstBranch = branchManager.getBranch(branchID);
        firstBranch.setBranchInfo(name, baseBranch, baseTimeStamp);
      }
      else if (firstBranch == null)
      {
        firstBranch = branchManager.getBranch(branchID);
      }

      if (changeKind == ChangeKind.RENAMED)
      {
        String name = in.readString();
        firstBranch.basicSetName(name);

        // basicSetName() has already fired the branch-changed event through the branch manager.
        return;
      }
    }

    branchManager.handleBranchChanged(firstBranch, changeKind, branchIDs);
  }
}
