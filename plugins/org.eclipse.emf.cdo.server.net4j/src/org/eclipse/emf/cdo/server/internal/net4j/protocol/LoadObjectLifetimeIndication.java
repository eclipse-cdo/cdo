/*
 * Copyright (c) 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadObjectLifetimeIndication extends CDOServerReadIndication
{
  private CDOID id;

  private CDOBranchPoint branchPoint;

  public LoadObjectLifetimeIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_OBJECT_LIFETIME);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    id = in.readCDOID();
    branchPoint = in.readCDOBranchPoint();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    CDOBranchPointRange range = revisionManager.getObjectLifetime(id, branchPoint);
    CDOBranchUtil.writeRangeOrNull(out, range);
  }
}
