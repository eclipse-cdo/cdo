/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchRequest extends CDOClientRequest<Integer>
{
  private BranchInfo branchInfo;

  public CreateBranchRequest(CDOClientProtocol protocol, BranchInfo branchInfo)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREATE_BRANCH);
    this.branchInfo = branchInfo;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    branchInfo.write(out);
  }

  @Override
  protected Integer confirming(CDODataInput in) throws IOException
  {
    return in.readInt();
  }
}
