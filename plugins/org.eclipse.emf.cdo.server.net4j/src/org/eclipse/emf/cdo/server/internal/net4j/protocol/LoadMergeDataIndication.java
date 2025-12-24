/*
 * Copyright (c) 2010-2012, 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.MergeDataResult;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadMergeDataIndication extends CDOServerReadIndicationWithMonitoring
{
  private CDORevisionAvailabilityInfo targetInfo;

  private CDORevisionAvailabilityInfo sourceInfo;

  private CDORevisionAvailabilityInfo targetBaseInfo;

  private CDORevisionAvailabilityInfo sourceBaseInfo;

  private int infos;

  private boolean auto;

  public LoadMergeDataIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA);
  }

  @Override
  protected int getIndicatingWorkPercent()
  {
    return 10;
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    infos = in.readXInt();
    monitor.begin(infos);

    try
    {
      targetInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      sourceInfo = readRevisionAvailabilityInfo(in, monitor.fork());

      if (infos > 2)
      {
        targetBaseInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      }

      if (infos > 3)
      {
        sourceBaseInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private CDORevisionAvailabilityInfo readRevisionAvailabilityInfo(CDODataInput in, OMMonitor monitor) throws IOException
  {
    if (in.readBoolean())
    {
      CDOBranchPoint branchPoint = in.readCDOBranchPoint();
      CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);

      int size = in.readXInt();
      monitor.begin(size);

      try
      {
        for (int i = 0; i < size; i++)
        {
          CDOID id = in.readCDOID();
          info.getAvailableRevisions().put(id, null);
          monitor.worked();
        }

        return info;
      }
      finally
      {
        monitor.done();
      }
    }

    auto = true;
    return new CDORevisionAvailabilityInfo(CDOBranchUtil.AUTO_BRANCH_POINT);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    monitor.begin(2 + infos);

    try
    {
      InternalRepository repository = getRepository();
      MergeDataResult result = repository.getMergeData2(targetInfo, sourceInfo, targetBaseInfo, sourceBaseInfo, monitor.fork());

      Set<CDOID> targetIDs = result.getTargetIDs();
      Set<CDOID> targetAndSourceIDs = new HashSet<>();
      Set<CDOID> sourceIDs = result.getSourceIDs();

      // Write IDs of objects that are changed only in target.
      for (CDOID id : targetIDs)
      {
        if (sourceIDs.remove(id))
        {
          targetAndSourceIDs.add(id);
        }
        else
        {
          out.writeCDOID(id);
        }
      }

      out.writeCDOID(null);

      // Write IDs of objects that are changed in both target and source.
      for (CDOID id : targetAndSourceIDs)
      {
        out.writeCDOID(id);
      }

      out.writeCDOID(null);

      // Write IDs of objects that are changed only in source.
      for (CDOID id : sourceIDs)
      {
        out.writeCDOID(id);
      }

      out.writeCDOID(null);
      monitor.worked();

      if (auto)
      {
        out.writeCDOBranchPoint(targetBaseInfo.getBranchPoint());
        if (targetBaseInfo.getBranchPoint().equals(sourceBaseInfo.getBranchPoint()))
        {
          out.writeBoolean(false);
          infos = 3;
        }
        else
        {
          out.writeBoolean(true);
          out.writeCDOBranchPoint(sourceBaseInfo.getBranchPoint());
        }
      }

      Set<CDORevisionKey> writtenRevisions = new HashSet<>();
      writeRevisionAvailabilityInfo(out, targetInfo, writtenRevisions, monitor.fork());
      writeRevisionAvailabilityInfo(out, sourceInfo, writtenRevisions, monitor.fork());

      if (infos > 2)
      {
        writeRevisionAvailabilityInfo(out, targetBaseInfo, writtenRevisions, monitor.fork());
      }

      if (infos > 3)
      {
        writeRevisionAvailabilityInfo(out, sourceBaseInfo, writtenRevisions, monitor.fork());
      }

      CDOBranchUtil.writeBranchPointOrNull(out, result.getResultBase());
    }
    finally
    {
      monitor.done();
    }
  }

  private void writeRevisionAvailabilityInfo(final CDODataOutput out, CDORevisionAvailabilityInfo info, Set<CDORevisionKey> writtenRevisions, OMMonitor monitor)
      throws IOException
  {
    Collection<CDORevisionKey> revisions = info.getAvailableRevisions().values();
    for (Iterator<CDORevisionKey> it = revisions.iterator(); it.hasNext();)
    {
      CDORevisionKey key = it.next();
      if (key == null)
      {
        it.remove();
      }
    }

    int size = revisions.size();
    out.writeXInt(size);
    monitor.begin(size);

    try
    {
      for (CDORevisionKey revision : revisions)
      {
        CDORevisionKey key = CDORevisionUtil.copyRevisionKey(revision);
        if (writtenRevisions.add(key))
        {
          out.writeBoolean(true);
          out.writeCDORevision((CDORevision)revision, CDORevision.UNCHUNKED); // Exposes revision to client side
        }
        else
        {
          out.writeBoolean(false);
          out.writeCDORevisionKey(key);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
