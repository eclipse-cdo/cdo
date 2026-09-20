/*
 * Copyright (c) 2009-2012, 2016-2018, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.ChunkRange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LoadChunkRequest extends CDOClientRequest<Object>
{
  private final InternalCDORevision revision;

  private final EStructuralFeature feature;

  private final List<ChunkRange> ranges;

  public LoadChunkRequest(CDOClientProtocol protocol, InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex, int fromIndex,
      int toIndex)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
    this.revision = revision;
    this.feature = feature;
    ranges = Collections.singletonList(new ChunkRange(accessIndex, fetchIndex, fromIndex, toIndex));
  }

  public LoadChunkRequest(CDOClientProtocol protocol, InternalCDORevision revision, EStructuralFeature feature, List<ChunkRange> ranges)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
    this.revision = revision;
    this.feature = feature;
    this.ranges = ranges;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOID(revision.getID());
    out.writeCDOBranch(revision.getBranch());
    out.writeXInt(revision.getVersion());
    out.writeCDOClassifierRef(feature.getEContainingClass());
    out.writeXInt(feature.getFeatureID());

    out.writeXInt(ranges.size());
    for (ChunkRange range : ranges)
    {
      int diffIndex = range.getAccessIndex() - range.getFetchIndex();
      out.writeXInt(range.getFromIndex() - diffIndex);
      out.writeXInt(range.getToIndex() - diffIndex);
    }
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    InternalCDOList list = (InternalCDOList)revision.getListOrNull(feature);
    Object accessID = null;

    for (ChunkRange range : ranges)
    {
      for (int i = range.getFromIndex(); i <= range.getToIndex(); i++)
      {
        Object value = type.readValue(in);
        list.loadValue(i, value);

        if (ranges.size() == 1 && i == range.getAccessIndex())
        {
          accessID = value;
        }
      }
    }

    return accessID;
  }
}
