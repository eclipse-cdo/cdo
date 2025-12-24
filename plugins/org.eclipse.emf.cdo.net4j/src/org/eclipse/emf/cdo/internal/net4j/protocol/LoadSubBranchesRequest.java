/*
 * Copyright (c) 2010-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.SubBranchInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadSubBranchesRequest extends CDOClientRequest<SubBranchInfo[]>
{
  private int branchID;

  public LoadSubBranchesRequest(CDOClientProtocol protocol, int branchID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_SUB_BRANCHES);
    this.branchID = branchID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(branchID);
  }

  @Override
  protected SubBranchInfo[] confirming(CDODataInput in) throws IOException
  {
    int size = in.readXInt();
    SubBranchInfo[] infos = new SubBranchInfo[size];
    for (int i = 0; i < infos.length; i++)
    {
      infos[i] = new SubBranchInfo(in);
    }

    return infos;
  }

  @Override
  protected String getAdditionalInfo()
  {
    return "branchID=" + branchID;
  }
}
