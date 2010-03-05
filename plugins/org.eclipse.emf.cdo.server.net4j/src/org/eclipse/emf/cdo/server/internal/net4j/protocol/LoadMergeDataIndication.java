/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadMergeDataIndication extends CDOReadIndication
{
  private CDORevisionAvailabilityInfo ancestorInfo;

  private CDORevisionAvailabilityInfo targetInfo;

  private CDORevisionAvailabilityInfo sourceInfo;

  public LoadMergeDataIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    ancestorInfo = readRevisionAvailabilityInfo(in);
    targetInfo = readRevisionAvailabilityInfo(in);
    sourceInfo = readRevisionAvailabilityInfo(in);
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    InternalRepository repository = getRepository();
    System.out.println(CDORevisionUtil.dumpAllRevisions(((MEMStore)repository.getStore()).getAllRevisions()));
    System.out.println(CDORevisionUtil.dumpAllRevisions(repository.getRevisionManager().getCache().getAllRevisions()));

    Set<CDOID> ids = repository.loadMergeData(ancestorInfo, targetInfo, sourceInfo, new CDORevisionHandler()
    {
      public void handleRevision(CDORevision revision)
      {
        try
        {
          out.writeBoolean(true);
          out.writeCDORevision(revision, CDORevision.UNCHUNKED);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    out.writeBoolean(false);
    out.writeInt(ids.size());
    for (CDOID id : ids)
    {
      out.writeCDOID(id);
    }
  }

  private CDORevisionAvailabilityInfo readRevisionAvailabilityInfo(CDODataInput in) throws IOException
  {
    CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      info.getAvailableRevisions().add(id);
    }

    return info;
  }
}
