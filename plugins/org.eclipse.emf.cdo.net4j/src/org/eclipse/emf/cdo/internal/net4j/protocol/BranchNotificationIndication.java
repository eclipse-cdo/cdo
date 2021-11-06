/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class BranchNotificationIndication extends CDOClientIndication
{
  public BranchNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_BRANCH_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalCDOBranchManager branchManager = getSession().getBranchManager();
    InternalCDOBranch firstBranch = null;

    ChangeKind changeKind = in.readEnum(ChangeKind.class);
    int size = in.readXInt();
    int[] branchIDs = new int[size];

    for (int i = 0; i < size; i++)
    {
      int branchID = in.readXInt();
      branchIDs[i] = branchID;

      if (firstBranch == null)
      {
        firstBranch = branchManager.getBranch(branchID);
      }

      if (changeKind == ChangeKind.RENAMED)
      {
        String name = in.readString();
        firstBranch.basicSetName(name);
        break;
      }
    }

    branchManager.handleBranchChanged(firstBranch, changeKind, branchIDs);
  }
}
