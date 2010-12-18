/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadMergeDataRequest extends CDOClientRequestWithMonitoring<Set<CDOID>>
{
  private CDORevisionAvailabilityInfo ancestorInfo;

  private CDORevisionAvailabilityInfo targetInfo;

  private CDORevisionAvailabilityInfo sourceInfo;

  public LoadMergeDataRequest(CDOClientProtocol protocol, CDORevisionAvailabilityInfo ancestorInfo,
      CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA);
    this.ancestorInfo = ancestorInfo;
    this.targetInfo = targetInfo;
    this.sourceInfo = sourceInfo;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    monitor.begin(3);

    try
    {
      writeRevisionAvailabilityInfo(out, ancestorInfo, monitor.fork());
      writeRevisionAvailabilityInfo(out, targetInfo, monitor.fork());
      writeRevisionAvailabilityInfo(out, sourceInfo, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  private void writeRevisionAvailabilityInfo(CDODataOutput out, CDORevisionAvailabilityInfo info, OMMonitor monitor)
      throws IOException
  {
    Set<CDOID> availableRevisions = info.getAvailableRevisions().keySet();
    int size = availableRevisions.size();

    out.writeCDOBranchPoint(info.getBranchPoint());
    out.writeInt(size);

    monitor.begin(size);

    try
    {
      for (CDOID id : availableRevisions)
      {
        out.writeCDOID(id);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected Set<CDOID> confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    Set<CDOID> result = new HashSet<CDOID>();
    int size = in.readInt();

    monitor.begin(size + 3);

    try
    {
      for (int i = 0; i < size; i++)
      {
        CDOID id = in.readCDOID();
        result.add(id);
        monitor.worked();
      }

      readRevisionAvailabilityInfo(in, ancestorInfo, result, monitor.fork());
      readRevisionAvailabilityInfo(in, targetInfo, result, monitor.fork());
      readRevisionAvailabilityInfo(in, sourceInfo, result, monitor.fork());
      return result;
    }
    finally
    {
      monitor.done();
    }
  }

  private void readRevisionAvailabilityInfo(CDODataInput in, CDORevisionAvailabilityInfo info, Set<CDOID> result,
      OMMonitor monitor) throws IOException
  {
    int size = in.readInt();
    monitor.begin(size + 1);

    try
    {
      for (int i = 0; i < size; i++)
      {
        CDORevision revision;
        if (in.readBoolean())
        {
          revision = in.readCDORevision();
        }
        else
        {
          CDORevisionKey key = in.readCDORevisionKey();
          revision = getRevision(key, ancestorInfo);
          if (revision == null)
          {
            revision = getRevision(key, targetInfo);
          }
        }

        info.addRevision(revision);
        monitor.worked();
      }

      Set<Map.Entry<CDOID, CDORevisionKey>> entrySet = info.getAvailableRevisions().entrySet();
      for (Iterator<Map.Entry<CDOID, CDORevisionKey>> it = entrySet.iterator(); it.hasNext();)
      {
        Map.Entry<CDOID, CDORevisionKey> entry = it.next();
        if (!result.contains(entry.getKey()))
        {
          it.remove();
        }
      }

      monitor.worked();
    }
    finally
    {
      monitor.done();
    }
  }

  private CDORevision getRevision(CDORevisionKey key, CDORevisionAvailabilityInfo info)
  {
    CDORevisionKey revision = info.getRevision(key.getID());
    if (revision instanceof CDORevision)
    {
      if (key.equals(revision))
      {
        return (CDORevision)revision;
      }
    }

    return null;
  }
}
