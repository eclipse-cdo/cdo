/*
 * Copyright (c) 2009-2012, 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionRequest extends CDOClientRequest<InternalCDORevision>
{
  private CDOID id;

  private CDOBranchVersion branchVersion;

  private int referenceChunk;

  public LoadRevisionByVersionRequest(CDOClientProtocol protocol, CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION);
    this.id = id;
    this.branchVersion = branchVersion;
    this.referenceChunk = referenceChunk;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOID(id);
    out.writeCDOBranchVersion(branchVersion);
    out.writeXInt(referenceChunk);
  }

  @Override
  protected InternalCDORevision confirming(CDODataInput in) throws IOException
  {
    return RevisionInfo.readResult(in, id, branchVersion.getBranch());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("LoadRevisionByVersionRequest(id={0}, branchVersion={1}, referenceChunk={2})", id, branchVersion, referenceChunk);
  }
}
