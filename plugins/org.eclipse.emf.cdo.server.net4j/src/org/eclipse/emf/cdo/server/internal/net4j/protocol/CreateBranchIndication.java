/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchIndication extends CDOReadIndication
{
  private BranchInfo branchInfo;

  public CreateBranchIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREATE_BRANCH);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchInfo = new BranchInfo(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    InternalCDOBranch baseBranch = branchManager.getBranch(branchInfo.getBaseBranchID());
    InternalCDOBranch branch = branchManager.createBranch(branchInfo.getName(), baseBranch, branchInfo
        .getBaseTimeStamp());
    out.writeInt(branch.getID());

    InternalSessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.handleBranchNotification(branch, getSession());
  }
}
