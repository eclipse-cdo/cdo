/*
 * Copyright (c) 2010-2012, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadBranchesIndication extends CDOServerReadIndication
{
  private int startID;

  private int endID;

  public LoadBranchesIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_BRANCHES);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    startID = in.readXInt();
    endID = in.readXInt();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    branchManager.getBranches(startID, endID, new CDOBranchHandler()
    {
      @Override
      public void handleBranch(CDOBranch branch)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.REPLICATE_BRANCH);
          out.writeCDOBranch(branch);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    out.writeByte(CDOProtocolConstants.REPLICATE_FINISHED);
  }
}
