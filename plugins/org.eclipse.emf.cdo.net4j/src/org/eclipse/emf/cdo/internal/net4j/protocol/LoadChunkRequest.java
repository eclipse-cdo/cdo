/*
 * Copyright (c) 2009-2012, 2016-2018, 2021 Eike Stepper (Loehne, Germany) and others.
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

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkRequest extends CDOClientRequest<Object>
{
  private InternalCDORevision revision;

  private EStructuralFeature feature;

  private int accessIndex;

  private int fromIndex;

  private int toIndex;

  private int fetchIndex;

  public LoadChunkRequest(CDOClientProtocol protocol, InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex, int fromIndex,
      int toIndex)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
    this.revision = revision;
    this.feature = feature;
    this.accessIndex = accessIndex;
    this.fetchIndex = fetchIndex;
    this.fromIndex = fromIndex;
    this.toIndex = toIndex;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOID(revision.getID());
    out.writeCDOBranch(revision.getBranch());
    out.writeXInt(revision.getVersion());
    out.writeCDOClassifierRef(feature.getEContainingClass());
    out.writeXInt(feature.getFeatureID());

    int diffIndex = accessIndex - fetchIndex;
    out.writeXInt(fromIndex - diffIndex);
    out.writeXInt(toIndex - diffIndex);
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    Object accessID = null;
    InternalCDOList list = (InternalCDOList)revision.getListOrNull(feature);
    for (int i = fromIndex; i <= toIndex; i++)
    {
      Object value = type.readValue(in);
      list.setWithoutFrozenCheck(i, value);
      if (i == accessIndex)
      {
        accessID = value;
      }
    }

    return accessID;
  }
}
