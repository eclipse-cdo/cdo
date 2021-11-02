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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionIndication extends CDOServerReadIndication
{
  private CDOID id;

  private CDOBranchVersion branchVersion;

  private int referenceChunk;

  public LoadRevisionByVersionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    id = in.readCDOID();
    branchVersion = in.readCDOBranchVersion();
    referenceChunk = in.readXInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    InternalCDORevision revision = revisionManager.getRevisionByVersion(id, branchVersion, referenceChunk, true);
    RevisionInfo.writeResult(out, revision, true, referenceChunk, null); // Exposes revision to client side
  }
}
