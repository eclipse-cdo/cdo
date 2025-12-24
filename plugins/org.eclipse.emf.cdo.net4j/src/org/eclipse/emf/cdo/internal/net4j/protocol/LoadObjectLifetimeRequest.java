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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class LoadObjectLifetimeRequest extends CDOClientRequest<CDOBranchPointRange>
{
  private CDOID id;

  private CDOBranchPoint branchPoint;

  public LoadObjectLifetimeRequest(CDOClientProtocol protocol, CDOID id, CDOBranchPoint branchPoint)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_OBJECT_LIFETIME);
    this.id = id;
    this.branchPoint = branchPoint;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOID(id);
    out.writeCDOBranchPoint(branchPoint);
  }

  @Override
  protected CDOBranchPointRange confirming(CDODataInput in) throws IOException
  {
    return CDOBranchUtil.readRangeOrNull(in);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("LoadFirstRevisionsRequest(id={0}, branchPoint={1})", id, branchPoint);
  }
}
