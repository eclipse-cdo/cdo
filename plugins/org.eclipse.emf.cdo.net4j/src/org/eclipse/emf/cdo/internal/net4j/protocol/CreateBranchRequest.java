/*
 * Copyright (c) 2010-2013, 2017 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import org.eclipse.net4j.util.collection.Pair;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchRequest extends CDOClientRequest<Pair<Integer, Long>>
{
  private int branchID;

  private BranchInfo branchInfo;

  public CreateBranchRequest(CDOClientProtocol protocol, int branchID, BranchInfo branchInfo)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREATE_BRANCH);
    this.branchID = branchID;
    this.branchInfo = branchInfo;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(branchID);
    branchInfo.write(out);
  }

  @Override
  protected Pair<Integer, Long> confirming(CDODataInput in) throws IOException
  {
    branchID = in.readXInt();
    long baseTimeStamp = in.readXLong();
    return Pair.create(branchID, baseTimeStamp);
  }
}
