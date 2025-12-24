/*
 * Copyright (c) 2013-2015, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Mathieu Velten - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.ObjectUtil;

import java.io.IOException;

/**
 * @author Mathieu Velten
 */
public class RenameBranchIndication extends CDOServerWriteIndication
{
  private int branchID;

  private String oldName;

  private String newName;

  public RenameBranchIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_RENAME_BRANCH);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchID = in.readXInt();
    oldName = in.readString();
    newName = in.readString();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    InternalCDOBranch branch = branchManager.getBranch(branchID);
    if (!ObjectUtil.equals(branch.getName(), oldName))
    {
      throw new IllegalStateException("Branch name has been changed by someone else in the meantime");
    }

    branch.setName(newName);

    InternalSession session = getSession();
    InternalSessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.sendBranchNotification(session, ChangeKind.RENAMED, branch);

    out.writeBoolean(true);
  }
}
